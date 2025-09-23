CREATE TABLE user_crm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    given_name VARCHAR(20) NOT NULL,
    surname VARCHAR(20) NOT NULL,
    email_address VARCHAR(40) UNIQUE,
    mobile VARCHAR(15) UNIQUE,
    service_level INT DEFAULT 1,
    last_seen TIMESTAMP NOT NULL
);

CREATE TABLE transaction_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    user_defined BOOLEAN
);

CREATE TABLE transactions_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    txn_time TIMESTAMP NOT NULL,
    description VARCHAR(20) NOT NULL,  --Name of shop
    amount DECIMAL(12, 2) NOT NULL,
    category_id BIGINT NOT NULL,
    category_source VARCHAR(30), --How source was determined
    unique_weight INT DEFAULT 0, --How is it counted in global classification (only counts if category changes)
    payment_method VARCHAR(20) NOT NULL,
    comment VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES user_crm(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
);

CREATE TABLE user_transaction_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    description VARCHAR(20) NOT NULL,
    category_id BIGINT NOT NULL,
    regular BOOLEAN NOT NULL, --Weather or not this transaction repetitive
    FOREIGN KEY (user_id) REFERENCES user_crm(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
);

CREATE TABLE global_transaction_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(20) NOT NULL,
    category_id BIGINT NOT NULL,
    regular BOOLEAN NOT NULL,
--    is_default BOOLEAN  Maybe used to determine periodically and avoid frequent changes
    vote_count BIGINT DEFAULT 0, --Count of description/category combination
    FOREIGN KEY (category_id) REFERENCES transaction_categories(id)
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
