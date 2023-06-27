/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_views;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.maintanance.NoteIT_User;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.website.webview_components.CreateAccountDialog;
import com.jakubwawak.website.webview_components.MessageComponent;
import com.jakubwawak.website.webview_components.TwoFactorComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
        NoteitApplication.main_layout = this;
        email_field = new TextField("E-Mail");
        password_field = new PasswordField("Password");
        login_button = new Button("Login",this::login_action);
        createaccount_button = new Button("", VaadinIcon.PLUS.create(),this::createaccount_action);

        prepare_view();

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
    }

    /**
     * Function for preparing views
     */
    void prepare_view(){
        H2 header_quote = new H2(NoteitApplication.database.get_random_loginlabel());
        add(header_quote);
        StreamResource res = new StreamResource("icon.png", () -> {
            return LoginView.class.getClassLoader().getResourceAsStream("images/icon.png");
        });
        HorizontalLayout hl_main = new HorizontalLayout();
        VerticalLayout vl_left = new VerticalLayout(); VerticalLayout vl_right = new VerticalLayout();
        Image logo = new Image(res,"noteIT");
        logo.setHeight("512px");
        logo.setWidth("512px");
        vl_left.add(logo);
        vl_right.add(new H2("noteIT"),email_field,password_field);

        if (NoteitApplication.database.get_newusercreationflag() == 1){
            vl_right.add(new HorizontalLayout(createaccount_button,login_button));
        }
        else{
            vl_right.add(login_button);
        }

        vl_left.setSizeFull();
        vl_left.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vl_left.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        vl_left.getStyle().set("text-align", "center");

        vl_right.setSizeFull();
        vl_right.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        vl_right.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        vl_right.getStyle().set("text-align", "center");

        hl_main.add(vl_left,vl_right);
        add(hl_main);
        add(new Text(NoteitApplication.build+"/"+NoteitApplication.version));
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
                    NoteitApplication.logged = new NoteIT_User();
                    NoteitApplication.logged.twofactor_flag = 1;
                    TwoFactorComponent tfc = new TwoFactorComponent(NoteitApplication.logged.getNoteit_user_id());
                    tfc.create_dialog();
                    add(tfc.main_dialog);
                    tfc.main_dialog.open();
                    break;
                }
                case 1:
                {
                    // login successfull - 2fa is not on - go to home page
                    //go to home page
                    login_button.getUI().ifPresent(ui ->
                            ui.navigate("home"));
                    break;
                }
                case 0:
                {
                    // user with given email not found
                    Notification.show(email_field.getValue()+" not found in database!");
                    email_field.setValue("");
                    password_field.setValue("");
                    break;
                }
                case -2:
                {
                    // user is not active
                    MessageComponent mc = new MessageComponent("User is not active.Contact server administrator!");
                    add(mc.main_dialog);
                    mc.main_dialog.open();
                    break;
                }
                case -3:
                {
                    // user email not confirmed but logged successfully
                    MessageComponent mc = new MessageComponent("User email is not confirmed! Check your mail.");
                    add(mc.main_dialog);
                    mc.main_dialog.open();
                    // open mail-confirm page!
                    login_button.getUI().ifPresent(ui ->
                            ui.navigate("mailconfirm"));
                    break;
                }
                case -4:
                {
                    // database error
                    Notification.show("Database error! Check server log!");
                    break;
                }
                default:
                {
                    Notification.show("Wrong login or password!");
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
