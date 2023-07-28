/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_layouts;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_content_objects.note_components.CreateNoteDialog;
import com.jakubwawak.website.website_content_objects.note_components.TextToNoteImportDialog;
import com.jakubwawak.website.website_content_objects.todo_components.CreateToDoDialog;
import com.jakubwawak.website.website_content_objects.todo_components.ToDoListDialog;
import com.jakubwawak.website.website_content_objects.todo_components.ToDoListGrid;
import com.jakubwawak.website.website_content_objects.vault_components.VaultListDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.theme.lumo.Lumo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Main Web Application layout
 */
public class MainLayout extends AppLayout {

    /**
     * Constructor
     */

    Button home_button, logout_button, adminpanel_button, todolist_button, vaultlist_button;
    Button proeditor_button;

    MultiFileMemoryBuffer buffer1;
    Upload upload_component;
    String value;

    Button addtodo_button,addnote_button;
    DrawerToggle main_toggle;

    public MainLayout(){
        this.getElement().setAttribute("theme", Lumo.DARK);
        home_button = new Button("NoteIT",this::homebutton_action);
        logout_button = new Button("Log out!",this::logoutbutton_action);
        addtodo_button = new Button("Add new ToDo",VaadinIcon.PLUS.create(),this::addtodobutton_action);
        addnote_button = new Button("Add new Note",VaadinIcon.NOTEBOOK.create(),this::addnotebutton_action);
        adminpanel_button = new Button("Manage Server",VaadinIcon.COMPILE.create(),this::manageserverbutton_action);
        todolist_button = new Button("Your ToDo List",VaadinIcon.CHECK.create(),this::todolistbutton_action);
        vaultlist_button = new Button("Your Vaults",VaadinIcon.BOOK.create(),this::vaultistbutton_action);
        proeditor_button = new Button("Pro Note Editor",VaadinIcon.PENCIL.create(),this::openproeditorbutton_action);
        buffer1 = new MultiFileMemoryBuffer();
        upload_component = new Upload(buffer1);
        upload_component.setDropAllowed(true);
        main_toggle = new DrawerToggle();
        this.setDrawerOpened(false);


        upload_component.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer1.getInputStream(fileName);
            try{
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader bf = new BufferedReader(isr);
                int lines = 0;
                while(bf.ready()){
                    value = value + bf.readLine() + "\n";
                    lines++;
                }
                if ( lines > 0 ){
                    Notification.show("Loaded "+lines+" lines!");
                    // open window to upload component
                    TextToNoteImportDialog ttnid = new TextToNoteImportDialog(value);
                    NoteitApplication.main_layout.add(ttnid.main_dialog);
                    ttnid.main_dialog.open();
                }
                else{
                    Notification.show("File is empty!");
                }
            }catch(Exception ex){
                NoteitApplication.log.add("IMPORT-NOTE-FAILED","Failed to import note ("+ex.toString()+")");
                Notification.show("Failed to import ("+ex.toString()+")");
            }
        });

        logout_button.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_PRIMARY);
        home_button.addThemeVariants(ButtonVariant.LUMO_CONTRAST,ButtonVariant.LUMO_PRIMARY);
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
     * Function for opening dialog for creating new note object
     * @param e
     */
    private void addnotebutton_action(ClickEvent e){
        CreateNoteDialog cnd = new CreateNoteDialog(0);
        NoteitApplication.main_layout.add(cnd.main_dialog);
        cnd.main_dialog.open();
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
     * Function for opening proeditor
     * @param e
     */
    private void openproeditorbutton_action(ClickEvent e){
        // open pro editor
        proeditor_button.getUI().ifPresent(ui ->
                ui.navigate("proeditor"));
    }
    /**
     * Function for creating header
     */
    private void createHeader(){
        FlexLayout center_layout = new FlexLayout();
        center_layout.setSizeFull();
        center_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        center_layout.setAlignItems(FlexComponent.Alignment.CENTER);

        FlexLayout left_layout = new FlexLayout();
        left_layout.setSizeFull();
        left_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        left_layout.setAlignItems(FlexComponent.Alignment.START);

        FlexLayout right_layout = new FlexLayout();
        right_layout.setSizeFull();
        right_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        right_layout.setAlignItems(FlexComponent.Alignment.END);

        left_layout.add(main_toggle,home_button);

        center_layout.add(addtodo_button,addnote_button);

        right_layout.add(logout_button);


        if ( NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            HorizontalLayout header = new HorizontalLayout(left_layout,center_layout,right_layout);
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
            proeditor_button.setSizeFull(); upload_component.setSizeFull();
            adminpanel_button.setHeight("50px");logout_button.setHeight("50px");todolist_button.setHeight("50px");vaultlist_button.setHeight("50px");
            proeditor_button.setHeight("50px");upload_component.setHeight("100px");
            VerticalLayout vl = new VerticalLayout();

            vl.setSizeFull();
            vl.setSpacing(true);
            vl.getThemeList().add("spacing-xl");
            vl.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            vl.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
            vl.getStyle().set("text-align", "center");

            vl.getStyle().set("background-color","#000000");

            if ( NoteitApplication.logged.getNoteit_user_role().equals("SUPERUSER")){
                vl.add(adminpanel_button);
            }
            vl.add(vaultlist_button);
            vl.add(todolist_button);
            vl.add(proeditor_button);
            vl.add(new H6("Drop Zone"));
            vl.add(upload_component);

            vl.add(new Text(NoteitApplication.build+"/"+NoteitApplication.version));
            addToDrawer(vl);
        }
    }

}
