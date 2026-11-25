package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.DatosInvalidosException;
import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.ReservaServicio;
import com.coworkhub.persistence.entity.*;
import com.coworkhub.persistence.enums.BookingStatus;
import com.coworkhub.persistence.enums.MemberStatus;
import com.coworkhub.persistence.repository.AdditionalServiceRepository;
import com.coworkhub.persistence.repository.BookingRepository;
import com.coworkhub.persistence.repository.MemberRepository;
import com.coworkhub.persistence.repository.RoomRepository;
import com.coworkhub.presentation.dto.request.ReservaSolicitud;
import com.coworkhub.presentation.dto.request.ServicioSolicitud;
import com.coworkhub.presentation.dto.response.CancelacionRespuesta;
import com.coworkhub.presentation.dto.response.ReservaRespuesta;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservaServicioImpl implements ReservaServicio {

    private static final List<BookingStatus> ACTIVAS = List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);
    private static final List<BookingStatus> CONSUMEN_HORAS =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.COMPLETED);

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final AdditionalServiceRepository serviceRepository;
    private final CalculadoraCostos calculadora;
    private final Clock reloj;

    // Constructor escrito a mano para usar @Qualifier en el Clock (módulo 02)
    public ReservaServicioImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
                               MemberRepository memberRepository, AdditionalServiceRepository serviceRepository,
                               CalculadoraCostos calculadora, @Qualifier("relojNegocio") Clock reloj) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
        this.serviceRepository = serviceRepository;
        this.calculadora = calculadora;
        this.reloj = reloj;
    }

    /**
     * RF-04. RN-01 con concurrencia: se bloquea la fila de la sala (SELECT ... FOR UPDATE) y se usa
     * READ_COMMITTED para que la segunda solicitud vea la reserva que la primera acaba de guardar.
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservaRespuesta crear(ReservaSolicitud s) {
        Long miembroId = resolverMiembro(s.miembroId());
        Room sala = roomRepository.lockById(s.salaId())
                .orElseThrow(() -> new NoEncontradoException("No existe la sala " + s.salaId()));
        Member miembro = memberRepository.findWithPlanById(miembroId)
                .orElseThrow(() -> new NoEncontradoException("No existe el miembro " + miembroId));
        LocalDateTime inicio = s.inicio();
        LocalDateTime fin = s.fin();
        LocalDateTime ahora = LocalDateTime.now(reloj);

        validarDuracion(inicio, fin);                               // RN-02
        validarHorario(sala.getLocation(), inicio, fin, ahora);     // RN-03
        if (!Boolean.TRUE.equals(sala.getActive())) {               // RN-04
            throw new ReglaNegocioException("RN-04", "La sala no está activa");
        }
        if (s.asistentes() > sala.getCapacity()) {
            throw new ReglaNegocioException("RN-04",
                    "Los asistentes superan la capacidad de la sala (" + sala.getCapacity() + ")");
        }
        if (miembro.getStatus() != MemberStatus.ACTIVE) {           // RN-05
            throw new ReglaNegocioException("RN-05", "El miembro está suspendido y no puede reservar");
        }
        if (bookingRepository.existsByRoomIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                sala.getId(), ACTIVAS, fin, inicio)) {              // RN-01
            throw new ReglaNegocioException("RN-01", "La sala ya está reservada en ese horario");
        }
        MembershipPlan plan = miembro.getPlan();
        long activas = bookingRepository.countByMemberIdAndStatusInAndStartTimeAfter(miembroId, ACTIVAS, ahora);
        if (activas >= plan.getMaxActiveBookings()) {               // RN-06
            throw new ReglaNegocioException("RN-06", "El miembro ya tiene el máximo de reservas activas de su plan ("
                    + plan.getMaxActiveBookings() + ")");
        }

        Booking reserva = new Booking();
        reserva.setRoom(sala);
        reserva.setMember(miembro);
        reserva.setStartTime(inicio);
        reserva.setEndTime(fin);
        reserva.setAttendees(s.asistentes());
        reserva.setStatus(BookingStatus.PENDING);
        if (s.servicios() != null) {
            s.servicios().forEach(item -> agregarItem(reserva, item.servicioId(), item.cantidad()));
        }
        reserva.setRoomCost(calculadora.costoSala(Duration.between(inicio, fin).toMinutes(),   // RN-07
                minutosUsadosEnElMes(miembroId, inicio), plan.getIncludedHoursPerMonth(),
                sala.getHourlyRate(), plan.getOverageDiscount()));
        recalcularServicios(reserva);
        return Mapeador.aReserva(bookingRepository.save(reserva));
    }

    @Override
    public ReservaRespuesta obtener(Long id) {
        return Mapeador.aReserva(buscar(id));
    }

    /** RF-05: por miembro, por sala y fecha, o por estado. */
    @Override
    public List<ReservaRespuesta> listar(Long miembroId, Long salaId, LocalDate fecha, BookingStatus estado) {
        List<Booking> reservas;
        if (miembroId != null) {
            reservas = estado == null
                    ? bookingRepository.findByMemberIdOrderByStartTimeAsc(miembroId)
                    : bookingRepository.findByMemberIdAndStatusOrderByStartTimeAsc(miembroId, estado);
        } else if (salaId != null) {
            if (fecha == null) {
                throw new DatosInvalidosException("Para filtrar por sala debe enviar también la fecha");
            }
            reservas = bookingRepository
                    .findByRoomIdAndStartTimeGreaterThanEqualAndStartTimeLessThanOrderByStartTimeAsc(
                            salaId, fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
        } else if (estado != null) {
            reservas = bookingRepository.findByStatusOrderByStartTimeAsc(estado);
        } else {
            reservas = bookingRepository.findAllByOrderByStartTimeAsc();
        }
        return reservas.stream().map(Mapeador::aReserva).toList();
    }

    /** RF-06 + RN-10: los servicios solo cambian mientras la reserva está PENDING. */
    @Override
    @Transactional
    public ReservaRespuesta agregarServicio(Long id, ServicioSolicitud s) {
        Booking reserva = buscarParaCambiarServicios(id);
        agregarItem(reserva, s.servicioId(), s.cantidad());
        recalcularServicios(reserva);
        return Mapeador.aReserva(reserva);
    }

    @Override
    @Transactional
    public ReservaRespuesta quitarServicio(Long id, Long servicioId) {
        Booking reserva = buscarParaCambiarServicios(id);
        BookingItem item = reserva.getItems().stream()
                .filter(i -> i.getService().getId().equals(servicioId))
                .findFirst()
                .orElseThrow(() -> new NoEncontradoException("La reserva no tiene el servicio " + servicioId));
        reserva.removeItem(item);   // orphanRemoval borra la fila de booking_items
        recalcularServicios(reserva);
        return Mapeador.aReserva(reserva);
    }

    /** RF-07 + RN-09: PENDING -> CONFIRMED. */
    @Override
    @Transactional
    public ReservaRespuesta confirmar(Long id) {
        Booking reserva = buscar(id);
        if (reserva.getStatus() != BookingStatus.PENDING) {
            throw new ReglaNegocioException("RN-09", "Solo se puede confirmar una reserva PENDING (estado actual: "
                    + reserva.getStatus() + ")");
        }
        reserva.setStatus(BookingStatus.CONFIRMED);
        return Mapeador.aReserva(reserva);
    }

    /** RF-09 + RN-09: CONFIRMED -> COMPLETED. */
    @Override
    @Transactional
    public ReservaRespuesta completar(Long id) {
        Booking reserva = buscar(id);
        if (reserva.getStatus() != BookingStatus.CONFIRMED) {
            throw new ReglaNegocioException("RN-09", "Solo se puede completar una reserva CONFIRMED (estado actual: "
                    + reserva.getStatus() + ")");
        }
        reserva.setStatus(BookingStatus.COMPLETED);
        return Mapeador.aReserva(reserva);
    }

    /** RF-08 + RN-08 + RN-09: PENDING o CONFIRMED -> CANCELLED, con el cargo que corresponda. */
    @Override
    @Transactional
    public CancelacionRespuesta cancelar(Long id) {
        Booking reserva = buscar(id);
        if (!ACTIVAS.contains(reserva.getStatus())) {
            throw new ReglaNegocioException("RN-09", "Solo se puede cancelar una reserva PENDING o CONFIRMED (estado actual: "
                    + reserva.getStatus() + ")");
        }
        BigDecimal cargo = calculadora.cargoCancelacion(LocalDateTime.now(reloj), reserva.getStartTime(),
                reserva.getRoomCost());
        reserva.setStatus(BookingStatus.CANCELLED);
        reserva.setCancellationFee(cargo);
        boolean sinCargo = cargo.signum() == 0;
        return new CancelacionRespuesta(reserva.getId(), reserva.getStatus(), cargo, sinCargo,
                sinCargo ? "Cancelada sin cargo; las horas se devuelven al plan"
                        : "Cancelada con cargo del 50 % del costo de la sala");
    }

    // ------------------------------------------------------------------ auxiliares

    /** En la fase 08 se agrega: un MEMBER solo reserva a su nombre. */
    private Long resolverMiembro(Long miembroId) {
        if (miembroId == null) {
            throw new DatosInvalidosException("miembroId es obligatorio");
        }
        return miembroId;
    }

    /** RN-02: entre 30 min y 8 h, en múltiplos de 30 min, el mismo día. */
    private void validarDuracion(LocalDateTime inicio, LocalDateTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException("RN-02", "La hora de fin debe ser posterior a la de inicio");
        }
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new ReglaNegocioException("RN-02", "La reserva debe empezar y terminar el mismo día");
        }
        long minutos = Duration.between(inicio, fin).toMinutes();
        if (minutos < 30 || minutos > 480 || minutos % 30 != 0) {
            throw new ReglaNegocioException("RN-02",
                    "La duración debe estar entre 30 minutos y 8 horas, en múltiplos de 30 minutos");
        }
    }

    /** RN-03: dentro del horario de la sede, en el futuro y con al menos 1 h de anticipación. */
    private void validarHorario(Location sede, LocalDateTime inicio, LocalDateTime fin, LocalDateTime ahora) {
        if (inicio.toLocalTime().isBefore(sede.getOpeningTime()) || fin.toLocalTime().isAfter(sede.getClosingTime())) {
            throw new ReglaNegocioException("RN-03", "La reserva está fuera del horario de la sede ("
                    + sede.getOpeningTime() + " - " + sede.getClosingTime() + ")");
        }
        if (inicio.isBefore(ahora.plusHours(1))) {
            throw new ReglaNegocioException("RN-03", "La reserva debe hacerse con al menos 1 hora de anticipación");
        }
    }

    private long minutosUsadosEnElMes(Long miembroId, LocalDateTime inicio) {
        YearMonth mes = YearMonth.from(inicio);
        return bookingRepository.monthlyUsage(miembroId, mes.atDay(1).atStartOfDay(),
                        mes.plusMonths(1).atDay(1).atStartOfDay(), CONSUMEN_HORAS, BookingStatus.CANCELLED)
                .stream()
                .mapToLong(b -> Duration.between(b.getStartTime(), b.getEndTime()).toMinutes())
                .sum();
    }

    /** Si el servicio ya está en la reserva suma la cantidad; si no, crea el item con el precio actual. */
    private void agregarItem(Booking reserva, Long servicioId, Integer cantidad) {
        AdditionalService servicio = serviceRepository.findById(servicioId)
                .orElseThrow(() -> new NoEncontradoException("No existe el servicio adicional " + servicioId));
        reserva.getItems().stream()
                .filter(i -> i.getService().getId().equals(servicioId))
                .findFirst()
                .ifPresentOrElse(i -> i.setQuantity(i.getQuantity() + cantidad), () -> {
                    BookingItem item = new BookingItem();
                    item.setService(servicio);
                    item.setQuantity(cantidad);
                    item.setAppliedUnitPrice(servicio.getUnitPrice());
                    reserva.addItem(item);
                });
    }

    private void recalcularServicios(Booking reserva) {
        BigDecimal servicios = reserva.getItems().stream()
                .map(BookingItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        reserva.setServicesCost(servicios);
        reserva.setTotalCost(reserva.getRoomCost().add(servicios));
    }

    private Booking buscarParaCambiarServicios(Long id) {
        Booking reserva = buscar(id);
        if (reserva.getStatus() != BookingStatus.PENDING) {
            throw new ReglaNegocioException("RN-10", "Los servicios solo se pueden cambiar mientras la reserva está PENDING");
        }
        return reserva;
    }

    private Booking buscar(Long id) {
        return bookingRepository.findDetailById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe la reserva " + id));
    }
}
