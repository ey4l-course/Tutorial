CREATE TABLE transactions_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    txn_time DATE,
    description VARCHAR(20) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    category VARCHAR(10),
    comment VARCHAR(50)
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
    user_id BIGINT NOT NULL,
    user_name VARCHAR(20) UNIQUE NOT NULL,
    hashed_password VARCHAR(100) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user_crm(id)
);

CREATE TABLE common_ip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    last_seen TIMESTAMP NOT NULL,
    usage_count INT DEFAULT 1,
    is_sus BOOLEAN DEFAULT FALSE,
    FOREIGN KEY user_id REFERENCES user_crm(id)
);