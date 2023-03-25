/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Set;

/**
 * Component for maintain users and requests on application
 */
public class AdminUserManagerDialog {

    public Dialog main_dialog;
    VerticalLayout main_layout;
    Grid<StringElement> user_grid;
    Grid<StringElement> request_grid;
    Button enableuser_button, createaccount_button, resetpassword_button;

    /**
     * Constructor
     */
    public AdminUserManagerDialog(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        user_grid = new Grid<>(StringElement.class,false);
        request_grid = new Grid<>(StringElement.class,false);
        enableuser_button = new Button("Enable User",this::enableuser_button_button);
        createaccount_button = new Button("Create Account", VaadinIcon.PLUS.create(),this::createaccount_button_action);
        resetpassword_button = new Button("Reset Password",this::resetpassword_button_action);
        enableuser_button.setSizeFull();createaccount_button.setSizeFull();resetpassword_button.setSizeFull();
        prepare_dialog();
    }

    /**
     * Function for preparing dialog data
     */
    void prepare_dialog(){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);

        user_grid.addColumn(StringElement::getContent).setHeader("User Accounts");
        user_grid.setItems(dniu.get_list_of_users());
        user_grid.setWidth("400px");user_grid.setHeight("250px");

        request_grid.addColumn(StringElement::getContent).setHeader("Unactive User Accounts");
        request_grid.setItems(dniu.get_list_of_unactive_users());
        request_grid.setWidth("400px");request_grid.setHeight("250px");

        enableuser_button.setHeight("30px");createaccount_button.setHeight("30px");resetpassword_button.setHeight("30px");

        // grid event selection listener for user grid
        user_grid.addItemClickListener(
                event ->{
                    StringElement selected = event.getItem();
                    try{
                        int noteit_user_id = Integer.parseInt(selected.getContent().split(":")[0]);
                        if ( dniu.check_user_active_status(noteit_user_id) == 1 ){
                            enableuser_button.setText("Disable User");
                        }
                        else{
                            enableuser_button.setText("Enable User");
                        }
                    }catch(Exception ex){}
                }
        );
        // grid event selection listener for request grid
        request_grid.addItemClickListener(
                event ->{
                    StringElement selected = event.getItem();
                    try{
                        int noteit_user_id = Integer.parseInt(selected.getContent().split(":")[0]);
                        if ( dniu.check_user_active_status(noteit_user_id) == 1 ){
                            enableuser_button.setText("Disable User");
                        }
                        else{
                            enableuser_button.setText("Enable User");
                        }
                    }catch(Exception ex){}
                }
        );
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
        left_layout.add(new VerticalLayout(request_grid,enableuser_button));
        right_layout.add(new VerticalLayout(user_grid,createaccount_button));
        secondary_layout.add(left_layout,right_layout);
        main_layout.add(secondary_layout);

        main_layout.add(resetpassword_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    private void enableuser_button_button(ClickEvent e){
        switch(enableuser_button.getText()){
            case "Enable User":{
                Set<StringElement> selected_user = request_grid.getSelectedItems();
                Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
                for(StringElement selected : selected_user){
                    try{
                        int noteit_user_id = Integer.parseInt(selected.getContent().split(":")[0]);
                        if ( noteit_user_id == NoteitApplication.logged.getNoteit_user_id()){
                            Notification.show("You cannot use that action on your own account!");
                        }
                        else{
                            int ans = dniu.enable_noteit_user(noteit_user_id);
                            if ( ans == 1 ){
                                Notification.show("User enabled!");
                            }
                            else{
                                Notification.show("Database error");
                            }
                        }

                    }catch(Exception ex){}
                }
                break;
            }
            case "Disable User":{
                Set<StringElement> selected_user = request_grid.getSelectedItems();
                Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
                for(StringElement selected : selected_user){
                    try{
                        int noteit_user_id = Integer.parseInt(selected.getContent().split(":")[0]);
                        if ( noteit_user_id == NoteitApplication.logged.getNoteit_user_id()){
                            Notification.show("You cannot use that action on your own account!");
                        }
                        else{
                            int ans = dniu.disable_noteit_user(noteit_user_id);
                            if ( ans == 1 ){
                                Notification.show("User disabled!");
                            }
                            else{
                                Notification.show("Database error");
                            }
                        }
                    }catch(Exception ex){}
                }
                break;
            }
        }
    }

    /**
     * Function for creating action for createaccount_button
     * @param e
     */
    private void createaccount_button_action(ClickEvent e){
        CreateAccountDialog cad = new CreateAccountDialog();
        cad.create_dialog();
        main_layout.add(cad.main_dialog);
        cad.main_dialog.open();
    }

    /**
     * Function for creating action for resetpassword_button
     * @param e
     */
    private void resetpassword_button_action(ClickEvent e){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        Set<StringElement> selected_user = user_grid.getSelectedItems();
        if ( selected_user.size() == 0){
            Notification.show("User is not selected!");
        }
        else{
            for(StringElement selected : selected_user){
                try{
                    Notification.show("Selected: "+selected.getContent());
                    int noteit_user_id = Integer.parseInt(selected.getContent().split(":")[0]);
                    String query = dniu.resetuserpassword(noteit_user_id);
                    MessageComponent mc = new MessageComponent("Password set: "+query);
                    main_layout.add(mc.main_dialog);
                    mc.main_dialog.open();
                    break;
                }catch(Exception ex){
                    Notification.show(ex.toString());
                }
            }
        }

    }
}
