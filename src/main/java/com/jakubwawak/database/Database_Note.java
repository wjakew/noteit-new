/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for managing notes on database
 */
public class Database_Note {

    public Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Note(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for adding note
     * @param to_add
     * @return 1 - note added to database, -1 - database error
     */
    public int add_note(Note to_add){
        String query = "INSERT INTO NOTEIT_OBJECT (noteit_vault_id,noteit_object_time,noteit_object_title,noteit_object_rawtext," +
                "noteit_object_blob) VALUES (?,?,?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,to_add.noteit_vault_id);
            ppst.setObject(2,to_add.noteit_object_time);
            ppst.setString(3,to_add.noteit_object_title);
            ppst.setString(4,to_add.noteit_object_rawtext);
            ppst.setObject(5,to_add);
            ppst.execute();
            NoteitApplication.log.add("NOTE-ADD","Added new note to database! Added to vault ID:"+to_add.noteit_vault_id);
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("NOTE-ADD-FAILED","Failed to add note to database ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for updating note
     * @param to_update
     * @return 1 - note updated, -1 - database error
     */
    public int update_note(Note to_update){
        String query = "UPDATE NOTEIT_OBJECT SET noteit_object_title=?, noteit_object_rawtext=?, noteit_object_time = ?, noteit_object_blob=? where noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,to_update.noteit_object_title);
            ppst.setString(2,to_update.noteit_object_rawtext);
            ppst.setObject(3,to_update.noteit_object_time);
            ppst.setObject(4,to_update);
            ppst.setInt(5,to_update.noteit_object_id);
            ppst.execute();
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("NOTE-UPDATE-FAILED","Failed to update note to database ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for loading collection of all notes in vault
     * @param noteit_vault_id
     * @return
     */
    public ArrayList<Note> get_note_list(int noteit_vault_id){
        String query = "SELECT * FROM NOTEIT WHERE noteit_vault_id = ?;";
        ArrayList<Note> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(new Note(rs));
            }
        }catch(SQLException e){
            NoteitApplication.log.add("NOTEIT-LIST-FAILED","Failed to load note list ("+e.toString()+")");
        }
        return data;
    }
}
