/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.web_layouts;

import com.jakubwawak.noteit.NoteitApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Main Web Application layout
 */
public class MainLayout extends AppLayout {

    /**
     * Constructor
     */

    Button home_button, logout_button;
    H3 logo;

    public MainLayout(){
        this.getElement().setAttribute("theme", Lumo.DARK);
        home_button = new Button("NoteIT",this::homebutton_action);
        logout_button = new Button("Log out!",this::logoutbutton_action);
        if ( NoteitApplication.logged != null ){
            logo = new H3("Welcome, "+NoteitApplication.logged.noteit_user_name+" "+NoteitApplication.logged.getNoteit_user_surname());
        }
        else{
            logo = new H3("");
        }
        createHeader();
    }

    /**
     * Function for setting user to home page
     * @param e
     */
    private void homebutton_action(ClickEvent e){
        home_button.getUI().ifPresent(ui ->
                ui.navigate("home"));
    }

    /**
     * Function for logging out user and routing to login page
     * @param e
     */
    private void logoutbutton_action(ClickEvent e){
        NoteitApplication.log.add("LOGOUT","User "+NoteitApplication.logged.getNoteit_user_email()+" logged out!");
        Notification.show("See you soon "+NoteitApplication.logged.noteit_user_name+" "+NoteitApplication.logged.getNoteit_user_surname()+"!");
        NoteitApplication.logged = null;
        logout_button.getUI().ifPresent(ui ->
                ui.navigate("login"));
    }

    /**
     * Function for creating header
     */
    private void createHeader(){
        if ( NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            Icon vaadinIcon = new Icon(VaadinIcon.NOTEBOOK);
            HorizontalLayout header = new HorizontalLayout(vaadinIcon,home_button,logout_button,logo);
            header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            header.setWidth("100%");
            header.addClassNames("py-0", "px-m");
            addToNavbar(header);
        }
        else{
            Icon vaadinIcon = new Icon(VaadinIcon.NOTEBOOK);
            HorizontalLayout header = new HorizontalLayout(vaadinIcon,new H3("Error - user not logged!"));
            header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
            header.setWidth("100%");
            header.addClassNames("py-0", "px-m");
            addToNavbar(header);
        }
    }
}
