/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.mailconnector;

import com.jakubwawak.noteit.NoteitApplication;

/**
 * Object for storing email details
 */
public class EmailDetails {

    public String from;

    // Class data members
    public String recipient;
    public String msgBody;
    public String subject;
    public String attachment;

    /**
     * Constructor
     */
    public EmailDetails(){
        from = NoteitApplication.database.get_mail_data().get(2);
        recipient = "";
        msgBody = "";
        subject = "";
        attachment = "";
    }
}
