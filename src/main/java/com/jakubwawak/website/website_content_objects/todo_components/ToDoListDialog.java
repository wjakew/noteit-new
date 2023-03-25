/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.todo_components;

import com.jakubwawak.database.Database_ToDo;
import com.jakubwawak.noteit.NoteitApplication;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Component for showing dialog window for to-do listing
 */
public class ToDoListDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;
    ToDoListGrid toDoListGrid;

    Button markasdone_button, createnew_button, showdata_button;

    /**
     * Constructor
     */
    public ToDoListDialog(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        toDoListGrid = new ToDoListGrid(NoteitApplication.logged.getNoteit_user_id());
        markasdone_button = new Button("Mark as done", VaadinIcon.CHECK.create(),this::markasdonebutton_action);
        createnew_button = new Button("Create new",VaadinIcon.PLUS.create(),this::createnewbutton_action);
        showdata_button = new Button(VaadinIcon.MAGIC.create());
        prepare_dialog();
    }

    /**
     * Function for preparing dialog components
     */
    void prepare_dialog(){
        main_layout.add(new H3("Your ToDo List"));
        main_layout.add(new HorizontalLayout(toDoListGrid.grid_searchbox_field,showdata_button));
        main_layout.add(toDoListGrid.grid);
        main_layout.add(new HorizontalLayout(createnew_button,markasdone_button));
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    /**
     * Function for marking to-do as done
     * @param e
     */
    private void markasdonebutton_action(ClickEvent e){
        Database_ToDo dtd = new Database_ToDo(NoteitApplication.database);
        int noteit_todo_id = toDoListGrid.get_selected_object_id();
        if ( noteit_todo_id > 0 ){
            int ans = dtd.set_todo_inactive(noteit_todo_id);
            if ( ans == 1){
                Notification.show("ToDo object set to 1!");
            }
            else{
                Notification.show("Database error, check log");
            }
        }
        else{
            Notification.show("No object selected!");
        }
        toDoListGrid.update();
    }

    /**
     * Function for adding new to-do object
     * @param e
     */
    private void createnewbutton_action(ClickEvent e){
        CreateToDoDialog ctdd = new CreateToDoDialog(0);
        main_layout.add(ctdd.main_dialog);
        ctdd.main_dialog.open();
        toDoListGrid.update();
    }
}
