// Vista "Miembros" (solo ADMIN y RECEPTIONIST): registrar, editar, suspender, activar, eliminar y ver consumo
import { GET, POST, PUT, PATCH, DELETE } from '../api.js';
import { esc, insignia, ESTADOS_MIEMBRO, abrirModal, confirmar, toast, toastError, campos, leer } from '../ui.js';

let contenedorActual;
let planes = [];

export async function render(contenedor) {
  contenedorActual = contenedor;
  planes = await GET('/api/planes');
  contenedor.innerHTML = `
    <div class="encabezado">
      <div><h2>Miembros</h2><p>Clientes del coworking con su plan de membresía.</p></div>
      <button class="btn" id="nuevo">+ Registrar miembro</button>
    </div>
    <div class="tarjeta">
      <div class="filtros">
        <div><label for="f-nombre">Nombre</label><input id="f-nombre" placeholder="Buscar por nombre"></div>
        <div><label for="f-estado">Estado</label><select id="f-estado"><option value="">Todos</option>
          ${Object.entries(ESTADOS_MIEMBRO).map(([v, l]) => `<option value="${v}">${l}</option>`).join('')}</select></div>
      </div>
      <div id="tabla"></div>
    </div>`;
  contenedor.querySelector('#nuevo').addEventListener('click', registrar);
  contenedor.querySelector('#f-estado').addEventListener('change', cargar);
  let espera;
  contenedor.querySelector('#f-nombre').addEventListener('input', () => { clearTimeout(espera); espera = setTimeout(cargar, 300); });
  await cargar();
}

async function cargar() {
  const c = contenedorActual;
  const params = new URLSearchParams();
  if (c.querySelector('#f-nombre').value.trim()) params.set('nombre', c.querySelector('#f-nombre').value.trim());
  if (c.querySelector('#f-estado').value) params.set('estado', c.querySelector('#f-estado').value);
  const lista = await GET('/api/miembros' + (params.size ? '?' + params : ''));
  c.querySelector('#tabla').innerHTML = !lista.length ? '<p class="vacio">No hay miembros con ese filtro.</p>' : `
    <div class="tabla-contenedor"><table>
      <thead><tr><th>Documento</th><th>Nombre</th><th>Email</th><th>Plan</th><th>Estado</th><th>Acciones</th></tr></thead>
      <tbody>${lista.map(m => `<tr>
        <td>${esc(m.numeroDocumento)}</td><td>${esc(m.nombreCompleto)}</td><td>${esc(m.email)}</td>
        <td>${esc(m.planNombre)}</td><td>${insignia(m.estado, ESTADOS_MIEMBRO)}</td>
        <td class="acciones">
          <button class="btn sec chico" data-accion="consumo" data-id="${m.id}">Consumo</button>
          <button class="btn sec chico" data-accion="editar" data-id="${m.id}">Editar</button>
          ${m.estado === 'ACTIVE'
            ? `<button class="btn sec chico" data-accion="suspender" data-id="${m.id}">Suspender</button>`
            : `<button class="btn chico" data-accion="activar" data-id="${m.id}">Activar</button>`}
          <button class="btn peligro chico" data-accion="eliminar" data-id="${m.id}">Eliminar</button>
        </td></tr>`).join('')}
      </tbody></table></div>`;
  c.querySelectorAll('[data-accion]').forEach(b => b.addEventListener('click', () =>
    accion(b.dataset.accion, lista.find(m => m.id === Number(b.dataset.id)))));
}

const opcionesPlan = () => planes.map(p => ({ v: p.id, l: `${p.nombre} (${p.horasIncluidasMes} h/mes)` }));

function registrar() {
  const lista = [
    { n: 'numeroDocumento', l: 'Documento', req: true },
    { n: 'nombreCompleto', l: 'Nombre completo', req: true },
    { n: 'email', l: 'Email', t: 'email', req: true },
    { n: 'planId', l: 'Plan', t: 'select', req: true, numero: true, opciones: opcionesPlan() },
    { n: 'usuario', l: 'Usuario para iniciar sesión', req: true },
    { n: 'contrasena', l: 'Contraseña (mín. 6)', t: 'password', req: true }
  ];
  abrirModal({
    titulo: 'Registrar miembro',
    cuerpo: campos(lista) + '<p style="color:var(--texto-suave);font-size:.85rem;margin-bottom:0">Se crea el miembro y su cuenta con rol MEMBER.</p>',
    alEnviar: async form => {
      await POST('/api/miembros', leer(form, lista));
      toast('Miembro registrado', 'exito');
      await cargar();
    }
  });
}

async function accion(tipo, m) {
  try {
    if (tipo === 'editar') {
      const lista = [
        { n: 'nombreCompleto', l: 'Nombre completo', req: true, completo: true },
        { n: 'email', l: 'Email', t: 'email', req: true },
        { n: 'planId', l: 'Plan', t: 'select', req: true, numero: true, opciones: opcionesPlan() }
      ];
      return abrirModal({
        titulo: `Editar ${m.nombreCompleto}`, cuerpo: campos(lista, m),
        alEnviar: async form => {
          await PUT(`/api/miembros/${m.id}`, leer(form, lista));
          toast('Miembro actualizado', 'exito');
          await cargar();
        }
      });
    }
    if (tipo === 'consumo') return await verConsumo(m);
    if (tipo === 'suspender' || tipo === 'activar') {
      await PATCH(`/api/miembros/${m.id}/${tipo}`);
      toast(tipo === 'suspender' ? 'Miembro suspendido: ya no puede reservar' : 'Miembro activado', 'exito');
    }
    if (tipo === 'eliminar') {
      if (!await confirmar('Eliminar miembro', `¿Eliminar a ${m.nombreCompleto} y su cuenta? No se puede si tiene reservas.`, 'Sí, eliminar')) return;
      await DELETE(`/api/miembros/${m.id}`);
      toast('Miembro eliminado', 'exito');
    }
    await cargar();
  } catch (e) {
    toastError(e);
  }
}

async function verConsumo(m) {
  const mes = new Date().toISOString().slice(0, 7);
  const c = await GET(`/api/miembros/${m.id}/consumo?mes=${mes}`);
  abrirModal({
    titulo: `Consumo de ${m.nombreCompleto} (${c.mes})`,
    cuerpo: `<dl class="detalle">
      <dt>Horas incluidas</dt><dd>${c.horasIncluidas}</dd>
      <dt>Horas usadas</dt><dd>${c.horasUsadas}</dd>
      <dt>Horas restantes</dt><dd>${c.horasRestantes}</dd>
      <dt>Horas excedentes</dt><dd>${c.horasExcedentes}</dd>
    </dl>`
  });
}
