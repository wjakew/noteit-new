/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_layouts;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_content_objects.todo_components.CreateToDoDialog;
import com.jakubwawak.website.website_content_objects.todo_components.ToDoListDialog;
import com.jakubwawak.website.website_content_objects.todo_components.ToDoListGrid;
import com.jakubwawak.website.website_content_objects.vault_components.VaultListDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Main Web Application layout
 */
public class MainLayout extends AppLayout {

    /**
     * Constructor
     */

    Button home_button, logout_button, adminpanel_button, todolist_button, vaultlist_button;

    Button addtodo_button;
    DrawerToggle main_toggle;

    public MainLayout(){
        this.getElement().setAttribute("theme", Lumo.DARK);
        home_button = new Button("NoteIT",this::homebutton_action);
        logout_button = new Button("Log out!",this::logoutbutton_action);
        addtodo_button = new Button("Add new ToDo",VaadinIcon.PLUS.create(),this::addtodobutton_action);
        adminpanel_button = new Button("Manage Server",VaadinIcon.COMPILE.create(),this::manageserverbutton_action);
        todolist_button = new Button("Your ToDo List",VaadinIcon.CHECK.create(),this::todolistbutton_action);
        vaultlist_button = new Button("Your Vaults",VaadinIcon.BOOK.create(),this::vaultistbutton_action);
        main_toggle = new DrawerToggle();
        this.setDrawerOpened(false);
        createHeader();
        createMenu();
    }

    /**
     * Function for opening vault list
     * @param e
     */
    private void vaultistbutton_action(ClickEvent e){
        VaultListDialog vld = new VaultListDialog();
        NoteitApplication.main_layout.add(vld.main_dialog);
        vld.main_dialog.open();
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
     * Function for opening server admin panel
     * @param e
     */
    private void manageserverbutton_action(ClickEvent e){
        adminpanel_button.getUI().ifPresent(ui -> ui.navigate("manage-server"));
    }

    /**
     * Function for opening dialog for creating new to-do object
     * @param e
     */
    private void addtodobutton_action(ClickEvent e){
        CreateToDoDialog ctdd = new CreateToDoDialog(0);
        NoteitApplication.main_layout.add(ctdd.main_dialog);
        ctdd.main_dialog.open();
    }

    /**
     * Function for opening dialog for showing todolist
     * @param e
     */
    private void todolistbutton_action(ClickEvent e){
        ToDoListDialog tld = new ToDoListDialog();
        NoteitApplication.main_layout.add(tld.main_dialog);
        tld.main_dialog.open();
    }

    /**
     * Function for creating header
     */
    private void createHeader(){
        if ( NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            HorizontalLayout header = new HorizontalLayout(main_toggle,home_button,addtodo_button);
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

    /**
     * Function for creating side menu (drawer)
     */
    private void createMenu(){
        if ( NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            adminpanel_button.setSizeFull();logout_button.setSizeFull();todolist_button.setSizeFull();vaultlist_button.setSizeFull();
            adminpanel_button.setHeight("50px");logout_button.setHeight("50px");todolist_button.setHeight("50px");vaultlist_button.setHeight("50px");
            VerticalLayout vl = new VerticalLayout();
            if ( NoteitApplication.logged.getNoteit_user_role().equals("SUPERUSER")){
                vl.add(adminpanel_button);
            }
            vl.add(vaultlist_button);
            vl.add(todolist_button);

            vl.add(logout_button);
            vl.add(new Text(NoteitApplication.build+"/"+NoteitApplication.version));
            vl.setAlignItems(FlexComponent.Alignment.CENTER);
            addToDrawer(vl);
        }
    }

}
