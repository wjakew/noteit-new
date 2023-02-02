-- makefile for mysql database for noteit application
-- by Jakub Wawak 2023
-- kubawawak@gmail.com
-- all rights reserved

-- CREATING ENVIRONMENT
DROP DATABASE IF EXISTS noteit_database;
CREATE DATABASE IF NOT EXISTS noteit_database;
USE noteit_database;
SET SQL_MODE = 'ALLOW_INVALID_DATES';
-- CREATING TABLES
CREATE TABLE NOTEIT_APPLOG -- table for storing app log messages
(
    noteit_log_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_log_time TIMESTAMP,
    noteit_2fa_code VARCHAR(100),
    noteit_log_code VARCHAR(200),
    noteit_log_desc VARCHAR(400)
);
CREATE TABLE NOTEIT_HEALTH -- table for storing application data
(
	noteit_database_version VARCHAR(100),
    noteit_mailserver_host VARCHAR(100),
    noteit_mailserver_port VARCHAR(100),
    noteit_mailserver_username VARCHAR(100),
    noteit_mailserver_password VARCHAR(200),
    noteit_adminpanel_password VARCHAR(200),
    noteit_mailsend_flag INT, -- 1 - send email, 0 - not set emails,
    noteit_2fa_flag INT -- 1 - 2fa activated, 0 2fa off
);
INSERT INTO NOTEIT_HEALTH (noteit_database_version,noteit_mailserver_host,
noteit_mailserver_port,noteit_mailserver_username,noteit_mailserver_password,noteit_adminpanel_password,noteit_mailsend_flag
,noteit_2fa_flag)
VALUES ('100','smtp.gmail.com','587','main.tes.instruments@gmail.com','kufiynyvzjdtwjar','',0,0);
CREATE TABLE NOTEIT_USER -- table for storing application users
(
    noteit_user_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_name VARCHAR(100),
    noteit_user_email VARCHAR(200), -- email is a login to application
    noteit_user_surname VARCHAR(300),
    noteit_user_role VARCHAR(10), -- SUPERUSER, USER

    noteit_user_password VARCHAR(300), -- hashed password
    noteit_user_active INT, -- 0 - inactive, 1 - active
    noteit_user_email_confirmed INT -- 0 - email not confirmed, 1 - email confirmed
)AUTO_INCREMENT = 1000000;
CREATE TABLE NOTEIT_ACCCONFIRM -- table for storing confirmation codes for accounts
(
    noteit_accconfirm_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_accconfirm_code INT,

    CONSTRAINT fk_noteaccc1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_TODO -- table for storing todo objects
(
    noteit_todo_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_todo_time TIMESTAMP,
    noteit_todo_deadline TIMESTAMP,
    noteit_todo_desc TEXT,
    noteit_todo_state INT,

    CONSTRAINT fk_todo1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_WALL -- table for storing wall data
(
    noteit_wall_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_wall_name VARCHAR(100),
    noteit_Valut_members VARCHAR(200),

    CONSTRAINT fk_wall1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_MESSAGE -- table for storing messages from wall
(
    noteit_message_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_wall_id INT,
    noteit_message_time TIMESTAMP,
    noteit_message_text TEXT,

    CONSTRAINT fk_noteitm1 FOREIGN KEY (noteit_user_id) REFERENCES  NOTEIT_USER (noteit_user_id),
    CONSTRAINT fk_noteitm2 FOREIGN KEY (noteit_wall_id) REFERENCES NOTEIT_WALL(noteit_wall_id)
);
CREATE TABLE NOTEIT_VAULT -- table for storing note vaults
(
    noteid_vault_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_vault_name VARCHAR(300),
    noteit_vault_members VARCHAR(300),

    CONSTRAINT fk_vault1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id) ON DELETE CASCADE
)AUTO_INCREMENT = 1000;
CREATE TABLE NOTEIT_USER_CONFIGURATION -- table for storing user configuration
(
    noteit_user_configuration_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_configuration1 VARCHAR(100),
    noteit_configuration2 VARCHAR(100),
    noteit_configuration3 VARCHAR(100),
    noteit_configuration4 VARCHAR(100),

    CONSTRAINT fk_config1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_USER_LOG -- table for storing user log
(
    noteit_user_log_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_user_log_time TIMESTAMP,
    noteit_user_log_code VARCHAR(100),
    noteit_user_log_desc VARCHAR(300),

    CONSTRAINT fk_userlog1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_2FA -- table for storing 2fa codes
(
    noteit_2fa_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_2fa_time TIMESTAMP,
    noteit_2fa_code VARCHAR(100),
    noteit_2fa_active INT,

    CONSTRAINT fk_2fa1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);

CREATE TABLE NOTEIT_OBJECT -- table for storing notes
(
    noteit_object_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_vault_id INT,
    noteit_object_name TIMESTAMP,
    noteit_object_title VARCHAR(300),
    noteit_object_blob BLOB,

    CONSTRAINT fk_object1 FOREIGN KEY (noteit_vault_id) REFERENCES NOTEIT_VAULT (noteid_vault_id)
);