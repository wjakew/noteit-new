/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Component for creating message window
 */
public class MessageComponent {

    public Dialog main_dialog;
    public VerticalLayout main_layout;
    Label message_label;
    Button ok_button;


    /**
     * Constructor
     * @param message
     */
    public MessageComponent(String message){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        ok_button = new Button("Ok", VaadinIcon.CHECK.create(),this::closewindow_action);
        message_label = new Label(message);
        create_dialog();
    }

    /**
     * Function for creating dialog component
     */
    void create_dialog(){
        main_layout.add(message_label,ok_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_layout.getStyle().set("--lumo-font-family","Monospace");
        main_dialog.add(main_layout);
    }

    /**
     * Function for closing window
     * @param e
     */
    private void closewindow_action(ClickEvent e){
        main_dialog.close();
    }
}
