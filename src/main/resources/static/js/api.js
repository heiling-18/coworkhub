// =====================================================================
// Conexión con la API REST y manejo de la sesión (token JWT)
// =====================================================================

const CLAVE_SESION = 'cw.sesion';

/** Sesión del usuario: {token, usuario, rol, miembroId}. Se guarda en sessionStorage (se borra al cerrar la pestaña). */
export const sesion = {
  guardar(datos) {
    sessionStorage.setItem(CLAVE_SESION, JSON.stringify(datos));
  },
  obtener() {
    try {
      return JSON.parse(sessionStorage.getItem(CLAVE_SESION));
    } catch {
      return null;
    }
  },
  cerrar() {
    sessionStorage.removeItem(CLAVE_SESION);
    location.href = 'index.html';
  }
};

/** Error de la API con el formato único del backend: {error, codigo, timestamp}. */
export class ErrorApi extends Error {
  constructor(mensaje, codigo, estado) {
    super(mensaje);
    this.codigo = codigo;
    this.estado = estado;
  }
}

/**
 * Hace una petición a la API. Agrega el token JWT si hay sesión.
 * Si el backend responde 401 (token vencido o alterado), cierra la sesión y vuelve al login.
 */
export async function api(metodo, ruta, cuerpo) {
  const s = sesion.obtener();
  const cabeceras = {};
  if (cuerpo !== undefined) cabeceras['Content-Type'] = 'application/json';
  if (s?.token) cabeceras['Authorization'] = 'Bearer ' + s.token;

  let respuesta;
  try {
    respuesta = await fetch(ruta, {
      method: metodo,
      headers: cabeceras,
      body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined
    });
  } catch {
    throw new ErrorApi('No se pudo conectar con el servidor. ¿Está corriendo la app?', 'SIN_CONEXION', 0);
  }

  const texto = await respuesta.text();
  let datos = null;
  try {
    datos = texto ? JSON.parse(texto) : null;
  } catch {
    datos = null;
  }

  if (!respuesta.ok) {
    if (respuesta.status === 401 && ruta !== '/auth/login') {
      sesion.cerrar();
    }
    throw new ErrorApi(datos?.error || `Error ${respuesta.status}`, datos?.codigo || 'ERROR', respuesta.status);
  }
  return datos;
}

// Atajos
export const GET = ruta => api('GET', ruta);
export const POST = (ruta, cuerpo) => api('POST', ruta, cuerpo ?? {});
export const PUT = (ruta, cuerpo) => api('PUT', ruta, cuerpo);
export const PATCH = ruta => api('PATCH', ruta, {});
export const DELETE = ruta => api('DELETE', ruta);
