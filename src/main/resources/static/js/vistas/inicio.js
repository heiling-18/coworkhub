// Vista "Inicio": resumen según el rol
import { GET } from '../api.js';
import { esc, dinero, fecha, hora, insignia, ESTADOS_RESERVA, ROLES } from '../ui.js';

export async function render(contenedor, ctx) {
  const { sesion } = ctx;
  let html = `
    <div class="encabezado">
      <div>
        <h2>Hola, ${esc(sesion.usuario)}</h2>
        <p>Ingresaste como <strong>${esc(ROLES[sesion.rol])}</strong>.</p>
      </div>
      <a class="btn" href="#disponibilidad">Buscar sala libre</a>
    </div>`;

  if (ctx.esMiembro) {
    const mes = new Date().toISOString().slice(0, 7);
    const [consumo, reservas] = await Promise.all([
      GET(`/api/miembros/${sesion.miembroId}/consumo?mes=${mes}`),
      GET('/api/reservas')
    ]);
    const proximas = reservas.filter(r => ['PENDING', 'CONFIRMED'].includes(r.estado) && new Date(r.inicio) > new Date());
    html += tarjetaConsumo(consumo) + tablaProximas(proximas);
  } else {
    const [pendientes, confirmadas, salas, miembros] = await Promise.all([
      GET('/api/reservas?estado=PENDING'),
      GET('/api/reservas?estado=CONFIRMED'),
      GET('/api/salas'),
      GET('/api/miembros')
    ]);
    const proximas = [...pendientes, ...confirmadas]
      .filter(r => new Date(r.inicio) > new Date())
      .sort((a, b) => a.inicio.localeCompare(b.inicio));
    html += `
      <div class="rejilla" style="margin-bottom:1.1rem">
        ${estadistica(pendientes.length, 'Reservas pendientes por confirmar')}
        ${estadistica(confirmadas.length, 'Reservas confirmadas')}
        ${estadistica(salas.filter(x => x.activa).length, `Salas activas (de ${salas.length})`)}
        ${estadistica(miembros.filter(m => m.estado === 'ACTIVE').length, `Miembros activos (de ${miembros.length})`)}
      </div>` + tablaProximas(proximas.slice(0, 8));
  }
  contenedor.innerHTML = html;
}

const estadistica = (valor, etiqueta) =>
  `<div class="estadistica"><div class="valor">${valor}</div><div class="etiqueta">${esc(etiqueta)}</div></div>`;

function tarjetaConsumo(c) {
  const incluidas = Number(c.horasIncluidas);
  const usadas = Number(c.horasUsadas);
  const porcentaje = incluidas > 0 ? Math.min(100, (usadas / incluidas) * 100) : 100;
  return `
    <div class="tarjeta">
      <h3>Tu consumo de ${esc(c.mes)}</h3>
      <div class="progreso ${usadas > incluidas ? 'excedido' : ''}"><div style="width:${porcentaje}%"></div></div>
      <div class="rejilla">
        ${estadistica(incluidas, 'Horas incluidas en tu plan')}
        ${estadistica(usadas, 'Horas usadas')}
        ${estadistica(Number(c.horasRestantes), 'Horas restantes')}
        ${estadistica(Number(c.horasExcedentes), 'Horas excedentes (se cobran)')}
      </div>
    </div>`;
}

function tablaProximas(lista) {
  if (!lista.length) {
    return `<div class="tarjeta"><h3>Próximas reservas</h3><p class="vacio">No hay reservas próximas.</p></div>`;
  }
  return `
    <div class="tarjeta">
      <h3>Próximas reservas</h3>
      <div class="tabla-contenedor"><table>
        <thead><tr><th>Fecha</th><th>Horario</th><th>Sala</th><th>Miembro</th><th>Estado</th><th class="derecha">Total</th></tr></thead>
        <tbody>${lista.map(r => `
          <tr>
            <td>${esc(fecha(r.inicio))}</td><td>${hora(r.inicio)} - ${hora(r.fin)}</td>
            <td>${esc(r.salaNombre)}</td><td>${esc(r.miembroNombre)}</td>
            <td>${insignia(r.estado, ESTADOS_RESERVA)}</td><td class="derecha">${dinero(r.costoTotal)}</td>
          </tr>`).join('')}
        </tbody>
      </table></div>
      <p style="margin:.75rem 0 0"><a href="#reservas">Ver todas las reservas →</a></p>
    </div>`;
}
