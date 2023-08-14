/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_views;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_content_objects.MessageComponent;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.jakubwawak.website.website_content_objects.note_components.CreateNoteDialog;
import com.jakubwawak.website.website_content_objects.vault_components.ModifyVaultComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.management.Notification;

@PageTitle("noteIT home")
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    Button createvault_button, createnote_button;

    /**
     * Constructor
     */
    public HomeView(){
        create_view();
        NoteitApplication.main_layout = this;
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
        getStyle().set("--lumo-font-family","Monospace");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        createvault_button = new Button("Create New Vault",VaadinIcon.PLUS.create(),this::createvault_action);
        createnote_button = new Button("Create New Note",VaadinIcon.NOTEBOOK.create(),this::createnote_action);
    }

    /**
     * Function for creating other compontents like grids,list etc
     */
    void create_components(){

    }

    /**
     * Function for creating view components
     */
    void create_view(){
        create_buttons();
        create_components();
        if (NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            // view can be created, user is logged
            add(new H1("Welcome, "+NoteitApplication.logged.noteit_user_name+" "+NoteitApplication.logged.getNoteit_user_surname()+"!"));
            HorizontalLayout quickbutton_layout = new HorizontalLayout();
            quickbutton_layout.add(createvault_button,createnote_button);
            add(quickbutton_layout);

        }
        else{
            // show error screen
            add(new H1("User login error!"));
        }
    }

    //----------------section for actions and validators
    private void createvault_action(ClickEvent e){
        ModifyVaultComponent mvc = new ModifyVaultComponent(0,null);
        add(mvc.main_dialog);
        mvc.main_dialog.open();
    }
    private void createnote_action(ClickEvent e){
        CreateNoteDialog cnd = new CreateNoteDialog(0);
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        if ( dv.get_vaults().size() == 0 ){
            MessageComponent mc = new MessageComponent("No active vaults!");
            add(mc.main_dialog);
            mc.main_dialog.open();
        }
        else{
            add(cnd.main_dialog);
            cnd.main_dialog.open();
        }
    }
    //----------------end of section
}
