/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.noteit;

import com.jakubwawak.maintanance.ConsoleColors;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Object for creating menu data for noteit application
 */
public class NoteitMenu {

    public ArrayList<String> menuhistory;

    /**
     * Constructor
     */
    public NoteitMenu(){
        menuhistory = new ArrayList<>();
    }

    /**
     * Function for running menu
     */
    public void run(){
        String user_input;
        Scanner sc = new Scanner(System.in);
        System.out.print(ConsoleColors.GREEN_BOLD+"noteit>"+ConsoleColors.RESET);
        user_input = sc.nextLine();
    }

    /**
     * Function for managing user input
     * @param user_input
     */
    void mind(String user_input){
        String[] words = user_input.split(" ");
        for(String word : words){
            switch(word){
                case "exit":
                {
                    System.out.println("Bye!");
                    System.exit(0);
                }
                case "setrole":
                {

                }
            }
        }
    }

    /**
     * Function for setting user role on database
     * @param user_input
     * setrole noteit_user_login -role
     * -role - admin, user
     */
    void function_setrole(String user_input){
        String [] words = user_input.split(" ");
        if ( words.length == 3 ){

        }
        else{
            System.out.println("Wrong command usage!");
        }
    }

}
