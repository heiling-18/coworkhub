// Vista "Reservas": listar, crear, ver detalle, cambiar servicios, confirmar, cancelar y completar
import { GET, POST, DELETE } from '../api.js';
import {
  esc, dinero, fecha, hora, diaISO, insignia, ESTADOS_RESERVA,
  abrirModal, confirmar, toast, toastError, campos, leer
} from '../ui.js';

let ctxActual;
let contenedorActual;

export async function render(contenedor, ctx) {
  ctxActual = ctx;
  contenedorActual = contenedor;
  const miembros = ctx.esStaff ? await GET('/api/miembros') : [];
  contenedor.innerHTML = `
    <div class="encabezado">
      <div>
        <h2>${ctx.esMiembro ? 'Mis reservas' : 'Reservas'}</h2>
        <p>${ctx.esMiembro ? 'Solo ves tus reservas.' : 'Todas las reservas de la red de coworking.'}</p>
      </div>
      <button class="btn" id="nueva">+ Nueva reserva</button>
    </div>
    <div class="tarjeta">
      <div class="filtros">
        <div><label for="f-estado">Estado</label>
          <select id="f-estado"><option value="">Todos</option>
            ${Object.entries(ESTADOS_RESERVA).map(([v, l]) => `<option value="${v}">${l}</option>`).join('')}
          </select></div>
        ${ctx.esStaff ? `<div><label for="f-miembro">Miembro</label>
          <select id="f-miembro"><option value="">Todos</option>
            ${miembros.map(m => `<option value="${m.id}">${esc(m.nombreCompleto)}</option>`).join('')}
          </select></div>` : ''}
      </div>
      <div id="tabla"></div>
    </div>`;
  contenedor.querySelector('#nueva').addEventListener('click', () => abrirNuevaReserva(ctx, {}, cargar));
  contenedor.querySelector('#f-estado').addEventListener('change', cargar);
  contenedor.querySelector('#f-miembro')?.addEventListener('change', cargar);
  await cargar();
}

/** Carga la tabla con los filtros elegidos. */
async function cargar() {
  const c = contenedorActual;
  const estado = c.querySelector('#f-estado').value;
  const miembro = c.querySelector('#f-miembro')?.value;
  const params = new URLSearchParams();
  if (miembro) params.set('miembroId', miembro);
  if (estado) params.set('estado', estado);
  let lista = await GET('/api/reservas' + (params.size ? '?' + params : ''));
  if (estado && miembro) lista = lista.filter(r => r.estado === estado);
  lista.sort((a, b) => b.inicio.localeCompare(a.inicio));
  c.querySelector('#tabla').innerHTML = lista.length ? tabla(lista) : '<p class="vacio">No hay reservas con ese filtro.</p>';
  c.querySelectorAll('[data-accion]').forEach(b => b.addEventListener('click', () => accion(b.dataset.accion, Number(b.dataset.id))));
}

function tabla(lista) {
  const staff = ctxActual.esStaff;
  return `<div class="tabla-contenedor"><table>
    <thead><tr><th>#</th><th>Fecha</th><th>Horario</th><th>Sala</th>${staff ? '<th>Miembro</th>' : ''}
      <th>Estado</th><th class="derecha">Total</th><th>Acciones</th></tr></thead>
    <tbody>${lista.map(r => {
      const activa = ['PENDING', 'CONFIRMED'].includes(r.estado);
      return `<tr>
        <td>${r.id}</td><td>${esc(fecha(r.inicio))}</td><td>${hora(r.inicio)} - ${hora(r.fin)}</td>
        <td>${esc(r.salaNombre)}</td>${staff ? `<td>${esc(r.miembroNombre)}</td>` : ''}
        <td>${insignia(r.estado, ESTADOS_RESERVA)}</td><td class="derecha">${dinero(r.costoTotal)}</td>
        <td class="acciones">
          <button class="btn sec chico" data-accion="ver" data-id="${r.id}">Ver</button>
          ${staff && r.estado === 'PENDING' ? `<button class="btn chico" data-accion="confirmar" data-id="${r.id}">Confirmar</button>` : ''}
          ${staff && r.estado === 'CONFIRMED' ? `<button class="btn chico" data-accion="completar" data-id="${r.id}">Completar</button>` : ''}
          ${activa ? `<button class="btn peligro chico" data-accion="cancelar" data-id="${r.id}">Cancelar</button>` : ''}
        </td></tr>`;
    }).join('')}</tbody></table></div>`;
}

