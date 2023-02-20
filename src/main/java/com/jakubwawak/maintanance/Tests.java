/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.maintanance;

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
        NoteIT_User user = new NoteIT_User(1000000);
        System.out.println(user.noteit_user_email);
    }
}
