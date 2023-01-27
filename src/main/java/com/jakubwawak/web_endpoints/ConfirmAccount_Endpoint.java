/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.web_endpoints;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

@PageTitle("Welcome to noteIT")
@Route(value = "/activate")
/**
 * Object for confirming account endpoint
 */
public class ConfirmAccount_Endpoint extends VerticalLayout implements HasUrlParameter<String> {

    TextField code_field;
    Button activate_button;

    /**
     * Constructor
     */
    public ConfirmAccount_Endpoint(){
        code_field = new TextField();
        activate_button = new Button("Activate Account");
    }

    /**
     * Function for setting window content with given parameter
     * @param event
     * @param parameter
     */
    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {

        if ( parameter == null ){
            add(new H1("Insert Activation Code"));
            add(code_field);
            add(activate_button);
        }
        else{

        }

    }


}
