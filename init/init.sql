use master
go

CREATE DATABASE FlowerShop;
GO

USE FlowerShop
GO

CREATE TABLE Categories (
    Id INT IDENTITY PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL
);
GO

CREATE TABLE Accounts (
    Username VARCHAR(50) PRIMARY KEY,
    Password VARCHAR(255), 
    Fullname NVARCHAR(50) NOT NULL,
    Email NVARCHAR(50) UNIQUE,
    Photo NVARCHAR(255),
	Address NVARCHAR(100), 
	Phone VARCHAR(20) , 
	CreateDate DATETIME DEFAULT GETDATE(),
    Activated BIT NOT NULL DEFAULT 1
);
GO

CREATE TABLE Roles (
	Id INT IDENTITY PRIMARY KEY,
	Name VARCHAR(50) NOT NULL UNIQUE,
	Fullname NVARCHAR(100) NOT NULL UNIQUE
);
GO

CREATE TABLE AccountRoles (
    Username VARCHAR(50) NOT NULL,
    RoleId INT NOT NULL,

    CONSTRAINT PK_User_Role PRIMARY KEY (Username, RoleId),

    CONSTRAINT FK_UserRole_Account
        FOREIGN KEY (Username) REFERENCES Accounts(Username),

    CONSTRAINT FK_UserRole_Role
        FOREIGN KEY (RoleId) REFERENCES Roles(Id)
);
GO

CREATE TABLE Products (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(50) NOT NULL,
    Image NVARCHAR(50),
	CostPrice FLOAT NOT NULL,
    RetailPercentage FLOAT DEFAULT 0.1,
    CreateDate DATE DEFAULT GETDATE(),
    Available BIT NOT NULL DEFAULT 1,
	Amount INT NOT NULL DEFAULT 5,
	Sales BIGINT NOT NULL DEFAULT 0,
    CategoryId INT NOT NULL,
    CONSTRAINT FK_Products_Categories
        FOREIGN KEY (CategoryId)
        REFERENCES Categories(Id)
);
GO

CREATE TABLE Orders (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) NOT NULL,
	Fullname NVARCHAR(100),
	Phone VARCHAR(11),
	Address NVARCHAR(255),
    CreateDate DATETIME DEFAULT GETDATE(),
	Total FLOAT NOT NULL,
	Status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT FK_Orders_Accounts
        FOREIGN KEY (Username)
        REFERENCES Accounts (Username)
);
GO

CREATE TABLE OrderDetails (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    OrderId BIGINT NOT NULL,
    ProductId INT NOT NULL,
	Price FLOAT NOT NULL,
    Quantity INT NOT NULL,
	SubTotal FLOAT,
    CONSTRAINT FK_OrderDetails_Orders
        FOREIGN KEY (OrderId)
        REFERENCES Orders(Id),

    CONSTRAINT FK_OrderDetails_Products
        FOREIGN KEY (ProductId)
        REFERENCES Products(Id)
);
GO

CREATE TABLE Carts (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
	CreateDate DATETIME DEFAULT GETDATE(),
    Username VARCHAR(50) NOT NULL,
    Total FLOAT NOT NULL,
    CONSTRAINT FK_Cart_Account
        FOREIGN KEY (Username)
        REFERENCES Accounts(Username)
);
GO

CREATE TABLE Items (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    CartId BIGINT NOT NULL,
    ProductId INT NOT NULL,
    Quantity INT NOT NULL,
    SubTotal FLOAT NOT NULL,
    CONSTRAINT FK_Item_Cart
        FOREIGN KEY (CartId)
        REFERENCES Carts(Id), 

    CONSTRAINT FK_Item_Product
        FOREIGN KEY (ProductId)
        REFERENCES Products(Id)
);
GO

CREATE TABLE Discounts (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    ProductId INT NOT NULL,
	Value FLOAT NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    Active BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_Discounts_Products
        FOREIGN KEY (ProductId)
        REFERENCES Products(Id)
);
GO

