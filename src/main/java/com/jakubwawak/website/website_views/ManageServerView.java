/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_views;
/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.jakubwawak.website.webview_components.AdminUserManagerDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

/**
 * Template view for noteIT application
 */
@PageTitle("Server Manager")
@Route(value = "/manage-server", layout = MainLayout.class)
public class ManageServerView extends VerticalLayout {

    public HorizontalLayout main_layout;
    public VerticalLayout left_layout,center_layout,right_layout;

    Button enablenewusers_button;
    Button adminusermanager_button;

    /**
     * Constructor
     */
    public ManageServerView(){
        create_view();
        NoteitApplication.main_layout = this;
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        enablenewusers_button = new Button("",this::enablenewusers_button_action);
        enablenewusers_button.setHeight("50px");enablenewusers_button.setWidth("150px");

        adminusermanager_button = new Button("User Accout Manager",this::adminusermanager_button_action);
        adminusermanager_button.setHeight("50px");adminusermanager_button.setWidth("150px");

    }

    /**
     * Function for creating other compontents like grids,list etc
     */
    void create_components(){
        main_layout = new HorizontalLayout();
        left_layout = new VerticalLayout();
        center_layout = new VerticalLayout();
        right_layout = new VerticalLayout();
        if ( NoteitApplication.database.get_newusercreationflag() == 1 ){
            enablenewusers_button.setText("1 : User creation enabled!");
        }
        else{
            enablenewusers_button.setText("0 : User creation disabled!");
        }
    }

    /**
     * Function for creating view components
     */
    void create_view(){
        create_buttons();
        create_components();
        if (NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            // view can be created, user is logged
            if ( NoteitApplication.logged.getNoteit_user_role().equals("SUPERUSER"))
                prepare_view();
            else{
                add(new H1("User without permission!"));
            }
        }
        else{
            // show error screen
            add(new H1("User login error!"));
        }
    }

    /**
     * Function for preparing view
     */
    void prepare_view(){
        // left layout
        left_layout.add(enablenewusers_button,adminusermanager_button);

        // center layout

        // right layout

        // setting layouts
        add(new H1("Server Management"));
        main_layout.add(left_layout,center_layout,right_layout);
        add(main_layout);

        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "center");
    }


    //----------------section for actions and validators
    private void enablenewusers_button_action(ClickEvent e){
        switch(enablenewusers_button.getText()){
            case "0 : User creation disabled!":
            {
                NoteitApplication.database.set_newusercreationflag(1);
                enablenewusers_button.setText("1 : User creation enabled!");
                Notification.show("Updated!");
                break;
            }
            case "1 : User creation enabled!":
            {
                NoteitApplication.database.set_newusercreationflag(0);
                enablenewusers_button.setText("0 : User creation disabled!");
                Notification.show("Updated!");
                break;
            }
        }
    }
    private void adminusermanager_button_action(ClickEvent e){
        AdminUserManagerDialog aumd = new AdminUserManagerDialog();
        add(aumd.main_dialog);
        aumd.main_dialog.open();
    }
    //----------------end of section
}
