INSERT INTO user_crm (given_name, surname, email_address, mobile, service_level, last_seen) VALUES
('Alice', 'Goldman', 'alice.goldman@example.com', '0501234567', 2, '2025-08-04 10:15:00'),
('Ben', 'Cohen', 'ben.cohen@example.com', '0502345678', 1, '2025-08-03 18:45:00'),
('Clara', 'Levi', 'clara.levi@example.com', '0503456789', 3, '2025-08-04 09:30:00'),
('David', 'Mizrahi', 'david.mizrahi@example.com', '0504567890', 1, '2025-08-02 22:10:00'),
('Ella', 'Shapiro', 'ella.shapiro@example.com', '0505678901', 2, '2025-08-04 14:00:00'),
('Frank', 'Baron', 'frank.baron@example.com', '0506789012', 1, '2025-08-01 08:20:00'),
('Gina', 'Katz', 'gina.katz@example.com', '0507890123', 3, '2025-08-04 16:45:00'),
('Harel', 'Nadav', 'harel.nadav@example.com', '0508901234', 2, '2025-08-03 11:55:00'),
('Iris', 'Segal', 'iris.segal@example.com', '0509012345', 1, '2025-08-04 20:30:00'),
('Jonas', 'Peretz', 'jonas.peretz@example.com', '0500123456', 2, '2025-08-04 07:10:00');

INSERT INTO user_login (user_id, user_name, hashed_password, role, is_active) VALUES
(1, 'aliceg', 'HASHED_1', 'admin', TRUE),
(2, 'benc', 'HASHED_2', 'user', TRUE),
(3, 'claral', 'HASHED_3', 'user', FALSE),
(4, 'davidm', 'HASHED_4', 'manager', TRUE),
(5, 'ellas', 'HASHED_5', 'user', TRUE),
(6, 'frankb', 'HASHED_6', 'user', FALSE),
(7, 'ginak', 'HASHED_7', 'admin', TRUE),
(8, 'hareln', 'HASHED_8', 'user', TRUE),
(9, 'iriss', 'HASHED_9', 'manager', FALSE),
(10, 'jonasp', 'HASHED_10', 'user', TRUE);

INSERT INTO transaction_categories (name, user_defined) VALUES
('groceries', FALSE),
('education', FALSE),
('fuel', FALSE),
('insurance', FALSE),
('rent', FALSE),
('utilities', FALSE),
('mortgage', FALSE),
('dining', FALSE),
('electricity', FALSE);


INSERT INTO transactions_table
(user_id, txn_time, description, amount, category_id, category_source, unique_weight, payment_method, comment)
VALUES
-- Groceries (cat 1)
(1, '2025-08-01 09:15:00', 'Shufersal', 85.20, 1, 'SPECIAL_CLASSIFICATION', 0, 'credit_card', 'Weekly shopping'),
(2, '2025-08-02 18:30:00', 'Rami Levy', 150.75, 1, 'GLOBAL_DEFAULT', 0, 'debit_card', NULL),
(1, '2025-08-04 11:45:00', 'Mega Market', 67.90, 1, 'PERMANENT_CLASSIFICATION', 0, 'credit_card', NULL),
(2, '2025-08-06 17:10:00', 'Shufersal Online', 210.30, 1, 'GLOBAL_DEFAULT', 1, 'paypal', 'Online order'),

-- Fuel (cat 3)
(1, '2025-08-03 08:00:00', 'PazGas', 190.00, 3, 'GLOBAL_DEFAULT', 0, 'credit_card', NULL),
(2, '2025-08-05 07:45:00', 'Sonol', 220.50, 3, 'SPECIAL_CLASSIFICATION', 0, 'debit_card', 'Morning refill'),
(1, '2025-08-08 19:20:00', 'Delek', 175.60, 3, 'PERMANENT_CLASSIFICATION', 1, 'credit_card', NULL),
(2, '2025-08-10 06:55:00', 'Paz Station', 199.90, 3, 'GLOBAL_DEFAULT', 0, 'credit_card', NULL),

-- Utilities (cat 6)
(1, '2025-08-07 14:15:00', 'ElectricCo', 360.40, 6, 'PERMANENT_CLASSIFICATION', 0, 'direct_debit', 'Monthly bill'),
(2, '2025-08-09 16:45:00', 'Water Auth', 120.75, 6, 'GLOBAL_DEFAULT', 0, 'direct_debit', NULL),
(1, '2025-08-11 10:30:00', 'Internet ISP', 89.90, 6, 'SPECIAL_CLASSIFICATION', 0, 'credit_card', NULL),
(2, '2025-08-12 08:25:00', 'Cellcom', 149.50, 6, 'PERMANENT_CLASSIFICATION', 1, 'direct_debit', 'Mobile plan'),

-- Dining (cat 8)
(1, '2025-08-13 20:10:00', 'Pizza Hut', 98.00, 8, 'GLOBAL_DEFAULT', 0, 'credit_card', NULL),
(2, '2025-08-14 21:05:00', 'McDonalds', 55.40, 8, 'SPECIAL_CLASSIFICATION', 0, 'debit_card', NULL),
(1, '2025-08-15 19:50:00', 'Aroma Cafe', 42.30, 8, 'PERMANENT_CLASSIFICATION', 0, 'credit_card', 'Coffee with friends'),
(2, '2025-08-16 12:30:00', 'Burger Ranch', 75.25, 8, 'GLOBAL_DEFAULT', 1, 'credit_card', NULL),

-- Mixed additional
(1, '2025-08-17 15:45:00', 'Shufersal', 132.80, 1, 'GLOBAL_DEFAULT', 0, 'credit_card', NULL),
(2, '2025-08-18 07:15:00', 'Delek', 188.60, 3, 'SPECIAL_CLASSIFICATION', 0, 'debit_card', 'Commuting'),
(1, '2025-08-19 09:40:00', 'ElectricCo', 310.25, 6, 'PERMANENT_CLASSIFICATION', 0, 'direct_debit', NULL),
(2, '2025-08-20 22:00:00', 'Pizza Hut', 102.50, 8, 'GLOBAL_DEFAULT', 0, 'credit_card', 'Family dinner');

INSERT INTO user_transaction_classification (user_id, description, category_id, regular)
VALUES
(2, 'PazGas', 2, true);
