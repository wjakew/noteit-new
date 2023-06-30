/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.website.website_content_objects.MessageComponent;
import com.jakubwawak.website.website_content_objects.user_components.UserMultiSelector;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.sql.SQLClientInfoException;
import java.util.Set;

/**
 * Object for changing owner for vault
 */
public class ChangeOwnerDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    UserMultiSelector ums;

    Button change_button;
    int noteit_vault_id;

    /**
     * Constructor
     * @param noteit_vault_id
     */
    public ChangeOwnerDialog(int noteit_vault_id){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        ums = new UserMultiSelector();
        this.noteit_vault_id = noteit_vault_id;
        change_button = new Button("Change Owner",this::change_action);
        prepare_dialog();
    }

    /**
     * Function for preparing dialog data
     */
    void prepare_dialog(){
        main_dialog.add(new H2("Pick new Owner"));
        main_dialog.add(ums.multiSelectComboBox);
        main_dialog.add(change_button);
    }

    /**
     * Function for changing action
     * @param ex
     */
    private void change_action(ClickEvent ex){
        try{
            Set<StringElement> selected = ums.multiSelectComboBox.getSelectedItems();
            Database_Vault dv = new Database_Vault(NoteitApplication.database);
            for(StringElement object : selected){
                int noteit_user_id = Integer.parseInt(object.getContent().split(":")[0]);
                int ans = dv.change_owner(noteit_vault_id,noteit_user_id);
                if ( ans == 1 ){
                    UI.getCurrent().getPage().reload();
                    Notification.show("Owner changed");
                    main_dialog.close();
                }
                else{
                    MessageComponent mc = new MessageComponent("Something went wrong.. Check log!");
                    main_layout.add(mc.main_dialog);
                    mc.main_dialog.open();
                }
                break;
            }
        }catch(Exception e){}
    }
}
