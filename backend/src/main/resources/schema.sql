-- Create tables
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    price DECIMAL(10, 2),
    category VARCHAR(50)
);

CREATE TABLE sales (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    product_id INTEGER REFERENCES products(id),
    amount DECIMAL(10, 2),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data with Turkish characters
INSERT INTO users (name, email) VALUES 
('Ahmet Yılmaz', 'ahmet@example.com'),
('Ayşe Demir', 'ayse@example.com'),
('Mehmet Kaya', 'mehmet@example.com');

INSERT INTO products (name, price, category) VALUES 
('Laptop', 15000.00, 'Electronics'),
('Mouse', 200.00, 'Electronics'),
('Keyboard', 500.00, 'Electronics');

INSERT INTO sales (user_id, product_id, amount, sale_date) VALUES 
(1, 1, 15000.00, DATEADD('DAY', -10, CURRENT_TIMESTAMP)),
(1, 2, 200.00, DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
(2, 3, 500.00, DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
(3, 1, 15000.00, DATEADD('DAY', -40, CURRENT_TIMESTAMP));
