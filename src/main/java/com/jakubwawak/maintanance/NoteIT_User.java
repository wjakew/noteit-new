/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.noteit.NoteitApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object for storing noteit user information
 */
public class NoteIT_User {

    private int noteit_user_id;
    public String noteit_user_name;
    private String noteit_user_email;
    private String noteit_user_surname;
    private String noteit_user_role;

    public boolean error;
    public int twofactor_flag;

    /**
     * Constructor
     * @param to_add
     */
    public NoteIT_User(ResultSet to_add){
        try{
            noteit_user_id = to_add.getInt("noteit_user_id");
            noteit_user_name = to_add.getString("noteit_user_name");
            noteit_user_email = to_add.getString("noteit_user_email");
            noteit_user_surname = to_add.getString("noteit_user_surname");
            noteit_user_role = to_add.getString("noteit_user_role");
            twofactor_flag = 0;
            error = false;
        }catch(SQLException e){
            error = true;
        }
    }

    /**
     * Constructor
     * @param noteit_user_id
     */
    public NoteIT_User(int noteit_user_id){
        String query = "SELECT * NOTEIT_USER WHERE noteit_user_id = ?;";
        this.noteit_user_id = noteit_user_id;
        try{
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1, noteit_user_id);
            ResultSet to_add = ppst.executeQuery();
            if ( to_add.next() ){
                noteit_user_name = to_add.getString("noteit_user_name");
                noteit_user_email = to_add.getString("noteit_user_email");
                noteit_user_surname = to_add.getString("noteit_user_surname");
                noteit_user_role = to_add.getString("noteit_user_role");
                twofactor_flag = 0;
                error = false;
            }
        }catch(SQLException e){
            error = true;
        }
    }

    /**
     * Getter for email
     * @return email field
     */
    public String getNoteit_user_email(){
        return noteit_user_email;
    }

    /**
     * Getter for noteit_user_id
     * @return user id field
     */
    public int getNoteit_user_id(){
        return noteit_user_id;
    }

}
