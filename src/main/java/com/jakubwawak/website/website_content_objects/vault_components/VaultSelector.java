/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Vault;
import com.vaadin.flow.component.combobox.ComboBox;

import java.util.ArrayList;

/**
 * Object for creating component for selecting vaults
 */
public class VaultSelector {
    public ComboBox<Vault> combobox;
    ArrayList<Vault> content;

    /**
     * Constructor
     */
    public VaultSelector(){
        combobox = new ComboBox("Your Vaults");
        create_component();
    }

    /**
     * Function for creating components
     */
    void create_component(){
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        combobox.setItems(dv.get_vaults());
        content = dv.get_vaults();
        combobox.setItemLabelGenerator(Vault::get_glance);
        combobox.setAllowCustomValue(false);
    }

    /**
     * Function for setting selected value
     * @param noteit_vault_id
     */
    public void set_selected_value(int noteit_vault_id){
        for(Vault vault : content){
            if ( vault.noteit_vault_id == noteit_vault_id){
                combobox.setValue(vault);
            }
        }
    }

    /**
     * Function for getting selected values from combobox
     * @return ArrayList collection of values
     */
    public Vault get_selected_vaules(){
        return (Vault) combobox.getValue();
    }
}
