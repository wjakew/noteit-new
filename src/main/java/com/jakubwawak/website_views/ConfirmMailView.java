/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website_views;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.Lumo;

@PageTitle("Mail Confirm")
@Route(value = "/mailconfirm")
public class ConfirmMailView extends VerticalLayout {

    H1 header;
    Button confirm_button;
    TextField activationcode_field;

    /**
     * Constructor
     */
    public ConfirmMailView(){
        this.getElement().setAttribute("theme", Lumo.DARK); // loading state
        header = new H1("Confirm your E-Mail!");
        confirm_button = new Button("Submit!",this::confirmbutton_action);
        activationcode_field = new TextField();
        prepare_view();
    }

    /**
     * Function for preparing components
     */
    void prepare_view(){
        StreamResource res = new StreamResource("icon-dark.png", () -> {
            return LoginView.class.getClassLoader().getResourceAsStream("images/icon-dark.png");
        });
        Image logo = new Image(res,"noteIT");
        logo.setHeight("256px");
        logo.setWidth("256px");
        add(logo);
        add(header);
        add(activationcode_field);
        add(confirm_button);
        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    /**
     * Function for confirming button action
     * @param e
     */
    private void confirmbutton_action(ClickEvent e){
        if ( !activationcode_field.getValue().equals("") ){
            Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
            int ans = dniu.confirm_email(activationcode_field.getValue());
            switch(ans){
                case 1:
                {
                    Notification.show("Email confirmed!");
                    confirm_button.getUI().ifPresent(ui ->
                            ui.navigate("login"));;
                    break;
                }
                case -2:
                {
                    Notification.show("Given code don't exist!");
                    break;
                }
            }
        }
    }
}
