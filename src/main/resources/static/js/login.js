// Pantalla de inicio de sesión: POST /auth/login y guarda el token
import { api, sesion } from './api.js';

// Si ya hay sesión, ir directo al panel
if (sesion.obtener()?.token) location.href = 'app.html';

const form = document.getElementById('form-login');
const error = document.getElementById('error');

// Botones de usuarios de prueba: llenan el formulario
document.querySelectorAll('[data-usuario]').forEach(boton => boton.addEventListener('click', () => {
  form.usuario.value = boton.dataset.usuario;
  form.contrasena.value = 'clave123';
  form.contrasena.focus();
}));

form.addEventListener('submit', async ev => {
  ev.preventDefault();
  error.hidden = true;
  if (!form.reportValidity()) return;
  const boton = form.querySelector('button[type=submit]');
  boton.disabled = true;
  boton.textContent = 'Ingresando...';
  try {
    const r = await api('POST', '/auth/login', {
      usuario: form.usuario.value.trim(),
      contrasena: form.contrasena.value
    });
    sesion.guardar({ token: r.token, usuario: r.usuario, rol: r.rol, miembroId: r.miembroId });
    location.href = 'app.html';
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  } finally {
    boton.disabled = false;
    boton.textContent = 'Ingresar';
  }
});
