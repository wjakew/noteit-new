/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.website.website_content_objects.user_components.UserMultiSelector;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

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
        change_button = new Button("Change Owner");
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

    }
}
