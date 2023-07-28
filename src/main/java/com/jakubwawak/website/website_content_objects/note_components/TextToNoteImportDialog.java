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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Object for creating dialog window for importing text to notes
 */
public class TextToNoteImportDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;
    String value;

    TextField title_field;

    VaultSelector vaultSelector;
    Button import_button;

    /**
     * Constructor
     * @param value
     */
    public TextToNoteImportDialog(String value){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        vaultSelector = new VaultSelector();
        title_field = new TextField();
        title_field.setPlaceholder("Note Title");
        this.value = value;
        import_button = new Button("Import",this::importbutton_action);
        import_button.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY);
        prepare_dialog();
    }

    /**
     * Function for preparing dialog
     */
    void prepare_dialog(){
        main_layout.add(new H6("Save Note"));
        main_layout.add(new HorizontalLayout(title_field,vaultSelector.combobox));
        main_layout.add(import_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    private void importbutton_action(ClickEvent e){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        int noteit_vault_id = -1;
        Note note = new Note(null);
        try{
            noteit_vault_id = vaultSelector.combobox.getValue().noteit_vault_id;
        }catch(Exception ex){}
        if ( noteit_vault_id > 0 && !title_field.getValue().equals("")){
            note.noteit_vault_id = noteit_vault_id;
            note.noteit_object_rawtext = value;
            note.noteit_object_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
            note.noteit_object_title =  title_field.getValue();
            int ans = dn.add_note(note);
            if ( ans == 1 ){
                main_dialog.close();
                Notification.show("Note added to vault!");
            }
            else{
                Notification.show("Database error. Check application log");
            }
        }
        else{
            Notification.show("Wrong user input!");
        }
    }
}
