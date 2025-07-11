CREATE DATABASE student_db;

\c student_db;

CREATE TABLE student (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INT,
    grade DECIMAL(5,2)
);

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255)
);
