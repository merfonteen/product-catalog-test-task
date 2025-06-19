CREATE TABLE products
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(1000),
    price       DECIMAL(15, 2) NOT NULL,
    category    VARCHAR(255),
    stock       INTEGER,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);



CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_category ON products(category);



INSERT INTO products (name, description, price, category, stock, created_at, updated_at)
VALUES ('Apple iPhone 14', 'Latest model of iPhone', 999.99, 'Electronics', 25, NOW(), NOW()),
       ('Samsung Galaxy S23', 'Newest Galaxy smartphone', 899.99, 'Electronics', 40, NOW(), NOW()),
       ('Office Chair', 'Comfortable ergonomic chair', 149.99, 'Furniture', 15, NOW(), NOW());