package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.PlanServicio;
import com.coworkhub.persistence.entity.MembershipPlan;
import com.coworkhub.persistence.repository.MemberRepository;
import com.coworkhub.persistence.repository.MembershipPlanRepository;
import com.coworkhub.presentation.dto.request.PlanSolicitud;
import com.coworkhub.presentation.dto.response.PlanRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanServicioImpl implements PlanServicio {

    private final MembershipPlanRepository planRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<PlanRespuesta> listar() {
        return planRepository.findAllByOrderByIdAsc().stream().map(Mapeador::aPlan).toList();
    }

    @Override
    public PlanRespuesta obtener(Long id) {
        return Mapeador.aPlan(buscar(id));
    }

    @Override
    @Transactional
    public PlanRespuesta crear(PlanSolicitud s) {
        if (planRepository.existsByNameIgnoreCase(s.nombre().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe un plan con ese nombre");
        }
        MembershipPlan plan = new MembershipPlan();
        copiar(s, plan);
        return Mapeador.aPlan(planRepository.save(plan));
    }

    @Override
    @Transactional
    public PlanRespuesta actualizar(Long id, PlanSolicitud s) {
        MembershipPlan plan = buscar(id);
        if (planRepository.existsByNameIgnoreCaseAndIdNot(s.nombre().trim(), id)) {
            throw new ReglaNegocioException("RN-11", "Ya existe un plan con ese nombre");
        }
        copiar(s, plan);
        return Mapeador.aPlan(plan);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        MembershipPlan plan = buscar(id);
        if (memberRepository.existsByPlanId(id)) {   // RN-12
            throw new ReglaNegocioException("RN-12", "No se puede eliminar el plan porque tiene miembros");
        }
        planRepository.delete(plan);
    }

    private void copiar(PlanSolicitud s, MembershipPlan plan) {
        plan.setName(s.nombre().trim());
        plan.setIncludedHoursPerMonth(s.horasIncluidasMes());
        plan.setOverageDiscount(s.descuentoExcedente());
        plan.setMaxActiveBookings(s.maxReservasActivas());
    }

    private MembershipPlan buscar(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el plan " + id));
    }
}
