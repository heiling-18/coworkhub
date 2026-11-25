// Vista "Catálogos": sedes, salas, equipamiento, servicios y planes.
// Todos los roles consultan; solo ADMIN crea, edita y elimina.
import { GET, POST, PUT, DELETE } from '../api.js';
import { esc, dinero, hora, TIPOS_SALA, abrirModal, confirmar, toast, toastError, campos, leer } from '../ui.js';

/** Configuración de cada catálogo: columnas de la tabla y campos del formulario. */
const CATALOGOS = {
  sedes: {
    titulo: 'Sedes', ruta: '/api/sedes',
    columnas: [['Nombre', r => r.nombre], ['Ciudad', r => r.ciudad], ['Dirección', r => r.direccion],
      ['Horario', r => `${hora(r.horaApertura)} - ${hora(r.horaCierre)}`]],
    campos: async () => [
      { n: 'nombre', l: 'Nombre', req: true }, { n: 'ciudad', l: 'Ciudad', req: true },
      { n: 'direccion', l: 'Dirección', req: true, completo: true },
      { n: 'horaApertura', l: 'Hora de apertura', t: 'time', req: true },
      { n: 'horaCierre', l: 'Hora de cierre', t: 'time', req: true }]
  },
  salas: {
    titulo: 'Salas', ruta: '/api/salas',
    columnas: [['Nombre', r => r.nombre], ['Sede', r => r.sedeNombre], ['Tipo', r => TIPOS_SALA[r.tipo] ?? r.tipo],
      ['Capacidad', r => r.capacidad], ['Tarifa/h', r => dinero(r.tarifaHora)],
      ['Equipamiento', r => r.equipamientos.map(e => e.nombre).join(', ') || '—'],
      ['Estado', r => r.activa ? 'Activa' : 'Inactiva']],
    campos: async () => {
      const [sedes, equipos] = await Promise.all([GET('/api/sedes'), GET('/api/equipamientos')]);
      return [
        { n: 'nombre', l: 'Nombre', req: true },
        { n: 'sedeId', l: 'Sede', t: 'select', req: true, numero: true, opciones: sedes.map(s => ({ v: s.id, l: s.nombre })) },
        { n: 'tipo', l: 'Tipo', t: 'select', req: true, opciones: Object.entries(TIPOS_SALA).map(([v, l]) => ({ v, l })) },
        { n: 'capacidad', l: 'Capacidad', t: 'number', req: true, min: 1 },
        { n: 'tarifaHora', l: 'Tarifa por hora', t: 'number', req: true, min: 0, step: 100 },
        { n: 'activa', l: 'Sala activa', t: 'checkbox', v: true },
        { n: 'equipamientoIds', l: 'Equipamiento', t: 'checks', opciones: equipos.map(e => ({ v: e.id, l: e.nombre })) }];
    },
    // La respuesta trae "equipamientos" como objetos; el formulario usa ids
    aFormulario: r => ({ ...r, equipamientoIds: r.equipamientos.map(e => e.id) })
  },
  equipamientos: {
    titulo: 'Equipamiento', ruta: '/api/equipamientos',
    columnas: [['Nombre', r => r.nombre]],
    campos: async () => [{ n: 'nombre', l: 'Nombre', req: true, completo: true }]
  },
  servicios: {
    titulo: 'Servicios adicionales', ruta: '/api/servicios-adicionales',
    columnas: [['Nombre', r => r.nombre], ['Precio unitario', r => dinero(r.precioUnitario)]],
    campos: async () => [{ n: 'nombre', l: 'Nombre', req: true },
      { n: 'precioUnitario', l: 'Precio unitario', t: 'number', req: true, min: 0, step: 100 }]
  },
  planes: {
    titulo: 'Planes', ruta: '/api/planes',
    columnas: [['Nombre', r => r.nombre], ['Horas incluidas/mes', r => r.horasIncluidasMes],
      ['Descuento excedente', r => `${Number(r.descuentoExcedente)} %`], ['Máx. reservas activas', r => r.maxReservasActivas]],
    campos: async () => [{ n: 'nombre', l: 'Nombre', req: true, completo: true },
      { n: 'horasIncluidasMes', l: 'Horas incluidas por mes', t: 'number', req: true, min: 0 },
      { n: 'descuentoExcedente', l: 'Descuento sobre excedentes (%)', t: 'number', req: true, min: 0, max: 100, step: 0.5 },
      { n: 'maxReservasActivas', l: 'Máximo de reservas activas', t: 'number', req: true, min: 1 }]
  }
};

