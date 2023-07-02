/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
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
                    noteit_vault_members = noteit_vault_members + nami_user + ",";
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
     * Function for removing vault object
     * @param noteit_vault_id
     * @return Integer
     */
    public int remove_vault(int noteit_vault_id){
        // remove notes corelated to given vault
        String query = "DELETE FROM NOTEIT_OBJECT WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ppst.execute();
            // notes removed
            query = "DELETE FORM NOTEIT_VAULT WHERE noteit_vault_id = ?;";
            ppst = database.con.prepareStatement(query);
            ppst.execute();
            NoteitApplication.log.add("VAULT-REMOVE","Removed vault id:"+noteit_vault_id);
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULT-REMOVE-FAILED","Failed to remove vault ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for adding vault blob to database
     * @param noteit_vault_id
     * @param noteit_user_id
     * @return Integer
     */
    public int add_vault_blob(int noteit_vault_id,int noteit_user_id){
        String query = "INSERT INTO BLOB_ARCHIVE (noteit_user_id, noteit_blob_category,noteit_blob)" +
                "VALUES (?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            Vault vault = new Vault(noteit_vault_id);

        }catch(SQLException ex){
            NoteitApplication.log.add("BLOB-ADD-FAILED","Failed to add blob data ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for changing owner of the vault
     * @param noteit_vault_id
     * @param noteit_user_id
     * @return 0 - logged user not owner, 1 - owner changed, -1 - database errors
     */
    public int change_owner(int noteit_vault_id, int noteit_user_id){
        String query = "UPDATE NOTEIT_VAULT SET noteit_user_id = ?;";
        Vault vault = this.get_vault(noteit_vault_id);
        if ( vault.is_logged_owner() ){
            try{
                PreparedStatement ppst = database.con.prepareStatement(query);
                ppst.setInt(1,noteit_user_id);
                ppst.execute();
                NoteitApplication.log.add("VAULT-CHANGE-OWNER","Owner for vault "+noteit_vault_id+" changed to "+noteit_user_id);
                return 1;
            }catch(SQLException ex){
                NoteitApplication.log.add("VAULT-CHANGE-OWNER-FAILED","Failed to change owner ("+ex.toString()+")");
                return -1;
            }
        }
        return 0;
    }

    /**
     * Function for listing vaults with members
     * @return ArrayList collection
     */
    ArrayList<Vault> get_vaults_member(){
        String query = "SELECT * FROM NOTEIT_VAULT;";
        ArrayList<Vault> data =new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                if ( rs.getString("noteit_vault_members").contains(Integer.toString(NoteitApplication.logged.getNoteit_user_id()))){
                    data.add(new Vault(rs));
                }
            }
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULTMEMBER-LIST-FAILED","Failed to list member vaults ("+ex.toString()+")");
        }
        return data;
    }

    /**
     * Function for listing all vaults in which logged user is owner or member
     * @return ArrayList collection
     */
    public ArrayList<Vault> get_vaults(){
        String query = "SELECT * FROM NOTEIT_VAULT WHERE noteit_user_id = ?;";
        ArrayList<Vault> data = new ArrayList<>();
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,NoteitApplication.logged.getNoteit_user_id());
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){
                    data.add(new Vault(rs));
            }
            NoteitApplication.log.add("VAULT-LIST-LOAD","Loaded "+data.size()+" vaults");
        }catch(SQLException e){
            NoteitApplication.log.add("VAULT-LIST-FAILED","Failed to list vaults ("+e.toString()+")");
        }
        data.addAll(get_vaults_member());
        return data;
    }

    /**
     * Function for getting vault
     * @param noteit_vault_id
     * @return
     */
    public Vault get_vault(int noteit_vault_id){
        String query = "SELECT * FROM NOTEIT_VAULT where noteid_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                NoteitApplication.log.add("VAULT-GET-LOAD","Loaded "+rs.getString("noteit_vault_name"));
                return new Vault(rs);
            }
            return null;
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULT-GET-FAILED","Failed to get vault ("+ex.toString()+")");
            return null;
        }
    }
}
