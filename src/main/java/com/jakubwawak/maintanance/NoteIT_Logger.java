/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.noteit.NoteitApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/**
 * Object for storing log data
 */
public class NoteIT_Logger {
    public ArrayList<String> raw_log;

    /**
     * Constructor
     */
    public NoteIT_Logger(){
        raw_log = new ArrayList<>();
        raw_log.add(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString()+"|START| Started new log saving!");
        raw_log.add(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString()+"|START| Welcome, by Jakub Wawak.");
    }

    /**
     * Function for adding log to object
     * @param log_code
     * @param log_desc
     */
    public void add(String log_code,String log_desc){
        raw_log.add(LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString()+"|"+log_code+"| "+log_desc);
        if (NoteitApplication.debug == 1){
            if ( log_code.contains("ERROR") || log_code.contains("FAILED")){
                System.out.println(ConsoleColors.BLUE_BOLD+LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString()+ConsoleColors.RED_BACKGROUND+
                        ConsoleColors.BLACK_BOLD+"|"+log_code+"| "
                        +ConsoleColors.BLACK_BOLD+log_desc+ConsoleColors.RESET);
            }
            else{
                System.out.println(ConsoleColors.BLUE_BOLD+LocalDateTime.now(ZoneId.of("Europe/Warsaw")).toString()+ConsoleColors.WHITE_BACKGROUND+
                        ConsoleColors.BLACK_BOLD+"|"+log_code+"| "
                        +ConsoleColors.BLACK_BOLD+log_desc+ConsoleColors.RESET);
            }
        }
        // adding log to database if database connected and not null
        if ( NoteitApplication.database != null && NoteitApplication.database.connected ){
            NoteitApplication.database.add_log(log_code,log_desc,NoteitApplication.user_2fa_code);
        }
    }
}
