CREATE TABLE transactions_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    txn_time DATE,
    description VARCHAR(20) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    category VARCHAR(10),
    comment VARCHAR(50)
);

INSERT INTO transactions_table (txn_time, description, amount, category, comment) VALUES
('2025-06-01', 'SHUFERSAL TLV', 182.40, 'Food', 'Groceries for the week'),
('2025-06-02', 'NETFLIX.COM', 38.90,  'Leisure', 'Monthly subscription'),
('2025-06-04', 'CLAL INSURANCE', 129.50,  'Health', 'Supplementary health plan'),
('2025-06-06', 'CAFE GREG', 28.00,  'Food', 'Coffee and pastry'),
('2025-06-08', 'EGGED BUS JERUSALEM', 9.60, 'Transport', 'Work commute'),
('2025-06-09', 'STEIMATZKY ONLINE', 54.90, 'Education', 'Finance book'),
('2025-06-10', 'COURSERA.ORG', 210.00, 'Education', 'Online course fee'),
('2025-06-12', 'WOLT*PIZZA HUT', 76.30, 'Food', 'Dinner delivery'),
('2025-06-14', 'HOT MOBILE', 65.90, 'Utilities', 'Cell phone plan'),
('2025-06-15', 'YES PLANET', 47.00, 'Leisure', 'Movie night');