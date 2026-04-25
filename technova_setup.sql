-- ============================================================
--  TechNova – Faculty Management System
--  Database Setup Script
--  Run this in MySQL before starting the application
-- ============================================================

CREATE DATABASE IF NOT EXISTS technova;
USE technova;

-- ── ROLES (reference) ──────────────────────────────────────
-- role_id: 1=Admin, 2=Lecturer, 3=Technical Officer, 4=Undergraduate

-- ── USERS ──────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id     INT(11)      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    role_id     INT(11)      DEFAULT NULL,
    full_name   VARCHAR(100) DEFAULT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    phone       VARCHAR(20)  DEFAULT NULL,
    profile_pic VARCHAR(255) DEFAULT NULL
);

-- ── NOTICE ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notice (
    notice_id INT          PRIMARY KEY,
    notice    VARCHAR(200)
);

-- ── COURSE UNIT ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS course_unit (
    c_code      CHAR(8)     NOT NULL PRIMARY KEY,
    c_name      VARCHAR(50) NOT NULL,
    credit      INT         NOT NULL,
    is_theory   ENUM('YES','NO') NOT NULL DEFAULT 'YES',
    is_practicel ENUM('YES','NO') NOT NULL DEFAULT 'YES'
);

-- ── TIMETABLE ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS timetable (
    session_id  VARCHAR(8)  NOT NULL PRIMARY KEY,
    c_code      VARCHAR(8),
    session_date DATE        NOT NULL,
    type        ENUM('PRACTICAL','THEORY') NOT NULL DEFAULT 'THEORY',
    duration    TIME         NOT NULL,
    lec_hall    VARCHAR(10)  NOT NULL,
    FOREIGN KEY (c_code) REFERENCES course_unit(c_code) ON DELETE SET NULL
);

-- ============================================================
--  SEED DATA
-- ============================================================

-- Admin user (password: admin123)
INSERT INTO users (username, password, role_id, full_name, email, phone)
VALUES ('admin', 'admin', 1, 'System Administrator', 'admin@technova.lk', '0771234567');

-- Lecturers
INSERT IGNORE INTO users (username, password, role_id, full_name, email, phone) VALUES
('lec_silva',   'pass123', 2, 'Dr. Kamal Silva',     'k.silva@technova.lk',   '0772345678'),
('lec_perera',  'pass123', 2, 'Ms. Nimal Perera',    'n.perera@technova.lk',  '0773456789'),
('lec_jayaW',   'pass123', 2, 'Mr. Janaka Jayawardena','j.jaya@technova.lk',  '0774567890'),
('lec_fernando','pass123', 2, 'Dr. Saman Fernando',  's.fernando@technova.lk','0775678901'),
('lec_bandara', 'pass123', 2, 'Ms. Dilani Bandara',  'd.bandara@technova.lk', '0776789012');

-- Technical Officers
INSERT IGNORE INTO users (username, password, role_id, full_name, email, phone) VALUES
('tech_kumara',  'pass123', 3, 'Mr. Ruwan Kumara',   'r.kumara@technova.lk',  '0777890123'),
('tech_rathnay', 'pass123', 3, 'Ms. Thilini Rathnayake','t.rathna@technova.lk','0778901234'),
('tech_dissana', 'pass123', 3, 'Mr. Pradeep Dissanayake','p.dissa@technova.lk','0779012345'),
('tech_wickram', 'pass123', 3, 'Ms. Chamari Wickramasinghe','c.wick@technova.lk','0770123456');

