/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.support_objects;

import com.jakubwawak.noteit.NoteitApplication;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Object for representing note
 */
public class Note implements Serializable {
    /**
     CREATE TABLE NOTEIT_OBJECT -- table for storing notes
     (
     noteit_object_id INT AUTO_INCREMENT PRIMARY KEY,
     noteit_vault_id INT,
     noteit_object_time TIMESTAMP,
     noteit_object_title VARCHAR(300),
     noteit_object_rawtext TEXT,
     noteit_object_blob BLOB,

     CONSTRAINT fk_object1 FOREIGN KEY (noteit_vault_id) REFERENCES NOTEIT_VAULT (noteid_vault_id)
     );
     */

    public int noteit_object_id;
    public int noteit_vault_id;
    public String noteit_object_title;
    public LocalDateTime noteit_object_time;

    public String noteit_object_rawtext;

    public boolean error;

    /**
     * Constructor with database support
     * @param to_add
     */
    public Note(ResultSet to_add){
        if (to_add != null){
            try {
                noteit_object_id = to_add.getInt("noteit_object_id");
                noteit_vault_id = to_add.getInt("noteit_vault_id");
                noteit_object_title = to_add.getString("noteit_object_title");
                noteit_object_time = to_add.getObject("noteit_object_title", LocalDateTime.class);
                noteit_object_rawtext = to_add.getString("noteit_object_rawtext");
                error = false;
            }catch(SQLException e){
                NoteitApplication.log.add("NOTE-LOAD-FAILED","Failed to load note ("+e.toString()+")");
                error = true;
            }
        }
        else{
            noteit_object_id = -1;
            noteit_vault_id = -1;
            noteit_object_title = "";
            noteit_object_time = null;
            noteit_object_rawtext = "";
            error = false;
        }
    }

    /**
     * Constructor with noteit_object_id ID database support
     * @param noteit_object_id
     */
    public Note(int noteit_object_id){
        String query = "SELECT * FROM NOTEIT_OBJECT WHERE noteit_object_id = ?;";
        try{
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1,noteit_object_id);
            ResultSet rs = ppst.executeQuery();
            if(rs.next()){
                new Note(rs);
            }
            new Note(null);
        }catch(SQLException e){
            NoteitApplication.log.add("NOTE-LOAD-FAILED","Failed to load note ("+e.toString()+")");
            error = true;
        }
    }

    public int getNoteit_object_id() {
        return noteit_object_id;
    }
    public String getNoteit_object_title(){
        return noteit_object_title;
    }
    public String getNoteit_object_rawtext(){
        if ( noteit_object_rawtext.length() > 20 ){
            return noteit_object_rawtext.substring(0,20);
        }
        return noteit_object_rawtext;
    }
}