CREATE TABLE Comments ( 
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ProductId INT NOT NULL,
    Username VARCHAR(50) NOT NULL,
    Content NVARCHAR(MAX) NOT NULL,
    CreateDate DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_ProductComments_Product
        FOREIGN KEY (ProductId)
        REFERENCES Products(Id),

    CONSTRAINT FK_ProductComments_User
        FOREIGN KEY (Username)
        REFERENCES Accounts(Username)
);
GO

CREATE TABLE Replies (
    Id BIGINT IDENTITY(1,1) PRIMARY KEY,      
    CommentId BIGINT NOT NULL,                
    Username VARCHAR(50) NOT NULL,            
    Content NVARCHAR(500) NOT NULL,           
    CreateDate DATETIME DEFAULT GETDATE(),    

    CONSTRAINT FK_Replies_Comments
        FOREIGN KEY (CommentId)
        REFERENCES Comments(Id),                  -- Delete replies if comment is deleted

    CONSTRAINT FK_Replies_Accounts
        FOREIGN KEY (Username)
        REFERENCES Accounts(Username)
);
GO

INSERT INTO Categories (Name) VALUES
(N'Hoa bó'), -- 1--
(N'Hoa cưới'), -- 2 --
(N'Giỏ hoa'), -- 3 --
(N'Hộp hoa'), -- 4 --
(N'Bình hoa'), -- 5 --
(N'Kệ hoa chúc mừng'), -- 6 --
(N'Kệ hoa chia buồn'), -- 7 --
(N'Giỏ hoa trái cây'), -- 8 --
(N'Hoa để bàn'); -- 9 --
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Hoa bó 01', 'hoabo_01.jpg', 350000, 1),
(N'Hoa bó 02', 'hoabo_02.jpg', 360000, 1),
(N'Hoa bó 03', 'hoabo_03.jpg', 370000, 1),
(N'Hoa bó 04', 'hoabo_04.jpg', 380000, 1),
(N'Hoa bó 05', 'hoabo_05.jpg', 390000, 1),
(N'Hoa bó 06', 'hoabo_06.jpg', 400000, 1),
(N'Hoa bó 07', 'hoabo_07.jpg', 410000, 1),
(N'Hoa bó 08', 'hoabo_08.jpg', 420000, 1),
(N'Hoa bó 09', 'hoabo_09.jpg', 430000, 1),
(N'Hoa bó 10', 'hoabo_10.jpg', 440000, 1),
(N'Hoa bó 11', 'hoabo_11.jpg', 450000, 1),
(N'Hoa bó 12', 'hoabo_12.jpg', 460000, 1),
(N'Hoa bó 13', 'hoabo_13.jpg', 470000, 1),
(N'Hoa bó 14', 'hoabo_14.jpg', 480000, 1),
(N'Hoa bó 15', 'hoabo_15.jpg', 490000, 1),
(N'Hoa bó 16', 'hoabo_16.jpg', 500000, 1),
(N'Hoa bó 17', 'hoabo_17.jpg', 510000, 1),
(N'Hoa bó 18', 'hoabo_18.jpg', 520000, 1),
(N'Hoa bó 19', 'hoabo_19.jpg', 530000, 1),
(N'Hoa bó 20', 'hoabo_20.jpg', 540000, 1),
(N'Hoa bó 21', 'hoabo_21.jpg', 550000, 1),
(N'Hoa bó 22', 'hoabo_22.jpg', 560000, 1),
(N'Hoa bó 23', 'hoabo_23.jpg', 570000, 1),
(N'Hoa bó 24', 'hoabo_24.jpg', 580000, 1),
(N'Hoa bó 25', 'hoabo_25.jpg', 590000, 1);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Bình hoa 01', 'binhhoa_01.webp', 650000, 5),
(N'Bình hoa 02', 'binhhoa_02.webp', 660000, 5),
(N'Bình hoa 03', 'binhhoa_03.webp', 670000, 5),
(N'Bình hoa 04', 'binhhoa_04.webp', 680000, 5),
(N'Bình hoa 05', 'binhhoa_05.webp', 690000, 5),
(N'Bình hoa 06', 'binhhoa_06.webp', 700000, 5),
(N'Bình hoa 07', 'binhhoa_07.webp', 710000, 5),
(N'Bình hoa 08', 'binhhoa_08.webp', 720000, 5),
(N'Bình hoa 09', 'binhhoa_09.webp', 730000, 5),
(N'Bình hoa 10', 'binhhoa_10.webp', 740000, 5);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Giỏ hoa 01', 'giohoa_01.jpg', 550000, 3),
(N'Giỏ hoa 02', 'giohoa_02.jpg', 560000, 3),
(N'Giỏ hoa 03', 'giohoa_03.jpg', 570000, 3),
(N'Giỏ hoa 04', 'giohoa_04.jpg', 580000, 3),
(N'Giỏ hoa 05', 'giohoa_05.jpg', 590000, 3),
(N'Giỏ hoa 06', 'giohoa_06.jpg', 600000, 3),
(N'Giỏ hoa 07', 'giohoa_07.jpg', 610000, 3),
(N'Giỏ hoa 08', 'giohoa_08.jpg', 620000, 3),
(N'Giỏ hoa 09', 'giohoa_09.jpg', 630000, 3),
(N'Giỏ hoa 10', 'giohoa_10.jpg', 640000, 3),
(N'Giỏ hoa 11', 'giohoa_11.jpg', 650000, 3);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Hộp hoa 01', 'hophoa_01.jpg', 600000, 4),
(N'Hộp hoa 02', 'hophoa_02.jpg', 610000, 4),
(N'Hộp hoa 03', 'hophoa_03.jpg', 620000, 4),
(N'Hộp hoa 04', 'hophoa_04.jpg', 630000, 4),
(N'Hộp hoa 05', 'hophoa_05.jpg', 640000, 4),
(N'Hộp hoa 06', 'hophoa_06.jpg', 650000, 4),
(N'Hộp hoa 07', 'hophoa_07.jpg', 660000, 4),
(N'Hộp hoa 08', 'hophoa_08.jpg', 670000, 4),
(N'Hộp hoa 09', 'hophoa_09.jpg', 680000, 4),
(N'Hộp hoa 10', 'hophoa_10.jpg', 690000, 4),
(N'Hộp hoa 11', 'hophoa_11.jpg', 700000, 4),
(N'Hộp hoa 12', 'hophoa_12.jpg', 710000, 4);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Hoa cưới 01', 'hoacuoi_01.jpg', 1200000, 2),
(N'Hoa cưới 02', 'hoacuoi_02.jpg', 1250000, 2),
(N'Hoa cưới 03', 'hoacuoi_03.webp', 1300000, 2),
(N'Hoa cưới 04', 'hoacuoi_04.jpg', 1350000, 2),
(N'Hoa cưới 05', 'hoacuoi_05.jpg', 1400000, 2),
(N'Hoa cưới 06', 'hoacuoi_06.jpg', 1450000, 2),
(N'Hoa cưới 07', 'hoacuoi_07.jpg', 1500000, 2),
(N'Hoa cưới 08', 'hoacuoi_08.jpg', 1550000, 2),
(N'Hoa cưới 09', 'hoacuoi_09.jpeg', 1600000, 2),
(N'Hoa cưới 10', 'hoacuoi_10.jpeg', 1650000, 2),
(N'Hoa cưới 11', 'hoacuoi_11.jpeg', 1700000, 2),
(N'Hoa cưới 12', 'hoacuoi_12.jpg', 1750000, 2);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Kệ chúc mừng 01', 'kechucmung_01.webp', 1500000, 6),
(N'Kệ chúc mừng 02', 'kechucmung_02.webp', 1550000, 6),
(N'Kệ chúc mừng 03', 'kechucmung_03.webp', 1600000, 6),
(N'Kệ chúc mừng 04', 'kechucmung_04.webp', 1650000, 6),
(N'Kệ chúc mừng 05', 'kechucmung_05.webp', 1700000, 6),
(N'Kệ chúc mừng 06', 'kechucmung_06.webp', 1750000, 6),
(N'Kệ chúc mừng 07', 'kechucmung_07.webp', 1800000, 6),
(N'Kệ chúc mừng 08', 'kechucmung_08.webp', 1850000, 6),
(N'Kệ chúc mừng 09', 'kechucmung_09.webp', 1900000, 6),
(N'Kệ chúc mừng 10', 'kechucmung_10.webp', 1950000, 6);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Kệ chia buồn 01', 'kechiabuon_01.webp', 1400000, 7),
(N'Kệ chia buồn 02', 'kechiabuon_02.webp', 1450000, 7),
(N'Kệ chia buồn 03', 'kechiabuon_03.webp', 1500000, 7),
(N'Kệ chia buồn 04', 'kechiabuon_04.webp', 1550000, 7),
(N'Kệ chia buồn 05', 'kechiabuon_05.webp', 1600000, 7),
(N'Kệ chia buồn 06', 'kechiabuon_06.webp', 1650000, 7),
(N'Kệ chia buồn 07', 'kechiabuon_07.webp', 1700000, 7),
(N'Kệ chia buồn 08', 'kechiabuon_08.webp', 1750000, 7),
(N'Kệ chia buồn 09', 'kechiabuon_09.webp', 1800000, 7),
(N'Kệ chia buồn 10', 'kechiabuon_10.webp', 1850000, 7);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Giỏ trái cây 01', 'giohoatraicay_01.jpg', 900000, 8),
(N'Giỏ trái cây 02', 'giohoatraicay_02.jpg', 950000, 8),
(N'Giỏ trái cây 03', 'giohoatraicay_03.jpg', 1000000, 8),
(N'Giỏ trái cây 04', 'giohoatraicay_04.jpg', 1050000, 8),
(N'Giỏ trái cây 05', 'giohoatraicay_05.jpg', 1100000, 8);
GO

