/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.webview_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.maintanance.NoteIT_User;
import com.jakubwawak.noteit.NoteitApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

/**
 * Object for creating two-factor authentication
 */
public class TwoFactorComponent {
    public Dialog main_dialog;
    public VerticalLayout main_layout;

    TextField code_field;
    Button login_button;
    NoteIT_User user;

    /**
     * Constructor
     * @param noteit_user_id
     */
    public TwoFactorComponent(int noteit_user_id ){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        code_field = new TextField();
        code_field.setPlaceholder("Code");
        login_button = new Button("Log in!",this::login);
        user = new NoteIT_User(noteit_user_id);
    }

    /**
     * Function for creating dialog components
     */
    public void create_dialog(){
        main_layout.add(VaadinIcon.KEY.create());
        main_layout.add(new H1("noteIT guard"));
        main_layout.add(new Text("Type your 2FA code for account "+user.getNoteit_user_email()));
        main_layout.add(code_field);
        main_layout.add(login_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    /**
     * Function for logging user with 2fa code
     * @param e
     */
    private void login(ClickEvent e){
        if ( !code_field.getValue().equals("") ){
            Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
            int ans = dniu.login_user(code_field.getValue());
            switch(ans){
                // @return 1 - user logged successfully, -1 - database error -2 - wrong 2fa code, -3 - 2fa code too old, -4 - 2fa code not active
                case 1:
                {
                    // logged successfully
                    // move to home page
                    login_button.getUI().ifPresent(ui ->
                            ui.navigate("home"));
                    break;
                }
                case -1:
                {
                    Notification.show("Database error! Check server log!");
                    break;
                }
                case -2:
                {
                    Notification.show("Wrong code! Check code and type again!");
                    break;
                }
                case -3:
                {
                    MessageComponent mc = new MessageComponent("Code too old, please login again!");
                    main_layout.add(mc.main_dialog);
                    mc.main_dialog.open();
                    login_button.getUI().ifPresent(ui -> ui.navigate("login"));
                    mc.main_dialog.close();
                    break;
                }
                case -4:
                {
                    MessageComponent mc = new MessageComponent("Inserted code is not active");
                    main_layout.add(mc.main_dialog);
                    mc.main_dialog.open();
                    login_button.getUI().ifPresent(ui -> ui.navigate("login"));
                    break;
                }
            }
        }
    }

}
