/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.support_objects;

/**
 * Object for storing string data for usage in Vaadin components
 */
public class StringElement {

    private String content;

    /**
     * Constructor
     * @param string
     */
    public StringElement(String string){
        content = string;
    }

    /**
     * Getter for content
     * @return
     */
    public String getContent(){
        return content;
    }

}
