package com.coworkhub.business.mapper;

import com.coworkhub.persistence.entity.*;
import com.coworkhub.presentation.dto.response.*;

import java.util.Comparator;

/** Convierte entidades en DTO de salida. La API nunca devuelve entidades. */
public final class Mapeador {

    private Mapeador() {
    }

    public static SedeRespuesta aSede(Location l) {
        return new SedeRespuesta(l.getId(), l.getName(), l.getCity(), l.getAddress(),
                l.getOpeningTime(), l.getClosingTime());
    }

    public static EquipamientoRespuesta aEquipamiento(Equipment e) {
        return new EquipamientoRespuesta(e.getId(), e.getName());
    }

    public static SalaRespuesta aSala(Room r) {
        return new SalaRespuesta(r.getId(), r.getName(), r.getType(), r.getCapacity(), r.getHourlyRate(),
                r.getActive(), r.getLocation().getId(), r.getLocation().getName(),
                r.getEquipment().stream()
                        .sorted(Comparator.comparing(Equipment::getId))
                        .map(Mapeador::aEquipamiento)
                        .toList());
    }

    public static ServicioAdicionalRespuesta aServicio(AdditionalService s) {
        return new ServicioAdicionalRespuesta(s.getId(), s.getName(), s.getUnitPrice());
    }

    public static PlanRespuesta aPlan(MembershipPlan p) {
        return new PlanRespuesta(p.getId(), p.getName(), p.getIncludedHoursPerMonth(), p.getOverageDiscount(),
                p.getMaxActiveBookings());
    }

    public static MiembroRespuesta aMiembro(Member m) {
        return new MiembroRespuesta(m.getId(), m.getDocumentNumber(), m.getFullName(), m.getEmail(),
                m.getStatus(), m.getPlan().getId(), m.getPlan().getName());
    }

    public static ReservaRespuesta aReserva(Booking b) {
        return new ReservaRespuesta(b.getId(), b.getRoom().getId(), b.getRoom().getName(),
                b.getMember().getId(), b.getMember().getFullName(),
                b.getStartTime(), b.getEndTime(), b.getAttendees(), b.getStatus(),
                b.getRoomCost(), b.getServicesCost(), b.getTotalCost(), b.getCancellationFee(),
                b.getItems().stream()
                        .map(i -> new ItemReservaRespuesta(i.getService().getId(), i.getService().getName(),
                                i.getQuantity(), i.getAppliedUnitPrice(), i.subtotal()))
                        .toList());
    }
}
