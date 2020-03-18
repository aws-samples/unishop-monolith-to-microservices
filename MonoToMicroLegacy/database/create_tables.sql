/**
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

CREATE SCHEMA `unishop` DEFAULT CHARACTER SET UTF8MB4 ;

CREATE TABLE  `unishop`.`unicorns` (
-- general fields 
  	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  	`creation_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  	`last_modified_date` timestamp NULL,
  	`created_by_user_id` int(10) unsigned DEFAULT NULL,
  	`last_modified_by_user_id` int(10) unsigned DEFAULT NULL,
  	`active` tinyint(1) DEFAULT NULL,
--	model fields  
	`uuid` varchar(64) NOT NULL,
	`name` varchar(64) DEFAULT NULL,
	`description` varchar(256) DEFAULT NULL,  	
  	`price` decimal(6,2) unsigned DEFAULT NULL,
  	`image` varchar(256) DEFAULT NULL,
  	CONSTRAINT UnicornUnique UNIQUE (uuid),
  	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE  `unishop`.`unicorns_basket` (
-- general fields 
  	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  	`creation_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  	`last_modified_date` timestamp NULL,
  	`created_by_user_id` int(10) unsigned DEFAULT NULL,
  	`last_modified_by_user_id` int(10) unsigned DEFAULT NULL,
  	`active` tinyint(1) DEFAULT NULL,
--	model fields  
	`uuid` varchar(64) NOT NULL,
  	`unicornUuid` varchar(64) NOT NULL,		
  	CONSTRAINT UnicornUnique UNIQUE (uuid, unicornUuid),
  	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE  `unishop`.`unicorn_user` (
-- general fields 
  	`id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  	`creation_date` timestamp DEFAULT CURRENT_TIMESTAMP,
  	`last_modified_date` timestamp NULL,
  	`created_by_user_id` int(10) unsigned DEFAULT NULL,
  	`last_modified_by_user_id` int(10) unsigned DEFAULT NULL,
  	`active` tinyint(1) DEFAULT NULL,
--	model fields  
	`uuid` varchar(64) NOT NULL,
	`email` varchar(64) NOT NULL,
  	`first_name` varchar(64) DEFAULT NULL,
  	`last_name` varchar(64) DEFAULT NULL,
  	CONSTRAINT UserUnique UNIQUE (email),
  	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornFloat', 'Big Unicorn Float! Giant Glitter Unicorn Pool Floaty', 100, 'UnicornFloat');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornHipHop', 'Rainbow Hip Hop Unicorn With Sunglasses Kids Tshirt', 100, 'UnicornHipHop');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornPartyDress', 'Girls Unicorn Party Dress - Tutu Pastel Rainbow Princess Power!', 100, 'UnicornPartyDress');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornGlitter', 'Unicorn Glitter Backpack - Shop for Unique Unicorn Gifts for Girls!', 100, 'UnicornGlitter');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornBeddings', 'Rainbow Unicorn Bedding Set - The Perfect Kids or Adults Unicorn Duvet Set', 100, 'UnicornBeddings');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornPink', 'Pretty Pink Baby Unicorn Summer Party Dress', 100, 'UnicornPink');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornBackpack', 'Top Rated Classy Unicorn Backpack - Kawaii School Bag', 100, 'UnicornBackpack');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornBlanket', 'Superfun Bestselling Unicorn Hooded Blanket', 100, 'UnicornBlanket');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornCool', 'Cool Dabbing Unicorn Mens Hip-hop Shirts', 100, 'UnicornCool');
INSERT INTO `unishop`.`unicorns` (uuid, name, description, price, image) VALUES (UUID(),'UnicornFluffy', 'Stylish Fluffy Unicorn Slippers', 100, 'UnicornFluffy');
