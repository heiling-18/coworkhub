package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.NoEncontradoException;
import com.coworkhub.business.exception.ReglaNegocioException;
import com.coworkhub.business.mapper.Mapeador;
import com.coworkhub.business.service.ServicioAdicionalServicio;
import com.coworkhub.persistence.entity.AdditionalService;
import com.coworkhub.persistence.repository.AdditionalServiceRepository;
import com.coworkhub.persistence.repository.BookingItemRepository;
import com.coworkhub.presentation.dto.request.ServicioAdicionalSolicitud;
import com.coworkhub.presentation.dto.response.ServicioAdicionalRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicioAdicionalServicioImpl implements ServicioAdicionalServicio {

    private final AdditionalServiceRepository serviceRepository;
    private final BookingItemRepository itemRepository;

    @Override
    public List<ServicioAdicionalRespuesta> listar() {
        return serviceRepository.findAllByOrderByIdAsc().stream().map(Mapeador::aServicio).toList();
    }

    @Override
    public ServicioAdicionalRespuesta obtener(Long id) {
        return Mapeador.aServicio(buscar(id));
    }

    @Override
    @Transactional
    public ServicioAdicionalRespuesta crear(ServicioAdicionalSolicitud s) {
        if (serviceRepository.existsByNameIgnoreCase(s.nombre().trim())) {
            throw new ReglaNegocioException("RN-11", "Ya existe un servicio adicional con ese nombre");
        }
        AdditionalService servicio = new AdditionalService();
        copiar(s, servicio);
        return Mapeador.aServicio(serviceRepository.save(servicio));
    }

    @Override
    @Transactional
    public ServicioAdicionalRespuesta actualizar(Long id, ServicioAdicionalSolicitud s) {
        AdditionalService servicio = buscar(id);
        if (serviceRepository.existsByNameIgnoreCaseAndIdNot(s.nombre().trim(), id)) {
            throw new ReglaNegocioException("RN-11", "Ya existe un servicio adicional con ese nombre");
        }
        // Cambiar el precio no afecta reservas viejas: guardan appliedUnitPrice
        copiar(s, servicio);
        return Mapeador.aServicio(servicio);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        AdditionalService servicio = buscar(id);
        if (itemRepository.existsByServiceId(id)) {   // RN-12
            throw new ReglaNegocioException("RN-12", "No se puede eliminar el servicio porque está en reservas");
        }
        serviceRepository.delete(servicio);
    }

    private void copiar(ServicioAdicionalSolicitud s, AdditionalService servicio) {
        servicio.setName(s.nombre().trim());
        servicio.setUnitPrice(s.precioUnitario());
    }

    private AdditionalService buscar(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el servicio adicional " + id));
    }
}
