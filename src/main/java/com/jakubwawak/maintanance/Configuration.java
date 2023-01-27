/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.maintanance;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Object for setting and keeping the configuration
 */
public class Configuration {

    public String database_name,database_user,database_password,database_ip;
    public ArrayList<String> raw_lines;
    public int debug_flag;
    public boolean error, newfile;
    String filenamesrc = "config.noteit";

    File dir;

    /**
     * Constructor
     */
    public Configuration(){
        //set blank
        database_name = "";
        database_user = "";
        database_password = "";
        database_ip = "";
        debug_flag = 0;
        raw_lines = new ArrayList<>();

        // checking for file
        dir = new File(".");
        File[] directoryListening = dir.listFiles();
        for(File child : directoryListening){
            if ( child.getName().contains(filenamesrc) ){
                filenamesrc = child.getAbsolutePath();
                break;
            }
        }

        // checking if file was found
        if ( filenamesrc.equals("config.noteit") ){
            // create new file
            try{
                user_input(); // loading user input
                FileWriter fw = new FileWriter(filenamesrc);
                fw.write("noteit configuration file!\n");
                fw.write("by Jakub Wawak\n");
                fw.write("#database_name%" + database_name + "\n");
                fw.write("#database_user%" + database_user + "\n");
                fw.write("#database_password%" + database_password + "\n");
                fw.write("#database_ip%" + database_ip  + "\n");
                fw.write("#debugflag%" + debug_flag + "\n");
                fw.close();
                newfile = true;
                error = false;
            }catch(Exception e){
                // error
                System.out.println("Error reading config file ("+e.toString()+")");
                error = true;
            }
        }
        else{
            // file found, loading file
            newfile = false;
            try{
                BufferedReader reader = new BufferedReader(new FileReader(filenamesrc));
                String line = reader.readLine();
                while ( line!= null ){
                    if (line.startsWith("#")){
                        switch(line.split("%")[0]){
                            case "#database_name":
                            {
                                database_name = line.split("%")[1];
                                break;
                            }
                            case "#database_user":
                            {
                                database_user = line.split("%")[1];
                                break;
                            }
                            case "#database_password":
                            {
                                database_password = line.split("%")[1];
                                break;
                            }
                            case "#database_ip":
                            {
                                database_ip = line.split("%")[1];
                                break;
                            }
                            case "#debugflag":
                            {
                                debug_flag = Integer.parseInt(line.split("%")[1]);
                                break;
                            }
                        }
                    }
                    line = reader.readLine();
                }
            }catch(Exception e){
                error = true;
                System.out.println("Failed reading configuration ("+e.toString()+")");
            }
        }
    }

    /**
     * Function for saving configuration file data
     */
    void savetofile(){
        try{
            FileWriter fw = new FileWriter(filenamesrc);
            fw.write("noteit configuration file!\n");
            fw.write("by Jakub Wawak\n");
            fw.write("#database_name%" + database_name + "\n");
            fw.write("#database_user%" + database_user + "\n");
            fw.write("#database_password%" + database_password + "\n");
            fw.write("#database_ip%" + database_ip  + "\n");
            fw.write("#debugflag%" + debug_flag + "\n");
            fw.close();
            newfile = true;
            error = false;
        }catch(Exception e){
            // error
            System.out.println("Error reading config file ("+e.toString()+")");
            error = true;
        }
    }

    /**
     * Function for saving user input
     */
    public void user_input(){
        Console cnsl = System.console();
        Scanner sc = new Scanner(System.in);
        System.out.println("New Configuration File creator!");

        System.out.print("database_ip:");
        database_ip = sc.nextLine();

        System.out.print("database_name:");
        database_name = sc.nextLine();

        System.out.print("database_user:");
        database_user = sc.nextLine();

        System.out.print("database_password: ");
        if ( cnsl != null){
            database_password = cnsl.readPassword().toString();
        }
        else{
            database_password = sc.nextLine();
        }
        debug_flag = 1;
    }
}