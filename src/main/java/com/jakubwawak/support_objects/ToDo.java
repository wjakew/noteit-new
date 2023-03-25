/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.support_objects;

import com.jakubwawak.noteit.NoteitApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Object for storing to-do objects
 */
public class ToDo {
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
     */

    public int noteit_todo_id;
    public int noteit_user_id;
    public LocalDateTime noteit_todo_time;
    public LocalDateTime noteit_todo_deadline;
    public String noteit_todo_desc;
    public int noteit_todo_state;

    public boolean error;
    /**
     * Constructor
     */
    public ToDo(){
        noteit_todo_id = -1;
        noteit_user_id = NoteitApplication.logged.getNoteit_user_id();
        noteit_todo_time = null;
        noteit_todo_deadline = null;
        noteit_todo_desc = "";
        noteit_todo_state = -1;
        error = false;
    }

    /**
     * Constructor with database support
     * @param to_add
     */
    public ToDo(ResultSet to_add){
        try{
            noteit_todo_id = to_add.getInt("noteit_todo_id");
            noteit_user_id = to_add.getInt("noteit_user_id");
            noteit_todo_time = to_add.getObject("noteit_todo_time",LocalDateTime.class);
            noteit_todo_deadline = to_add.getObject("noteit_todo_deadline",LocalDateTime.class);
            noteit_todo_desc = to_add.getString("noteit_todo_desc");
            noteit_todo_state = to_add.getInt("noteit_todo_state");
            error = false;
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-GET-FAILED","Failed to get ToDo object from database ("+ex.toString());
            error = true;
        }
    }

    /**
     * Constructor with
     * @param noteit_todo_id
     */
    public ToDo(int noteit_todo_id){
        String query = "SELECT * FROM NOTEIT_TODO WHERE noteit_todo_id = ?;";
        try{
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1,noteit_todo_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next())
                new ToDo(rs);
            else{
                new ToDo();
            }
        }catch(SQLException ex){
            NoteitApplication.log.add("TODO-GET-FAILED","Failed to get ToDo object from database ("+ex.toString());
            error = true;
            new ToDo();
        }
    }

    public int getNoteit_todo_id(){
        return noteit_todo_id;
    }

    public String getNoteit_todo_desc() {
        return noteit_todo_desc;
    }
}
