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
    noteit_log_desc TEXT
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
    noteit_2fa_flag INT, -- 1 - 2fa activated, 0 2fa off
    noteit_newuser_flag INT -- 1 - enable new account creation, -1 - disable new account creation
);
INSERT INTO NOTEIT_HEALTH (noteit_database_version,noteit_mailserver_host,
noteit_mailserver_port,noteit_mailserver_username,noteit_mailserver_password,noteit_adminpanel_password,noteit_mailsend_flag
,noteit_2fa_flag,noteit_newuser_flag)
VALUES ('101','smtp.gmail.com','587','main.tes.instruments@gmail.com','bqcyaizlzgwyahxn','',0,0,1);
CREATE TABLE NOTEIT_USER -- table for storing application users
(
    noteit_user_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_name VARCHAR(100),
    noteit_user_email VARCHAR(200), -- email is a login to application
    noteit_user_surname VARCHAR(300),
    noteit_user_role VARCHAR(10), -- SUPERUSER, USER

    noteit_user_password VARCHAR(300), -- hashed password
    noteit_user_active INT, -- 0 - inactive, 1 - active
    noteit_user_email_confirmed INT, -- 0 - email not confirmed, 1 - email confirmed
    noteit_user_hash_code VARCHAR(250),
    noteit_user_dayofcreation TIMESTAMP
)AUTO_INCREMENT = 1000000;
CREATE TABLE NOTEIT_ACCCONFIRM -- table for storing confirmation codes for accounts
(
    noteit_accconfirm_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_accconfirm_code INT,

    CONSTRAINT fk_noteaccc1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_TODO -- table for storing to-do objects
(
    noteit_todo_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_todo_time TIMESTAMP,
    noteit_todo_deadline TIMESTAMP,
    noteit_todo_desc TEXT,
    noteit_todo_state INT, -- 1 - to-do done, 0 - to-do started, -1 - fresh to-do

    CONSTRAINT fk_todo1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);
CREATE TABLE NOTEIT_WALL -- table for storing wall data
(
    noteit_wall_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_wall_name VARCHAR(100),
    noteit_wall_members VARCHAR(200), -- data stored with user ids and commas

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
    noteit_object_time TIMESTAMP,
    noteit_object_title VARCHAR(300),
    noteit_object_rawtext TEXT,
    noteit_object_blob BLOB,

    CONSTRAINT fk_object1 FOREIGN KEY (noteit_vault_id) REFERENCES NOTEIT_VAULT (noteid_vault_id)
);
CREATE TABLE NOTEIT_MAIL_ARCHIVE -- table for storing mail archive
(
    noteit_mail_archive_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_mail_emailto VARCHAR(200),
    noteit_mail_time TIMESTAMP,
    noteit_mail_content TEXT
);
CREATE TABLE NOTEIT_DIARY -- table for storing diary elements
(
    noteit_diary_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_diary_timestamp TIMESTAMP,
    noteit_diary_content TEXT,
    noteit_diary_moodtracker INT,
    noteit_diary_sleeptime INT,
    noteit_diary_stresstracker INT,
    noteit_diary_quoteoftheday TEXT,

    CONSTRAINT fk_diary1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
);

CREATE TABLE NOTEIT_WELCOMENOTES -- table for storing welcome notes
(
    noteit_welcomenotes_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_welcomenotes_text TEXT
);

CREATE TABLE BLOB_ARCHIVE -- table for storing removed objects blob
(
    noteit_blob_archive_id INT AUTO_INCREMENT PRIMARY KEY,
    noteit_user_id INT,
    noteit_blob_category VARCHAR(100),
    noteit_blob LONGBLOB
);
-- creating qoutes for login screen
INSERT INTO NOTEIT_WELCOMENOTES (noteit_welcomenotes_text) VALUES('You didn’t expect these notes to turn into my therapy session, did you?');
INSERT INTO NOTEIT_WELCOMENOTES (noteit_welcomenotes_text) VALUES('Gift yourself with a journal for your own notes.');

-- account creation for tests
INSERT INTO NOTEIT_USER (noteit_user_name,noteit_user_surname,noteit_user_email,noteit_user_role,noteit_user_password,noteit_user_active,noteit_user_email_confirmed,noteit_user_hash_code,noteit_user_dayofcreation)
VALUES ('Admin','Admin','kubawawak@gmail.com','SUPERUSER','d7b7f6c245586ebf19960b22713ea7d7',1,1,'asdfhbsaasj',null);