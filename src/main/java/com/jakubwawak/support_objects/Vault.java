/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.support_objects;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for storing Vault object data from NOTEIT_VAULT table
 */
public class Vault implements Serializable {

    /**
     * CREATE TABLE NOTEIT_VAULT -- table for storing note vaults
     * (
     *     noteid_vault_id INT AUTO_INCREMENT PRIMARY KEY,
     *     noteit_user_id INT,
     *     noteit_vault_name VARCHAR(300),
     *     noteit_vault_members VARCHAR(300),
     *
     *     CONSTRAINT fk_vault1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id) ON DELETE CASCADE
     * )AUTO_INCREMENT = 1000;
     */

    public int noteit_vault_id;
    public String noteit_vault_name;

    public ArrayList<String> noteit_vault_members;
    public int noteit_user_id;
    public boolean error;

    public Vault(){
        noteit_vault_id = -1;
        noteit_vault_name = "";
        noteit_vault_members = new ArrayList<>();
    }

    /**
     * Constructor
     * @param to_add - database object
     */
    public Vault(ResultSet to_add){
        try{
            Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
            noteit_vault_members = new ArrayList<>();
            noteit_vault_id = to_add.getInt("noteid_vault_id");
            noteit_vault_name = to_add.getString("noteit_vault_name");
            this.noteit_user_id = to_add.getInt("noteit_user_id");
            for(String element : to_add.getString("noteit_vault_members").split(",")){
                try{
                    int noteit_user_id = Integer.parseInt(element);
                    if ( noteit_user_id > 0 ){
                        noteit_vault_members.add(dniu.getuseremail(noteit_user_id));
                    }
                }catch(NumberFormatException e){}
            }
        }catch(SQLException e){
            NoteitApplication.log.add("VAULT-LOAD-FAILED","Failed to get Vault object ("+e.toString()+")");
            error = true;
        }
    }

    /**
     * Constructor with noteit_vault_support
     * @param noteit_vault_id
     */
    public Vault(int noteit_vault_id){
        this.noteit_vault_id = noteit_vault_id;
        String query = "SELECT * FROM NOTEIT_VAULT where noteid_vault_id = ?;";
        try{
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                new Vault(rs);
            }
            new Vault();
            error = false;
        }catch(SQLException e){
            NoteitApplication.log.add("VAULT-LOAD-FAILED","Failed to get Vault object ("+e.toString()+")");
            error = true;
        }
    }

    /**
     * Function for getting owner of vault
     * @return String
     */
    public String get_owner(){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        return dniu.getuseremail(noteit_user_id);
    }

    /**
     * Function for checking if logged user is owner of object
     * @return boolean
     */
    public boolean is_logged_owner(){
        return NoteitApplication.logged.getNoteit_user_id() == noteit_user_id;
    }

    public int getNoteit_vault_id(){return noteit_vault_id;}

    public String getNoteit_vault_name(){return noteit_vault_name;}

    /**
     * Function for returning naming glance for web components
     * @return String
     */
    public String get_glance(){
        return noteit_vault_id+":"+noteit_vault_name;
    }
}
