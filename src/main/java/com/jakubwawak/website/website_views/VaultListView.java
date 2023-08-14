/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_views;
/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
import com.jakubwawak.database.Database_Note;
import com.jakubwawak.database.Database_Vault;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Vault;
import com.jakubwawak.website.website_content_objects.note_components.CreateNoteDialog;
import com.jakubwawak.website.website_content_objects.note_components.ImportNoteDialog;
import com.jakubwawak.website.website_content_objects.note_components.NoteListGrid;
import com.jakubwawak.website.website_content_objects.note_components.ShareNoteDialog;
import com.jakubwawak.website.website_content_objects.vault_components.VaultDetailsComponent;
import com.jakubwawak.website.website_layouts.MainLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Template view for noteIT application
 */
@PageTitle("Vault Explorer")
@Route(value = "/vault", layout = MainLayout.class)
public class VaultListView extends VerticalLayout {

    NoteListGrid noteListGrid;

    TextArea note_text_area;

    Button createnewnote_button, refresh_button, share_button, importexport_button, information_button;

    /**
     * Constructor
     */
    public VaultListView(){
        create_view();
        NoteitApplication.main_layout = this;
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        getStyle().set("background-color","#000000");
        getStyle().set("--lumo-font-family","Monospace");
    }

    /**
     * Function for creating buttons
     */
    void create_buttons(){
        createnewnote_button = new Button("Create New Note", VaadinIcon.PLUS.create(),this::createnewnote_action);
        refresh_button = new Button("Refresh Vault",VaadinIcon.REFRESH.create(),this::refreshnote_action);
        share_button = new Button("Share",VaadinIcon.SHARE.create(),this::share_action);
        importexport_button = new Button("Import",VaadinIcon.DOWNLOAD.create(),this::import_action);
        information_button = new Button("Details",VaadinIcon.INFO.create(),this::details_action);
    }

    /**
     * Function for creating other compontents like grids,list etc
     */
    void create_components(){
        Database_Note dn = new Database_Note(NoteitApplication.database);
        noteListGrid = new NoteListGrid(NoteitApplication.noteit_vault_id);

        note_text_area = new TextArea("");
        note_text_area.setValue("");
        note_text_area.setEnabled(false);
        note_text_area.setSizeFull();
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
     * Function for creating actions for objects
     */
    void create_actions(){
        noteListGrid.grid.addItemClickListener(event ->{
            note_text_area.setValue(noteListGrid.get_selected_note().noteit_object_rawtext);
        });
    }

    /**
     * Function for preparing view
     */
    void prepare_view(){
        create_actions();
        Database_Vault dv = new Database_Vault(NoteitApplication.database);
        Vault vault =  dv.get_vault(NoteitApplication.noteit_vault_id);
        add(new H1(vault.getNoteit_vault_name()));
        HorizontalLayout main_layout = new HorizontalLayout();
        add(new HorizontalLayout(createnewnote_button, refresh_button, share_button, importexport_button,information_button));
        main_layout.add(noteListGrid.grid,note_text_area);
        main_layout.setSizeFull();
        add(main_layout);
    }

    //----------------section for actions and validators
    private void createnewnote_action(ClickEvent ex){
        CreateNoteDialog cnd = new CreateNoteDialog(0);
        add(cnd.main_dialog);
        cnd.main_dialog.open();
        noteListGrid.reload_grid();
    }

    private void refreshnote_action(ClickEvent ex){
        noteListGrid.reload_grid();
        Notification.show("List grid reloaded!");
    }

    private void share_action(ClickEvent ex){
        if ( noteListGrid.get_selected_note() != null ){
            ShareNoteDialog snd = new ShareNoteDialog(noteListGrid.get_selected_note());
            add(snd.main_dialog);
            snd.main_dialog.open();
        }
        else{
            Notification.show("Note is not selected!");
        }
    }

    private void import_action(ClickEvent ex){
        ImportNoteDialog ind = new ImportNoteDialog(NoteitApplication.noteit_vault_id);
        add(ind.main_dialog);
        ind.main_dialog.open();
    }

    private void details_action(ClickEvent ex){
        VaultDetailsComponent vdc = new VaultDetailsComponent(NoteitApplication.noteit_vault_id);
        add(vdc.main_dialog);
        vdc.main_dialog.open();
    }
    //----------------end of section
}
