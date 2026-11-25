package com.coworkhub.business.service;

import com.coworkhub.presentation.dto.request.LoginSolicitud;
import com.coworkhub.presentation.dto.response.LoginRespuesta;
import com.coworkhub.presentation.dto.response.PerfilRespuesta;

public interface AuthServicio {

    LoginRespuesta login(LoginSolicitud solicitud);

    PerfilRespuesta perfil();
}
