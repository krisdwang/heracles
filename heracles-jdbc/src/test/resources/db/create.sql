CREATE DATABASE jdbc1_master;
CREATE DATABASE jdbc1_slave1;
CREATE DATABASE jdbc1_slave2;

CREATE DATABASE jdbc2_master;
CREATE DATABASE jdbc2_slave1;
CREATE DATABASE jdbc2_slave2;

#group 1
USE jdbc1_master;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

USE jdbc1_slave1;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

USE jdbc1_slave2;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

#group 2
USE jdbc2_master;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

USE jdbc2_slave1;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

USE jdbc2_slave2;
CREATE TABLE `cust_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `cust` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_0` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_order_1` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;