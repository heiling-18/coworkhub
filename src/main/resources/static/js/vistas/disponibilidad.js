// Vista "Disponibilidad": salas libres de una sede en una fecha y rango horario (RF-03)
import { GET } from '../api.js';
import { esc, dinero, diaISO, TIPOS_SALA, toastError } from '../ui.js';
import { abrirNuevaReserva } from './reservas.js';

export async function render(contenedor, ctx) {
  const [sedes, equipos] = await Promise.all([GET('/api/sedes'), GET('/api/equipamientos')]);
  contenedor.innerHTML = `
    <div class="encabezado">
      <div><h2>Disponibilidad</h2><p>Busque salas libres y resérvelas en un clic.</p></div>
    </div>
    <form class="tarjeta" id="buscar">
      <div class="campos">
        <div class="campo"><label for="sede">Sede</label><select id="sede" required>
          ${sedes.map(s => `<option value="${s.id}">${esc(s.nombre)} (${esc(s.ciudad)}, ${s.horaApertura.slice(0, 5)}-${s.horaCierre.slice(0, 5)})</option>`).join('')}
        </select></div>
        <div class="campo"><label for="fecha">Fecha</label><input id="fecha" type="date" required min="${diaISO(0)}" value="${diaISO(1)}"></div>
        <div class="campo"><label for="inicio">Desde</label><input id="inicio" type="time" step="1800" required value="10:00"></div>
        <div class="campo"><label for="fin">Hasta</label><input id="fin" type="time" step="1800" required value="12:00"></div>
        <div class="campo"><label for="capacidad">Capacidad mínima</label><input id="capacidad" type="number" min="1" placeholder="Cualquiera"></div>
        <div class="campo completo"><label>Equipamiento necesario</label><div class="checks">
          ${equipos.map(e => `<label><input type="checkbox" name="equipo" value="${e.id}"> ${esc(e.nombre)}</label>`).join('')}
        </div></div>
      </div>
      <div style="margin-top:1rem"><button class="btn" type="submit">Buscar salas libres</button></div>
    </form>
    <div id="resultados"></div>`;

  const form = contenedor.querySelector('#buscar');
  const buscar = async ev => {
    ev?.preventDefault();
    if (!form.reportValidity()) return;
    const params = new URLSearchParams({
      sedeId: form.sede.value, fecha: form.fecha.value, horaInicio: form.inicio.value, horaFin: form.fin.value
    });
    if (form.capacidad.value) params.set('capacidadMinima', form.capacidad.value);
    form.querySelectorAll('input[name=equipo]:checked').forEach(c => params.append('equipamientoIds', c.value));
    const resultados = contenedor.querySelector('#resultados');
    try {
      const salas = await GET('/api/disponibilidad?' + params);
      resultados.innerHTML = salas.length
        ? `<h3>${salas.length} sala(s) libre(s)</h3><div class="rejilla">${salas.map(tarjeta).join('')}</div>`
        : '<div class="tarjeta"><p class="vacio">No hay salas libres con esos criterios (revise también el horario de la sede).</p></div>';
      resultados.querySelectorAll('[data-reservar]').forEach(b => b.addEventListener('click', () =>
        abrirNuevaReserva(ctx, {
          salaId: Number(b.dataset.reservar), fecha: form.fecha.value,
          horaInicio: form.inicio.value, horaFin: form.fin.value
        }, () => buscar()).catch(toastError)));
    } catch (e) {
      toastError(e);
    }
  };
  form.addEventListener('submit', buscar);
  await buscar();
}

const tarjeta = s => `
  <div class="tarjeta sala">
    <h3>${esc(s.nombre)}</h3>
    <div class="meta">${esc(TIPOS_SALA[s.tipo] ?? s.tipo)} · ${esc(s.sedeNombre)} · capacidad ${s.capacidad}</div>
    <div class="meta">${s.equipamientos.map(e => esc(e.nombre)).join(', ') || 'Sin equipamiento'}</div>
    <div class="precio">${dinero(s.tarifaHora)} / hora</div>
    <button class="btn chico" data-reservar="${s.id}">Reservar</button>
  </div>`;
