/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website;
/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Template view for noteIT application
 */
@PageTitle("noteIT template")
@Route(value = "template", layout = MainLayout.class)
public class TemplateView extends VerticalLayout {


    /**
     * Constructor
     */
    public TemplateView(){
        create_view();
        NoteitApplication.main_layout = this;
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){

    }

    /**
     * Function for creating other compontents like grids,list etc
     */
    void create_components(){

    }

    /**
     * Function for creating view components
     */
    void create_view(){
        create_buttons();
        create_components();
        if (NoteitApplication.logged != null && NoteitApplication.logged.getNoteit_user_id() > 0){
            // view can be created, user is logged
            prepare_view();
        }
        else{
            // show error screen
            add(new H1("User login error!"));
        }
    }

    /**
     * Function for preparing view
     */
    void prepare_view(){

    }


    //----------------section for actions and validators

    //----------------end of section
}
