-- ============================================================================
-- CoWorkHub - migration.sql
-- Crea TODAS las tablas y carga los datos de prueba en una base MySQL 8 vacia.
-- Uso manual (sin arrancar la app):
--   docker exec -i coworkhub-mysql mysql -ucoworkhub -pcoworkhub coworkhub < src/main/resources/migration.sql
-- Nota: al arrancar, la app NO usa este archivo; Hibernate crea las tablas (ddl-auto=create)
-- y carga import.sql, que tiene los mismos datos.
-- ============================================================================
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `booking_items`;
DROP TABLE IF EXISTS `bookings`;
DROP TABLE IF EXISTS `user_accounts`;
DROP TABLE IF EXISTS `members`;
DROP TABLE IF EXISTS `membership_plans`;
DROP TABLE IF EXISTS `additional_services`;
DROP TABLE IF EXISTS `room_equipment`;
DROP TABLE IF EXISTS `rooms`;
DROP TABLE IF EXISTS `equipment`;
DROP TABLE IF EXISTS `locations`;

-- ----------------------------------------------------------------- Esquema
CREATE TABLE `additional_services` (
  `unit_price` decimal(10,2) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKf7cdh07s4bs1qavhcdtiftbv4` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `booking_items` (
  `applied_unit_price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `booking_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrw74irmyat5c39cnjkn02u99m` (`booking_id`),
  KEY `FKq2m1rsv5kb7464rmpliamu490` (`service_id`),
  CONSTRAINT `FKq2m1rsv5kb7464rmpliamu490` FOREIGN KEY (`service_id`) REFERENCES `additional_services` (`id`),
  CONSTRAINT `FKrw74irmyat5c39cnjkn02u99m` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `bookings` (
  `attendees` int NOT NULL,
  `cancellation_fee` decimal(10,2) NOT NULL,
  `room_cost` decimal(10,2) NOT NULL,
  `services_cost` decimal(10,2) NOT NULL,
  `total_cost` decimal(10,2) NOT NULL,
  `end_time` datetime(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','PENDING') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_booking_room_time` (`room_id`,`start_time`,`end_time`),
  KEY `idx_booking_member` (`member_id`,`start_time`),
  CONSTRAINT `FK2hu43b2w26um1gql30wbf0173` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`),
  CONSTRAINT `FKrgoycol97o21kpjodw1qox4nc` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKm6whtgwe6ya84f59coio1j1wh` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `locations` (
  `closing_time` time NOT NULL,
  `opening_time` time NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `address` varchar(150) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL,
  `document_number` varchar(20) NOT NULL,
  `email` varchar(150) NOT NULL,
  `full_name` varchar(150) NOT NULL,
  `status` enum('ACTIVE','SUSPENDED') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd5c97lgxv17hn0p5c4ttbxxqk` (`document_number`),
  UNIQUE KEY `UK9d30a9u1qpg8eou0otgkwrp5d` (`email`),
  KEY `FKjuwy40aicmdogitnkjj38s78k` (`plan_id`),
  CONSTRAINT `FKjuwy40aicmdogitnkjj38s78k` FOREIGN KEY (`plan_id`) REFERENCES `membership_plans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `membership_plans` (
  `included_hours_per_month` int NOT NULL,
  `max_active_bookings` int NOT NULL,
  `overage_discount` decimal(5,2) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnconflq2x1uvxvs7sc0mnmh4` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `room_equipment` (
  `equipment_id` bigint NOT NULL,
  `room_id` bigint NOT NULL,
  PRIMARY KEY (`equipment_id`,`room_id`),
  KEY `FKiyhaivc7it7c8go6ursatnvte` (`room_id`),
  CONSTRAINT `FKiyhaivc7it7c8go6ursatnvte` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  CONSTRAINT `FKrmdm2ihfq0rjieivqjmnty6hq` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `rooms` (
  `active` bit(1) NOT NULL,
  `capacity` int NOT NULL,
  `hourly_rate` decimal(10,2) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `location_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `type` enum('DESK','MEETING_ROOM','PRIVATE_OFFICE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK2fk15xsp37m41umeq08ey1ps8` (`name`,`location_id`),
  KEY `FKn44nnd0bt5f0gwkdtw5ub1esg` (`location_id`),
  CONSTRAINT `FKn44nnd0bt5f0gwkdtw5ub1esg` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `user_accounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` enum('ADMIN','MEMBER','RECEPTIONIST') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlxwlgwuy2yrbye2vgs9w9x7mr` (`username`),
  UNIQUE KEY `UKr3au8txwg06sajwqs7ccpmk6j` (`member_id`),
  CONSTRAINT `FKi5xgh0k33trcuvdqc0dhokec` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------- Datos de prueba (iguales a import.sql)
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
