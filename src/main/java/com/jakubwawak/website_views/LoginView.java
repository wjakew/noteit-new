/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website_views;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.webview_components.CreateAccountDialog;
import com.jakubwawak.webview_components.TwoFactorComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Object for creating login view
 */
@PageTitle("Welcome to noteIT")
@Route(value = "/login")
@RouteAlias(value = "/")
public class LoginView extends VerticalLayout {

    TextField email_field;
    PasswordField password_field;
    Button login_button, createaccount_button;

    /**
     * Constructor
     */
    public LoginView(){
        this.getElement().setAttribute("theme", Lumo.DARK); // loading state
        email_field = new TextField("E-Mail");
        password_field = new PasswordField("Password");
        login_button = new Button("Login");
        createaccount_button = new Button("", VaadinIcon.PLUS.create(),this::createaccount_action);

        prepare_view();

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    /**
     * Function for preparing views
     */
    void prepare_view(){
        StreamResource res = new StreamResource("icon.png", () -> {
            return LoginView.class.getClassLoader().getResourceAsStream("images/icon.png");
        });
        Image logo = new Image(res,"noteIT");
        logo.setHeight("512px");
        logo.setWidth("512px");
        add(logo);
        add(email_field);
        add(password_field);
        add(new HorizontalLayout(createaccount_button,login_button));
    }

    /**
     * Function for validating fields
     * @return true if fields are correct
     */
    boolean validate_fields(){
        return !email_field.getValue().equals("") && !password_field.getValue().equals("");
    }

    /**
     * Function for creating account
     * @param e
     */
    private void createaccount_action(ClickEvent e){
        CreateAccountDialog cad = new CreateAccountDialog();
        cad.create_dialog();
        add(cad.main_dialog);
        cad.main_dialog.open();
    }

    /**
     * Function for login into the application
     * @param e
     */
    private void login_action(ClickEvent e){
        if ( validate_fields() ){
            // fields are correct
            Database_NoteITUser dni = new Database_NoteITUser(NoteitApplication.database);
            int flag = dni.login_user(email_field.getValue(),password_field.getValue());
            switch(flag){
                case 2:
                {
                    // login successfull - show 2fa window to confirm
                    NoteitApplication.logged.twofactor_flag = 1;
                    TwoFactorComponent tfc = new TwoFactorComponent(NoteitApplication.logged.getNoteit_user_id());
                    tfc.create_dialog();
                    add(tfc.main_dialog);
                    break;
                }
                case 1:
                {
                    // login successfull - 2fa is not on - go to home page
                    break;
                }
                case 0:
                {
                    // user with given email not found
                    break;
                }
                case -2:
                {
                    // user is not active
                    break;
                }
                case -3:
                {
                    // user email not confirmed but logged successfully
                    break;
                }
                case -4:
                {
                    // database error
                    break;
                }
            }
        }
        else{
            // fields are wrong
            Notification.show("EMail or Password field are empty!");
        }
    }
}
