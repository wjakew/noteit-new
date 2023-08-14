/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_views.LoginView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

/**
 * Object for creating account dialog
 */
public class CreateAccountDialog {

    public Dialog main_dialog;
    VerticalLayout main_layout;

    TextField name_field,surname_field,email_field;
    PasswordField password1_field, password2_field;

    Button create_button;

    /**
     * Constructor
     */
    public CreateAccountDialog(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        name_field = new TextField("Name");
        surname_field = new TextField("Surname");
        email_field = new TextField("Email");
        password1_field = new PasswordField("Password");
        password2_field = new PasswordField("Re-type password");
        create_button = new Button("Create",this::createaccout_action);
    }

    /**
     * Function for creating dialog content
     */
    public void create_dialog(){
        StreamResource res = new StreamResource("icon-dark.png", () -> {
            return LoginView.class.getClassLoader().getResourceAsStream("images/icon-dark.png");
        });
        Image logo = new Image(res,"noteIT");
        logo.setHeight("128px");
        logo.setWidth("128px");
        main_layout.add(logo);
        main_layout.add(new H1("Your note-taking starts here!"));
        email_field.setSizeFull();
        main_layout.add(email_field);
        HorizontalLayout hl1 = new HorizontalLayout(name_field,surname_field);
        main_layout.add(hl1);
        HorizontalLayout hl2 = new HorizontalLayout(password1_field,password2_field);
        main_layout.add(hl2);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_layout.getStyle().set("--lumo-font-family","Monospace");
        create_button.setSizeFull();
        main_layout.add(create_button);
        main_dialog.add(main_layout);
    }

    /**
     * Function for validating fields
     * @return true - all fields contain content, false - at least one empty
     */
    boolean validate_fields(){
        return !email_field.getValue().equals("") && !name_field.getValue().equals("")
                && !surname_field.getValue().equals("") && !password1_field.getValue().equals("")
                && !password2_field.getValue().equals("");
    }

    /**
     * Function for validating password fields
     * @return true - passwords match
     */
    boolean validate_passwords(){
        return password1_field.getValue().equals(password2_field.getValue());
    }

    /**
     * Function for checking is password meets policies
     * @param password
     * @return true - password correct
     */
    boolean checkpasswordpolicy(String password){
        return password.length() > 8 && password.length() < 55;
    }

    /**
     * Button action for creating account
     * @param e
     */
    private void createaccout_action(ClickEvent e){
        if ( validate_fields() ){
            if ( validate_passwords() ){
                if ( checkpasswordpolicy(password1_field.getValue())){
                    Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
                    int ans = dniu.createuser(name_field.getValue(),surname_field.getValue(),email_field.getValue(),"USER",password1_field.getValue());
                    if ( ans == 1 ){
                        Notification.show("Account requested! Wait for server owner response!");
                    }
                    else if (ans == -1){
                        Notification.show("Account requested but mail failed to send,\nContact system administrator!");
                    }
                    else if (ans == -3){
                        Notification.show("Account with this email address is already created!");
                    }
                    else{
                        Notification.show("Critical fail, check server log! ("+ans+")");
                    }
                }
                else{
                    Notification.show("Password didn't meet application policies!");
                }
            }
            else{
                password1_field.setValue("");
                password2_field.setValue("");
                Notification.show("Passwords didn't match!");
            }
        }
        else{
            Notification.show("Check fields! At least one empty");
        }
    }
}
