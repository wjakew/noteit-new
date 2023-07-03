/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.user_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.maintanance.NoteIT_User;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.support_objects.StringElement;
import com.vaadin.flow.component.grid.Grid;

import java.util.Set;

/**
 * Object for selecting users from grid
 */
public class UserGridSelector {

    public Grid<StringElement> grid;

    /**
     * Constructor
     */
    public UserGridSelector(){
        grid = new Grid(NoteIT_User.class,false);
        prepare_components();
    }

    /**
     * Function for preparing object components
     */
    void prepare_components(){
        Database_NoteITUser dniu = new Database_NoteITUser(NoteitApplication.database);
        grid.addColumn(StringElement::getContent).setHeader("List of Users");
        grid.setItems(dniu.get_list_of_users());

        grid.setSizeFull();grid.setWidth("300px");grid.setHeight("400px");
    }

    /**
     * Function for returning selected user
     * @return NoteIT_User
     */
    public NoteIT_User get_selected_user(){
        Set<StringElement> selected = grid.getSelectedItems();
        for(StringElement obj : selected){
            return new NoteIT_User(obj.getContent().split(":")[1]);
        }
        return null;
    }

}
