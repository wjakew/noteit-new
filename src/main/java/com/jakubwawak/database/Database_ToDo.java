/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.ToDo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for maintaining to-do objects on database
 */
public class Database_ToDo {

    Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_ToDo(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for getting all active to-do objects from database for given user
     * @param noteit_user_id
     * @return collection of user To-Do objects
     */
    public ArrayList<ToDo> get_list_todo_active(int noteit_user_id){
        String query = "SELECT * FROM NOTEIT_TODO WHERE noteit_user_id = ? and noteit_todo_state = -1 OR noteit_todo_state = 0;";
        ArrayList<ToDo> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(new ToDo(rs));
            }
            NoteitApplication.log.add("GET-TODO","Loaded "+data.size()+" ToDo objects from database");
        }catch(SQLException ex){
            NoteitApplication.log.add("GET-TODO-FAILED","Failed to get ToDo objects from database ("+ ex.toString()+")");
        }
        return data;
    }

    /**
     * Function for getting archived to-do objects from database for given user
     * @param noteit_user_id
     * @return ArrayList
     */
    public ArrayList<ToDo> get_list_todo_archive(int noteit_user_id){
        String query = "SELECT * FROM NOTEIT_TODO WHERE noteit_user_id = ? and noteit_todo_state = 1;";
        ArrayList<ToDo> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(new ToDo(rs));
            }
            NoteitApplication.log.add("GET-TODO","Loaded "+data.size()+" ToDo objects from database");
        }catch(SQLException ex){
            NoteitApplication.log.add("GET-TODO-FAILED","Failed to get ToDo objects from database ("+ ex.toString()+")");
        }
        return data;
    }

    /**
     * Function for setting to-do object inactive
     * @param noteit_todo_id
     * @return Integer
     */
    public int set_todo_inactive(int noteit_todo_id){
        String query = "UPDATE NOTEIT_TODO SET noteit_todo_state = 1 WHERE noteit_todo_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_todo_id);
            ppst.execute();
            NoteitApplication.log.add("TODO-INACTIVE-UPDATE","Updated todo! (ID"+noteit_todo_id+")");
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-INACTIVE-UPDATE-FAILED","Failed to update todo state ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for setting to-do object active
     * @param noteit_todo_id
     * @return Integer
     */
    public int set_todo_active(int noteit_todo_id){
        String query = "UPDATE NOTEIT_TODO SET noteit_todo_state = 0 WHERE noteit_todo_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_todo_id);
            ppst.execute();
            NoteitApplication.log.add("TODO-INACTIVE-UPDATE","Updated todo! (ID"+noteit_todo_id+")");
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-INACTIVE-UPDATE-FAILED","Failed to update todo state ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for adding to-do objects to database
     * @return 1 - object added, -1 - database error
     */
    public int add_todo_object(ToDo to_add){
        /**
         * CREATE TABLE NOTEIT_TODO -- table for storing to-do objects
         * (
         *     noteit_todo_id INT AUTO_INCREMENT PRIMARY KEY,
         *     noteit_user_id INT,
         *     noteit_todo_time TIMESTAMP,
         *     noteit_todo_deadline TIMESTAMP,
         *     noteit_todo_desc TEXT,
         *     noteit_todo_state INT,
         *
         *     CONSTRAINT fk_todo1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
         * );
         */
        String query = "INSERT INTO NOTEIT_TODO (noteit_user_id,noteit_todo_time,noteit_todo_deadline,noteit_todo_desc," +
                "noteit_todo_state) VALUES (?,?,?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,to_add.noteit_user_id);
            ppst.setObject(2,to_add.noteit_todo_time);
            ppst.setObject(3,to_add.noteit_todo_deadline);
            ppst.setString(4,to_add.noteit_todo_desc);
            ppst.setInt(5,to_add.noteit_todo_state);
            ppst.execute();
            NoteitApplication.log.add("TODO-ADD-OBJECT","Added new to-do object ("+to_add.noteit_todo_desc+")");
            return 1;
        }catch (SQLException ex){
            NoteitApplication.log.add("TODO-ADD-OBJECT-FAILED","Failed to add to-do object to database ("+ex.toString());
            return -1;
        }
    }

    /**
     * Function for updating to-do object on database
     * @param to_update
     * @return 1 - object updated, -1 - database error
     */
    public int update_todo_object(ToDo to_update){
        String query = "UPDATE NOTEIT_TODO SET noteit_todo_time = ?,noteit_todo_deadline = ?, noteit_todo_desc = ? where noteit_todo_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setObject(1,to_update.noteit_todo_time);
            ppst.setObject(2,to_update.noteit_todo_deadline);
            ppst.setString(3,to_update.noteit_todo_desc);
            ppst.setInt(4,to_update.noteit_todo_id);
            ppst.execute();
            NoteitApplication.log.add("TODO-UPDATE-OBJECT","Updated new to-do object ("+to_update.noteit_todo_id+")");
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-UPDATE-OBJECT-FAILED","Failed to update to-do object on database ("+ex.toString());
            return -1;
        }
    }

    /**
     * Function for getting to-do objects from database
     * @param noteit_todo_id
     * @return To-Do object
     */
    public ToDo get_todo_object(int noteit_todo_id){
        String query = "SELECT * FROM NOTEIT_TODO WHERE noteit_todo_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_todo_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                NoteitApplication.log.add("TODO-GET-OBJECT","Loaded new to-do object ("+rs.getInt("noteit_todo_id")+")");
                return new ToDo(rs);
            }
            return null;
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-GET-OBJECT-FAILED","Failed to get to-do object from database ("+ex.toString());
            return null;
        }
    }
}
