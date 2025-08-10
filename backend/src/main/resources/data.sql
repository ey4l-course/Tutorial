CREATE TABLE transactions_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    txn_time TIMESTAMP NOT NULL,
    description VARCHAR(20) NOT NULL,  --Name of shop
    amount DECIMAL(12, 2) NOT NULL,
    category_id BIGINT NOT NULL,
    category_source VARCHAR(10), --How source was determined
    unique_weight INT DEFAULT 0, --How is it counted in global classification (only counts if category changes)
    payment_method VARCHAR(20) NOT NULL,
    comment VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES user_crm(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
);

CREATE TABLE transaction_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    user_defined BOOLEAN
);

CREATE TABLE user_transaction_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    description VARCHAR(20) NOT NULL,
    category BIGINT NOT NULL,
    regular BOOLEAN NOT NULL, --Weather or not this transaction repetitive
    FOREIGN KEY (user_id) REFERENCES user_crm(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
);

CREATE TABLE global_transaction_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(20) NOT NULL,
    category BIGINT NOT NULL,
    regular BOOLEAN NOT NULL,
--    is_default BOOLEAN  Maybe used to determine periodically and avoid frequent changes
    vote_count BIGINT DEFAULT 0, --Count of description/category combination
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
);

CREATE TABLE user_crm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    given_name VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    email_address VARCHAR(40) UNIQUE,
    mobile VARCHAR(15) UNIQUE,
    service_level INT DEFAULT 1,
    last_seen TIMESTAMP NOT NULL
);

CREATE TABLE user_login (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    user_name VARCHAR(20) UNIQUE NOT NULL,
    hashed_password VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL,
    is_active BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user_crm(id) ON DELETE CASCADE
);

CREATE TABLE common_ip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    last_seen TIMESTAMP NOT NULL,
    usage_count INT DEFAULT 1,
    is_sus BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user_login(id)
);

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
