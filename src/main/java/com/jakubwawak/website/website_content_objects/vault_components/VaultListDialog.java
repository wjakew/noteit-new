/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Vault;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Set;

/**
 * Object for creating dialog window for showing list of user vaults
 */
public class VaultListDialog {

    public Dialog main_dialog;
    VerticalLayout main_layout;

    VaultListGrid vaultListGrid;
    Button open_button;

    /**
     * Constructor
     */
    public VaultListDialog(){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        vaultListGrid = new VaultListGrid();
        open_button = new Button("Open Vault",this::openbutton_action);
        prepare_dialog();
    }

    /**
     * Function for preparing dialog
     */
    void prepare_dialog(){
        main_layout.add(new H3("Your Vaults"));
        main_layout.add(vaultListGrid.grid_searchbox_field);
        main_layout.add(vaultListGrid.grid);
        main_layout.add(open_button);
        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    private void openbutton_action(ClickEvent e){
        Set<Vault> selected = vaultListGrid.grid.getSelectedItems();
        for(Vault obj : selected){
            NoteitApplication.noteit_vault_id = obj.noteit_vault_id;
        }
        open_button.getUI().ifPresent(ui -> ui.navigate("vault"));
    }
}
