/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.mailconnector;

import com.jakubwawak.application_objects.NoteIT_User;
import com.jakubwawak.noteit.NoteitApplication;

/**
 * Object for creating mail templates
 */
public class MailTemplates {

    public EmailDetails emailobj;

    /**
     * Constructor
     */
    public MailTemplates(){
        emailobj = new EmailDetails();
    }

    /**
     * Function for creating template: NEW ACCOUNT
     */
    public void create_newaacount_template(int noteit_user_id,int code){
        try{
            emailobj.subject = "Account creation - noteit application - Welcome!";
            NoteIT_User user = new NoteIT_User(noteit_user_id);
            emailobj.msgBody = "Welcome to noteit "+user.noteit_user_name+"!\nThank you for using our services.\nTo verify your account use code below\n" +
                    + code + "\n\nNoteIT Team";
            emailobj.recipient = user.noteit_user_email;
        }catch(Exception e){
            NoteitApplication.log.add("MTEMPLATE-FAILED","Failed to create mail template ("+e.toString()+")");
        }
    }

    /**
     * Function for creating template: 2FA AUTH
     * @param noteit_user_id
     * @param code
     */
    public void create_2fa_template(int noteit_user_id,String code){
        try{
            emailobj.subject = "Login activity - 2FA AUTH";
            NoteIT_User user = new NoteIT_User(noteit_user_id);
            emailobj.msgBody = "Dear "+user.noteit_user_name+",\nyour login code is: "+code+"\n\nNoteIT Team";
            emailobj.recipient = user.noteit_user_email;
        }catch(Exception e){
            NoteitApplication.log.add("MTEMPLATE-FAILED","Failed to create mail template ("+e.toString()+")");
        }
    }
}