async function accion(tipo, id) {
  try {
    if (tipo === 'ver') return await verDetalle(id);
    if (tipo === 'confirmar') {
      await POST(`/api/reservas/${id}/confirmar`);
      toast(`Reserva ${id} confirmada`, 'exito');
    }
    if (tipo === 'completar') {
      await POST(`/api/reservas/${id}/completar`);
      toast(`Reserva ${id} completada`, 'exito');
    }
    if (tipo === 'cancelar') {
      const ok = await confirmar('Cancelar reserva',
        'Con 24 h o más de anticipación no tiene cargo. Entre 24 h y 2 h se cobra el 50 % de la sala. Con menos de 2 h no se puede cancelar. ¿Continuar?',
        'Sí, cancelar');
      if (!ok) return;
      const r = await POST(`/api/reservas/${id}/cancelar`);
      toast(`${r.mensaje}. Cargo: ${dinero(r.cargoCancelacion)}`, 'exito');
    }
    await cargar();
  } catch (e) {
    toastError(e);
  }
}

/** Modal con el detalle de la reserva. Si está PENDING permite agregar o quitar servicios. */
async function verDetalle(id) {
  const [r, servicios] = await Promise.all([GET(`/api/reservas/${id}`), GET('/api/servicios-adicionales')]);
  const pendiente = r.estado === 'PENDING';
  const dlg = abrirModal({
    titulo: `Reserva #${r.id}`,
    cuerpo: `
      <dl class="detalle">
        <dt>Estado</dt><dd>${insignia(r.estado, ESTADOS_RESERVA)}</dd>
        <dt>Sala</dt><dd>${esc(r.salaNombre)}</dd>
        <dt>Miembro</dt><dd>${esc(r.miembroNombre)}</dd>
        <dt>Fecha</dt><dd>${esc(fecha(r.inicio))}, ${hora(r.inicio)} a ${hora(r.fin)}</dd>
        <dt>Asistentes</dt><dd>${r.asistentes}</dd>
        <dt>Costo de la sala</dt><dd>${dinero(r.costoSala)}</dd>
        <dt>Servicios</dt><dd>${dinero(r.costoServicios)}</dd>
        <dt>Total</dt><dd class="total">${dinero(r.costoTotal)}</dd>
        ${r.estado === 'CANCELLED' ? `<dt>Cargo de cancelación</dt><dd>${dinero(r.cargoCancelacion)}</dd>` : ''}
      </dl>
      <h3>Servicios adicionales</h3>
      ${r.servicios.length ? `<table><thead><tr><th>Servicio</th><th>Cant.</th><th class="derecha">Precio</th><th class="derecha">Subtotal</th>${pendiente ? '<th></th>' : ''}</tr></thead>
        <tbody>${r.servicios.map(s => `<tr><td>${esc(s.servicioNombre)}</td><td>${s.cantidad}</td>
          <td class="derecha">${dinero(s.precioUnitarioAplicado)}</td><td class="derecha">${dinero(s.subtotal)}</td>
          ${pendiente ? `<td><button type="button" class="btn peligro chico" data-quitar="${s.servicioId}">Quitar</button></td>` : ''}</tr>`).join('')}
        </tbody></table>` : '<p class="vacio">Sin servicios.</p>'}
      ${pendiente ? `
        <div class="filtros" style="margin-top:1rem">
          <div><label for="nuevo-servicio">Agregar servicio</label><select id="nuevo-servicio">
            ${servicios.map(s => `<option value="${s.id}">${esc(s.nombre)} (${dinero(s.precioUnitario)})</option>`).join('')}
          </select></div>
          <div style="min-width:90px"><label for="nueva-cantidad">Cantidad</label><input id="nueva-cantidad" type="number" min="1" value="1"></div>
          <button type="button" class="btn" id="agregar-servicio">Agregar</button>
        </div>` : '<p style="color:var(--texto-suave)">Los servicios solo se pueden cambiar mientras la reserva está pendiente (RN-10).</p>'}`
  });
  const recargar = async () => { dlg.close(); await cargar(); await verDetalle(id); };
  dlg.querySelectorAll('[data-quitar]').forEach(b => b.addEventListener('click', async () => {
    try {
      await DELETE(`/api/reservas/${id}/servicios/${b.dataset.quitar}`);
      toast('Servicio quitado', 'exito');
      await recargar();
    } catch (e) { toastError(e); }
  }));
  dlg.querySelector('#agregar-servicio')?.addEventListener('click', async () => {
    try {
      await POST(`/api/reservas/${id}/servicios`, {
        servicioId: Number(dlg.querySelector('#nuevo-servicio').value),
        cantidad: Number(dlg.querySelector('#nueva-cantidad').value)
      });
      toast('Servicio agregado', 'exito');
      await recargar();
    } catch (e) { toastError(e); }
  });
}

