/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.support_objects.Wall;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for maintaining wall data on database
 */
public class Database_Wall {

    /**
     * CREATE TABLE NOTEIT_MESSAGE -- table for storing messages from wall
     * (
     *     noteit_message_id INT AUTO_INCREMENT PRIMARY KEY,
     *     noteit_user_id INT,
     *     noteit_wall_id INT,
     *     noteit_message_time TIMESTAMP,
     *     noteit_message_text TEXT,
     *
     *     CONSTRAINT fk_noteitm1 FOREIGN KEY (noteit_user_id) REFERENCES  NOTEIT_USER (noteit_user_id),
     *     CONSTRAINT fk_noteitm2 FOREIGN KEY (noteit_wall_id) REFERENCES NOTEIT_WALL(noteit_wall_id)
     * );
     *
     * CREATE TABLE NOTEIT_WALL -- table for storing wall data
     * (
     *     noteit_wall_id INT AUTO_INCREMENT PRIMARY KEY,
     *     noteit_user_id INT,
     *     noteit_wall_name VARCHAR(100),
     *     noteit_wall_members VARCHAR(200),
     *
     *     CONSTRAINT fk_wall1 FOREIGN KEY (noteit_user_id) REFERENCES NOTEIT_USER (noteit_user_id)
     * );
     */

    Database_Connector database;


    /**
     * Constructor
     */
    public Database_Wall(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for checking user wall object on database
     * @param noteit_user_id
     * @return noteit_wall_id, 0 - wall not found, -1 - database error
     */
    public int check_user_wall(int noteit_user_id){
        String query = "SELECT noteit_wall_id FROM NOTEIT_WALL WHERE noteit_user_id =?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            if (rs.next()){
                return rs.getInt("noteit_wall_id");
            }
            return 0;
        }catch(SQLException ex){
            NoteitApplication.log.add("CHECK-USER-WALL-FAILED","Failed to check user wall object ("+ ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for creating user wall
     * @param noteit_user_id
     * @return 1 - wall created, -1 - database error, 2 - wall already created
     */
    public int create_user_wall(int noteit_user_id){
        String query = "INSERT INTO NOTEIT_WALL (noteit_user_id, noteit_wall_name,noteit_wall_members) VALUES (?,?,?)";
        if ( check_user_wall(NoteitApplication.logged.getNoteit_user_id()) == 0 ){
            try{
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,noteit_user_id);
                ppst.setString(2,"Personal User Wall");
                ppst.setString(3,"");
                ppst.execute();
                NoteitApplication.log.add("CREATE-USER-WALL","Wall created for noteit_user_id: "+noteit_user_id);
                return 1;
            }catch(SQLException ex){
                NoteitApplication.log.add("CREATE-USER-WALL-FAILED","Failed creating wall for user ("+ex.toString()+")");
                return -1;
            }
        }
        return 2;
    }

    /**
     * Function for loading user data
     * @param noteit_user_id
     * @return
     */
    public ArrayList<Wall> get_user_wall(int noteit_user_id){
        ArrayList<Wall> data = new ArrayList<>();
        String query = "SELECT * FROM NOTEIT_WALL WHERE noteit_user_id = ? " +
                "OR noteit_wall_members LIKE '%,"+noteit_user_id+"%' OR noteit_wall_members LIKE '%"+noteit_user_id+",%';";
        try{
            PreparedStatement ppst = NoteitApplication.database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                data.add(new Wall(rs));
            }
        }catch(SQLException ex){
            NoteitApplication.log.add("LIST-WALL-FAILED","Failed to list walls ("+ex.toString()+")");
        }
        return data;
    }
}
