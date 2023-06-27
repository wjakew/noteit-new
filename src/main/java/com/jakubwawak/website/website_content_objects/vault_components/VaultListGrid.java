/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.ToDo;
import com.jakubwawak.support_objects.Vault;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;

/**
 * Object for creating grid and search in vault object
 */
public class VaultListGrid {

    Grid<Vault> grid;
    TextField grid_searchbox_field;
    ArrayList<Vault> content;

    /**
     * Constructor
     */
    public VaultListGrid(){
        grid = new Grid(Vault.class,false);
        grid_searchbox_field = new TextField();
        grid_searchbox_field.setPlaceholder("Search Vaults...");
        prepare_components();
    }

    /**
     * Function for preparing object components
     */
    void prepare_components(){
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        content = dv.get_vaults();
        grid.addColumn(Vault::getNoteit_vault_id).setHeader("Vault ID");
        grid.addColumn(Vault::getNoteit_vault_name).setHeader("Vault Name");
        grid.setItems(content);
        GridListDataView<Vault> dataView = grid.setItems(content);
        grid_searchbox_field.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        grid_searchbox_field.setValueChangeMode(ValueChangeMode.EAGER);
        grid_searchbox_field.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(vault_obj -> {
            try{
                String searchTerm = grid_searchbox_field.getValue().trim();

                if (searchTerm.isEmpty())
                    return true;

                boolean matchesdata = vault_obj.noteit_vault_name.contains(searchTerm);

                return matchesdata;
            }catch(Exception ex){
                return false;
            }
        });
        grid_searchbox_field.setWidth("200px");
        grid.setSizeFull();
        grid.setWidth("500px");grid.setHeight("300px");
    }
}
