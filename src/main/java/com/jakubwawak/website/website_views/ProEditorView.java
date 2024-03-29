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
import com.jakubwawak.database.Database_Note;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.website.website_content_objects.vault_components.VaultSelector;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Template view for noteIT application
 */
@PageTitle("PROEditor")
@Route(value = "proeditor", layout = MainLayout.class)
public class ProEditorView extends VerticalLayout {

    VaultSelector vaultSelector;
    TextField notetitle_field;
    TextArea notecontent_field;
    Button create_button;

    MultiFileMemoryBuffer buffer1;
    Upload upload_component;

    String value;

    Note note;

    /**
     * Constructor
     */
    public ProEditorView(){
        this.note = NoteitApplication.current_note;
        NoteitApplication.main_layout = this;

        create_view();
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
        getStyle().set("--lumo-font-family","Monospace");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        create_button = new Button("Create Note",this::createnote_action);
        if (NoteitApplication.current_note_new == 1) {
            create_button.setText("Update Note");
        }

        create_button.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_SUCCESS);
    }

    /**
     * Function for creating other compontents like grids,list etc
     */
    void create_components(){
        notecontent_field = new TextArea("Note Content");
        notetitle_field = new TextField("Note Title");
        vaultSelector = new VaultSelector();

        buffer1 = new MultiFileMemoryBuffer();
        upload_component = new Upload(buffer1);

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
                    notecontent_field.setValue(notecontent_field.getValue()+"\n"+value);
                }
                else{
                    Notification.show("File is empty!");
                }
            }catch(Exception ex){
                NoteitApplication.log.add("IMPORT-NOTE-FAILED","Failed to import note ("+ex.toString()+")");
                Notification.show("Failed to import ("+ex.toString()+")");
            }
        });
    }

    /**
     * Function for creating view components
     */
    void create_view(){
        create_buttons();
        create_components();
        if (NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            // view can be created, user is logged
            prepare_view();
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
        add(new HorizontalLayout(notetitle_field,vaultSelector.combobox));
        notecontent_field.setSizeFull();
        add(upload_component);
        add(notecontent_field);
        add(new HorizontalLayout(create_button));

        if ( note != null ){
            notecontent_field.setValue(note.noteit_object_rawtext);
            notetitle_field.setValue(note.noteit_object_title);
            vaultSelector.set_selected_value(note.noteit_vault_id);
        }

    }


    //----------------section for actions and validators
    /**
     * Function for validating fields
     * @return boolean
     */
    boolean validate_fields(){
        return vaultSelector.combobox.getValue()!=null && !notecontent_field.getValue().equals("") && !notetitle_field.getValue().equals("");
    }
    /**
     * Function for creating note
     * @param e
     */
    private void createnote_action(ClickEvent e){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        int noteit_vault_id = -1;
        try{
            noteit_vault_id = vaultSelector.combobox.getValue().noteit_vault_id;
        }catch(Exception ex){}
        switch(create_button.getText()){
            case "Create Note":
            {
                if ( noteit_vault_id > 0 ){
                    if ( validate_fields() ){
                        note.noteit_vault_id = noteit_vault_id;
                        note.noteit_object_rawtext = notecontent_field.getValue();
                        note.noteit_object_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
                        note.noteit_object_title =  notetitle_field.getValue();
                        int ans = dn.add_note(note);
                        if ( ans == 1 ){
                            Notification.show("Note added to vault!");
                            create_button.getUI().ifPresent(ui -> ui.navigate("vault"));
                        }
                        else{
                            Notification.show("Database error. Check application log");
                        }
                    }
                }
                else{
                    Notification.show("Vault not set!");
                }
                break;
            }
            case "Update Note":
            {
                if ( noteit_vault_id > 0 ){
                    if ( validate_fields() ){
                        note.noteit_vault_id = noteit_vault_id;
                        note.noteit_object_rawtext = notecontent_field.getValue();
                        note.noteit_object_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
                        int ans = dn.update_note(note);
                        if ( ans == 1 ){
                            Notification.show("Note updated on vault!");
                        }
                        else{
                            Notification.show("Database error. Check application log");
                        }
                    }
                }
                else{
                    Notification.show("Vault not set!");
                }
                break;
            }
        }
    }
    //----------------end of section
}
