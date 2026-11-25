// =====================================================================
// Utilidades de interfaz: formato, notificaciones, modales y formularios
// =====================================================================

/** Escapa texto antes de insertarlo como HTML (evita inyección de código). */
export function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  }[c]));
}

const formatoPesos = new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 });
export const dinero = n => formatoPesos.format(Number(n ?? 0));

/** "2030-06-03T10:00:00" -> "mar 3 jun 2030" */
export function fecha(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('es-CO', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' });
}

/** "2030-06-03T10:00:00" o "10:00:00" -> "10:00" */
export function hora(valor) {
  if (!valor) return '';
  const t = valor.includes('T') ? valor.split('T')[1] : valor;
  return t.slice(0, 5);
}

/** Fecha local yyyy-MM-dd sumando días a hoy. */
export function diaISO(dias = 0) {
  const d = new Date();
  d.setDate(d.getDate() + dias);
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

export const ESTADOS_RESERVA = { PENDING: 'Pendiente', CONFIRMED: 'Confirmada', COMPLETED: 'Completada', CANCELLED: 'Cancelada' };
export const ESTADOS_MIEMBRO = { ACTIVE: 'Activo', SUSPENDED: 'Suspendido' };
export const TIPOS_SALA = { MEETING_ROOM: 'Sala de reuniones', PRIVATE_OFFICE: 'Oficina privada', DESK: 'Escritorio' };
export const ROLES = { ADMIN: 'Administrador', RECEPTIONIST: 'Recepción', MEMBER: 'Miembro' };

export const insignia = (estado, textos) =>
  `<span class="insignia ${esc(estado)}">${esc(textos[estado] ?? estado)}</span>`;

// ---------------------------------------------------------------- notificaciones
export function toast(mensaje, tipo = '', codigo = '') {
  let caja = document.querySelector('.notificaciones');
  if (!caja) {
    caja = document.createElement('div');
    caja.className = 'notificaciones';
    document.body.append(caja);
  }
  const t = document.createElement('div');
  t.className = `toast ${tipo}`;
  t.innerHTML = esc(mensaje) + (codigo ? `<code>${esc(codigo)}</code>` : '');
  caja.append(t);
  setTimeout(() => t.remove(), tipo === 'error' ? 6000 : 3500);
}

/** Muestra el error de la API (mensaje + código RN-xx). */
export const toastError = e => toast(e.message, 'error', e.codigo && e.codigo !== 'ERROR' ? e.codigo : '');

// ---------------------------------------------------------------- modal
/**
 * Abre un modal. Si recibe alEnviar(form), muestra botón Guardar y ejecuta la función al enviar;
 * si la función lanza un error, lo muestra dentro del modal sin cerrarlo.
 */
export function abrirModal({ titulo, cuerpo, textoBoton = 'Guardar', alEnviar, peligro = false }) {
  const dlg = document.getElementById('modal');
  dlg.innerHTML = `
    <form class="modal-form" novalidate>
      <header><h3>${esc(titulo)}</h3><button type="button" class="icono" data-cerrar aria-label="Cerrar">×</button></header>
      <div class="modal-cuerpo">${cuerpo}</div>
      <p class="mensaje-error modal-error" hidden></p>
      <footer>
        ${alEnviar
          ? `<button type="button" class="btn sec" data-cerrar>Cancelar</button>
             <button type="submit" class="btn ${peligro ? 'peligro' : ''}">${esc(textoBoton)}</button>`
          : `<button type="button" class="btn sec" data-cerrar>Cerrar</button>`}
      </footer>
    </form>`;
  const form = dlg.querySelector('form');
  const error = dlg.querySelector('.modal-error');
  dlg.querySelectorAll('[data-cerrar]').forEach(b => b.addEventListener('click', () => dlg.close()));
  form.addEventListener('submit', async ev => {
    ev.preventDefault();
    if (!alEnviar) return dlg.close();
    if (!form.reportValidity()) return;
    const boton = form.querySelector('[type=submit]');
    boton.disabled = true;
    error.hidden = true;
    try {
      await alEnviar(form);
      dlg.close();
    } catch (e) {
      error.textContent = e.message + (e.codigo && e.codigo !== 'ERROR' ? `  (${e.codigo})` : '');
      error.hidden = false;
    } finally {
      boton.disabled = false;
    }
  });
  dlg.showModal();
  return dlg;
}

/** Pregunta Sí/No con un modal. Devuelve una promesa con true o false. */
export function confirmar(titulo, mensaje, textoBoton = 'Sí, continuar', peligro = true) {
  return new Promise(resolver => {
    let aceptado = false;
    const dlg = abrirModal({
      titulo, cuerpo: `<p>${esc(mensaje)}</p>`, textoBoton, peligro,
      alEnviar: async () => { aceptado = true; }
    });
    dlg.addEventListener('close', () => resolver(aceptado), { once: true });
  });
}

// ---------------------------------------------------------------- formularios
/**
 * Construye campos a partir de una descripción:
 * {n: nombre, l: etiqueta, t: text|number|time|date|email|password|select|checkbox|checks,
 *  req, min, max, step, opciones: [{v, l}], completo}
 */
export function campos(lista, valores = {}) {
  return `<div class="campos">${lista.map(c => campo(c, valores[c.n])).join('')}</div>`;
}

function campo(c, valor) {
  const t = c.t || 'text';
  const id = `f-${c.n}`;
  const req = c.req ? 'required' : '';
  const clase = `campo ${c.completo ? 'completo' : ''} ${t === 'checkbox' ? 'check' : ''}`;
  if (t === 'checkbox') {
    return `<div class="${clase}"><label><input type="checkbox" name="${c.n}" ${valor ?? c.v ? 'checked' : ''}> ${esc(c.l)}</label></div>`;
  }
  if (t === 'checks') {
    const marcados = new Set((valor ?? []).map(Number));
    return `<div class="${clase} completo"><label>${esc(c.l)}</label><div class="checks">${
      c.opciones.map(o => `<label><input type="checkbox" name="${c.n}" value="${esc(o.v)}" ${marcados.has(Number(o.v)) ? 'checked' : ''}> ${esc(o.l)}</label>`).join('')
      || '<span class="vacio">No hay opciones</span>'}</div></div>`;
  }
  if (t === 'select') {
    return `<div class="${clase}"><label for="${id}">${esc(c.l)}</label><select id="${id}" name="${c.n}" ${req}>
      ${c.vacio ? `<option value="">${esc(c.vacio)}</option>` : ''}
      ${c.opciones.map(o => `<option value="${esc(o.v)}" ${String(o.v) === String(valor ?? c.v ?? '') ? 'selected' : ''}>${esc(o.l)}</option>`).join('')}
    </select></div>`;
  }
  const v = t === 'time' && valor ? hora(valor) : (valor ?? c.v ?? '');
  const extra = ['min', 'max', 'step', 'placeholder'].filter(a => c[a] !== undefined).map(a => `${a}="${esc(c[a])}"`).join(' ');
  return `<div class="${clase}"><label for="${id}">${esc(c.l)}</label><input id="${id}" name="${c.n}" type="${t}" value="${esc(v)}" ${req} ${extra}></div>`;
}

/** Lee los valores de un formulario según la misma descripción de campos (convierte números y booleanos). */
export function leer(form, lista) {
  const datos = {};
  for (const c of lista) {
    const t = c.t || 'text';
    if (t === 'checkbox') {
      datos[c.n] = form.elements[c.n].checked;
    } else if (t === 'checks') {
      datos[c.n] = [...form.querySelectorAll(`input[name="${c.n}"]:checked`)].map(i => Number(i.value));
    } else {
      const v = form.elements[c.n].value.trim();
      datos[c.n] = v === '' ? null : (t === 'number' || c.numero ? Number(v) : v);
    }
  }
  return datos;
}
