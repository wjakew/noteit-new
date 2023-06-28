/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.note_components;

import com.jakubwawak.database.Database_Note;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Function for importing note dialog
 */
public class ImportNoteDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    Upload upload_component;
    MultiFileMemoryBuffer buffer1;

    TextField notetitle_field;

    Button import_button;

    int noteit_vault_id;

    String value;

    /**
     * Constructor
     */
    public ImportNoteDialog(int noteit_vault_id){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        buffer1 = new MultiFileMemoryBuffer();
        upload_component = new Upload(buffer1);
        upload_component.setDropAllowed(true);
        this.noteit_vault_id = noteit_vault_id;
        notetitle_field = new TextField();
        notetitle_field.setPlaceholder("Note Title...");
        value = "";
        import_button = new Button("Import", VaadinIcon.DOWNLOAD.create(),this::import_action);
        import_button.setEnabled(false);
        prepare_dialog();
    }

    /**
     * Function for preparing dialog
     */
    void prepare_dialog(){
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
                    import_button.setEnabled(true);
                }
                else{
                    Notification.show("File is empty!");
                }
            }catch(Exception ex){
                NoteitApplication.log.add("IMPORT-NOTE-FAILED","Failed to import note ("+ex.toString()+")");
                Notification.show("Failed to import ("+ex.toString()+")");
            }
        });
        main_layout.add(new H2("Import Note"));
        main_layout.add(upload_component);
        main_layout.add(notetitle_field);
        main_layout.add(import_button);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");

        main_dialog.add(main_layout);
    }

    /**
     * Function for importing data to noteobject
     * @param e
     */
    private void import_action(ClickEvent e){
        if ( !value.equals("") ) {
            if (!notetitle_field.getValue().equals("")) {
                Database_Note dn = new Database_Note(NoteitApplication.database);
                Note note = new Note(notetitle_field.getValue(), value, NoteitApplication.noteit_vault_id);
                int ans = dn.add_note(note);
                if (ans == 1) {
                    Notification.show("Note imported!");
                    main_dialog.close();
                } else {
                    Notification.show("Import failed, check log.");
                }
            }
        }
        else{
            Notification.show("Nothing to import");
        }
    }

}
