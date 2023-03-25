/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.database.Database_ToDo;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.support_objects.ToDo;

import java.util.ArrayList;

/**
 * Object for running applicationt tests
 */
public class Tests {

    /**
     * Constructor
     */
    public Tests(){
        System.out.println("Starting tests!");
        run();
    }

    /**
     * Function for setting component tests
     */
    public void run(){
        Database_ToDo dtd = new Database_ToDo(NoteitApplication.database);
        ArrayList<ToDo> data = dtd.get_list_todo_active(1000000);
        System.out.println("exit");
    }
}
