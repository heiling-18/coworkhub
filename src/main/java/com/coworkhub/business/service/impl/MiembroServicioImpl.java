package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.MiembroServicio;
import com.coworkhub.persistence.entity.Member;
import com.coworkhub.persistence.entity.MembershipPlan;
import com.coworkhub.persistence.entity.UserAccount;
import com.coworkhub.persistence.enums.BookingStatus;
import com.coworkhub.persistence.enums.MemberStatus;
import com.coworkhub.persistence.enums.Role;
import com.coworkhub.persistence.repository.*;
import com.coworkhub.presentation.dto.request.MiembroActualizacionSolicitud;
import com.coworkhub.presentation.dto.request.MiembroRegistroSolicitud;
import com.coworkhub.presentation.dto.response.ConsumoRespuesta;
import com.coworkhub.presentation.dto.response.MiembroRespuesta;
import com.coworkhub.security.UsuarioActual;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MiembroServicioImpl implements MiembroServicio {

    private final MemberRepository memberRepository;
    private final MembershipPlanRepository planRepository;
    private final UserAccountRepository accountRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioActual usuarioActual;

    @Override
    public List<MiembroRespuesta> listar(String nombre, MemberStatus estado) {
        String filtro = (nombre == null || nombre.isBlank()) ? null : nombre.trim();
        return memberRepository.search(filtro, estado).stream().map(Mapeador::aMiembro).toList();
    }

    @Override
    public MiembroRespuesta obtener(Long id) {
        usuarioActual.verificarDueno(id);   // RN-13: un MEMBER solo ve su propio perfil
        return Mapeador.aMiembro(buscar(id));
    }

    /** RF-02: crea el miembro y su cuenta (rol MEMBER) en la misma transacción. */
    @Override
    @Transactional
    public MiembroRespuesta registrar(MiembroRegistroSolicitud s) {
        if (memberRepository.existsByDocumentNumber(s.numeroDocumento().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe un miembro con ese documento");
        }
        if (memberRepository.existsByEmailIgnoreCase(s.email().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe un miembro con ese email");
        }
        if (accountRepository.existsByUsername(s.usuario().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe ese nombre de usuario");
        }
        Member miembro = new Member();
        miembro.setDocumentNumber(s.numeroDocumento().trim());
        miembro.setFullName(s.nombreCompleto().trim());
        miembro.setEmail(s.email().trim());
        miembro.setStatus(MemberStatus.ACTIVE);
        miembro.setPlan(buscarPlan(s.planId()));
        memberRepository.save(miembro);

        UserAccount cuenta = new UserAccount();
        cuenta.setUsername(s.usuario().trim());
        cuenta.setPassword(passwordEncoder.encode(s.contrasena()));   // se guarda el hash BCrypt
        cuenta.setRole(Role.MEMBER);
        cuenta.setMember(miembro);
        accountRepository.save(cuenta);
        return Mapeador.aMiembro(miembro);
    }

    @Override
    @Transactional
    public MiembroRespuesta actualizar(Long id, MiembroActualizacionSolicitud s) {
        Member miembro = buscar(id);
        if (memberRepository.existsByEmailIgnoreCaseAndIdNot(s.email().trim(), id)) {
            throw new ReglaNegocioException("RN-11", "Ya existe un miembro con ese email");
        }
        miembro.setFullName(s.nombreCompleto().trim());
        miembro.setEmail(s.email().trim());
        miembro.setPlan(buscarPlan(s.planId()));
        return Mapeador.aMiembro(miembro);
    }

    @Override
    @Transactional
    public MiembroRespuesta cambiarEstado(Long id, MemberStatus estado) {
        Member miembro = buscar(id);
        miembro.setStatus(estado);
        return Mapeador.aMiembro(miembro);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Member miembro = buscar(id);
        if (bookingRepository.existsByMemberId(id)) {   // RN-12
            throw new ReglaNegocioException("RN-12", "No se puede eliminar el miembro porque tiene reservas");
        }
        accountRepository.findByMemberId(id).ifPresent(accountRepository::delete);
        memberRepository.delete(miembro);
    }

    /** RF-10: horas incluidas, usadas, restantes y excedentes del mes. */
    @Override
    public ConsumoRespuesta consumo(Long id, YearMonth mes) {
        usuarioActual.verificarDueno(id);   // RN-13
        Member miembro = buscar(id);
        long minutosUsados = bookingRepository.monthlyUsage(id, mes.atDay(1).atStartOfDay(),
                        mes.plusMonths(1).atDay(1).atStartOfDay(),
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.COMPLETED),
                        BookingStatus.CANCELLED)
                .stream()
                .mapToLong(b -> Duration.between(b.getStartTime(), b.getEndTime()).toMinutes())
                .sum();
        long minutosIncluidos = miembro.getPlan().getIncludedHoursPerMonth() * 60L;
        return new ConsumoRespuesta(id, mes.toString(), horas(minutosIncluidos), horas(minutosUsados),
                horas(Math.max(0, minutosIncluidos - minutosUsados)),
                horas(Math.max(0, minutosUsados - minutosIncluidos)));
    }

    private static BigDecimal horas(long minutos) {
        return BigDecimal.valueOf(minutos).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private Member buscar(Long id) {
        return memberRepository.findWithPlanById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el miembro " + id));
    }

    private MembershipPlan buscarPlan(Long planId) {
        return planRepository.findById(planId)
                .orElseThrow(() -> new NoEncontradoException("No existe el plan " + planId));
    }
}
