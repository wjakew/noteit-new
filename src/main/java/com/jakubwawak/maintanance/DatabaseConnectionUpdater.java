/**
 * by Jakub Wawak
 * all rights reserved
 * kubawawak@gmail.com / j.wawak@usp.pl
 */
package com.jakubwawak.maintanance;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;

/**
 * Object for updating connection to database
 */
public class DatabaseConnectionUpdater implements Runnable{

    /**
     *
     */
    int time;

    /**
     * Constructor
     * @param time
     */
    public DatabaseConnectionUpdater(int time){
        NoteitApplication.log.add("DB-RECONNECTION","Started auto database reconnection thread!");
        this.time = time;
    }

    /**
     * Object for reconnecting database
     */
    @Override
    public void run() {
        try{
            while(true){
                if (!NoteitApplication.config.error){
                    System.out.println("Connecting to database...");
                    NoteitApplication.database = new Database_Connector(NoteitApplication.config);
                    NoteitApplication.database.connect();
                    if (NoteitApplication.database.connected){
                        NoteitApplication.log.add("DB-RECONNECTION","Database reconnected!");
                    }
                    else{
                        NoteitApplication.log.add("DB-RECONNECTION-ERROR","Error reconnecting database!");
                    }
                }
                Thread.sleep(time);
            }
        }catch(Exception ex){
            NoteitApplication.log.add("DB-RECONNECTION-FAILED","Failed to automatically reconnect database ("+ex.toString()+")");
        }

    }
}