INSERT INTO Products (Name, Image, CostPrice, CategoryId) VALUES
(N'Hoa để bàn 01', 'hoadeban_01.jpg', 300000, 9),
(N'Hoa để bàn 02', 'hoadeban_02.jpg', 320000, 9),
(N'Hoa để bàn 03', 'hoadeban_03.jpg', 340000, 9),
(N'Hoa để bàn 04', 'hoadeban_04.jpg', 360000, 9),
(N'Hoa để bàn 05', 'hoadeban_05.jpg', 380000, 9);
GO

-- Password: 123 --
INSERT INTO Accounts (Username, Password, Fullname, Email, Photo, Address, Phone) VALUES
('derek', '$2a$10$WYh9nMcwPW2wz13wdj3U4OtO0Xgrctfqp/cdR.uWj/teOxMLIfihG', N'Derek Chauvin', 'racistcop@gmail.com', 'racist-cop-lol.jpg', N'58/10 Tân Lập 1, Hiệp Phú, Quận 9, Tp. Hồ Chí Minh', '0559104706'),
('james', '$2a$10$WYh9nMcwPW2wz13wdj3U4OtO0Xgrctfqp/cdR.uWj/teOxMLIfihG', N'James Floyd', 'whitefloydcanbreathe@gmail.com', 'james_floyd.jpg', N'112 Củ Chi, Vĩnh Hải, Nha Trang, Khánh Hoà', '0706164448'),
('george', '$2a$10$WYh9nMcwPW2wz13wdj3U4OtO0Xgrctfqp/cdR.uWj/teOxMLIfihG', N'George Floyd', 'nvuongkhoinguyen@gmail.com', 'george_floyd.png', N'38th Street and Chicago Avenue in Minneapolis, Minnesota.', '0706164448');
GO

-- stack 3 roles to gain full controlls --
INSERT INTO Roles (name, Fullname) values
('ROLE_ADMIN', N'Quản trị viên'), -- full das1oard controlls --
('ROLE_MANAGER', N'Quản lý'), -- staff only order das1oard --
('ROLE_USER', N'Người dùng'); -- only access account cart and account order --
GO

INSERT INTO AccountRoles (Username, RoleId) VALUES
('derek', 1),
('derek', 2),
('derek', 3),
('james', 2),
('james', 3),
('george', 3);
-- PENDING / PAID / CONFIRM / PROCESSING / SHIPPING / DELIVERED / CANCELLED / REFUND --
GO
