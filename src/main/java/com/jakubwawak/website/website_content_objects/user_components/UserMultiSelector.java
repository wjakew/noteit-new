/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.user_components;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;

import java.util.ArrayList;

/**
 * Object for creating MultiSelector with noteIT user data
 */
public class UserMultiSelector {
    ArrayList<StringElement> userlist;
    Database_NoteITUser dniu;
    public MultiSelectComboBox<StringElement> multiSelectComboBox;

    /**
     * Constructor
     */
    public UserMultiSelector(){
        dniu = new Database_NoteITUser(NoteitApplication.database);
        userlist = dniu.get_list_of_users();
        multiSelectComboBox = new MultiSelectComboBox<>("noteIT users");
        multiSelectComboBox.setItems(userlist);
        multiSelectComboBox.setItemLabelGenerator(StringElement::getContent);
    }

}
