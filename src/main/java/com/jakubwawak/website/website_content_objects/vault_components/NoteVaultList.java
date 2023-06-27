/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Note;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Object for loading grid content of notes in the selected vault
 */
public class NoteVaultList {

    Grid<Note> content_grid;
    TextField search_field;

    int noteit_vault_id;

    /**
     * Constructor
     * @param noteit_vault_id
     */
    public NoteVaultList(int noteit_vault_id){
        this.noteit_vault_id = noteit_vault_id;
        content_grid = new Grid<>(Note.class,false);
        search_field = new TextField();
        search_field.setPlaceholder("Search Notes...");
        prepare_object();
    }

    /**
     * Function for preparing objects
     */
    void prepare_object(){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        content_grid.addColumn(Note::getNoteit_object_title).setHeader("Note Title");
        content_grid.addColumn(Note::getNoteit_object_rawtext).setHeader("Content");
        content_grid.setItems(dn.get_note_list(noteit_vault_id));
        content_grid.setSizeFull();content_grid.setWidth("400px");
    }

}
