-- ============================================================================
-- Datos de prueba de CoWorkHub. Se ejecuta en cada arranque (ddl-auto=create).
-- Una sentencia por linea; padres antes que hijos. Fechas relativas a hoy (CURDATE/NOW)
-- para que nunca envejezcan. Contrasena de TODOS los usuarios: clave123 (BCrypt).
-- ============================================================================
-- Sedes
INSERT INTO locations (id, name, city, address, opening_time, closing_time) VALUES (1, 'CoWork Centro', 'Bogotá', 'Cra 7 # 32-16', '07:00:00', '21:00:00');
INSERT INTO locations (id, name, city, address, opening_time, closing_time) VALUES (2, 'CoWork Norte', 'Medellín', 'Cl 10 # 43-20', '08:00:00', '20:00:00');
-- Equipamiento
INSERT INTO equipment (id, name) VALUES (1, 'Proyector');
INSERT INTO equipment (id, name) VALUES (2, 'Tablero');
INSERT INTO equipment (id, name) VALUES (3, 'Televisor');
INSERT INTO equipment (id, name) VALUES (4, 'Videoconferencia');
-- Salas (la 6 esta inactiva)
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (1, 'Sala Andes', 'MEETING_ROOM', 8, 40000.00, true, 1);
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (2, 'Sala Caribe', 'MEETING_ROOM', 12, 60000.00, true, 1);
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (3, 'Oficina 101', 'PRIVATE_OFFICE', 4, 35000.00, true, 1);
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (4, 'Escritorio A1', 'DESK', 1, 15000.00, true, 2);
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (5, 'Sala Pacífico', 'MEETING_ROOM', 10, 50000.00, true, 2);
INSERT INTO rooms (id, name, type, capacity, hourly_rate, active, location_id) VALUES (6, 'Sala Amazonas', 'MEETING_ROOM', 6, 30000.00, false, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (1, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (1, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (2, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (2, 3);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (2, 4);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (3, 2);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (5, 1);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (5, 4);
INSERT INTO room_equipment (room_id, equipment_id) VALUES (6, 2);
-- Servicios adicionales
INSERT INTO additional_services (id, name, unit_price) VALUES (1, 'Café', 5000.00);
INSERT INTO additional_services (id, name, unit_price) VALUES (2, 'Refrigerio', 12000.00);
INSERT INTO additional_services (id, name, unit_price) VALUES (3, 'Impresiones', 1000.00);
-- Planes
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings) VALUES (1, 'Básico', 5, 0.00, 2);
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings) VALUES (2, 'Profesional', 10, 10.00, 3);
INSERT INTO membership_plans (id, name, included_hours_per_month, overage_discount, max_active_bookings) VALUES (3, 'Premium', 20, 20.00, 5);
-- Miembros (Jorge esta suspendido; Marta, Jorge y Sofia no tienen cuenta)
INSERT INTO members (id, document_number, full_name, email, status, plan_id) VALUES (1, '1010101010', 'Ana Gómez', 'ana.gomez@correo.com', 'ACTIVE', 3);
INSERT INTO members (id, document_number, full_name, email, status, plan_id) VALUES (2, '2020202020', 'Luis Pérez', 'luis.perez@correo.com', 'ACTIVE', 2);
INSERT INTO members (id, document_number, full_name, email, status, plan_id) VALUES (3, '3030303030', 'Marta Ruiz', 'marta.ruiz@correo.com', 'ACTIVE', 1);
INSERT INTO members (id, document_number, full_name, email, status, plan_id) VALUES (4, '4040404040', 'Jorge Díaz', 'jorge.diaz@correo.com', 'SUSPENDED', 2);
INSERT INTO members (id, document_number, full_name, email, status, plan_id) VALUES (5, '5050505050', 'Sofía Castro', 'sofia.castro@correo.com', 'ACTIVE', 1);
-- Cuentas (contrasena: clave123)
INSERT INTO user_accounts (id, username, password, role, member_id) VALUES (1, 'admin', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'ADMIN', NULL);
INSERT INTO user_accounts (id, username, password, role, member_id) VALUES (2, 'recepcion', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'RECEPTIONIST', NULL);
INSERT INTO user_accounts (id, username, password, role, member_id) VALUES (3, 'ana', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'MEMBER', 1);
INSERT INTO user_accounts (id, username, password, role, member_id) VALUES (4, 'luis', '$2a$10$RZvYleXh7WZIKIQavb5iVe4SB1RVnktwwZkqnlaCmBPEnWeheJi.S', 'MEMBER', 2);
-- Reservas (15) en distintos estados
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (1, TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 1 DAY, '10:00:00'), 4, 'CONFIRMED', 0.00, 10000.00, 10000.00, 0.00, 1, 1);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (2, TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '10:00:00'), TIMESTAMP(CURDATE() + INTERVAL 2 DAY, '12:00:00'), 6, 'PENDING', 0.00, 24000.00, 24000.00, 0.00, 2, 2);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (3, TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '09:00:00'), TIMESTAMP(CURDATE() - INTERVAL 3 DAY, '11:00:00'), 3, 'COMPLETED', 0.00, 0.00, 0.00, 0.00, 1, 3);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (4, TIMESTAMP(CURDATE() - INTERVAL 5 DAY, '14:00:00'), TIMESTAMP(CURDATE() - INTERVAL 5 DAY, '16:00:00'), 2, 'COMPLETED', 0.00, 0.00, 0.00, 0.00, 3, 1);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (5, TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '08:00:00'), TIMESTAMP(CURDATE() - INTERVAL 7 DAY, '10:00:00'), 5, 'COMPLETED', 0.00, 0.00, 0.00, 0.00, 5, 2);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (6, TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '10:00:00'), TIMESTAMP(CURDATE() - INTERVAL 10 DAY, '13:00:00'), 4, 'COMPLETED', 0.00, 0.00, 0.00, 0.00, 2, 5);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (7, TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '15:00:00'), TIMESTAMP(CURDATE() + INTERVAL 3 DAY, '17:00:00'), 2, 'CONFIRMED', 0.00, 0.00, 0.00, 0.00, 3, 3);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (8, TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '09:00:00'), TIMESTAMP(CURDATE() + INTERVAL 4 DAY, '11:00:00'), 4, 'PENDING', 0.00, 10000.00, 10000.00, 0.00, 5, 5);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (9, TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '16:00:00'), TIMESTAMP(CURDATE() - INTERVAL 2 DAY, '17:00:00'), 1, 'COMPLETED', 0.00, 0.00, 0.00, 0.00, 4, 2);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (10, TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '10:00:00'), TIMESTAMP(CURDATE() - INTERVAL 1 DAY, '12:00:00'), 6, 'COMPLETED', 0.00, 30000.00, 30000.00, 0.00, 2, 1);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (11, TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '08:00:00'), TIMESTAMP(CURDATE() + INTERVAL 5 DAY, '09:00:00'), 3, 'CANCELLED', 0.00, 0.00, 0.00, 0.00, 1, 4);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (12, TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '13:00:00'), TIMESTAMP(CURDATE() + INTERVAL 6 DAY, '14:00:00'), 2, 'CANCELLED', 0.00, 0.00, 0.00, 0.00, 1, 3);
-- Para las pruebas de cancelacion (RN-08): empiezan dentro de 30 h, 5 h y 1 h desde el arranque
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (13, NOW() + INTERVAL 30 HOUR, NOW() + INTERVAL 32 HOUR, 1, 'CONFIRMED', 30000.00, 0.00, 30000.00, 0.00, 4, 1);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (14, NOW() + INTERVAL 5 HOUR, NOW() + INTERVAL 7 HOUR, 3, 'PENDING', 100000.00, 12000.00, 112000.00, 0.00, 5, 1);
INSERT INTO bookings (id, start_time, end_time, attendees, status, room_cost, services_cost, total_cost, cancellation_fee, room_id, member_id) VALUES (15, NOW() + INTERVAL 1 HOUR, NOW() + INTERVAL 2 HOUR, 2, 'CONFIRMED', 35000.00, 0.00, 35000.00, 0.00, 3, 2);
-- Servicios de las reservas (applied_unit_price = precio al momento de agregarlo)
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, service_id) VALUES (1, 2, 5000.00, 1, 1);
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, service_id) VALUES (2, 2, 12000.00, 2, 2);
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, service_id) VALUES (3, 10, 1000.00, 8, 3);
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, service_id) VALUES (4, 6, 5000.00, 10, 1);
INSERT INTO booking_items (id, quantity, applied_unit_price, booking_id, service_id) VALUES (5, 1, 12000.00, 14, 2);
