create database technova;
use technova;

CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role_id INT,
    full_name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    profile_pic VARCHAR(255),

    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE student (
    reg_no VARCHAR(10) PRIMARY KEY,
    user_id INT NOT NULL,
    status ENUM('PROPER','BOTH REPEAT','CA REPEAT','END REPEAT','SUSPENDED') DEFAULT 'PROPER',

    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE course_unit (
    c_code CHAR(8) PRIMARY KEY,
    c_name VARCHAR(50) NOT NULL,
    credit INT NOT NULL,
    is_theory ENUM('YES','NO') DEFAULT 'YES',
    is_practicel ENUM('YES','NO') DEFAULT 'YES',
    semester VARCHAR(10)
);

CREATE TABLE timetable (
    session_id VARCHAR(8) PRIMARY KEY,
    c_code VARCHAR(8),
    session_date DATE NOT NULL,
    type ENUM('PRACTICAL','THEORY') DEFAULT 'THEORY',
    duration TIME NOT NULL,
    lec_hall VARCHAR(10) NOT NULL,

    FOREIGN KEY (c_code) REFERENCES course_unit(c_code)
);

CREATE TABLE attendance (
    att_id VARCHAR(8) PRIMARY KEY,
    type ENUM('theory','practical') DEFAULT 'theory',
    atten_date DATE,
    status ENUM('absent','present') DEFAULT 'present',
    reg_no VARCHAR(10),
    session_id VARCHAR(8),

    FOREIGN KEY (reg_no) REFERENCES student(reg_no),
    FOREIGN KEY (session_id) REFERENCES timetable(session_id)
);

CREATE TABLE marks (
    reg_no VARCHAR(10),
    c_code CHAR(8),
    q1_marks DECIMAL(5,2) DEFAULT 0.00,
    q2_marks DECIMAL(5,2) DEFAULT 0.00,
    q3_marks DECIMAL(5,2) DEFAULT 0.00,
    assignment_marks DECIMAL(5,2) DEFAULT 0.00,
    mid_marks DECIMAL(5,2) DEFAULT 0.00,
    end_marks DECIMAL(5,2) DEFAULT 0.00,

    PRIMARY KEY (reg_no, c_code),

    FOREIGN KEY (reg_no) REFERENCES student(reg_no),
    FOREIGN KEY (c_code) REFERENCES course_unit(c_code)
);

CREATE TABLE materials (
    mat_id VARCHAR(10) PRIMARY KEY,
    c_code VARCHAR(10),
    title VARCHAR(50),
    link VARCHAR(100),

    FOREIGN KEY (c_code) REFERENCES course_unit(c_code)
);

CREATE TABLE medical (
    medical_id VARCHAR(8) PRIMARY KEY,
    submission_date DATE,
    description TEXT,
    affectted_start_date DATE,
    affectted_end_date DATE,
    reg_no VARCHAR(10),

    FOREIGN KEY (reg_no) REFERENCES student(reg_no)
);

CREATE TABLE notice (
    notice_id INT PRIMARY KEY AUTO_INCREMENT,
    notice VARCHAR(200)
);



-- ================================================================
--  1. ROLES
-- ================================================================
INSERT INTO roles (role_id, role_name) VALUES
(1, 'Admin'),
(2, 'Lecturer'),
(3, 'Technical Officer'),
(4, 'Undergraduate');

-- ================================================================
--  2. USERS
--  Passwords stored as plain text (as per project setup)
--  Admin: 1, Lecturer: 2, Tech Officer: 3, Student: 4
-- ================================================================

-- ── ADMIN (1) ──────────────────────────────────────────────────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('admin', 'admin123', 1, 'System Administrator', 'admin@technova.lk', '0771000000');

-- ── LECTURERS (5) – username: Lec_<surname> ────────────────────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('Lec_Silva',     'lec123', 2, 'Dr. Kamal Silva',          'k.silva@technova.lk',     '0772100001'),
('Lec_Perera',    'lec123', 2, 'Ms. Niluka Perera',         'n.perera@technova.lk',    '0772100002'),
('Lec_Fernando',  'lec123', 2, 'Mr. Saman Fernando',        's.fernando@technova.lk',  '0772100003'),
('Lec_Jayawardena','lec123',2, 'Dr. Anura Jayawardena',     'a.jaya@technova.lk',      '0772100004'),
('Lec_Bandara',   'lec123', 2, 'Ms. Dilani Bandara',        'd.bandara@technova.lk',   '0772100005');

-- ── TECHNICAL OFFICERS (4) – username: TO_<name> ───────────────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('TO_Kumara',     'to123',  3, 'Mr. Ruwan Kumara',          'r.kumara@technova.lk',    '0773200001'),
('TO_Rathnayake', 'to123',  3, 'Ms. Thilini Rathnayake',    't.rathna@technova.lk',    '0773200002'),
('TO_Dissanayake','to123',  3, 'Mr. Pradeep Dissanayake',   'p.dissa@technova.lk',     '0773200003'),
('TO_Wickrama',   'to123',  3, 'Ms. Chamari Wickramasinghe','c.wick@technova.lk',      '0773200004');

-- ── PROPER STUDENTS (TG1700 – TG1710, 11 students) ────────────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('TG1700', 'stu123', 4, 'Amara Jayasinghe',    'TG1700@student.technova.lk', '0710001700'),
('TG1701', 'stu123', 4, 'Binara Perera',        'TG1701@student.technova.lk', '0710001701'),
('TG1702', 'stu123', 4, 'Chathura Kumara',      'TG1702@student.technova.lk', '0710001702'),
('TG1703', 'stu123', 4, 'Dilini Fernando',      'TG1703@student.technova.lk', '0710001703'),
('TG1704', 'stu123', 4, 'Eranda Silva',         'TG1704@student.technova.lk', '0710001704'),
('TG1705', 'stu123', 4, 'Fathima Nawas',        'TG1705@student.technova.lk', '0710001705'),
('TG1706', 'stu123', 4, 'Gayan Bandara',        'TG1706@student.technova.lk', '0710001706'),
('TG1707', 'stu123', 4, 'Hashini Wickrama',     'TG1707@student.technova.lk', '0710001707'),
('TG1708', 'stu123', 4, 'Isuru Rajapaksha',     'TG1708@student.technova.lk', '0710001708'),
('TG1709', 'stu123', 4, 'Janaki Dissanayake',   'TG1709@student.technova.lk', '0710001709'),
('TG1710', 'stu123', 4, 'Kavindu Madushanka',   'TG1710@student.technova.lk', '0710001710');

-- ── REPEAT STUDENTS (TG1600 – TG1604, 5 students) ─────────────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('TG1600', 'stu123', 4, 'Lahiru Senevirathne',  'TG1600@student.technova.lk', '0710001600'),
('TG1601', 'stu123', 4, 'Malsha Rathnayake',    'TG1601@student.technova.lk', '0710001601'),
('TG1602', 'stu123', 4, 'Nimesh Gamage',        'TG1602@student.technova.lk', '0710001602'),
('TG1603', 'stu123', 4, 'Oshadi Herath',        'TG1603@student.technova.lk', '0710001603'),
('TG1604', 'stu123', 4, 'Pasindu Abeysekara',   'TG1604@student.technova.lk', '0710001604');

-- ── BATCH MISSED STUDENTS (TG1720 – TG1724, 5 students) ───────
INSERT INTO users (username, password, role_id, full_name, email, phone) VALUES
('TG1720', 'stu123', 4, 'Qasim Farhan',         'TG1720@student.technova.lk', '0710001720'),
('TG1721', 'stu123', 4, 'Ruwanthi Tennakoon',   'TG1721@student.technova.lk', '0710001721'),
('TG1722', 'stu123', 4, 'Sampath Wijesinghe',   'TG1722@student.technova.lk', '0710001722'),
('TG1723', 'stu123', 4, 'Tharaka Mendis',       'TG1723@student.technova.lk', '0710001723'),
('TG1724', 'stu123', 4, 'Udari Prasadini',      'TG1724@student.technova.lk', '0710001724');

-- ================================================================
--  3. STUDENT TABLE
--  reg_no = username, user_id = FK to users
--  PROPER: TG1700-TG1710, REPEAT: TG1600-TG1604, BATCH MISSED: TG1720-TG1724
-- ================================================================
INSERT INTO student (reg_no, user_id, status)
SELECT username, user_id, 'PROPER'
FROM users WHERE username IN
  ('TG1700','TG1701','TG1702','TG1703','TG1704',
   'TG1705','TG1706','TG1707','TG1708','TG1709','TG1710');

INSERT INTO student (reg_no, user_id, status)
SELECT username, user_id, 'BOTH REPEAT'
FROM users WHERE username IN
  ('TG1600','TG1601','TG1602','TG1603','TG1604');

INSERT INTO student (reg_no, user_id, status)
SELECT username, user_id, 'PROPER'
FROM users WHERE username IN
  ('TG1720','TG1721','TG1722','TG1723','TG1724');

-- Mark batch missed as CA REPEAT (they missed their batch)
UPDATE student SET status = 'CA REPEAT'
WHERE reg_no IN ('TG1720','TG1721','TG1722','TG1723','TG1724');

-- ================================================================
--  4. COURSE UNITS
-- ================================================================
INSERT INTO course_unit (c_code, c_name, credit, is_theory, is_practicel, semester) VALUES
('ENG2122', 'English III',                              2, 'YES', 'NO',  'L2S1'),
('ICT2113', 'Data Structures and Algorithms',           3, 'YES', 'YES', 'L2S1'),
('ICT2122', 'Object Oriented Programming',              2, 'YES', 'NO',  'L2S1'),
('ICT2132', 'Object Oriented Programming Practicum',    2, 'NO',  'YES', 'L2S1'),
('ICT2142', 'Object Oriented Analysis and Design',      2, 'YES', 'NO',  'L2S1'),
('ICT2152', 'E-Commerce Implementation and Management', 2, 'YES', 'NO',  'L2S1'),
('TCS2112', 'Business Economics',                       2, 'YES', 'NO',  'L2S1'),
('TCS2122', 'Soft Skills',                              2, 'YES', 'NO',  'L2S1');

-- ================================================================
--  5. TIMETABLE
--  2 weeks: Week 1 (2026-04-28 Mon to 2026-05-01 Fri)
--           Week 2 (2026-05-05 Mon to 2026-05-09 Fri)
--  Each day has 2 sessions. Duration 2h theory, 2h practical.
--  Session IDs: S001-S020
-- ================================================================

-- ── WEEK 1 ─────────────────────────────────────────────────────

-- Monday 2026-04-28
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S001', 'ICT2122', '2026-04-28', 'THEORY',    '02:00:00', 'LH1'),
('S002', 'ICT2132', '2026-04-28', 'PRACTICAL', '02:00:00', 'LAB1');

-- Tuesday 2026-04-29
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S003', 'ICT2113', '2026-04-29', 'THEORY',    '02:00:00', 'LH2'),
('S004', 'ICT2113', '2026-04-29', 'PRACTICAL', '02:00:00', 'LAB2');

-- Wednesday 2026-04-30
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S005', 'ICT2142', '2026-04-30', 'THEORY',    '02:00:00', 'LH1'),
('S006', 'ENG2122', '2026-04-30', 'THEORY',    '02:00:00', 'LH3');

-- Thursday 2026-05-01
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S007', 'TCS2112', '2026-05-01', 'THEORY',    '02:00:00', 'LH2'),
('S008', 'ICT2152', '2026-05-01', 'THEORY',    '02:00:00', 'LH1');

-- Friday 2026-05-02
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S009', 'TCS2122', '2026-05-02', 'THEORY',    '02:00:00', 'LH3'),
('S010', 'ICT2122', '2026-05-02', 'THEORY',    '02:00:00', 'LH2');

-- ── WEEK 2 ─────────────────────────────────────────────────────

-- Monday 2026-05-05
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S011', 'ICT2122', '2026-05-05', 'THEORY',    '02:00:00', 'LH1'),
('S012', 'ICT2132', '2026-05-05', 'PRACTICAL', '02:00:00', 'LAB1');

-- Tuesday 2026-05-06
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S013', 'ICT2113', '2026-05-06', 'THEORY',    '02:00:00', 'LH2'),
('S014', 'ICT2113', '2026-05-06', 'PRACTICAL', '02:00:00', 'LAB2');

-- Wednesday 2026-05-07
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S015', 'ICT2142', '2026-05-07', 'THEORY',    '02:00:00', 'LH1'),
('S016', 'ENG2122', '2026-05-07', 'THEORY',    '02:00:00', 'LH3');

-- Thursday 2026-05-08
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S017', 'TCS2112', '2026-05-08', 'THEORY',    '02:00:00', 'LH2'),
('S018', 'ICT2152', '2026-05-08', 'THEORY',    '02:00:00', 'LH1');

-- Friday 2026-05-09
INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES
('S019', 'TCS2122', '2026-05-09', 'THEORY',    '02:00:00', 'LH3'),
('S020', 'ICT2122', '2026-05-09', 'THEORY',    '02:00:00', 'LH2');

-- ================================================================
--  6. ATTENDANCE
--  Only PROPER students: TG1700 – TG1710 (11 students)
--  All 20 sessions × 11 students = 220 records
--  Mix of present/absent with realistic patterns
--  att_id format: A + 4-digit number (A0001 – A0220)
-- ================================================================

-- Helper: student attendance pattern per session
-- Most present, some absences sprinkled in

-- TG1700 – good attendance, all present
INSERT INTO attendance VALUES
('A0001','theory',   '2026-04-28','present','TG1700','S001'),
('A0002','practical','2026-04-28','present','TG1700','S002'),
('A0003','theory',   '2026-04-29','present','TG1700','S003'),
('A0004','practical','2026-04-29','present','TG1700','S004'),
('A0005','theory',   '2026-04-30','present','TG1700','S005'),
('A0006','theory',   '2026-04-30','present','TG1700','S006'),
('A0007','theory',   '2026-05-01','present','TG1700','S007'),
('A0008','theory',   '2026-05-01','present','TG1700','S008'),
('A0009','theory',   '2026-05-02','present','TG1700','S009'),
('A0010','theory',   '2026-05-02','present','TG1700','S010'),
('A0011','theory',   '2026-05-05','present','TG1700','S011'),
('A0012','practical','2026-05-05','present','TG1700','S012'),
('A0013','theory',   '2026-05-06','present','TG1700','S013'),
('A0014','practical','2026-05-06','present','TG1700','S014'),
('A0015','theory',   '2026-05-07','present','TG1700','S015'),
('A0016','theory',   '2026-05-07','present','TG1700','S016'),
('A0017','theory',   '2026-05-08','present','TG1700','S017'),
('A0018','theory',   '2026-05-08','present','TG1700','S018'),
('A0019','theory',   '2026-05-09','present','TG1700','S019'),
('A0020','theory',   '2026-05-09','present','TG1700','S020');

-- TG1701 – 1 absent (S004 practical, S016 theory)
INSERT INTO attendance VALUES
('A0021','theory',   '2026-04-28','present','TG1701','S001'),
('A0022','practical','2026-04-28','present','TG1701','S002'),
('A0023','theory',   '2026-04-29','present','TG1701','S003'),
('A0024','practical','2026-04-29','absent', 'TG1701','S004'),
('A0025','theory',   '2026-04-30','present','TG1701','S005'),
('A0026','theory',   '2026-04-30','present','TG1701','S006'),
('A0027','theory',   '2026-05-01','present','TG1701','S007'),
('A0028','theory',   '2026-05-01','present','TG1701','S008'),
('A0029','theory',   '2026-05-02','present','TG1701','S009'),
('A0030','theory',   '2026-05-02','present','TG1701','S010'),
('A0031','theory',   '2026-05-05','present','TG1701','S011'),
('A0032','practical','2026-05-05','present','TG1701','S012'),
('A0033','theory',   '2026-05-06','present','TG1701','S013'),
('A0034','practical','2026-05-06','present','TG1701','S014'),
('A0035','theory',   '2026-05-07','present','TG1701','S015'),
('A0036','theory',   '2026-05-07','absent', 'TG1701','S016'),
('A0037','theory',   '2026-05-08','present','TG1701','S017'),
('A0038','theory',   '2026-05-08','present','TG1701','S018'),
('A0039','theory',   '2026-05-09','present','TG1701','S019'),
('A0040','theory',   '2026-05-09','present','TG1701','S020');

-- TG1702 – 2 absents (S002, S014)
INSERT INTO attendance VALUES
('A0041','theory',   '2026-04-28','present','TG1702','S001'),
('A0042','practical','2026-04-28','absent', 'TG1702','S002'),
('A0043','theory',   '2026-04-29','present','TG1702','S003'),
('A0044','practical','2026-04-29','present','TG1702','S004'),
('A0045','theory',   '2026-04-30','present','TG1702','S005'),
('A0046','theory',   '2026-04-30','present','TG1702','S006'),
('A0047','theory',   '2026-05-01','present','TG1702','S007'),
('A0048','theory',   '2026-05-01','present','TG1702','S008'),
('A0049','theory',   '2026-05-02','present','TG1702','S009'),
('A0050','theory',   '2026-05-02','present','TG1702','S010'),
('A0051','theory',   '2026-05-05','present','TG1702','S011'),
('A0052','practical','2026-05-05','present','TG1702','S012'),
('A0053','theory',   '2026-05-06','present','TG1702','S013'),
('A0054','practical','2026-05-06','absent', 'TG1702','S014'),
('A0055','theory',   '2026-05-07','present','TG1702','S015'),
('A0056','theory',   '2026-05-07','present','TG1702','S016'),
('A0057','theory',   '2026-05-08','present','TG1702','S017'),
('A0058','theory',   '2026-05-08','present','TG1702','S018'),
('A0059','theory',   '2026-05-09','present','TG1702','S019'),
('A0060','theory',   '2026-05-09','present','TG1702','S020');

-- TG1703 – 1 absent (S006)
INSERT INTO attendance VALUES
('A0061','theory',   '2026-04-28','present','TG1703','S001'),
('A0062','practical','2026-04-28','present','TG1703','S002'),
('A0063','theory',   '2026-04-29','present','TG1703','S003'),
('A0064','practical','2026-04-29','present','TG1703','S004'),
('A0065','theory',   '2026-04-30','present','TG1703','S005'),
('A0066','theory',   '2026-04-30','absent', 'TG1703','S006'),
('A0067','theory',   '2026-05-01','present','TG1703','S007'),
('A0068','theory',   '2026-05-01','present','TG1703','S008'),
('A0069','theory',   '2026-05-02','present','TG1703','S009'),
('A0070','theory',   '2026-05-02','present','TG1703','S010'),
('A0071','theory',   '2026-05-05','present','TG1703','S011'),
('A0072','practical','2026-05-05','present','TG1703','S012'),
('A0073','theory',   '2026-05-06','present','TG1703','S013'),
('A0074','practical','2026-05-06','present','TG1703','S014'),
('A0075','theory',   '2026-05-07','present','TG1703','S015'),
('A0076','theory',   '2026-05-07','present','TG1703','S016'),
('A0077','theory',   '2026-05-08','present','TG1703','S017'),
('A0078','theory',   '2026-05-08','present','TG1703','S018'),
('A0079','theory',   '2026-05-09','present','TG1703','S019'),
('A0080','theory',   '2026-05-09','present','TG1703','S020');

-- TG1704 – 3 absents (S008, S012, S018) – has medicals for S008 and S018
INSERT INTO attendance VALUES
('A0081','theory',   '2026-04-28','present','TG1704','S001'),
('A0082','practical','2026-04-28','present','TG1704','S002'),
('A0083','theory',   '2026-04-29','present','TG1704','S003'),
('A0084','practical','2026-04-29','present','TG1704','S004'),
('A0085','theory',   '2026-04-30','present','TG1704','S005'),
('A0086','theory',   '2026-04-30','present','TG1704','S006'),
('A0087','theory',   '2026-05-01','present','TG1704','S007'),
('A0088','theory',   '2026-05-01','absent', 'TG1704','S008'),
('A0089','theory',   '2026-05-02','present','TG1704','S009'),
('A0090','theory',   '2026-05-02','present','TG1704','S010'),
('A0091','theory',   '2026-05-05','present','TG1704','S011'),
('A0092','practical','2026-05-05','absent', 'TG1704','S012'),
('A0093','theory',   '2026-05-06','present','TG1704','S013'),
('A0094','practical','2026-05-06','present','TG1704','S014'),
('A0095','theory',   '2026-05-07','present','TG1704','S015'),
('A0096','theory',   '2026-05-07','present','TG1704','S016'),
('A0097','theory',   '2026-05-08','present','TG1704','S017'),
('A0098','theory',   '2026-05-08','absent', 'TG1704','S018'),
('A0099','theory',   '2026-05-09','present','TG1704','S019'),
('A0100','theory',   '2026-05-09','present','TG1704','S020');

-- TG1705 – 2 absents (S003, S015)
INSERT INTO attendance VALUES
('A0101','theory',   '2026-04-28','present','TG1705','S001'),
('A0102','practical','2026-04-28','present','TG1705','S002'),
('A0103','theory',   '2026-04-29','absent', 'TG1705','S003'),
('A0104','practical','2026-04-29','present','TG1705','S004'),
('A0105','theory',   '2026-04-30','present','TG1705','S005'),
('A0106','theory',   '2026-04-30','present','TG1705','S006'),
('A0107','theory',   '2026-05-01','present','TG1705','S007'),
('A0108','theory',   '2026-05-01','present','TG1705','S008'),
('A0109','theory',   '2026-05-02','present','TG1705','S009'),
('A0110','theory',   '2026-05-02','present','TG1705','S010'),
('A0111','theory',   '2026-05-05','present','TG1705','S011'),
('A0112','practical','2026-05-05','present','TG1705','S012'),
('A0113','theory',   '2026-05-06','present','TG1705','S013'),
('A0114','practical','2026-05-06','present','TG1705','S014'),
('A0115','theory',   '2026-05-07','absent', 'TG1705','S015'),
('A0116','theory',   '2026-05-07','present','TG1705','S016'),
('A0117','theory',   '2026-05-08','present','TG1705','S017'),
('A0118','theory',   '2026-05-08','present','TG1705','S018'),
('A0119','theory',   '2026-05-09','present','TG1705','S019'),
('A0120','theory',   '2026-05-09','present','TG1705','S020');

-- TG1706 – 1 absent (S019) – has medical for S019
INSERT INTO attendance VALUES
('A0121','theory',   '2026-04-28','present','TG1706','S001'),
('A0122','practical','2026-04-28','present','TG1706','S002'),
('A0123','theory',   '2026-04-29','present','TG1706','S003'),
('A0124','practical','2026-04-29','present','TG1706','S004'),
('A0125','theory',   '2026-04-30','present','TG1706','S005'),
('A0126','theory',   '2026-04-30','present','TG1706','S006'),
('A0127','theory',   '2026-05-01','present','TG1706','S007'),
('A0128','theory',   '2026-05-01','present','TG1706','S008'),
('A0129','theory',   '2026-05-02','present','TG1706','S009'),
('A0130','theory',   '2026-05-02','present','TG1706','S010'),
('A0131','theory',   '2026-05-05','present','TG1706','S011'),
('A0132','practical','2026-05-05','present','TG1706','S012'),
('A0133','theory',   '2026-05-06','present','TG1706','S013'),
('A0134','practical','2026-05-06','present','TG1706','S014'),
('A0135','theory',   '2026-05-07','present','TG1706','S015'),
('A0136','theory',   '2026-05-07','present','TG1706','S016'),
('A0137','theory',   '2026-05-08','present','TG1706','S017'),
('A0138','theory',   '2026-05-08','present','TG1706','S018'),
('A0139','theory',   '2026-05-09','absent', 'TG1706','S019'),
('A0140','theory',   '2026-05-09','present','TG1706','S020');

-- TG1707 – 2 absents (S007, S013)
INSERT INTO attendance VALUES
('A0141','theory',   '2026-04-28','present','TG1707','S001'),
('A0142','practical','2026-04-28','present','TG1707','S002'),
('A0143','theory',   '2026-04-29','present','TG1707','S003'),
('A0144','practical','2026-04-29','present','TG1707','S004'),
('A0145','theory',   '2026-04-30','present','TG1707','S005'),
('A0146','theory',   '2026-04-30','present','TG1707','S006'),
('A0147','theory',   '2026-05-01','absent', 'TG1707','S007'),
('A0148','theory',   '2026-05-01','present','TG1707','S008'),
('A0149','theory',   '2026-05-02','present','TG1707','S009'),
('A0150','theory',   '2026-05-02','present','TG1707','S010'),
('A0151','theory',   '2026-05-05','present','TG1707','S011'),
('A0152','practical','2026-05-05','present','TG1707','S012'),
('A0153','theory',   '2026-05-06','absent', 'TG1707','S013'),
('A0154','practical','2026-05-06','present','TG1707','S014'),
('A0155','theory',   '2026-05-07','present','TG1707','S015'),
('A0156','theory',   '2026-05-07','present','TG1707','S016'),
('A0157','theory',   '2026-05-08','present','TG1707','S017'),
('A0158','theory',   '2026-05-08','present','TG1707','S018'),
('A0159','theory',   '2026-05-09','present','TG1707','S019'),
('A0160','theory',   '2026-05-09','present','TG1707','S020');

-- TG1708 – 1 absent (S010)
INSERT INTO attendance VALUES
('A0161','theory',   '2026-04-28','present','TG1708','S001'),
('A0162','practical','2026-04-28','present','TG1708','S002'),
('A0163','theory',   '2026-04-29','present','TG1708','S003'),
('A0164','practical','2026-04-29','present','TG1708','S004'),
('A0165','theory',   '2026-04-30','present','TG1708','S005'),
('A0166','theory',   '2026-04-30','present','TG1708','S006'),
('A0167','theory',   '2026-05-01','present','TG1708','S007'),
('A0168','theory',   '2026-05-01','present','TG1708','S008'),
('A0169','theory',   '2026-05-02','present','TG1708','S009'),
('A0170','theory',   '2026-05-02','absent', 'TG1708','S010'),
('A0171','theory',   '2026-05-05','present','TG1708','S011'),
('A0172','practical','2026-05-05','present','TG1708','S012'),
('A0173','theory',   '2026-05-06','present','TG1708','S013'),
('A0174','practical','2026-05-06','present','TG1708','S014'),
('A0175','theory',   '2026-05-07','present','TG1708','S015'),
('A0176','theory',   '2026-05-07','present','TG1708','S016'),
('A0177','theory',   '2026-05-08','present','TG1708','S017'),
('A0178','theory',   '2026-05-08','present','TG1708','S018'),
('A0179','theory',   '2026-05-09','present','TG1708','S019'),
('A0180','theory',   '2026-05-09','present','TG1708','S020');

-- TG1709 – 2 absents (S005, S017)
INSERT INTO attendance VALUES
('A0181','theory',   '2026-04-28','present','TG1709','S001'),
('A0182','practical','2026-04-28','present','TG1709','S002'),
('A0183','theory',   '2026-04-29','present','TG1709','S003'),
('A0184','practical','2026-04-29','present','TG1709','S004'),
('A0185','theory',   '2026-04-30','absent', 'TG1709','S005'),
('A0186','theory',   '2026-04-30','present','TG1709','S006'),
('A0187','theory',   '2026-05-01','present','TG1709','S007'),
('A0188','theory',   '2026-05-01','present','TG1709','S008'),
('A0189','theory',   '2026-05-02','present','TG1709','S009'),
('A0190','theory',   '2026-05-02','present','TG1709','S010'),
('A0191','theory',   '2026-05-05','present','TG1709','S011'),
('A0192','practical','2026-05-05','present','TG1709','S012'),
('A0193','theory',   '2026-05-06','present','TG1709','S013'),
('A0194','practical','2026-05-06','present','TG1709','S014'),
('A0195','theory',   '2026-05-07','present','TG1709','S015'),
('A0196','theory',   '2026-05-07','present','TG1709','S016'),
('A0197','theory',   '2026-05-08','absent', 'TG1709','S017'),
('A0198','theory',   '2026-05-08','present','TG1709','S018'),
('A0199','theory',   '2026-05-09','present','TG1709','S019'),
('A0200','theory',   '2026-05-09','present','TG1709','S020');

-- TG1710 – all present
INSERT INTO attendance VALUES
('A0201','theory',   '2026-04-28','present','TG1710','S001'),
('A0202','practical','2026-04-28','present','TG1710','S002'),
('A0203','theory',   '2026-04-29','present','TG1710','S003'),
('A0204','practical','2026-04-29','present','TG1710','S004'),
('A0205','theory',   '2026-04-30','present','TG1710','S005'),
('A0206','theory',   '2026-04-30','present','TG1710','S006'),
('A0207','theory',   '2026-05-01','present','TG1710','S007'),
('A0208','theory',   '2026-05-01','present','TG1710','S008'),
('A0209','theory',   '2026-05-02','present','TG1710','S009'),
('A0210','theory',   '2026-05-02','present','TG1710','S010'),
('A0211','theory',   '2026-05-05','present','TG1710','S011'),
('A0212','practical','2026-05-05','present','TG1710','S012'),
('A0213','theory',   '2026-05-06','present','TG1710','S013'),
('A0214','practical','2026-05-06','present','TG1710','S014'),
('A0215','theory',   '2026-05-07','present','TG1710','S015'),
('A0216','theory',   '2026-05-07','present','TG1710','S016'),
('A0217','theory',   '2026-05-08','present','TG1710','S017'),
('A0218','theory',   '2026-05-08','present','TG1710','S018'),
('A0219','theory',   '2026-05-09','present','TG1710','S019'),
('A0220','theory',   '2026-05-09','present','TG1710','S020');

-- ================================================================
--  7. MEDICAL RECORDS  (3 medicals for absent sessions only)
--
--  MED001: TG1704 was absent at S008 (2026-05-01 ICT2152 THEORY)
--  MED002: TG1704 was absent at S018 (2026-05-08 ICT2152 THEORY)
--  MED003: TG1706 was absent at S019 (2026-05-09 TCS2122 THEORY)
-- ================================================================
INSERT INTO medical
  (medical_id, submission_date, description,
   affectted_start_date, affectted_end_date, status, reg_no, session_id)
VALUES
(
  'MED001',
  '2026-05-02',
  'Student suffered from acute fever (39.5°C). Confirmed by Nawaloka Hospital physician. Advised complete bedrest for 24 hours.',
  '2026-05-01', '2026-05-01',
  'approved',
  'TG1704', 'S008'
),
(
  'MED002',
  '2026-05-09',
  'Recurrence of viral infection. Medical certificate issued by Asiri Medical Hospital. Student was unable to attend due to high temperature and fatigue.',
  '2026-05-08', '2026-05-08',
  'approved',
  'TG1704', 'S018'
),
(
  'MED003',
  '2026-05-09',
  'Student involved in a minor road accident on the way to university. Attended Colombo National Hospital for examination. Discharged same day with rest advised.',
  '2026-05-09', '2026-05-09',
  'not approved',
  'TG1706', 'S019'
);

-- ================================================================
--  8. MARKS
--  Insert marks for proper students (TG1700-TG1710)
--  for all 8 course units
--  All values out of 100
-- ================================================================

-- TG1700 – strong student
INSERT INTO marks VALUES
('TG1700','ENG2122', 82.00, 78.00, 85.00, 88.00, 79.00, 80.00),
('TG1700','ICT2113', 88.00, 90.00, 85.00, 92.00, 87.00, 83.00),
('TG1700','ICT2122', 79.00, 82.00, 80.00, 85.00, 78.00, 81.00),
('TG1700','ICT2132', 90.00, 88.00, 92.00, 95.00, 89.00, 87.00),
('TG1700','ICT2142', 75.00, 80.00, 78.00, 82.00, 76.00, 79.00),
('TG1700','ICT2152', 83.00, 85.00, 81.00, 87.00, 84.00, 82.00),
('TG1700','TCS2112', 70.00, 74.00, 72.00, 76.00, 71.00, 75.00),
('TG1700','TCS2122', 88.00, 85.00, 90.00, 92.00, 87.00, 89.00);

-- TG1701 – above average
INSERT INTO marks VALUES
('TG1701','ENG2122', 70.00, 72.00, 68.00, 74.00, 69.00, 71.00),
('TG1701','ICT2113', 75.00, 78.00, 72.00, 80.00, 74.00, 76.00),
('TG1701','ICT2122', 68.00, 70.00, 65.00, 72.00, 67.00, 70.00),
('TG1701','ICT2132', 80.00, 78.00, 82.00, 85.00, 79.00, 81.00),
('TG1701','ICT2142', 65.00, 68.00, 63.00, 70.00, 66.00, 68.00),
('TG1701','ICT2152', 72.00, 74.00, 70.00, 76.00, 73.00, 71.00),
('TG1701','TCS2112', 60.00, 63.00, 58.00, 65.00, 61.00, 63.00),
('TG1701','TCS2122', 75.00, 78.00, 72.00, 80.00, 76.00, 74.00);

-- TG1702 – average student
INSERT INTO marks VALUES
('TG1702','ENG2122', 55.00, 58.00, 52.00, 60.00, 56.00, 58.00),
('TG1702','ICT2113', 60.00, 62.00, 58.00, 65.00, 59.00, 62.00),
('TG1702','ICT2122', 52.00, 55.00, 50.00, 58.00, 53.00, 56.00),
('TG1702','ICT2132', 65.00, 62.00, 68.00, 70.00, 64.00, 66.00),
('TG1702','ICT2142', 50.00, 53.00, 48.00, 55.00, 51.00, 54.00),
('TG1702','ICT2152', 58.00, 60.00, 55.00, 62.00, 57.00, 60.00),
('TG1702','TCS2112', 48.00, 50.00, 45.00, 52.00, 49.00, 51.00),
('TG1702','TCS2122', 62.00, 65.00, 60.00, 68.00, 63.00, 61.00);

-- TG1703 – above average
INSERT INTO marks VALUES
('TG1703','ENG2122', 76.00, 79.00, 74.00, 82.00, 77.00, 78.00),
('TG1703','ICT2113', 82.00, 84.00, 80.00, 86.00, 81.00, 79.00),
('TG1703','ICT2122', 74.00, 77.00, 72.00, 80.00, 75.00, 76.00),
('TG1703','ICT2132', 86.00, 84.00, 88.00, 90.00, 85.00, 83.00),
('TG1703','ICT2142', 70.00, 73.00, 68.00, 76.00, 71.00, 73.00),
('TG1703','ICT2152', 78.00, 80.00, 76.00, 82.00, 79.00, 77.00),
('TG1703','TCS2112', 65.00, 68.00, 63.00, 70.00, 66.00, 68.00),
('TG1703','TCS2122', 82.00, 80.00, 84.00, 86.00, 81.00, 83.00);

-- TG1704 – good student (had medical absences)
INSERT INTO marks VALUES
('TG1704','ENG2122', 80.00, 82.00, 78.00, 85.00, 79.00, 81.00),
('TG1704','ICT2113', 85.00, 88.00, 82.00, 90.00, 84.00, 86.00),
('TG1704','ICT2122', 77.00, 80.00, 75.00, 83.00, 76.00, 78.00),
('TG1704','ICT2132', 88.00, 86.00, 90.00, 92.00, 87.00, 85.00),
('TG1704','ICT2142', 73.00, 76.00, 71.00, 78.00, 74.00, 75.00),
('TG1704','ICT2152', 81.00, 83.00, 79.00, 85.00, 80.00, 82.00),
('TG1704','TCS2112', 68.00, 71.00, 66.00, 73.00, 69.00, 70.00),
('TG1704','TCS2122', 85.00, 83.00, 87.00, 89.00, 84.00, 86.00);

-- TG1705 – slightly below average
INSERT INTO marks VALUES
('TG1705','ENG2122', 45.00, 48.00, 42.00, 50.00, 46.00, 48.00),
('TG1705','ICT2113', 50.00, 52.00, 48.00, 55.00, 49.00, 52.00),
('TG1705','ICT2122', 42.00, 45.00, 40.00, 48.00, 43.00, 46.00),
('TG1705','ICT2132', 55.00, 52.00, 58.00, 60.00, 54.00, 56.00),
('TG1705','ICT2142', 40.00, 43.00, 38.00, 45.00, 41.00, 44.00),
('TG1705','ICT2152', 48.00, 50.00, 45.00, 52.00, 47.00, 50.00),
('TG1705','TCS2112', 38.00, 40.00, 35.00, 42.00, 39.00, 41.00),
('TG1705','TCS2122', 52.00, 55.00, 50.00, 58.00, 53.00, 51.00);

-- TG1706 – good student
INSERT INTO marks VALUES
('TG1706','ENG2122', 78.00, 80.00, 76.00, 83.00, 77.00, 79.00),
('TG1706','ICT2113', 84.00, 86.00, 82.00, 88.00, 83.00, 81.00),
('TG1706','ICT2122', 76.00, 79.00, 74.00, 82.00, 77.00, 78.00),
('TG1706','ICT2132', 87.00, 85.00, 89.00, 91.00, 86.00, 84.00),
('TG1706','ICT2142', 72.00, 75.00, 70.00, 77.00, 73.00, 74.00),
('TG1706','ICT2152', 80.00, 82.00, 78.00, 84.00, 81.00, 80.00),
('TG1706','TCS2112', 67.00, 70.00, 65.00, 72.00, 68.00, 69.00),
('TG1706','TCS2122', 83.00, 81.00, 85.00, 87.00, 82.00, 84.00);

-- TG1707 – average student
INSERT INTO marks VALUES
('TG1707','ENG2122', 62.00, 65.00, 60.00, 68.00, 63.00, 65.00),
('TG1707','ICT2113', 67.00, 70.00, 65.00, 72.00, 66.00, 69.00),
('TG1707','ICT2122', 60.00, 63.00, 58.00, 66.00, 61.00, 63.00),
('TG1707','ICT2132', 72.00, 70.00, 74.00, 76.00, 71.00, 73.00),
('TG1707','ICT2142', 57.00, 60.00, 55.00, 62.00, 58.00, 60.00),
('TG1707','ICT2152', 65.00, 67.00, 63.00, 69.00, 64.00, 67.00),
('TG1707','TCS2112', 55.00, 58.00, 53.00, 60.00, 56.00, 58.00),
('TG1707','TCS2122', 69.00, 72.00, 67.00, 74.00, 70.00, 68.00);

-- TG1708 – above average
INSERT INTO marks VALUES
('TG1708','ENG2122', 73.00, 76.00, 71.00, 79.00, 74.00, 75.00),
('TG1708','ICT2113', 79.00, 82.00, 77.00, 84.00, 78.00, 80.00),
('TG1708','ICT2122', 71.00, 74.00, 69.00, 77.00, 72.00, 73.00),
('TG1708','ICT2132', 83.00, 81.00, 85.00, 87.00, 82.00, 80.00),
('TG1708','ICT2142', 67.00, 70.00, 65.00, 73.00, 68.00, 70.00),
('TG1708','ICT2152', 75.00, 77.00, 73.00, 79.00, 76.00, 74.00),
('TG1708','TCS2112', 62.00, 65.00, 60.00, 67.00, 63.00, 65.00),
('TG1708','TCS2122', 79.00, 77.00, 81.00, 83.00, 78.00, 80.00);

-- TG1709 – slightly below average
INSERT INTO marks VALUES
('TG1709','ENG2122', 48.00, 50.00, 46.00, 53.00, 49.00, 51.00),
('TG1709','ICT2113', 53.00, 55.00, 51.00, 58.00, 52.00, 55.00),
('TG1709','ICT2122', 46.00, 49.00, 44.00, 52.00, 47.00, 49.00),
('TG1709','ICT2132', 58.00, 56.00, 60.00, 63.00, 57.00, 59.00),
('TG1709','ICT2142', 43.00, 46.00, 41.00, 48.00, 44.00, 47.00),
('TG1709','ICT2152', 51.00, 53.00, 49.00, 55.00, 50.00, 52.00),
('TG1709','TCS2112', 41.00, 43.00, 39.00, 45.00, 42.00, 44.00),
('TG1709','TCS2122', 55.00, 58.00, 53.00, 61.00, 56.00, 54.00);

-- TG1710 – strong student
INSERT INTO marks VALUES
('TG1710','ENG2122', 85.00, 88.00, 83.00, 90.00, 86.00, 87.00),
('TG1710','ICT2113', 90.00, 92.00, 88.00, 94.00, 91.00, 89.00),
('TG1710','ICT2122', 83.00, 86.00, 81.00, 89.00, 84.00, 85.00),
('TG1710','ICT2132', 93.00, 91.00, 95.00, 96.00, 92.00, 90.00),
('TG1710','ICT2142', 79.00, 82.00, 77.00, 85.00, 80.00, 82.00),
('TG1710','ICT2152', 87.00, 89.00, 85.00, 91.00, 88.00, 86.00),
('TG1710','TCS2112', 74.00, 77.00, 72.00, 79.00, 75.00, 77.00),
('TG1710','TCS2122', 91.00, 89.00, 93.00, 95.00, 90.00, 92.00);

-- ================================================================
--  9. MATERIALS (sample per course)
-- ================================================================
INSERT INTO materials (mat_id, c_code, title, link) VALUES
('M001', 'ICT2122', 'Week 01 – Intro to OOP',            'https://lms.technova.lk/ICT2122/W1'),
('M002', 'ICT2122', 'Week 02 – Classes and Objects',      'https://lms.technova.lk/ICT2122/W2'),
('M003', 'ICT2132', 'Lab Sheet 01 – Java Basics',         'https://lms.technova.lk/ICT2132/Lab1'),
('M004', 'ICT2132', 'Lab Sheet 02 – Inheritance',         'https://lms.technova.lk/ICT2132/Lab2'),
('M005', 'ICT2113', 'DSA Lecture Notes – Arrays & Lists', 'https://lms.technova.lk/ICT2113/L1'),
('M006', 'ICT2113', 'DSA Lab – Stack & Queue',            'https://lms.technova.lk/ICT2113/Lab1'),
('M007', 'ICT2142', 'OOAD – UML Diagrams Reference',      'https://lms.technova.lk/ICT2142/UML'),
('M008', 'ICT2152', 'E-Commerce Platforms Overview',      'https://lms.technova.lk/ICT2152/W1'),
('M009', 'ENG2122', 'Academic Writing Guide III',         'https://lms.technova.lk/ENG2122/Guide'),
('M010', 'TCS2112', 'Business Economics – Chapter 1',     'https://lms.technova.lk/TCS2112/C1'),
('M011', 'TCS2122', 'Soft Skills – Presentation Tips',   'https://lms.technova.lk/TCS2122/Tips');

-- ================================================================
--  10. NOTICES (10 proper notices)
-- ================================================================
INSERT INTO notice (notice_id, notice) VALUES
(1,  'Semester II Level 2 timetable is now active. All undergraduates must follow the updated schedule from 28th April 2026. Any conflicts must be reported to the Faculty Office within 3 working days.'),
(2,  'The mini project for ICT2132 – OOP Practicum is due on 26th April 2026 by 11:59 PM. Submit your source code via the LMS and upload the group report. Late submissions will not be accepted.'),
(3,  'Mid-semester examinations for L2S1 will be held from 19th May 2026 to 23rd May 2026. The detailed timetable will be published on the LMS by 5th May 2026. Students must carry their student ID cards.'),
(4,  'All undergraduates must maintain a minimum of 80% attendance for both theory and practical components to be eligible to sit the final examination. Current attendance can be checked via the student portal.'),
(5,  'The Faculty Library will be closed from 25th April 2026 to 27th April 2026 for the annual stock-taking exercise. Students are advised to borrow required materials before the closure date.'),
(6,  'Medical certificates for absences must be submitted to the Technical Officers within 5 working days of the missed session. Late medical submissions will be marked as not approved automatically.'),
(7,  'ICT2122 – Object Oriented Programming supplementary lecture has been rescheduled from Friday 2nd May to Monday 5th May 2026 in Lecture Hall 1 at 8:00 AM. All students are required to attend.'),
(8,  'The Faculty of Technology Annual Academic Excellence Awards ceremony will be held on 30th May 2026 at the Main Auditorium. Nominations for outstanding students can be submitted through the Faculty Office by 10th May 2026.'),
(9,  'Students experiencing academic difficulties are encouraged to use the peer tutoring programme available every Tuesday and Thursday from 2:00 PM to 4:00 PM in Room 204. Register through the Student Affairs Division.'),
(10, 'Final examination registrations for L2S1 will open on 1st June 2026. Students who do not meet the CA eligibility criteria (CA marks >= 40%) will not be permitted to register. Check your eligibility status on the portal.');

-- ================================================================
--  RE-ENABLE FK CHECKS
-- ================================================================
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================================
--  VERIFY COUNTS
-- ================================================================
SELECT 'SUMMARY' AS section, '' AS detail UNION ALL
SELECT 'Users total',      COUNT(*) FROM users UNION ALL
SELECT 'Admin',            COUNT(*) FROM users WHERE role_id=1 UNION ALL
SELECT 'Lecturers',        COUNT(*) FROM users WHERE role_id=2 UNION ALL
SELECT 'Tech Officers',    COUNT(*) FROM users WHERE role_id=3 UNION ALL
SELECT 'Students (users)', COUNT(*) FROM users WHERE role_id=4 UNION ALL
SELECT 'Students (table)', COUNT(*) FROM student UNION ALL
SELECT '  PROPER',         COUNT(*) FROM student WHERE status='PROPER' UNION ALL
SELECT '  BOTH REPEAT',    COUNT(*) FROM student WHERE status='BOTH REPEAT' UNION ALL
SELECT '  CA REPEAT(BM)',  COUNT(*) FROM student WHERE status='CA REPEAT' UNION ALL
SELECT 'Courses',          COUNT(*) FROM course_unit UNION ALL
SELECT 'Timetable sessions',COUNT(*) FROM timetable UNION ALL
SELECT 'Attendance records',COUNT(*) FROM attendance UNION ALL
SELECT 'Medical records',  COUNT(*) FROM medical UNION ALL
SELECT 'Marks records',    COUNT(*) FROM marks UNION ALL
SELECT 'Materials',        COUNT(*) FROM materials UNION ALL
SELECT 'Notices',          COUNT(*) FROM notice;
