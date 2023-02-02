/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.mailconnector.MailConnector;
import com.jakubwawak.mailconnector.MailTemplates;
import com.jakubwawak.maintanance.NoteIT_Logger;
import com.jakubwawak.maintanance.NoteIT_User;
import com.jakubwawak.maintanance.Password_Validator;
import com.jakubwawak.maintanance.RandomString;
import com.jakubwawak.noteit.NoteitApplication;
import org.springframework.mail.MailSender;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

/**
 * Object for maintaining noteit user
 */
public class Database_NoteITUser {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_NoteITUser(Database_Connector database){
        this.database = database;
    }


    /**
     * Function for creating user on database
     * @param name
     * @param surname
     * @param email
     * @param role
     * @return -3 - mail already in the database, -2 - error creating user,
     * -1 - user created but mail failed to send, 0 - error creating user, 1 - account created
     */
    public int createuser(String name,String surname,String email,String role,String password){
        if ( checkemail(email) == 0){

            // email not in the database - create account
            String query = "INSERT INTO NOTEIT_USER (noteit_user_name,noteit_user_surname" +
                    ",noteit_user_email,noteit_user_password,noteit_user_role,noteit_user_active,noteit_user_email_confirmed) VALUES" +
                    "(?,?,?,?,?,?,?);";
            try{
                Password_Validator pv = new Password_Validator(password);
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setString(1,name);
                ppst.setString(2,surname);
                ppst.setString(3,email);
                ppst.setString(4,pv.hash());
                ppst.setString(5,"USER");
                ppst.setInt(6,1);
                ppst.setInt(7,0);
                ppst.execute();
                // here user already created
                int noteit_user_id = checkuserid(email);
                if ( noteit_user_id  > 0 ){
                    // send email, create configuration
                    createuserconfiguration(noteit_user_id); // create configuration
                    // sending email
                    MailTemplates template = new MailTemplates();
                    template.create_newaacount_template(noteit_user_id,createuseractivationcode(noteit_user_id)); // creating template
                    MailConnector mc = new MailConnector();
                    int mail = mc.send_email(template.emailobj);
                    NoteitApplication.log.add("USERCREATED","New user "+email+" created!");
                    if ( mail == 1 ){
                        return 1;
                    }
                    return -1;
                }
                return 0;
            }catch(Exception e){
                NoteitApplication.log.add("USERCREATION-FAILED","Failed to create user "+email+" ("+e.toString()+")");
                return -2;
            }
        }
        return -3;
    }

    /**
     * Function for logging user
     * @param email
     * @param password
     * @return 2 - user logged 2fa triggered, 1 - user login successfully, 0 - user not found, -1 - password error,
     *   -2 - user not active, -3 - user email confirmed logged successfully, -4 - database error
     */
    public int login_user(String email,String password){
        if ( getuserid_byemail(email) == 0 ){
            // user not found
            return 0;
        }
        else{
            // email in database

            String query = "SELECT * FROM NOTEIT_USER WHERE noteit_user_email = ? and noteit_user_password = ?;";
            try{
                Password_Validator pv = new Password_Validator(password);
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setString(1,email);
                ppst.setString(2,pv.hash());
                ResultSet rs = ppst.executeQuery();
                if ( rs.next() ){
                    if ( rs.getInt("noteit_user_active") == 1 ){
                        if ( rs.getInt("noteit_user_email_confirmed") == 1 ){
                            if ( database.check_2fa_flag() == 1 ){
                                // send 2fa auth
                                String code = create2facode(rs.getInt("noteit_user_id"));
                                MailTemplates mt = new MailTemplates();
                                mt.create_2fa_template(rs.getInt("noteit_user_id"),code);
                                MailConnector mc = new MailConnector();
                                mc.send_email(mt.emailobj);
                                return 2;
                            }
                            else{
                                NoteitApplication.logged = new NoteIT_User(rs);
                                return 1;
                            }
                        }
                        else{
                            // user email not confirmed by logged successfully
                            return -3;
                        }
                    }
                    return -2;
                }
                return -1;
            }catch(Exception e){
                NoteitApplication.log.add("LOGIN-FAILED","Failed to login user "+email+" ("+e.toString()+")");
                return -4;
            }
        }
    }

    /**
     * Function for creating standard user configuration on database
     * @param noteit_user_id
     * @return 1 - configuration created, -1 - database error
     */
    public int createuserconfiguration(int noteit_user_id){
        String query = "INSERT INTO NOTEIT_USER_CONFIGURATION (noteit_user_id, noteit_configuration1, noteit_configuration2" +
                ", noteit_configuration3, noteit_configuration4) VALUES (?,?,?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.setString(2,"");
            ppst.setString(3,"");
            ppst.setString(4,"");
            ppst.setString(5,"");
            ppst.execute();
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("CONFIGCREAT-FAILED","Failed to create user configuration ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for getting user email by given ID
     * @param noteit_user_id
     * @return return email data, if user not found returns ""
     */
    public String getuseremail(int noteit_user_id){
        String query = "SELECT noteit_user_email FROM NOTEIT_USER WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                NoteitApplication.log.add("GETUSERMAIL","Found user with ID: "+noteit_user_id+" and "+rs.getString("noteit_user_email"));
                return rs.getString("noteit_user_email");
            }
        }catch(SQLException e){
            NoteitApplication.log.add("GETUSERMAIL-FAILED","Failed to get user mail ("+e.toString()+")");
        }
        return "";
    }

