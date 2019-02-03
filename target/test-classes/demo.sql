--This script is used for unit test cases
DROP TABLE IF EXISTS User;

CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL, UserName VARCHAR(30) NOT NULL, EmailAddress VARCHAR(30) NOT NULL);

INSERT INTO User (UserName, EmailAddress) VALUES ('esantamarina','esteban.santamarina2@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('revolutUser','userTesting@revolut.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('testing','testing@revolut.com');

DROP TABLE IF EXISTS Account;

CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,UserName VARCHAR(30),Balance DECIMAL(19,2),CurrencyCode VARCHAR(30));

INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('esantamarina','100','USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('revolutUser','200','USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('testing','200','USD');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('esantamarina','50','EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('revolutUser','0','EUR');
INSERT INTO Account (UserName,Balance,CurrencyCode) VALUES ('testing','200','EUR');

