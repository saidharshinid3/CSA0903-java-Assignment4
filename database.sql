
CREATE DATABASE campus_events;

USE campus_events;

CREATE TABLE registrations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(30) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    event_name VARCHAR(100) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_student_event (student_id, event_name)
);
