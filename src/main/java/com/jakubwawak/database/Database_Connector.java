/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.maintanance.Configuration;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;

import javax.print.DocFlavor;
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
     * Function for loading noteit_newuser_flag value from database
     * @return noteit_newuser_flag value, -2 - database fragmentation error, -1 - database error
     */
    public int get_newusercreationflag(){
        String query = "SELECT noteit_newuser_flag FROM NOTEIT_HEALTH;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                return rs.getInt("noteit_newuser_flag");
            }
            return -2;
        }catch(SQLException ex){
            NoteitApplication.log.add("HEALTH-CR-FLAG-FAILED","Failed to load health creation user flag ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating value of noteit_newuser_flag
     * @param flag_value
     * @return 1 - value updated, -1 - database error
     */
    public int set_newusercreationflag(int flag_value){
        String query = "UPDATE NOTEIT_HEALTH SET noteit_newuser_flag = ?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,flag_value);
            ppst.execute();
            NoteitApplication.log.add("HEALTH-CR-FLAG-UPDATE","Updated user creation flag to: "+flag_value);
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("HEALTH-CR-FLAG-UPDATE-FAILED","Failed to update health creation user flag ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading log data
     * @return collection of StringElement object containing log data
     */
    public ArrayList<StringElement> get_application_log(){
        String query = "SELECT * FROM NOTEIT_APPLOG;";
        ArrayList<StringElement> data = new ArrayList<>();
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(new StringElement(rs.getObject("noteit_log_time",LocalDateTime.class).toString()
                        +": "+rs.getString("noteit_log_code")+" : "+rs.getString("noteit_log_desc")));
            }
            NoteitApplication.log.add("LOG-LOADER","Loaded "+data.size()+" lines of log!");
        }catch(SQLException ex){
            NoteitApplication.log.add("LOG-LOADER-FAILED","Failed to load log from server ("+ex.toString()+")");
        }

        return data;
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
                Database_NoteITUser dni = new Database_NoteITUser(this);
                dni.setuseractive(nami_user_id);

                // remove code from database

                query = "DELETE FROM NOTEIT_ACCCONFIRM WHERE noteit_acconfirm_code = ?;";
                ppst = con.prepareStatement(query);
                ppst.setInt(1,code);
                ppst.execute();
                NoteitApplication.log.add("AACCOUNT","Successfully activated user account!");
                return 1;
            }
            return -2;
        }catch(SQLException e){
            NoteitApplication.log.add("AACCOUNT-FAILED","Failed to activate user account" +
                    " ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking sendmail flag data
     * @return value of noteit_mailsend_flag from NOTEIT_HEALTH table
     */
    public int check_sendmail_flag(){
        String query = "SELECT noteit_mailsend_flag FROM NOTEIT_HEALTH;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("noteit_mailsend_flag");
            }
            return 0;
        }catch(SQLException e){
            NoteitApplication.log.add("MAILFLAG-FAILED","Failed to check send email flag ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting mail flag value
     * @param flag
     * @return 1 - value set, -1 if database error
     */
    public int set_sendmail_flag(int flag){
        String query = "UPDATE NOTEIT_HEALTH SET noteit_mailsend_flag = ?;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,flag);
            ppst.execute();
            NoteitApplication.log.add("MAILFLAG-SET","Flag set to "+flag+"!");
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("MAILFLAG-SET-FAILED","Failed to set flag ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking sendmail flag data
     * @return Integer
     */
    public int check_2fa_flag(){
        String query = "SELECT noteit_2fa_flag FROM NOTEIT_HEALTH;";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("noteit_2fa_flag");
            }
            return 0;
        }catch(SQLException e){
            NoteitApplication.log.add("MAILFLAG-FAILED","Failed to check send email flag ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting 2fa flag on database
     * @param mode
     * @return Integer
     */
    public int twofactor_settings(int mode){
        String query = "UPDATE NOTEIT_HEALTH SET noteit_2fa_flag = ?";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setInt(1,mode);
            ppst.execute();
            NoteitApplication.log.add("2FA-SETTING","Set 2FA flag to: "+mode);
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("2FA-SETTING-FAILED","Failed to set flag ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for saving mail on database
     * @param reciver
     * @param content
     * @return Integer
     */
    public int save_mail(String reciver, String content){
        /**
         * CREATE TABLE NOTEIT_MAIL_ARCHIVE -- table for storing mail archive
         * (
         *     noteit_mail_archive_id INT AUTO_INCREMENT PRIMARY KEY,
         *     noteit_mail_emailto VARCHAR(200),
         *     noteit_mail_time TIMESTAMP,
         *     noteit_mail_content TEXT
         * );
         */
        String query = "INSERT INTO NOTEIT_MAIL_ARCHIVE (noteit_mail_emailto,noteit_mail_time,noteit_mail_content) VALUES (?,?,?);";
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ppst.setString(1,reciver);
            ppst.setObject(2,LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
            ppst.setString(3,content);
            ppst.execute();
            NoteitApplication.log.add("MAIL-ARCHIVE","Added mail to archive! "+reciver);
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("MAIL-ARCHIVE-FAILED","Failed to add mail to archive ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading random login label
     * @return value from NOTEITWELCOMENOTES table
     */
    public String get_random_loginlabel(){
        String query = "SELECT * FROM NOTEIT_WELCOMENOTES;";
        ArrayList<String> data = new ArrayList<>();
        try{
            PreparedStatement ppst = con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while ( rs.next() ){
                data.add(rs.getString("noteit_welcomenotes_text"));
            }
            int index = (int)(Math.random() * data.size());
            return data.get(index);
        }catch(SQLException e){
            NoteitApplication.log.add("RN-LOGINLABEL-FAILED","Failed to get random login label ("+e.toString()+")");
            return "";
        }
    }
}
