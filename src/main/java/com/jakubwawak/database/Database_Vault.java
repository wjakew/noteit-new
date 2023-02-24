/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Object for modifying vault data on database
 */
public class Database_Vault {

    public Database_Connector database;

    /**
     * Constructor
     * @param database
     */
    public Database_Vault(Database_Connector database){
        this.database = database;
    }

    /**
     * Function for creating vault
     * @param noteit_user_id
     * @param noteit_vault_name
     * @return 1 - vault creation successfull, -1 - database error
     */
    public int create_vault(int noteit_user_id, String noteit_vault_name, ArrayList<String> members){
        String query = "INSERT INTO NOTEIT_VAULT (noteit_user_id,noteit_vault_name,noteit_vault_members) VALUES (?,?,?);";
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        String noteit_vault_members = "";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_user_id);
            ppst.setString(2,noteit_vault_name);
            for(String member_email : members){
                int nami_user = dniu.getuserid_byemail(member_email);
                if ( nami_user > 0 ){
                    noteit_vault_members = noteit_vault_name + nami_user + ",";
                }
            }
            ppst.setString(3,noteit_vault_members);
            ppst.execute();
            NoteitApplication.log.add("VAULT-CREATE","Vault created! "+noteit_vault_name+" "+dniu.getuseremail(noteit_user_id));
            return 1;
        }catch(SQLException e){
            NoteitApplication.log.add("VAULT-CREATE-FAILED","Failed to create vault ("+e.toString()+")");
            return -1;
        }
    }

    /**
     * Function for listing all vaults in which logged user is owner or member
     * @return ArrayList collection
     */
    public ArrayList<Vault> get_vaults(){
        String query = "SELECT * FROM NOTEIT_VAULT;";
        ArrayList<Vault> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                if ( rs.getInt("noteit_user_id") == NoteitApplication.logged.getNoteit_user_id() || rs.getString("noteit_vault_members").contains(NoteitApplication.logged.getNoteit_user_id()+",")){
                    data.add(new Vault(rs));
                }
            }
            NoteitApplication.log.add("VAULT-LIST-LOAD","Loaded "+data.size()+" vaults");
        }catch(SQLException e){
            NoteitApplication.log.add("VAULT-LIST-FAILED","Failed to list vaults ("+e.toString()+")");
        }
        return data;
    }

}
