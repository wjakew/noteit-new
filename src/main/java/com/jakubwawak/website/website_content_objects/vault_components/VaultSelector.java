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
        combobox.setItemLabelGenerator(Vault::get_glance);
        combobox.setAllowCustomValue(false);
    }

    /**
     * Function for getting selected values from combobox
     * @return ArrayList collection of values
     */
    public Vault get_selected_vaules(){
        return (Vault) combobox.getValue();
    }
}
