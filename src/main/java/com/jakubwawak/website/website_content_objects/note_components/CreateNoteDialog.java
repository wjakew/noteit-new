/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.note_components;

import com.jakubwawak.database.Database_Note;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.website.website_content_objects.vault_components.VaultSelector;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Component for creating notes
 */
public class CreateNoteDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    VaultSelector vaultSelector;
    TextField notetitle_field;
    TextArea notecontent_field;
    Button create_button;

    Button openproeditor_button;

    int noteit_vault_id;
    Note note;

    /**
     * Constructor
     */
    public CreateNoteDialog(int noteit_object_id){
        this.noteit_vault_id = noteit_object_id;
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        notecontent_field = new TextArea("Note Content");
        notetitle_field = new TextField("Note Title");
        vaultSelector = new VaultSelector();
        create_button = new Button("Create Note",this::createnote_action);
        openproeditor_button = new Button("Go PRO",this::gotopro_action);
        openproeditor_button.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_PRIMARY);
        prepare_components();
        prepare_dialog();
    }

    /**
     * Function for preparing components
     */
    void prepare_components(){
        notetitle_field.setPlaceholder("Note title...");
        notecontent_field.setPlaceholder("Note content...");
        notecontent_field.setHeight("500px"); notecontent_field.setWidth("600px");
        if ( noteit_vault_id != 0 ){
            // note is to be edited
            note = new Note(noteit_vault_id);
            notetitle_field.setValue(note.noteit_object_title);
            notecontent_field.setValue(note.noteit_object_rawtext);
            create_button.setText("Update Note");
            NoteitApplication.current_note_new = 1;
        }
        else{
            // new note
            note = new Note(null);
            NoteitApplication.current_note_new = 0;
        }
    }

    /**
     * Function for preparing dialog components
     */
    void prepare_dialog(){
        main_layout.add(new H3("Note Editor"));
        main_layout.add(openproeditor_button);
        main_layout.add(new HorizontalLayout(notetitle_field,vaultSelector.combobox));
        main_layout.add(notecontent_field);
        main_layout.add(create_button);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");

        main_dialog.add(main_layout);
    }

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
                            main_dialog.close();
                            Notification.show("Note added to vault!");
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
                            main_dialog.close();
                            Notification.show("Note added to vault!");
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

    /**
     * Function for calling action for gotopro_button
     * @param ex
     */
    private void gotopro_action(ClickEvent ex){
        int noteit_vault_id = -1;
        try{
            noteit_vault_id = vaultSelector.combobox.getValue().noteit_vault_id;
        }catch(Exception e){Notification.show(e.toString());}
        if ( noteit_vault_id > 0 ){
            note.noteit_vault_id = noteit_vault_id;
            note.noteit_object_rawtext = notecontent_field.getValue();
            note.noteit_object_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
            note.noteit_object_title =  notetitle_field.getValue();
            NoteitApplication.current_note = note;
            // open pro editor
            openproeditor_button.getUI().ifPresent(ui ->
                    ui.navigate("proeditor"));
        }
        else{
            Notification.show("Vault not set!");
        }
    }
}
