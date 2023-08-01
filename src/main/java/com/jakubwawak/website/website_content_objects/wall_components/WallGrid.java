/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.wall_components;

import com.jakubwawak.database.Database_Wall;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Wall;
import com.vaadin.flow.component.grid.Grid;

import java.util.ArrayList;

/**
 * Object for storing and creating grid for Wall objects
 */
public class WallGrid {

    public Grid<Wall> grid;
    public ArrayList<Wall> content;
    int noteit_user_id;
    /**
     * Constructor
     */
    public WallGrid(int noteit_user_id){
        grid = new Grid<>(Wall.class,false);
        content = new ArrayList<>();
        this.noteit_user_id = noteit_user_id;
        prepare_object();
    }

    /**
     * Function for preparing object content
     */
    void prepare_object(){
        // setting content
        grid.addColumn(Wall::getNoteit_wall_name).setHeader("Wall Name");
        Database_Wall dw = new Database_Wall(NoteitApplication.database);
        content.clear();
        content.addAll(dw.get_user_wall(noteit_user_id));
        grid.setItems(content);
        // setting size
        grid.setWidth("250px");
    }

    /**
     * Function for reloading grid elements
     */
    public void reload(){
        Database_Wall dw = new Database_Wall(NoteitApplication.database);
        content.clear();
        content.addAll(dw.get_user_wall(noteit_user_id));
        grid.getDataProvider().refreshAll();
    }
}
