package com.coworkhub.business.service.impl;

import com.coworkhub.business.exception.CredencialesInvalidasException;
import com.coworkhub.business.service.AuthServicio;
import com.coworkhub.persistence.entity.UserAccount;
import com.coworkhub.persistence.repository.UserAccountRepository;
import com.coworkhub.presentation.dto.request.LoginSolicitud;
import com.coworkhub.presentation.dto.response.LoginRespuesta;
import com.coworkhub.presentation.dto.response.PerfilRespuesta;
import com.coworkhub.security.JwtServicio;
import com.coworkhub.security.UsuarioActual;
import com.coworkhub.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServicioImpl implements AuthServicio {

    private final UserAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicio jwtServicio;
    private final UsuarioActual usuarioActual;

    /** RF-11: valida usuario y contraseña (BCrypt) y devuelve un JWT que vence en 60 minutos. */
    @Override
    public LoginRespuesta login(LoginSolicitud s) {
        UserAccount cuenta = accountRepository.findByUsername(s.usuario())
                .filter(c -> passwordEncoder.matches(s.contrasena(), c.getPassword()))
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));
        Long miembroId = cuenta.getMember() == null ? null : cuenta.getMember().getId();
        return new LoginRespuesta(jwtServicio.generar(cuenta), "Bearer", jwtServicio.getMinutosExpiracion(),
                cuenta.getUsername(), cuenta.getRole(), miembroId);
    }

    /** RF-14: usuario, rol y miembroId de quien inició sesión (sale del token). */
    @Override
    public PerfilRespuesta perfil() {
        UsuarioAutenticado u = usuarioActual.obtener();
        return new PerfilRespuesta(u.usuario(), u.rol(), u.miembroId());
    }
}
