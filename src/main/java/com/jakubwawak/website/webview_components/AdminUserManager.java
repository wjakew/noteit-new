/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Component for maintain users and requests on application
 */
public class AdminUserManager {

    public Dialog main_dialog;
    VerticalLayout main_layout;
    Grid<StringElement> user_grid;
    Grid<StringElement> request_grid;
    Button enableuser_button, createaccount_button, resetpassword_button;

    /**
     * Constructor
     */
    public AdminUserManager(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        user_grid = new Grid<>(StringElement.class,false);
        request_grid = new Grid<>(StringElement.class,false);
        enableuser_button = new Button("Enable User");
        createaccount_button = new Button("Create Account", VaadinIcon.PLUS.create());
        resetpassword_button = new Button("Reset Password");
        prepare_dialog();
    }

    /**
     * Function for preparing dialog data
     */
    void prepare_dialog(){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        user_grid
        user_grid.addColumn(StringElement::getContent).setHeader("User Accounts");
        user_grid.setItems(dniu.get_list_of_users());


        prepare_layout();
    }

    /**
     * Function for creating layout
     */
    void prepare_layout(){
        main_layout.add(new H3("Server Account Manager"));
        HorizontalLayout secondary_layout = new HorizontalLayout();
        HorizontalLayout left_layout = new HorizontalLayout();
        HorizontalLayout right_layout = new HorizontalLayout();
        left_layout.add(new VerticalLayout(request_grid,createaccount_button,resetpassword_button));
        right_layout.add(new VerticalLayout(user_grid,createaccount_button));

        main_layout.add(secondary_layout);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }
}
