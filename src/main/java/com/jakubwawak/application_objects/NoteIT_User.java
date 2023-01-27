/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.application_objects;

import com.jakubwawak.noteit.NoteitApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Function for representing application user
 */
public class NoteIT_User {

    public int noteit_user_id;
    public String noteit_user_name;
    public String noteit_user_email;
    public String noteit_user_surname;
    public String noteit_user_role;
    public int noteit_user_active;
    public int noteit_user_email_confirmed;

    /**
     * Constructor
     */
    public NoteIT_User(int noteit_user_id){
        this.noteit_user_id = noteit_user_id;
        try{
            String query = "SELECT * FROM NOTEIT_USER WHERE noteit_user_id = ?;";
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                noteit_user_name = rs.getString("noteit_user_name");
                noteit_user_email = rs.getString("noteit_user_email");
                noteit_user_surname = rs.getString("noteit_user_surname");
                noteit_user_role = rs.getString("noteit_user_role");
                noteit_user_active = rs.getInt("noteit_user_active");
                noteit_user_email_confirmed = rs.getInt("noteit_user_email_confirmed");
            }
        }catch(SQLException e){
            NoteitApplication.log.add("USERLOAD-FAILED","Failed to load user data ("+e.toString()+")");
        }
    }
}

