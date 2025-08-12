CREATE TABLE user (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    name VARCHAR(100),
    role VARCHAR(50) NOT NULL,
    wallet_address VARCHAR(66) , /*UNIQUE*/
    is_approved BOOLEAN DEFAULT FALSE,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE farm (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    farm_id VARCHAR(100) NOT NULL UNIQUE,
    owner_address VARCHAR(66) NOT NULL,
    location TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    certificate_cid VARCHAR(100),
    certificate_expiry DATETIME,
    tx_hash VARCHAR(66),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE durian_variety (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    variety_id VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    origin_region VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE trader_agency (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    agency_id VARCHAR(100) NOT NULL UNIQUE,
    owner_address VARCHAR(66) NOT NULL,
    agency_name VARCHAR(255) NOT NULL,
    export_license_cid VARCHAR(100),
    export_license_expiry DATETIME,
    tx_hash VARCHAR(66),
    is_approved BOOLEAN DEFAULT FALSE,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE logistics_company (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    company_id VARCHAR(100) NOT NULL UNIQUE,
    owner_address VARCHAR(66) NOT NULL,
    company_name TEXT NOT NULL,
    tx_hash VARCHAR(66),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE wallet_tx (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_address VARCHAR(66) NOT NULL,
    role VARCHAR(50) NOT NULL,           -- Replaced ENUM
    action VARCHAR(100) NOT NULL,
    reference_id VARCHAR(100),
    tx_hash VARCHAR(66) NOT NULL,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE batch (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    batch_id VARCHAR(100) NOT NULL UNIQUE,
    food_name VARCHAR(255) NOT NULL,
    farm_id VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    farm_location TEXT,
    batch_image_cid VARCHAR(100),
    delivery_destination TEXT,
    status VARCHAR(50) NOT NULL,
    farmer_address VARCHAR(66),
    trader_address VARCHAR(66),
    trader_agency_id VARCHAR(100),
    logistics_company_id VARCHAR(100),
    logistics_address VARCHAR(66),
    tx_hash VARCHAR(66),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE durian (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    durian_id VARCHAR(100) NOT NULL UNIQUE,
    image_url TEXT NOT NULL,
    image_hash VARCHAR(100) NOT NULL,
    farm_id VARCHAR(100) NOT NULL,
    batch_id VARCHAR(100),
    variety_id VARCHAR(100),
    wallet_address VARCHAR(66),
    scan_count INT UNSIGNED DEFAULT 0,
    on_chain BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'Pending',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


INSERT INTO user (username, password, email, phone, name, role, wallet_address, is_approved)
VALUES
  ('admin', '$2a$10$qQhi0JcGL0CFPZ.qp4tL9OWVhfeRngNfIGCC07YE5iLw2rlEHAt0G', 'admin@example.com', '0123456789', 'System Admin', 'admin', '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266', TRUE),
  ('farmer', '$2a$10$qQhi0JcGL0CFPZ.qp4tL9OWVhfeRngNfIGCC07YE5iLw2rlEHAt0G', 'farmer@example.com', '0123456789', 'Durian Farmer', 'farmer', '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266', TRUE),
  ('logistics', '$2a$10$qQhi0JcGL0CFPZ.qp4tL9OWVhfeRngNfIGCC07YE5iLw2rlEHAt0G', 'logistics@example.com', '0123456789', 'Logistics Team', 'logistics', '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266', TRUE),
  ('trader', '$2a$10$qQhi0JcGL0CFPZ.qp4tL9OWVhfeRngNfIGCC07YE5iLw2rlEHAt0G', 'trader@example.com', '0123456789', 'Durian Trader', 'trader', '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266', TRUE);

INSERT INTO durian_variety (variety_id, name, origin_region, description) VALUES
('VARIETY-0001', 'Musang King (D197)', 'Pahang, Malaysia', 'Bright yellow flesh, rich and creamy texture, bittersweet taste, small seeds. Most popular premium variety.'),
('VARIETY-0002', 'D24 (Sultan)', 'Pahang, Malaysia', 'Pale yellow flesh, smooth and creamy with a balanced bittersweet flavor. Popular before Musang King rose to fame.'),
('VARIETY-0003', 'Red Prawn (Udang Merah)', 'Penang, Malaysia', 'Orangey-red flesh, very sweet with custard-like texture. Popular in northern Malaysia.'),
('VARIETY-0004', 'XO', 'Johor & Pahang, Malaysia', 'Pale flesh, bitter alcoholic taste due to longer fermentation. Strong aroma.'),
('VARIETY-0005', 'Black Thorn (D200)', 'Penang, Malaysia', 'Deep orange flesh, sticky texture, very sweet with slight bitterness. High-end premium durian.'),
('VARIETY-0006', 'Tekka (D160 / Green Bamboo)', 'Johor, Malaysia', 'Pale yellow flesh, complex bitter-sweet taste, dry texture, often loved by older durian lovers.'),
('VARIETY-0007', 'Hor Lor', 'Balik Pulau, Penang', 'Long-shaped fruit, light yellow flesh, strong aroma, dry and slightly bitter taste.'),
('VARIETY-0008', 'D88', 'Johor, Malaysia', 'Creamy, mildly sweet taste with small seeds. Considered a balanced durian.'),
('VARIETY-0009', 'D101', 'Johor & Melaka, Malaysia', 'Bright yellow flesh, sweet and less pungent. Often suitable for first-time durian eaters.'),
('VARIETY-0010', 'Golden Phoenix (Jin Feng)', 'Malaysia & Singapore', 'Pale white-yellow flesh, very strong aroma, slightly bitter. Smaller in size.');
