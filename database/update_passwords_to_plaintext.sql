-- SQL Script to update passwords to plain text
-- Run this script to update existing users with plain text passwords

USE facility_feedback_helpdesk_request_system_db;
GO

-- Update passwords to plain text
UPDATE users SET password = 'demo123' WHERE email = 'demo@fpt.edu.vn';
UPDATE users SET password = '123456' WHERE email = 'student1@fpt.edu.vn';
UPDATE users SET password = 'staff123' WHERE email = 'staff@fpt.edu.vn';
UPDATE users SET password = 'admin123' WHERE email = 'admin@fpt.edu.vn';

-- Verify the update
SELECT id, email, full_name, password FROM users;
GO

PRINT 'Passwords updated to plain text successfully!';
GO