    /**
     * Function for getting user ID by given email
     * @param email
     * @return return noteit_user_id or -1 if database error
     */
    public int getuserid_byemail(String email){
        String query = "SELECT noteit_user_id FROM NOTEIT_USER WHERE noteit_user_email = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,email);
            ResultSet rs  = ppst.executeQuery();
            if ( rs.next() ){
                NoteitApplication.log.add("GETIDBYEMAIL","Found ID for email: "+email+" ID: "+rs.getInt("noteit_user_id"));
                return rs.getInt("noteit_user_id");
            }
            return 0;
        }catch(SQLException e){
            NoteitApplication.log.add("GETIDBYEMAIL-FAILED","Failed to get user ID ("+e.toString()+")");
            return -1;
        }
    }


    /**
     * Function for creating user activation code
     * @param noteit_user_id
     * @return activation code , -1 - database error
     */
    public int createuseractivationcode(int noteit_user_id){
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
        String query = "INSERT INTO NOTEIT_ACCCONFIRM (noteit_user_id,noteit_accconfirm_code) VALUES (?,?);";
        try{
            Random rand = new Random();
            int code = rand.nextInt(1000);
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.setInt(2,code);
            ppst.execute();
            return code;
        }catch(SQLException e){
            NoteitApplication.log.add("ACCCODE-FAILED","Failed to create user code ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for creating 2fa code
     * @param noteit_user_id
     * @return 2fa code for given user, if database error returns "" - blank string
     */
    public String create2facode(int noteit_user_id){
        /**
         * CREATE TABLE NOTEIT_2FA -- table for storing 2fa codes
         * (
         *     noteit_2fa_id INT AUTO_INCREMENT PRIMARY KEY,
         *     noteit_user_id INT,
         *     noteit_2fa_time TIMESTAMP,
         *     noteit_2fa_code VARCHAR(100),
         *     noteit_2fa_active INT,
         *
         *     CONSTRAINT fk_2fa1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
         * );
         */
        remove2faforuser(noteit_user_id); // removing old 2fa codes
        String query = "INSERT INTO NOTEIT_2FA (noteit_user_id, noteit_2fa_time,noteit_2fa_code,noteit_2fa_active) VALUES" +
                " (?,?,?,?);";
        try{
            RandomString randomStringEngine = new RandomString(6);
            String code = randomStringEngine.buf;
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.setObject(2, LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
            ppst.setString(3,code);
            ppst.setInt(4,1);
            ppst.execute();
            NoteitApplication.log.add("2FACREATE","Created "+code+" for ID: "+noteit_user_id);
            return code;
        }catch(SQLException e){
            NoteitApplication.log.add("2FACREATE-FAILED","Failed to create 2fa code ("+e.toString()+")");
            return "";
        }
    }

    /**
     * Function for checking email data
     * @param email
     * @return 0 - email exists on database, 1 - email didn't exist, -1 - database error
     */
    public int checkemail(String email){
        String query = "SELECT noteit_user_id from NOTEIT_USER WHERE noteit_user_email = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,email);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return 1;
            }
            return 0;
        }catch(SQLException e){
            NoteitApplication.log.add("CHECKMAIL","Check mail function failed ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking user id number
     * @param email
     * @return noteit_user_id value from database, 0 - user with given email not found, -1 - database error
     */
    public int checkuserid(String email){
        String query = "SELECT noteit_user_id from NOTEIT_USER WHERE noteit_user_email = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,email);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("noteit_user_id");
            }
            return 0;
        }catch(SQLException e){
            NoteitApplication.log.add("CHECKMAIL","Check mail function failed ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for checking if user is activated
     * @param noteit_user_id
     * @return email confirmation status, -1 - user not found, -1 -
     */
    public int checkuseremailconfirmed(int noteit_user_id){
        String query = "SELECT noteit_user_email_confirmed FROM NOTEIT_USER WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                return rs.getInt("noteit_user_email_confirmed");
            }
            return -1;
        }catch(SQLException e){
            NoteitApplication.log.add("CHECKEMAILCONFIRMED-FAILED","Failed to check user email confirmation ("+e.toString()+")");
            return -2;
        }
    }

    /**
     * Function for setting user active
     * @param noteit_user_id
     * @return 1 - user set to active, -1 database error
     */
    public int setuseractive(int noteit_user_id){
        String query = "UPDATE NOTEIT_USER SET noteit_user_active = 1 WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.execute();
            NoteitApplication.log.add("USERUNLOCK","Unlocked user "+database);
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("USERUNLOCK-FAILED","Failed to unlock user ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting user inactive
     * @param noteit_user_id
     * @return 1 - user set to inactive, -1 database error
     */
    public int setuserinactive(int noteit_user_id){
        String query = "UPDATE NOTEIT_USER SET noteit_user_active = 0 WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.execute();
            NoteitApplication.log.add("USERLOCK","Locked user "+database);
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("USERLOCK-FAILED","Failed to lock user ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting user role
     * @param noteit_user_id
     * @param mode - 1 - ADMIN - 0 - USER
     * @return 1 - user role updated, -1 database error
     */
    public int setuserrole(int noteit_user_id,int mode){
        String query = "UPDATE NOTEIT_USER SET noteit_user_role = ? WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            if ( mode == 1)
                ppst.setString(1,"ADMIN");
            if ( mode == 2)
                ppst.setString(1,"USER");
            else
                ppst.setString(1,"USER");
            ppst.setInt(2,noteit_user_id);
            ppst.execute();
            NoteitApplication.log.add("SETROLE","User role set to: (mode:"+mode+")");
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("SETROLE-FAILED","Failed to set user role ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for removing old 2fa codes from user (setting as inactive)
     * @param noteit_user_id
     * @return 1 - removed with success, -1 - database error
     */
    public int remove2faforuser(int noteit_user_id){
        String query = "UPDATE NOTEIT_2FA SET noteit_2fa_active = 0 WHERE noteit_user_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.execute();
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("2FAREMOVE-FAILED","Failed to remove 2fa codes ("+e.toString()+")");
            return -1;
        }
    }

}
