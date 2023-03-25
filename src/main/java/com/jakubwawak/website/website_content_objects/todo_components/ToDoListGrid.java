/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.todo_components;

import com.jakubwawak.database.Database_ToDo;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.ToDo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.Set;

/**
 * Component for showing grid and search field for grid
 */
public class ToDoListGrid {

    Grid<ToDo> grid;
    TextField grid_searchbox_field;

    ArrayList<ToDo> content;

    /**
     * Constructor
     * @param noteit_user_id
     */
    public ToDoListGrid(int noteit_user_id){
        grid = new Grid(ToDo.class,false);
        grid.setWidth("550px");grid.setHeight("400px");
        grid_searchbox_field = new TextField();
        grid_searchbox_field.setPlaceholder("Search Your todos..");
        content = null;
        prepare_components();
    }

    /**
     * Function for preparing components
     */
    void prepare_components(){
        Database_ToDo dtd = new Database_ToDo(NoteitApplication.database);
        content = dtd.get_list_todo_active(NoteitApplication.logged.getNoteit_user_id());
        grid.addColumn(ToDo::getNoteit_todo_id).setHeader("ToDo ID");
        grid.addColumn(ToDo::getNoteit_todo_desc).setHeader("Description");
        grid.setItems(content);
        GridListDataView<ToDo> dataView = grid.setItems(content);
        grid_searchbox_field.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        grid_searchbox_field.setValueChangeMode(ValueChangeMode.EAGER);
        grid_searchbox_field.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(todo_obj -> {
            try{
                String searchTerm = grid_searchbox_field.getValue().trim();

                if (searchTerm.isEmpty())
                    return true;

                boolean matchesFullName = todo_obj.getNoteit_todo_desc().contains(
                        searchTerm);
                boolean matchesEmail = todo_obj.getNoteit_todo_id() == Integer.parseInt(searchTerm);

                return matchesFullName || matchesEmail;
            }catch(Exception ex){
                return false;
            }
        });
    }

    /**
     * Function for updating grid component
     */
    public void update(){
        Database_ToDo dtd = new Database_ToDo(NoteitApplication.database);
        content = dtd.get_list_todo_active(NoteitApplication.logged.getNoteit_user_id());
        grid.getDataProvider().refreshAll();
    }

    /**
     * Function for getting selected object
     * @return ID of selected to-do object, -1 - if selection is empty
     */
    public int get_selected_object_id(){
        Set<ToDo> selected = grid.getSelectedItems();
        for(ToDo selected_object : selected){
            return  selected_object.getNoteit_todo_id();
        }
        return -1;
    }
}
