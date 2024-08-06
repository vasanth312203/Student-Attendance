# Student Attendence system 
-----------------------------------------------
## SQl Query

```
-- Create a database
CREATE DATABASE IF NOT EXISTS school;

-- Use the newly created database
USE school;

-- Create a table for students
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    attendance BOOLEAN
);

-- Insert some initial sample data (optional)
INSERT INTO students (name) VALUES ('Alice');
INSERT INTO students (name) VALUES ('Bob');


select * from students;


```
