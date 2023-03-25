/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.support_objects.Vault;
import com.jakubwawak.website.website_content_objects.user_components.UserMultiSelector;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.Set;

/**
 * Component for modifying vault or creating one
 */
public class ModifyVaultComponent {
    public Dialog main_dialog;
    public VerticalLayout main_layout;

    public ArrayList<String> members;
    public Vault vault;

    TextField vaultname_field;
    UserMultiSelector ums;
    Button addvault_button;

    /**
     * Constructor
     * @param noteit_vault_id - if 0 create new vault, else modify existing
     */
    public ModifyVaultComponent(int noteit_vault_id, ArrayList<String> members){
        if (members != null)
            this.members = members;
        else{
            this.members = new ArrayList<>();
        }
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();

        if ( noteit_vault_id != 0 ){
            vault = new Vault(noteit_vault_id);
        }
        else{
            vault = new Vault();
        }
        create_components();
        create_dialog();
    }

    /**
     * Function for creating components
     */
    void create_components(){
        vaultname_field = new TextField("Vault Name");
        ums = new UserMultiSelector();
        if ( vault.noteit_vault_id == -1)
            addvault_button = new Button("Add Vault",this::add_action);
        else
            addvault_button = new Button("Update Vault",this::add_action);

    }

    /**
     * Function for creating dialog components
     */
    void create_dialog(){
        if (vault.noteit_vault_id == -1){
            main_layout.add(new H3("New Vault"));
        }
        else{
            main_layout.add(new H3("Modify Vault"));
            vaultname_field.setValue(vault.noteit_vault_name);
        }

        main_layout.add(vaultname_field,ums.multiSelectComboBox,addvault_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    /**
     * Function for getting selected values
     * @return selected values
     */
    ArrayList<String> get_selected_values(){
        Set<StringElement> selected = ums.multiSelectComboBox.getValue();
        ArrayList<String> data = new ArrayList<>();
        for(StringElement element : selected){
            try{
                int noteit_user_id = Integer.parseInt(element.getContent().split(":")[0]);
                String noteit_user_email = element.getContent().split(":")[1];
                data.add(noteit_user_email);
            }catch(NumberFormatException e){}
        }
        return data;
    }

    /**
     * On Action of click add_button
     * @param e
     */
    private void add_action(ClickEvent e){
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        switch(addvault_button.getText()){
            case "Add Vault":
            {
                if (dv.create_vault(NoteitApplication.logged.getNoteit_user_id(),vaultname_field.getValue(),get_selected_values()) == 1){
                    Notification.show("Added new vault!");
                    main_dialog.close();
                }
                else{
                    Notification.show("Failed, check server log.");
                }
                break;
            }
            case "Update Vault":
            {
                break;
            }
        }
    }
}
