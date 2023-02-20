package com.jakubwawak.website_views;

import com.jakubwawak.web_layouts.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("noteIT home")
@Route(value = "home", layout = MainLayout.class)
public class HomeView extends VerticalLayout {

    VerticalLayout main_layout;

    /**
     * Constructor
     */
    public HomeView(){
        main_layout = new VerticalLayout();

    }
}
