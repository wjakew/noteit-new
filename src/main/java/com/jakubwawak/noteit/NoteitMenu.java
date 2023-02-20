/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.noteit;

import com.jakubwawak.database.Database_NoteITUser;
import com.jakubwawak.maintanance.ConsoleColors;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Object for creating menu data for noteit application
 */
public class NoteitMenu {

    public ArrayList<String> menuhistory;
    boolean run;

    /**
     * Constructor
     */
    public NoteitMenu(){
        menuhistory = new ArrayList<>();
        run = true;
    }

    /**
     * Function for running menu
     */
    public void run(){
        String user_input;
        Scanner sc = new Scanner(System.in);
        while(run){
            System.out.print(ConsoleColors.GREEN_BOLD+"noteit>"+ConsoleColors.RESET);
            user_input = sc.nextLine();
            mind(user_input);
        }
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
                    run = false;
                    System.exit(0);
                }
                case "setrole":
                {
                    function_setrole(user_input);
                    break;
                }
                case "2faenable":
                {
                    function_2faenable(user_input);
                    break;
                }
                case "2fadisable":
                {
                    function_2fadisable(user_input);
                    break;
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
            Database_NoteITUser dni = new Database_NoteITUser(NoteitApplication.database);
            int ans = 0;
            switch(words[2]){
                case "admin":
                {
                    ans = dni.setuserrole(dni.getuserid_byemail(words[1]),1);
                    break;
                }
                case "user":
                {
                    ans = dni.setuserrole(dni.getuserid_byemail(words[1]),0);
                    break;
                }
            }
            if ( ans == 1 ){
                System.out.println("Role updated!");
            }
            else{
                System.out.println("Error updating role!");
            }
        }
        else{
            System.out.println("Wrong command usage!");
        }
    }

    /**
     * Function for setting 2fa flag
     * @param user_input
     */
    void function_2faenable(String user_input){
        if ( user_input.equals("2faenable")){
            NoteitApplication.database.twofactor_settings(1);
        }
        else{
            System.out.println("Wrong command usage!");
        }
    }

    /**
     * Function for setting 2fa flag
     * @param user_input
     */
    void function_2fadisable(String user_input){
        if ( user_input.equals("2fadisable")){
            NoteitApplication.database.twofactor_settings(0);
        }
        else{
            System.out.println("Wrong command usage!");
        }
    }

}
