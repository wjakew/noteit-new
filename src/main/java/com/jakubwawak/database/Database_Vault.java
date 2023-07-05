/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.database;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.support_objects.Vault;
import com.sun.jdi.event.ExceptionEvent;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

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
        // adding vault data to blob storage
        add_vault_blob(noteit_vault_id,NoteitApplication.logged.getNoteit_user_id());

        // add vault notes to blob storage
        String query = "SELECT * FROM NOTEIT_OBJECT WHERE  noteit_vault_id=?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            while(rs.next()){

                int noteit_object_id = rs.getInt("noteit_object_id");
                add_note_blob(noteit_object_id,NoteitApplication.logged.getNoteit_user_id());
            }
        }catch(SQLException ex){
            NoteitApplication.log.add("NOTE-VAULT-REMOVE-FAILED","Failed to remove note from vault "+noteit_vault_id+" ("+ex.toString()+")");
        }

        // remove notes correlated to given vault
        query = "DELETE FROM NOTEIT_OBJECT WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ppst.execute();
            // notes removed
            query = "DELETE FROM NOTEIT_VAULT WHERE noteid_vault_id = ?;";
            ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ppst.execute();
            NoteitApplication.log.add("VAULT-REMOVE","Removed vault id:"+noteit_vault_id);
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULT-REMOVE-FAILED","Failed to remove vault ("+ex.toString()+")");
            return -1;
        }
    }

    /**
     * Function for adding members to the vault
     * @param noteit_vault_id
     * @return ArrayList
     */
    public ArrayList<String> get_members_of_vault(int noteit_vault_id){
        ArrayList<String> data = new ArrayList<>();
        String query = "SELECT noteit_vault_members FROM NOTEIT_VAULT WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            if ( rs.next() ){
                String[] raw_data = rs.getString("noteit_vault_members").split(",");
                data.addAll(Arrays.asList(raw_data));
            }
        }catch(Exception ex){
            NoteitApplication.log.add("VAULT-GET-MEMBERS-FAILED","Failed to get members of vault ("+ex.toString()+")");
        }
        return data;
    }
    /**
     * Function for adding members to the vault
     * @param noteit_vault_id
     * @return ArrayList
     */
    public ArrayList<StringElement> get_members_of_vault_as_content(int noteit_vault_id){
        ArrayList<StringElement> data = new ArrayList<>();
        String query = "SELECT noteit_vault_members FROM NOTEIT_VAULT WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setInt(1,noteit_vault_id);
            ResultSet rs = ppst.executeQuery();
            Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
            if ( rs.next() ){
                String[] raw_data = rs.getString("noteit_vault_members").split(",");
                for(String member : raw_data){
                    try{
                        int noteit_user_id = Integer.parseInt(member);
                        String user_data = noteit_user_id+":"+dniu.getuseremail(noteit_user_id);
                        data.add(new StringElement(user_data));
                    }catch(Exception e){}
                }
            }
        }catch(Exception ex){
            NoteitApplication.log.add("VAULT-GET-MEMBERS-FAILED","Failed to get members of vault ("+ex.toString()+")");
        }
        return data;
    }

    /**
     * Function for adding member to the vault
     * @param noteit_vault_id
     * @param noteit_user_id
     * @return Integer
     */
    public int add_member_to_the_vault(int noteit_vault_id, int noteit_user_id){
        ArrayList<String> members = get_members_of_vault(noteit_vault_id);
        members.add(Integer.toString(noteit_user_id));
        String data_to_update = "";
        for(String member : members){
            data_to_update = data_to_update + member + ",";
        }

        String query = "UPDATE NOTEIT_VAULT SET noteit_vault_members = ? WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,data_to_update);
            ppst.setInt(2,noteit_vault_id);
            ppst.execute();
            NoteitApplication.log.add("VAULT-ADD-MEMBER","Added new member to vault ("+noteit_vault_id+")");
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULT-ADD-MEMBER-FAILED","Failed to add new member to vault ("+noteit_vault_id+")");
            return -1;
        }
    }

    /**
     * Function for adding member to the vault
     * @param noteit_vault_id
     * @param noteit_user_id
     * @return Integer
     */
    public int remove_member_from_the_vault(int noteit_vault_id, int noteit_user_id){
        ArrayList<String> members = get_members_of_vault(noteit_vault_id);
        String data_to_update = "";
        for(String member : members){
            if (!member.equals(Integer.toString(noteit_user_id)))
                data_to_update = data_to_update + member + ",";
        }
        String query = "UPDATE NOTEIT_VAULT SET noteit_vault_members = ? WHERE noteit_vault_id = ?;";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            ppst.setString(1,data_to_update);
            ppst.setInt(2,noteit_vault_id);
            ppst.execute();
            NoteitApplication.log.add("VAULT-ADD-REMOVE","Removed member from vault ("+noteit_vault_id+")");
            return 1;
        }catch(SQLException ex){
            NoteitApplication.log.add("VAULT-ADD-REMOVE-FAILED","Failed to remove member from vault ("+noteit_vault_id+")");
            return -1;
        }
    }

    /**
     * Function for adding note serialized data to blob
     * @param noteit_note_id
     * @param noteit_user_id
     * @return
     */
    public int add_note_blob(int noteit_note_id,int noteit_user_id){
        byte[] data = null;
        String query = "INSERT INTO BLOB_ARCHIVE (noteit_user_id, noteit_blob_category,noteit_blob)" +
                "VALUES (?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            Note note = new Note(noteit_note_id);

            // saving data to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(note);
            oos.flush();
            oos.close();
            baos.close();
            data = baos.toByteArray();

            ppst.setInt(1,noteit_user_id);
            ppst.setString(2,"note");
            ppst.setObject(3,data);
            ppst.execute();
            return 1;
        }catch(Exception ex){
            NoteitApplication.log.add("BLOB-ADD-FAILED","Failed to add blob data ("+ex.toString()+")");
            return -1;
        }
    }


    /**
     * Function for adding vault blob to database
     * @param noteit_vault_id
     * @param noteit_user_id
     * @return Integer
     */
    public int add_vault_blob(int noteit_vault_id, int noteit_user_id){
        byte[] data = null;
        String query = "INSERT INTO BLOB_ARCHIVE (noteit_user_id, noteit_blob_category,noteit_blob)" +
                "VALUES (?,?,?);";
        try{
            PreparedStatement ppst = database.con.prepareStatement(query);
            Vault vault = new Vault(noteit_vault_id);

            // saving data to bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(vault);
            oos.flush();
            oos.close();
            baos.close();
            data = baos.toByteArray();

            ppst.setInt(1,noteit_user_id);
            ppst.setString(2,"vault");
            ppst.setObject(3,data);
            ppst.execute();
            return 1;
        }catch(Exception ex){
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
