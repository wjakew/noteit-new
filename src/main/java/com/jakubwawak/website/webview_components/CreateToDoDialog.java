/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.webview_components;

import com.jakubwawak.database.Database_ToDo;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.ToDo;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Object for creating component for adding to-do object to appliaction
 */
public class CreateToDoDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    int noteit_todo_id;

    TextArea tododesc_area;
    DatePicker duedate_picker;

    Button action_button;
    ToDo current_object;
    /**
     * Constructor
     */
    public CreateToDoDialog(int noteit_todo_id){
        this.noteit_todo_id = noteit_todo_id;
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        current_object = new ToDo();
        prepare_dialog();
    }

    /**
     * Function for preparing components
     */
    void prepare_components(){
        duedate_picker = new DatePicker("Due to");
        tododesc_area = new TextArea("ToDo description");
        tododesc_area.setWidth("300px"); tododesc_area.setHeight("250px");
        action_button = new Button("Create ToDo!",this::createtodo_action);
        if ( noteit_todo_id > 0 ){
            action_button.setText("Update ToDo!");
            current_object = new ToDo(noteit_todo_id);
            duedate_picker.setValue(current_object.noteit_todo_deadline.toLocalDate());
            tododesc_area.setValue(current_object.noteit_todo_desc);
        }
    }

    /**
     * Function for preparing dialog components
     */
    void prepare_dialog(){
        prepare_components();
        main_layout.add(VaadinIcon.TICKET.create());
        main_layout.add(duedate_picker,tododesc_area,action_button);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    /**
     * Function for validating components
     * @return true - component validated, false - not
     */
    boolean validate_component(){
        return !tododesc_area.getValue().equals("") && duedate_picker.getValue() != null;
    }

    /**
     * Object for creating to-do action
     * @param ex
     */
    private void createtodo_action(ClickEvent ex){
        Database_ToDo dtd = new Database_ToDo(NoteitApplication.database);
        if ( validate_component() ){
            current_object.noteit_todo_desc = tododesc_area.getValue();
            current_object.noteit_todo_deadline = duedate_picker.getValue().atStartOfDay();
            current_object.noteit_todo_time = LocalDateTime.now(ZoneId.of("Europe/Warsaw"));
            int ans = -1;
            switch(action_button.getText()){
                case "Create ToDo!":
                {
                    ans = dtd.add_todo_object(current_object);
                    break;
                }
                case "Update ToDo!":
                {
                    ans = dtd.update_todo_object(current_object);
                    break;
                }
            }

            if ( ans == 1 ){
                Notification.show("Object modified!");
                main_dialog.close();
            }
            else{
                Notification.show("Database error, check server log!");
            }
        }
        else{
            Notification.show("Wrong user input. Check all fields");
        }
    }
}
