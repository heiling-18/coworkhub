-- contraseña para todos: "clave123"
-- Sedes
INSERT INTO sedes (id, nombre, ciudad, direccion, hora_apertura, hora_cierre) VALUES (1, 'CoWork Centro', 'Bogotá', 'Cra 7 # 32-16', '07:00:00', '21:00:00');
INSERT INTO sedes (id, nombre, ciudad, direccion, hora_apertura, hora_cierre) VALUES (2, 'CoWork Norte', 'Medellín', 'Cl 10 # 43-20', '08:00:00', '20:00:00');
-- Equipamiento
INSERT INTO equipamientos (id, nombre) VALUES (1, 'Proyector');
INSERT INTO equipamientos (id, nombre) VALUES (2, 'Tablero');
INSERT INTO equipamientos (id, nombre) VALUES (3, 'Televisor');
INSERT INTO equipamientos (id, nombre) VALUES (4, 'Videoconferencia');
-- Salas (la 6 esta inactiva)
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (1, 'Sala Andes', 'SALA_REUNIONES', 8, 40000.00, true, 1);
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (2, 'Sala Caribe', 'SALA_REUNIONES', 12, 60000.00, true, 1);
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (3, 'Oficina 101', 'OFICINA_PRIVADA', 4, 35000.00, true, 1);
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (4, 'Escritorio A1', 'ESCRITORIO', 1, 15000.00, true, 2);
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (5, 'Sala Pacífico', 'SALA_REUNIONES', 10, 50000.00, true, 2);
INSERT INTO salas (id, nombre, tipo, capacidad, tarifa_hora, activa, sede_id) VALUES (6, 'Sala Amazonas', 'SALA_REUNIONES', 6, 30000.00, false, 2);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (1, 1);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (1, 2);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (2, 1);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (2, 3);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (2, 4);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (3, 2);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (5, 1);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (5, 4);
INSERT INTO sala_equipamiento (sala_id, equipamiento_id) VALUES (6, 2);
-- Servicios adicionales
INSERT INTO servicios_adicionales (id, nombre, precio_unitario) VALUES (1, 'Café', 5000.00);
INSERT INTO servicios_adicionales (id, nombre, precio_unitario) VALUES (2, 'Refrigerio', 12000.00);
INSERT INTO servicios_adicionales (id, nombre, precio_unitario) VALUES (3, 'Impresiones', 1000.00);
-- Planes
INSERT INTO planes_membresia (id, nombre, horas_incluidas_mes, descuento_excedente, max_reservas_activas) VALUES (1, 'Básico', 5, 0.00, 2);
INSERT INTO planes_membresia (id, nombre, horas_incluidas_mes, descuento_excedente, max_reservas_activas) VALUES (2, 'Profesional', 10, 10.00, 3);
INSERT INTO planes_membresia (id, nombre, horas_incluidas_mes, descuento_excedente, max_reservas_activas) VALUES (3, 'Premium', 20, 20.00, 5);
-- Miembros (Jorge esta suspendido; Marta, Jorge y Sofia no tienen cuenta)
INSERT INTO miembros (id, numero_documento, nombre_completo, correo_electronico, estado, plan_id) VALUES (1, '1010101010', 'Ana Gómez', 'ana.gomez@correo.com', 'ACTIVO', 3);
INSERT INTO miembros (id, numero_documento, nombre_completo, correo_electronico, estado, plan_id) VALUES (2, '2020202020', 'Luis Pérez', 'luis.perez@correo.com', 'ACTIVO', 2);
INSERT INTO miembros (id, numero_documento, nombre_completo, correo_electronico, estado, plan_id) VALUES (3, '3030303030', 'Marta Ruiz', 'marta.ruiz@correo.com', 'ACTIVO', 1);
INSERT INTO miembros (id, numero_documento, nombre_completo, correo_electronico, estado, plan_id) VALUES (4, '4040404040', 'Jorge Díaz', 'jorge.diaz@correo.com', 'SUSPENDIDO', 2);
INSERT INTO miembros (id, numero_documento, nombre_completo, correo_electronico, estado, plan_id) VALUES (5, '5050505050', 'Sofía Castro', 'sofia.castro@correo.com', 'ACTIVO', 1);
-- Cuentas (contrasena: clave123)
INSERT INTO cuentas_usuarios (id, nombre_usuario, contrasena, rol, miembro_id) VALUES (1, 'admin', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'ADMIN', NULL);
INSERT INTO cuentas_usuarios (id, nombre_usuario, contrasena, rol, miembro_id) VALUES (2, 'recepcion', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'RECEPCIONISTA', NULL);
INSERT INTO cuentas_usuarios (id, nombre_usuario, contrasena, rol, miembro_id) VALUES (3, 'ana', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'MIEMBRO', 1);
INSERT INTO cuentas_usuarios (id, nombre_usuario, contrasena, rol, miembro_id) VALUES (4, 'luis', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'MIEMBRO', 2);
-- Reservas (15) en distintos estados. Columnas: inicio, fin, asistentes, estado, costos, sala, miembro
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (1, TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '10:00:00'), 4, 'CONFIRMADA', 0.00, 10000.00, 10000.00, 0.00, 1, 1);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (2, TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '10:00:00'), TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '12:00:00'), 6, 'PENDIENTE', 0.00, 24000.00, 24000.00, 0.00, 2, 2);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (3, TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '09:00:00'), TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '11:00:00'), 3, 'COMPLETADA', 0.00, 0.00, 0.00, 0.00, 1, 3);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (4, TIMESTAMP(CURDATE() - INTERVAL 5 DAY, '14:00:00'), TIMESTAMP(CURDATE() - INTERVAL 5 DAY, '16:00:00'), 2, 'COMPLETADA', 0.00, 0.00, 0.00, 0.00, 3, 1);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (5, TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '08:00:00'), TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '10:00:00'), 5, 'COMPLETADA', 0.00, 0.00, 0.00, 0.00, 5, 2);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (6, TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '10:00:00'), TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '13:00:00'), 4, 'COMPLETADA', 0.00, 0.00, 0.00, 0.00, 2, 5);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (7, TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '15:00:00'), TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '17:00:00'), 2, 'CONFIRMADA', 0.00, 0.00, 0.00, 0.00, 3, 3);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (8, TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '11:00:00'), 4, 'PENDIENTE', 0.00, 10000.00, 10000.00, 0.00, 5, 5);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (9, TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '16:00:00'), TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '17:00:00'), 1, 'COMPLETADA', 0.00, 0.00, 0.00, 0.00, 4, 2);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (10, TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '10:00:00'), TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '12:00:00'), 6, 'COMPLETADA', 0.00, 30000.00, 30000.00, 0.00, 2, 1);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (11, TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '08:00:00'), TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '09:00:00'), 3, 'CANCELADA', 0.00, 0.00, 0.00, 0.00, 1, 4);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (12, TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '13:00:00'), TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '14:00:00'), 2, 'CANCELADA', 0.00, 0.00, 0.00, 0.00, 1, 3);
-- Para las pruebas de cancelacion (RN-08): empiezan dentro de 30 h, 5 h y 1 h desde el arranque
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (13, NOW() + INTERVAL 30 HOUR, NOW() + INTERVAL 32 HOUR, 1, 'CONFIRMADA', 30000.00, 0.00, 30000.00, 0.00, 4, 1);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (14, NOW() + INTERVAL 5 HOUR, NOW() + INTERVAL 7 HOUR, 3, 'PENDIENTE', 100000.00, 12000.00, 112000.00, 0.00, 5, 1);
INSERT INTO reservas (id, fecha_hora_inicio, fecha_hora_fin, asistentes, estado, costo_sala, costo_servicios, costo_total, cargo_cancelacion, sala_id, miembro_id) VALUES (15, NOW() + INTERVAL 1 HOUR, NOW() + INTERVAL 2 HOUR, 2, 'CONFIRMADA', 35000.00, 0.00, 35000.00, 0.00, 3, 2);
-- Servicios de las reservas (precio_unitario_aplicado = precio al momento de agregarlo)
INSERT INTO items_reserva (id, cantidad, precio_unitario_aplicado, reserva_id, servicio_id) VALUES (1, 2, 5000.00, 1, 1);
INSERT INTO items_reserva (id, cantidad, precio_unitario_aplicado, reserva_id, servicio_id) VALUES (2, 2, 12000.00, 2, 2);
INSERT INTO items_reserva (id, cantidad, precio_unitario_aplicado, reserva_id, servicio_id) VALUES (3, 10, 1000.00, 8, 3);
INSERT INTO items_reserva (id, cantidad, precio_unitario_aplicado, reserva_id, servicio_id) VALUES (4, 6, 5000.00, 10, 1);
INSERT INTO items_reserva (id, cantidad, precio_unitario_aplicado, reserva_id, servicio_id) VALUES (5, 1, 12000.00, 14, 2);
