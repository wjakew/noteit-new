/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.mailconnector;

import com.jakubwawak.noteit.NoteitApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Object for connecting to mail server
 */
public class MailConnector {

    @Autowired
    private JavaMailSender javaMailSender;

    public MailConnector(){
        javaMailSender = new JavaMailSenderImpl();
    }

    /**
     * Function for sending email based on EmailDetails object
     * @param message
     * @return 1 - mail sent, -1 - failed to send mail
     */
    public int send_email(EmailDetails message){
        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(message.from);
            mailMessage.setTo(message.recipient);
            mailMessage.setText(message.msgBody);
            mailMessage.setSubject(message.subject);

            if ( NoteitApplication.database.check_sendmail_flag() == 1 ){
                // Sending the mail
                javaMailSender.send(mailMessage);
                NoteitApplication.log.add("MAILSEND","Sent new mail ("+message.recipient+")");
            }
            else{
                NoteitApplication.log.add("MAILSENDSTOP","Data to ("+message.recipient+") : "+message.msgBody);
            }
            NoteitApplication.database.save_mail(message.recipient, message.msgBody);
            return 1;
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            NoteitApplication.log.add("MAILSEND-FAILED","Failed to send mail ("+e.toString()+")");
            return -1;
        }
    }
}