/**
 * Modal para crear una reserva. "preset" puede traer salaId, fecha, horaInicio, horaFin (desde Disponibilidad).
 * ADMIN y RECEPTIONIST eligen el miembro; un MEMBER reserva a su nombre.
 */
export async function abrirNuevaReserva(ctx, preset = {}, alCrear) {
  const [salas, servicios, miembros] = await Promise.all([
    GET('/api/salas'),
    GET('/api/servicios-adicionales'),
    ctx.esStaff ? GET('/api/miembros?estado=ACTIVE') : Promise.resolve([])
  ]);
  const lista = [
    ...(ctx.esStaff ? [{ n: 'miembroId', l: 'Miembro', t: 'select', req: true, numero: true, completo: true,
      vacio: 'Elija un miembro', opciones: miembros.map(m => ({ v: m.id, l: `${m.nombreCompleto} (plan ${m.planNombre})` })) }] : []),
    { n: 'salaId', l: 'Sala', t: 'select', req: true, numero: true, completo: true,
      opciones: salas.filter(s => s.activa).map(s => ({ v: s.id, l: `${s.nombre} · ${s.sedeNombre} · cap. ${s.capacidad} · ${dinero(s.tarifaHora)}/h` })) },
    { n: 'fecha', l: 'Fecha', t: 'date', req: true, min: diaISO(0) },
    { n: 'horaInicio', l: 'Hora de inicio', t: 'time', req: true, step: 1800 },
    { n: 'horaFin', l: 'Hora de fin', t: 'time', req: true, step: 1800 },
    { n: 'asistentes', l: 'Asistentes', t: 'number', req: true, min: 1 }
  ];
  const valores = { fecha: diaISO(1), horaInicio: '10:00', horaFin: '11:00', asistentes: 1, ...preset };
  abrirModal({
    titulo: 'Nueva reserva',
    textoBoton: 'Reservar',
    cuerpo: campos(lista, valores) + `
      <h3 style="margin-top:1.1rem">Servicios adicionales (opcional)</h3>
      <div class="campos">${servicios.map(s => `
        <div class="campo"><label for="srv-${s.id}">${esc(s.nombre)} (${dinero(s.precioUnitario)})</label>
        <input id="srv-${s.id}" type="number" min="0" value="0" data-servicio="${s.id}"></div>`).join('')}
      </div>
      <p style="color:var(--texto-suave);font-size:.85rem;margin-bottom:0">
        Duración entre 30 min y 8 h en bloques de 30 min, dentro del horario de la sede y con 1 h de anticipación.
        Las horas incluidas en el plan no se cobran.</p>`,
    alEnviar: async form => {
      const d = leer(form, lista);
      const cuerpo = {
        salaId: d.salaId,
        miembroId: ctx.esStaff ? d.miembroId : ctx.sesion.miembroId,
        inicio: `${d.fecha}T${d.horaInicio}:00`,
        fin: `${d.fecha}T${d.horaFin}:00`,
        asistentes: d.asistentes,
        servicios: [...form.querySelectorAll('[data-servicio]')]
          .filter(i => Number(i.value) > 0)
          .map(i => ({ servicioId: Number(i.dataset.servicio), cantidad: Number(i.value) }))
      };
      const r = await POST('/api/reservas', cuerpo);
      toast(`Reserva #${r.id} creada. Sala ${dinero(r.costoSala)} + servicios ${dinero(r.costoServicios)} = ${dinero(r.costoTotal)}`, 'exito');
      if (alCrear) await alCrear();
    }
  });
}
