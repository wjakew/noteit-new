/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.support_objects.Vault;
import com.jakubwawak.website.website_content_objects.note_components.CreateNoteDialog;
import com.jakubwawak.website.website_content_objects.note_components.NoteListGrid;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;

/**
 * Object for creating component for showing vault details
 */
public class VaultDetailsComponent {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    Vault vault;

    NoteListGrid noteGrid;

    Grid<StringElement> members_grid;

    Button changeowner_button, delete_button,addnote_button,editmembers_button;

    /**
     * Constructor
     * @param noteit_vault_id
     */
    public VaultDetailsComponent(int noteit_vault_id){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        vault = dv.get_vault(noteit_vault_id);
        noteGrid = new NoteListGrid(noteit_vault_id);

        changeowner_button = new Button("Change Owner",VaadinIcon.USER.create(),this::changeowner_action);

        delete_button = new Button("Delete", VaadinIcon.TRASH.create(),this::removevault_action);
        delete_button.addThemeVariants(ButtonVariant.LUMO_ERROR,ButtonVariant.LUMO_PRIMARY);

        addnote_button = new Button("Add note",VaadinIcon.PLUS.create(),this::addnote_action);
        addnote_button.addThemeVariants(ButtonVariant.LUMO_SUCCESS,ButtonVariant.LUMO_PRIMARY);

        editmembers_button = new Button("Edit Members",VaadinIcon.GROUP.create(),this::editmembers_action);

        members_grid = new Grid<>(StringElement.class,false);
        members_grid.addColumn(StringElement::getContent).setHeader("Members");
        ArrayList<StringElement> element = new ArrayList<>();
        for(String member : vault.noteit_vault_members){
            element.add(new StringElement(member));
        }
        members_grid.setItems(element);
        members_grid.setSizeFull();members_grid.setWidth("300px"); members_grid.setHeight("400px");
        noteGrid.grid.setHeight("400px");
        prepare_dialog();
    }

    /**
     *Function for preparing dialog
     *
     */
    void prepare_dialog(){
        main_layout.add(new H1(vault.getNoteit_vault_name()));
        main_layout.add(new H6("Owner: "+vault.get_owner()));
        HorizontalLayout tables_layout = new HorizontalLayout(members_grid,noteGrid.grid);
        HorizontalLayout button_layout = new HorizontalLayout();
        if ( vault.is_logged_owner() ){
            button_layout.add(changeowner_button, delete_button,addnote_button,editmembers_button);
        }
        else{
            button_layout.add(addnote_button);
        }
        tables_layout.setSizeFull();
        main_layout.add(tables_layout);
        main_layout.add(button_layout);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");
        main_dialog.add(main_layout);
    }

    /**
     * Function for action: change owner
     * @param e
     */
    private void changeowner_action(ClickEvent e){
        ChangeOwnerDialog cod = new ChangeOwnerDialog(vault.noteit_vault_id);
        main_layout.add(cod.main_dialog);
        cod.main_dialog.open();
    }

    /**
     * Function for adding note to vault
     * @param e
     */
    private void addnote_action(ClickEvent e){
        CreateNoteDialog cnd = new CreateNoteDialog(0);
        main_layout.add(cnd.main_dialog);
        cnd.main_dialog.open();
    }

    /**
     * Function for removing vault
     * @param e
     */
    private void removevault_action(ClickEvent e){
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        int ans = dv.remove_vault(vault.noteit_vault_id);
        if  (ans == 1){
            delete_button.getUI().ifPresent(ui ->
                    ui.navigate("home"));
            Notification.show("Vault removed!");
        }
    }

    /**
     * Function for changing vault members
     * @param e
     */
    private void editmembers_action(ClickEvent e){
        MemberEditorDialog med = new MemberEditorDialog(vault.noteit_vault_id);
        main_layout.add(med.main_dialog);
        med.main_dialog.open();
    }
}
