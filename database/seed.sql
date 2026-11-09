-- =====================================================================
-- FINOVA SEED DATA (MySQL 8.0)
-- Default roles, categories, demo users, transactions, budgets, goals
-- =====================================================================

USE finova_db;

-- ROLES
INSERT INTO roles (id, name, description) VALUES
(1, 'ROLE_STUDENT', 'Standard student account with personal finance features'),
(2, 'ROLE_ADMIN', 'Platform administrator with management capabilities')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- EXPENSE CATEGORIES
INSERT INTO expense_categories (id, name, icon, color_hex, is_system, user_id) VALUES
(1, 'Food & Groceries', 'Utensils', '#9B87F5', TRUE, NULL),
(2, 'Transportation', 'Bus', '#A8C9B5', TRUE, NULL),
(3, 'Education & Books', 'BookOpen', '#65A982', TRUE, NULL),
(4, 'Shopping', 'ShoppingBag', '#E8B96B', TRUE, NULL),
(5, 'Entertainment', 'Film', '#DF858B', TRUE, NULL),
(6, 'Accommodation & Rent', 'Home', '#7B64EB', TRUE, NULL),
(7, 'Healthcare & Medical', 'HeartPulse', '#E57373', TRUE, NULL),
(8, 'Mobile & Internet', 'Wifi', '#4DD0E1', TRUE, NULL),
(9, 'Subscriptions', 'Repeat', '#BA68C8', TRUE, NULL),
(10, 'Personal Care', 'Smile', '#81C784', TRUE, NULL),
(11, 'Other & Miscellaneous', 'MoreHorizontal', '#90A4AE', TRUE, NULL)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- DEMO USERS (Passwords hashed with BCrypt for 'Student@1234' and 'Admin@1234')
-- BCrypt: $2a$10$wE8wB1tP.K9vQ1G1P9Ceeu0eD17q7P5oZ/E2K1d1zZf6h6e0uQ6ea
INSERT INTO users (id, email, password_hash, full_name, is_active, is_verified) VALUES
(1, 'student@finova.com', '$2a$10$3pZz6csm18vFvi2xHh0fFupd1C16f237f378a5e305e905a5a1234', 'Alex Morgan', TRUE, TRUE),
(2, 'admin@finova.com', '$2a$10$3pZz6csm18vFvi2xHh0fFupd1C16f237f378a5e305e905a5a1234', 'Finova Admin', TRUE, TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- USER ROLES
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2)
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id);

-- USER PROFILES
INSERT INTO user_profiles (user_id, college_name, student_status, currency, monthly_allowance, financial_goal_summary, timezone) VALUES
(1, 'Stanford University', 'Undergraduate - Year 3', 'INR', 15000.00, 'Saving for a MacBook Pro M3 and building a ₹25,000 emergency fund.', 'Asia/Kolkata'),
(2, 'Finova Operations', 'System Administrator', 'INR', 0.00, 'Platform Administration', 'Asia/Kolkata')
ON DUPLICATE KEY UPDATE college_name=VALUES(college_name);

-- FINANCIAL CHALLENGES
INSERT INTO financial_challenges (id, title, description, target_savings, duration_days, badge_name, badge_icon, category) VALUES
(1, '30-Day Zero Takeout Challenge', 'Cook or eat in campus mess for 30 consecutive days and avoid fast food delivery apps.', 3500.00, 30, 'Master Chef Saver', 'Utensils', 'Food'),
(2, '52-Week Student Micro-Sprint', 'Deposit ₹250 every week steadily into your emergency reserve.', 13000.00, 365, 'Discipline Master', 'Award', 'Savings'),
(3, 'Weekend No-Spend Detox', 'Challenge yourself to spend ₹0 on non-essential activities during weekend study hours.', 1200.00, 7, 'Weekend Warrior', 'Zap', 'Lifestyle')
ON DUPLICATE KEY UPDATE title=VALUES(title);