let ctxActual;
let contenedorActual;
let actual = 'sedes';

export async function render(contenedor, ctx) {
  ctxActual = ctx;
  contenedorActual = contenedor;
  contenedor.innerHTML = `
    <div class="encabezado">
      <div><h2>Catálogos</h2><p>${ctx.esAdmin ? 'Como administrador puede crear, editar y eliminar.' : 'Solo consulta: los cambios los hace el administrador.'}</p></div>
    </div>
    <div class="sub-tabs">${Object.entries(CATALOGOS).map(([k, c]) => `<button data-cat="${k}">${esc(c.titulo)}</button>`).join('')}</div>
    <div class="tarjeta" id="catalogo"></div>`;
  contenedor.querySelectorAll('[data-cat]').forEach(b => b.addEventListener('click', () => { actual = b.dataset.cat; cargar(); }));
  await cargar();
}

async function cargar() {
  const cat = CATALOGOS[actual];
  contenedorActual.querySelectorAll('[data-cat]').forEach(b => b.classList.toggle('activo', b.dataset.cat === actual));
  const caja = contenedorActual.querySelector('#catalogo');
  try {
    const lista = await GET(cat.ruta);
    const admin = ctxActual.esAdmin;
    caja.innerHTML = `
      <div class="encabezado"><h3>${esc(cat.titulo)} (${lista.length})</h3>
        ${admin ? '<button class="btn" id="crear">+ Nuevo</button>' : ''}</div>
      ${!lista.length ? '<p class="vacio">No hay registros.</p>' : `
      <div class="tabla-contenedor"><table>
        <thead><tr>${cat.columnas.map(([t]) => `<th>${esc(t)}</th>`).join('')}${admin ? '<th>Acciones</th>' : ''}</tr></thead>
        <tbody>${lista.map(r => `<tr>${cat.columnas.map(([, f]) => `<td>${esc(f(r))}</td>`).join('')}
          ${admin ? `<td class="acciones">
            <button class="btn sec chico" data-editar="${r.id}">Editar</button>
            <button class="btn peligro chico" data-eliminar="${r.id}">Eliminar</button></td>` : ''}</tr>`).join('')}
        </tbody></table></div>`}`;
    caja.querySelector('#crear')?.addEventListener('click', () => formulario(cat).catch(toastError));
    caja.querySelectorAll('[data-editar]').forEach(b => b.addEventListener('click', () =>
      formulario(cat, lista.find(r => r.id === Number(b.dataset.editar))).catch(toastError)));
    caja.querySelectorAll('[data-eliminar]').forEach(b => b.addEventListener('click', () =>
      eliminar(cat, lista.find(r => r.id === Number(b.dataset.eliminar)))));
  } catch (e) {
    toastError(e);
  }
}

async function formulario(cat, registro) {
  const lista = await cat.campos();
  const valores = registro ? (cat.aFormulario ? cat.aFormulario(registro) : registro) : {};
  abrirModal({
    titulo: `${registro ? 'Editar' : 'Nuevo'}: ${cat.titulo}`,
    cuerpo: campos(lista, valores),
    alEnviar: async form => {
      const datos = leer(form, lista);
      if (registro) await PUT(`${cat.ruta}/${registro.id}`, datos);
      else await POST(cat.ruta, datos);
      toast(registro ? 'Cambios guardados' : 'Registro creado', 'exito');
      await cargar();
    }
  });
}

async function eliminar(cat, registro) {
  if (!await confirmar('Eliminar', `¿Eliminar "${registro.nombre}"? No se puede si está en uso (RN-12).`, 'Sí, eliminar')) return;
  try {
    await DELETE(`${cat.ruta}/${registro.id}`);
    toast('Registro eliminado', 'exito');
    await cargar();
  } catch (e) {
    toastError(e);
  }
}
