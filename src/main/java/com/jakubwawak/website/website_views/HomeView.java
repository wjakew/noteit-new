/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_views;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.jakubwawak.website.webview_components.ModifyVaultComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

@PageTitle("noteIT home")
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    Button createvault_button, createnote_button;

    /**
     * Constructor
     */
    public HomeView(){
        create_view();
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        createvault_button = new Button("Create New Vault",VaadinIcon.PLUS.create(),this::createvault_action);
        createnote_button = new Button("Create New Note",VaadinIcon.NOTEBOOK.create());
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
            quickbutton_layout.add(createvault_button);
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
    //----------------end of section
}
