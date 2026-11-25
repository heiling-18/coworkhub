// =====================================================================
// Panel principal: valida la sesión, arma el menú según el rol y muestra cada vista
// =====================================================================
import { sesion } from './api.js';
import { esc, ROLES, toastError } from './ui.js';
import * as inicio from './vistas/inicio.js';
import * as disponibilidad from './vistas/disponibilidad.js';
import * as reservas from './vistas/reservas.js';
import * as miembros from './vistas/miembros.js';
import * as catalogos from './vistas/catalogos.js';

const s = sesion.obtener();
if (!s?.token) location.href = 'index.html';

/** Contexto que reciben todas las vistas. */
const ctx = {
  sesion: s,
  esAdmin: s?.rol === 'ADMIN',
  esStaff: s?.rol === 'ADMIN' || s?.rol === 'RECEPTIONIST',
  esMiembro: s?.rol === 'MEMBER'
};

// Menú: cada vista dice qué roles la ven
const VISTAS = [
  { id: 'inicio', titulo: 'Inicio', modulo: inicio, roles: ['ADMIN', 'RECEPTIONIST', 'MEMBER'] },
  { id: 'disponibilidad', titulo: 'Disponibilidad', modulo: disponibilidad, roles: ['ADMIN', 'RECEPTIONIST', 'MEMBER'] },
  { id: 'reservas', titulo: ctx.esMiembro ? 'Mis reservas' : 'Reservas', modulo: reservas, roles: ['ADMIN', 'RECEPTIONIST', 'MEMBER'] },
  { id: 'miembros', titulo: 'Miembros', modulo: miembros, roles: ['ADMIN', 'RECEPTIONIST'] },
  { id: 'catalogos', titulo: 'Catálogos', modulo: catalogos, roles: ['ADMIN', 'RECEPTIONIST', 'MEMBER'] }
].filter(v => v.roles.includes(s?.rol));

// Encabezado
document.getElementById('nombre-usuario').textContent = s?.usuario ?? '';
document.getElementById('rol-usuario').textContent = ROLES[s?.rol] ?? s?.rol ?? '';
document.getElementById('salir').addEventListener('click', () => sesion.cerrar());
document.getElementById('navegacion').innerHTML =
  VISTAS.map(v => `<a href="#${v.id}" data-vista="${v.id}">${esc(v.titulo)}</a>`).join('');

/** Muestra la vista indicada en la URL (#reservas, #miembros...). */
async function mostrar() {
  const id = location.hash.slice(1) || 'inicio';
  const vista = VISTAS.find(v => v.id === id) ?? VISTAS[0];
  document.querySelectorAll('#navegacion a').forEach(a => a.classList.toggle('activo', a.dataset.vista === vista.id));
  const contenedor = document.getElementById('contenido');
  contenedor.innerHTML = '<p class="vacio">Cargando...</p>';
  try {
    await vista.modulo.render(contenedor, ctx);
  } catch (e) {
    contenedor.innerHTML = `<div class="tarjeta"><p class="mensaje-error">${esc(e.message)}</p></div>`;
    toastError(e);
  }
}

window.addEventListener('hashchange', mostrar);
if (s?.token) mostrar();
