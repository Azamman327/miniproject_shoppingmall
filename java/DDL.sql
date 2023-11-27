CREATE DATABASE shoppingmall_db;

USE shoppingmall_db;

CREATE TABLE Category (
	categoryId	int	NOT NULL AUTO_INCREMENT,
	name	varchar(20)	NOT NULL,
    PRIMARY KEY (categoryId)
);

CREATE TABLE Product (
	productId	int	NOT NULL  AUTO_INCREMENT,
	categoryId	int	NOT NULL,
	productName	varchar(50)	NOT NULL,
	price	float	NOT NULL,
	count	int	NOT NULL,
    PRIMARY KEY (productId),
    FOREIGN KEY (categoryId) REFERENCES Category (categoryId)
);

CREATE TABLE Purchase (
	purchaseId	int	NOT NULL  AUTO_INCREMENT,
	productId	int	NOT NULL,
	productName	varchar(50)	NOT NULL,
	count		int	NOT NULL,
	totalPrice	float	NOT NULL,
    PRIMARY KEY (purchaseId),
    FOREIGN KEY (productId) REFERENCES Product (productId)
);

INSERT INTO Category values (NULL, 'food');
INSERT INTO Category values (NULL, 'clothes');
INSERT INTO Category values (NULL, 'furniture');

INSERT INTO Product(productId, categoryId, productName, price, count) VALUES (NULL, 1, 'bread', 4000, 9999);
INSERT INTO Product(productId, categoryId, productName, price, count) VALUES (NULL, 2, 'shirt', 4000, 9999);
INSERT INTO Product(productId, categoryId, productName, price, count) VALUES (NULL, 1, 'orange', 1500, 9999);
INSERT INTO Product(productId, categoryId, productName, price, count) VALUES (NULL, 3, 'sofa', 1800000, 9999);







