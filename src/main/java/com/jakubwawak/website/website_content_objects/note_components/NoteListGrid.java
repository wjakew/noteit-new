/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.note_components;

import com.jakubwawak.database.Database_Note;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.Set;

public class NoteListGrid {

    public Grid<Note> grid;
    TextField grid_searchbox_field;

    ArrayList<Note> content;
    GridListDataView<Note> dataView;

    int note_vault_id;



    /**
     * Constructor
     * @param noteit_vault_id
     */
    public NoteListGrid(int noteit_vault_id){
        this.note_vault_id = noteit_vault_id;
        grid = new Grid(Note.class,false);
        grid.addColumn(Note::getNoteit_object_title).setHeader("List of Notes");
        init();
    }

    /**
     * Function for initializing object
     */
    void init(){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        content = new ArrayList<>();
        content.addAll(dn.get_note_list(note_vault_id));
        dataView = grid.setItems(content);
        grid.setItems(content);


        grid.setSizeFull(); grid.setWidth("300px");
    }
    /**
     * Function for reloading grid data
     */
    public void reload_grid(){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        content.clear();
        content.addAll(dn.get_note_list(note_vault_id));
        grid.getDataProvider().refreshAll();
    }

    /**
     * Function for loading selected object
     * @return Note
     */
    public Note get_selected_note(){
        Set<Note> selected = grid.getSelectedItems();
        for(Note note : selected){
            return note;
        }
        return null;
    }
}
