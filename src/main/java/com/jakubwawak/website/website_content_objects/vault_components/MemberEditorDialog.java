/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.website.website_content_objects.user_components.UserGridSelector;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.atmosphere.interceptor.AtmosphereResourceStateRecovery;

import java.util.ArrayList;

/**
 * Object for creating dialog window for changing members of vault
 */
public class MemberEditorDialog {

    int noteit_vault_id;

    public Dialog main_dialog;
    VerticalLayout main_layout;
    UserGridSelector user_grid;

    Grid<StringElement> user_selected_grid;
    ArrayList<StringElement> content;

    Button add_button, remove_button;

    /**
     * Constructor
     * @param noteit_vault_id
     */
    public MemberEditorDialog(int noteit_vault_id){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        this.noteit_vault_id = noteit_vault_id;
        add_button = new Button("Add Member", VaadinIcon.PLUS.create());
        remove_button = new Button("Remove Member",VaadinIcon.MINUS.create());

        user_grid = new UserGridSelector();

        content = new ArrayList<>();
        user_selected_grid = new Grid<>(StringElement.class,false);
        user_selected_grid.addColumn(StringElement::getContent).setHeader("Users with Privileges");
        user_selected_grid.setSizeFull();user_selected_grid.setWidth("450px");user_selected_grid.setHeight("300px");
        user_selected_grid.setItems(content);
        prepare_dialog();
    }

    /**
     * Function for preparing dialog data
     */
    void prepare_dialog(){
        main_layout.add(new H1("Vault Members Editor"));
        HorizontalLayout grid_layout = new HorizontalLayout();
        grid_layout.setSizeFull();
        grid_layout.add(user_grid.grid,user_selected_grid);
        main_layout.add(grid_layout);
        main_layout.add(new HorizontalLayout(remove_button,add_button));

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }
}