-- Undergraduates (20 students)
INSERT IGNORE INTO users (username, password, role_id, full_name, email, phone) VALUES
('ug_2021001', 'pass123', 4, 'Amara Jayasinghe',    'amara@student.technova.lk',  '0711000001'),
('ug_2021002', 'pass123', 4, 'Binara Perera',       'binara@student.technova.lk', '0711000002'),
('ug_2021003', 'pass123', 4, 'Chathura Kumara',     'chathura@student.technova.lk','0711000003'),
('ug_2021004', 'pass123', 4, 'Dilini Fernando',     'dilini@student.technova.lk', '0711000004'),
('ug_2021005', 'pass123', 4, 'Eranda Silva',        'eranda@student.technova.lk', '0711000005'),
('ug_2021006', 'pass123', 4, 'Fathima Nawas',       'fathima@student.technova.lk','0711000006'),
('ug_2021007', 'pass123', 4, 'Gayan Bandara',       'gayan@student.technova.lk',  '0711000007'),
('ug_2021008', 'pass123', 4, 'Hashini Wickrama',    'hashini@student.technova.lk','0711000008'),
('ug_2021009', 'pass123', 4, 'Isuru Rajapaksha',    'isuru@student.technova.lk',  '0711000009'),
('ug_2021010', 'pass123', 4, 'Janaki Dissanayake',  'janaki@student.technova.lk', '0711000010'),
('ug_2021011', 'pass123', 4, 'Kavindu Madushanka',  'kavindu@student.technova.lk','0711000011'),
('ug_2021012', 'pass123', 4, 'Lahiru Senevirathne', 'lahiru@student.technova.lk', '0711000012'),
('ug_2021013', 'pass123', 4, 'Malsha Rathnayake',   'malsha@student.technova.lk', '0711000013'),
('ug_2021014', 'pass123', 4, 'Nimesh Gamage',       'nimesh@student.technova.lk', '0711000014'),
('ug_2021015', 'pass123', 4, 'Oshadi Herath',       'oshadi@student.technova.lk', '0711000015'),
('ug_2021016', 'pass123', 4, 'Pasindu Abeysekara',  'pasindu@student.technova.lk','0711000016'),
('ug_2021017', 'pass123', 4, 'Qasim Farhan',        'qasim@student.technova.lk',  '0711000017'),
('ug_2021018', 'pass123', 4, 'Ruwanthi Tennakoon',  'ruwanthi@student.technova.lk','0711000018'),
('ug_2020001', 'pass123', 4, 'Sampath Wijesinghe',  'sampath@student.technova.lk','0711000019'),   -- repeat student
('ug_2020002', 'pass123', 4, 'Tharaka Mendis',      'tharaka@student.technova.lk','0711000020');   -- batch missed

-- Courses
INSERT IGNORE INTO course_unit (c_code, c_name, credit, is_theory, is_practicel) VALUES
('ICT2132', 'Object Oriented Programming Practicum', 3, 'YES', 'YES'),
('ICT2131', 'Object Oriented Programming',           3, 'YES', 'NO'),
('ICT2141', 'Data Structures and Algorithms',        3, 'YES', 'NO'),
('ICT2142', 'Data Structures Practicum',             2, 'NO',  'YES'),
('ICT2151', 'Database Management Systems',           3, 'YES', 'NO'),
('ICT2152', 'Database Management Practicum',         2, 'NO',  'YES'),
('ICT2161', 'Web Technologies',                      3, 'YES', 'YES'),
('ICT2171', 'Computer Networks',                     3, 'YES', 'NO');

-- Notices
INSERT IGNORE INTO notice (notice_id, notice) VALUES
(1, 'Mid-semester examinations will be held from 10th May 2026. Students must carry their student IDs.'),
(2, 'Mini project submissions are due on 26th April 2026 by 11:59 PM via LMS.'),
(3, 'Faculty library will be closed on 25th April 2026 for maintenance.'),
(4, 'All undergraduates must ensure 80% attendance to be eligible for final examinations.'),
(5, 'Timetable changes for week 12 have been uploaded. Please check the updated schedule.');

-- Timetable sessions
INSERT IGNORE INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S001', 'ICT2132', '2026-02-03', 'THEORY',    '02:00:00', 'LH1'),
('S002', 'ICT2132', '2026-02-05', 'PRACTICAL', '02:00:00', 'Lab1'),
('S003', 'ICT2131', '2026-02-04', 'THEORY',    '02:00:00', 'LH2'),
('S004', 'ICT2141', '2026-02-06', 'THEORY',    '02:00:00', 'LH1'),
('S005', 'ICT2142', '2026-02-07', 'PRACTICAL', '02:00:00', 'Lab2'),
('S006', 'ICT2151', '2026-02-10', 'THEORY',    '02:00:00', 'LH3'),
('S007', 'ICT2152', '2026-02-11', 'PRACTICAL', '02:00:00', 'Lab1'),
('S008', 'ICT2161', '2026-02-12', 'THEORY',    '02:00:00', 'LH2'),
('S009', 'ICT2161', '2026-02-13', 'PRACTICAL', '02:00:00', 'Lab2'),
('S010', 'ICT2171', '2026-02-17', 'THEORY',    '02:00:00', 'LH1');

-- ============================================================
--  Verify seed data
-- ============================================================
SELECT role_id, COUNT(*) AS count FROM users GROUP BY role_id;
SELECT COUNT(*) AS total_courses FROM course_unit;
SELECT COUNT(*) AS total_notices FROM notice;
SELECT COUNT(*) AS total_sessions FROM timetable;
