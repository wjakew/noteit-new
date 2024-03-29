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
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.jakubwawak.website.webview_components.AdminUserManagerDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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

    Grid<StringElement> log_grid;

    Button enablenewusers_button;
    Button adminusermanager_button;
    Button enable2fa_button;

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
        getStyle().set("--lumo-font-family","Monospace");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        enablenewusers_button = new Button("",this::enablenewusers_button_action);
        enablenewusers_button.setHeight("50px");enablenewusers_button.setWidth("150px");

        adminusermanager_button = new Button("User Accout Manager",this::adminusermanager_button_action);
        adminusermanager_button.setHeight("50px");adminusermanager_button.setWidth("150px");

        enable2fa_button = new Button("",this::enable2fa_button_action);
        enable2fa_button.setHeight("50px");enable2fa_button.setWidth("150px");

        if(NoteitApplication.database.check_2fa_flag() == 1 ){
            enable2fa_button.setText("1 : 2FA Enabled");
        }
        else if (NoteitApplication.database.check_2fa_flag() == 0){
            enable2fa_button.setText("0 : 2FA Disabled");
        }
        else{
            enable2fa_button.setText("2FA Error");
            enable2fa_button.setEnabled(false);
        }
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

        log_grid = new Grid<>(StringElement.class,false);
        log_grid.addColumn(StringElement::getContent).setHeader("Log Data");
        log_grid.setItems(NoteitApplication.database.get_application_log());
        log_grid.setWidth("650px");log_grid.setHeight("500px");

        left_layout.setSizeFull();center_layout.setSizeFull();right_layout.setSizeFull();
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
        enablenewusers_button.setSizeFull();adminusermanager_button.setSizeFull();
        enablenewusers_button.setHeight("90px");adminusermanager_button.setHeight("90px");
        enable2fa_button.setSizeFull();
        enable2fa_button.setHeight("90px");
        left_layout.add(enablenewusers_button,adminusermanager_button,enable2fa_button);
        // center layout
        center_layout.setSizeFull();
        // right layout
        right_layout.setSizeFull();
        right_layout.add(log_grid);

        // setting layouts
        add(new H1("Server Management"));
        main_layout.add(left_layout,center_layout,right_layout);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.getStyle().set("text-align", "center");

        add(main_layout);

        setSizeFull();
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
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
    private void enable2fa_button_action(ClickEvent e){
        if ( enable2fa_button.getText().contains("0") ){
            // enable 2fa
            NoteitApplication.database.twofactor_settings(1);
            enable2fa_button.setText("1 : 2FA Enabled");
        }
        else{
            // disable 2fa
            NoteitApplication.database.twofactor_settings(0);
            enable2fa_button.setText("0 : 2FA Disabled");
        }
    }
    //----------------end of section
}
