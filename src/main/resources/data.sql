CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    txn_time DATE,
    description VARCHAR(20) NOT NULL,
    amount FLOAT NOT NULL,
    category VARCHAR(10),
    comment VARCHAR(50)
);