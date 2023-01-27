/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.maintanance.Configuration;
import com.jakubwawak.noteit.NoteitApplication;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Object for connecting to database
 */
public class Database_Connector {

    Configuration config;
    public Connection con;
    Statement stmt;

    public boolean connected, error;

    /**
     * Constructor
     * @param config
     */
    public Database_Connector(Configuration config){
        this.config = config;
        connected = false;
    }

    /**
     * Function for connecting to database
     */
    public void connect(){
        try{
            con = DriverManager.getConnection("jdbc:mysql://"+config.database_ip+":3306/"+config.database_name,config.database_user,config.database_password);
            stmt = con.createStatement();
            connected = true;
        }catch(Exception e) {
            System.out.println("Failed to connect to database (" + e.toString() + ")");
            error = true;
            connected = false;
        }
    }

    /**
     * Function for creating user log entry
     * @param noteit_user_id
     * @param log_code
     * @param log_desc
     * @return 1 - log added, -1 - failed to add log
     */
    public int add_user_log(int noteit_user_id, String log_code,String log_desc){
        String query = "INSERT INTO NOTEIT_USER_LOG (noteit_user_id, noteit_user_log_time," +
                "noteit_user_log_code,noteit_user_log_desc) VALUES (?,?,?,?);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.setObject(2, LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
            ppst.setString(3,log_code);
            ppst.setString(4,log_desc);
            ppst.execute();
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("USERLOG-FAILED","Failed to add user log data ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting mail data from database
     * @return ArrayList collection of mail server information
     */
    public ArrayList<String> get_mail_data(){
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT * FROM NOTEIT_HEALTH";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                data.add(rs.getString("noteit_mailserver_host"));
                data.add(rs.getString("noteit_mailserver_port"));
                data.add(rs.getString("noteit_mailserver_username"));
                data.add(rs.getString("noteit_mailserver_password"));
            }
        }catch(SQLException e){
            NoteitApplication.log.add("GETMAILDATA-FAILED","Failed to get mail data ("+e.toString()+")");
        }
        return data;
    }

    /**
     * Function for adding log entry to database
     * @param log_code
     * @param log_desc
     * @return 1 - log added, -1 - falied to add log
     */
    public int add_log(String log_code,String log_desc,String code_2fa){
        /**
         * CREATE TABLE NOTEIT_APPLOG -- table for storing app log messages
         * (
         *     noteit_log_id INT AUTO_INCREMENT PRIMARY KEY,
         *     noteit_2fa_code VARCHAR(100),
         *     noteit_log_code VARCHAR(200),
         *     noteit_log_desc VARCHAR(400)
         * );
         */
        String query = "INSERT INTO NOTEIT_APPLOG (noteit_2fa_code,noteit_log_time,noteit_log_code,noteit_log_desc) " +
                "VALUES (?,?,?,?);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            if ( code_2fa.equals("")){
                ppst.setString(1,"blank");
            }
            else{
                ppst.setString(1,code_2fa);
            }
            ppst.setObject(2,LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
            ppst.setString(3,log_code);
            ppst.setString(4,log_desc);
            ppst.execute();
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("DATABASE-LOG-FAILED","Failed to add log" +
                    " to database ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for activating usera account
     * @param code
     * @return -1 - database error, -2 - code not found,
     */
    public int activate_user_account(int code){
        /**
         * CREATE TABLE NOTEIT_ACCCONFIRM -- table for storing confirmation codes for accounts
         * (
         *     noteit_accconfirm_id INT AUTO_INCREMENT PRIMARY KEY,
         *     noteit_user_id INT,
         *     noteit_accconfirm_code INT,
         *
         *     CONSTRAINT fk_noteaccc1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
         * );
         */
        String query = "SELECT * FROM NOTEIT_ACCCONFIRM WHERE noteit_accconfirm_code = ?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,code);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){

                // code found
                int nami_user_id = rs.getInt("noteit_user_id");

                // set user account as active

                // remove code from database

            }
            return -2;
        }catch(SQLException e){
            NoteitApplication.log.add("AACCOUNT-FAILED","Failed to activate user account" +
                    " ("+e.toString()+")");
            return -1;
        }
    }
}
