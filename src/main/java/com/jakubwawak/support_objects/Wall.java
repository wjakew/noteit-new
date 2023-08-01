/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.support_objects;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.maintanance.NoteIT_User;
import com.jakubwawak.noteit.NoteitApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for storing wall information
 */
public class Wall {

    public int noteit_wall_id;
    public int noteit_user_id; // owner
    public NoteIT_User owner;
    public String noteit_wall_name;
    public ArrayList<NoteIT_User> noteit_wall_members;

    public boolean error;

    /**
     * Constructor
     * @param to_add
     */
    public Wall(ResultSet to_add){
        try{
            Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
            noteit_wall_id = to_add.getInt("noteit_wall_id");
            noteit_user_id = to_add.getInt("noteit_user_id");
            noteit_wall_name = to_add.getString("noteit_wall_name");
            owner = dniu.get_user(noteit_user_id);
            load_members(to_add);
            error = false;
        }catch(SQLException ex){
            error = true;
        }
    }

    /**
     * Function for loading wall members
     * @param to_add
     */
    void load_members(ResultSet to_add){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        noteit_wall_members = new ArrayList<>();
        try {
            String members_data = to_add.getString("noteit_wall_members ");
            for(String member_id : members_data.split(",")){
                try{
                    int noteit_user_id = Integer.parseInt(member_id);
                    noteit_wall_members.add(dniu.get_user(noteit_user_id));
                }catch(NumberFormatException ex){}
            }
        }catch(Exception ex){error = true;}
    }

    public String getNoteit_wall_name(){
        return noteit_wall_name;
    }
}
