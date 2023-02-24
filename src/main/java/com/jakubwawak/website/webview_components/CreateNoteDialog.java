/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.sun.source.doctree.VersionTree;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Component for creating notes
 */
public class CreateNoteDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    TextField notetitle_field;
    TextArea notecontent_field;
    Button create_button;

    /**
     * Constructor
     */
    public CreateNoteDialog(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        notecontent_field = new TextArea("Note Content");
        notetitle_field = new TextField("Note Title");
        prepare_components();
        prepare_dialog();
    }

    /**
     * Function for preparing components
     */
    void prepare_components(){

    }

    /**
     * Function for preparing dialog components
     */
    void prepare_dialog(){

    }
}
