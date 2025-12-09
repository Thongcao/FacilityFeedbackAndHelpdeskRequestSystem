-- SQL Script to create tables for Facility Feedback & Helpdesk Request System
-- Run this script if you want to create tables manually before running the application

USE facility_feedback_helpdesk_request_system_db;
GO

-- Table: users
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[users]') AND type in (N'U'))
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        full_name NVARCHAR(100) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        password NVARCHAR(255) NOT NULL,
        role NVARCHAR(20) NOT NULL
    );
    PRINT 'Table users created successfully';
END
GO

-- Table: students
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[students]') AND type in (N'U'))
BEGIN
    CREATE TABLE students (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        student_code NVARCHAR(20) NOT NULL UNIQUE,
        class_name NVARCHAR(50) NOT NULL,
        user_id BIGINT NOT NULL UNIQUE,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
    PRINT 'Table students created successfully';
END
GO

-- Table: staff
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[staff]') AND type in (N'U'))
BEGIN
    CREATE TABLE staff (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        position NVARCHAR(100) NOT NULL,
        user_id BIGINT NOT NULL UNIQUE,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
    PRINT 'Table staff created successfully';
END
GO

-- Table: departments
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[departments]') AND type in (N'U'))
BEGIN
    CREATE TABLE departments (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        location NVARCHAR(200)
    );
    PRINT 'Table departments created successfully';
END
GO

-- Table: rooms
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[rooms]') AND type in (N'U'))
BEGIN
    CREATE TABLE rooms (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        room_name NVARCHAR(50) NOT NULL,
        department_id BIGINT NOT NULL,
        FOREIGN KEY (department_id) REFERENCES departments(id)
    );
    PRINT 'Table rooms created successfully';
END
GO

-- Table: feedback_categories
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[feedback_categories]') AND type in (N'U'))
BEGIN
    CREATE TABLE feedback_categories (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500),
        sla_hours INT NOT NULL
    );
    PRINT 'Table feedback_categories created successfully';
END
GO

-- Table: tickets
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tickets]') AND type in (N'U'))
BEGIN
    CREATE TABLE tickets (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        subject NVARCHAR(200) NOT NULL,
        description NVARCHAR(MAX) NOT NULL,
        priority NVARCHAR(20) NOT NULL,
        status NVARCHAR(20) NOT NULL,
        created_at DATETIME2 NOT NULL,
        created_by_id BIGINT NOT NULL,
        department_id BIGINT,
        room_id BIGINT,
        category_id BIGINT,
        FOREIGN KEY (created_by_id) REFERENCES users(id),
        FOREIGN KEY (department_id) REFERENCES departments(id),
        FOREIGN KEY (room_id) REFERENCES rooms(id),
        FOREIGN KEY (category_id) REFERENCES feedback_categories(id)
    );
    PRINT 'Table tickets created successfully';
END
GO

PRINT 'All tables created successfully!';
GO



