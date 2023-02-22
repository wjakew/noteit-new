/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.noteit;

import com.jakubwawak.database.Database_Connector;
import com.jakubwawak.maintanance.*;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableVaadin({"com.jakubwawak"})
public class NoteitApplication {

	public static String version = "v1.0.0";
	public static String build = "noteit-220223REV1";

	public static int debug = 1;
	public static int test = 0;

	public static NoteIT_Logger log;
	public static Database_Connector database;

	public static String user_2fa_code;
	public static NoteIT_User logged;

	/**
	 * Main application function
	 * @param args
	 */
	public static void main(String[] args) {
		show_header();
		user_2fa_code = "";
		logged = null;
		// looking for configuration file
		Configuration config = new Configuration();
		log = new NoteIT_Logger();

		// checking if file exists
		if ( config.newfile ){
			System.out.println("Configuration file creator!");
		}

		// checking if config has no error
		if ( !config.error ){

			// trying to connect to database
			System.out.println("Connecting to database...");
			database = new Database_Connector(config);
			database.connect();

			// check connection
			if ( database.connected ){
				// start main part of the program
				log.add("CONNECTION","Connected to database successfully!");
				if ( test == 1 ){
					//run tests
					Tests test = new Tests();
					test.run();
				}
				else{
					//run application server
					SpringApplication.run(NoteitApplication.class, args);
					NoteitMenu menu = new NoteitMenu();
					menu.run();
				}
			}
			// connection failed
			else{
				System.out.println("Failed to load database, check configuration file!");
			}
		}
	}

	/**
	 * Function for showing application header
	 */
	public static void show_header(){
		String header = "             _       ___ _____ \n" +
				" _ __   ___ | |_ ___|_ _|_   _|\n" +
				"| '_ \\ / _ \\| __/ _ \\| |  | |  \n" +
				"| | | | (_) | ||  __/| |  | |  \n" +
				"|_| |_|\\___/ \\__\\___|___| |_|  ";

		System.out.println(ConsoleColors.PURPLE_BOLD + header + version + "/" + build + ConsoleColors.RESET);

	}

}
