/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.note_components;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.jakubwawak.noteit.NoteitApplication;
import com.jakubwawak.support_objects.Note;
import com.jakubwawak.support_objects.StringElement;
import com.jakubwawak.website.webview_components.FileDownloaderComponent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Object for creating dialog for sharing note
 */
@JsModule("./recipe/copytoclipboard.js")
public class ShareNoteDialog {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    Button copytoclipboard_button,export_button;

    ComboBox<StringElement> exportoptions_combobox;

    Note note;
    /**
     * Constructor
     * @param note
     */
    public ShareNoteDialog(Note note){
        main_dialog = new Dialog();
        main_layout = new VerticalLayout();
        this.note = note;
        prepare_dialog();
    }

    /**
     * Function for preparing dialog components
     */
    void prepare_components(){
        copytoclipboard_button = new Button("Copy to clipboard",VaadinIcon.COPY.create(),this::copytoclipboard_action);
        export_button = new Button("Export",this::export_action);
        exportoptions_combobox = new ComboBox<>();
        ArrayList<StringElement> data = new ArrayList<>();
        data.add(new StringElement("Export to TXT"));
        data.add(new StringElement("Export to PDF"));
        exportoptions_combobox.setItems(data);
        exportoptions_combobox.setItemLabelGenerator(StringElement::getContent);
    }

    /**
     * Function for preparing dialog
     */
    void prepare_dialog(){
        prepare_components();
        main_layout.add(VaadinIcon.SHARE.create());
        main_layout.add(new H3("Share "+note.noteit_object_title));
        main_layout.add(copytoclipboard_button);
        main_layout.add(new H3("Export Options "));
        main_layout.add(exportoptions_combobox);
        main_layout.add(export_button);

        main_layout.setSizeFull();
        main_layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        main_layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        main_layout.getStyle().set("text-align", "center");

        main_dialog.add(main_layout);
    }

    /**
     * Function for coping to clipboard
     * @param ex
     */
    private void copytoclipboard_action(ClickEvent ex){
        UI.getCurrent().getPage().executeJs("window.copyToClipboard($0)", note.noteit_object_rawtext);
        Notification.show("Note text copied to clipboard!");
    }

    /**
     * Function for exporting to clipboard
     * @param ex
     */
    private void export_action(ClickEvent ex){
        String path_to_file = new File(".").getAbsolutePath();
        boolean error = false;
        if ( exportoptions_combobox.getValue() != null ){
            if ( exportoptions_combobox.getValue().getContent().contains("TXT")){
                // export to TXT
                path_to_file = path_to_file+"/"+note.noteit_object_title+".txt";
                try{
                    PrintWriter out = new PrintWriter(path_to_file);
                    out.println(note.noteit_object_rawtext);
                    out.close();
                }catch(Exception e){
                    error = true;
                    NoteitApplication.log.add("EXPORT-FAILED","Failed to export note ("+e.toString()+")");
                }
            }
            else if ( exportoptions_combobox.getValue().getContent().contains("PDF") ){
                // export to PDF
                try{
                    path_to_file = path_to_file+"/"+note.noteit_object_title+".pdf";
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(path_to_file));
                    document.open();
                    Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                    Chunk chunk = new Chunk(note.noteit_object_rawtext, font);
                    document.add(chunk);
                    document.close();
                }catch(Exception e){
                    error = true;
                    NoteitApplication.log.add("EXPORT-FAILED","Failed to export note ("+e.toString()+")");
                }
            }
            else{
                Notification.show("Failed to export!");
            }

            if ( !error ){
                FileDownloaderComponent fdc = new FileDownloaderComponent(new File(path_to_file));
                main_layout.add(fdc.dialog);
                fdc.dialog.open();
                main_dialog.close();
            }
            else{
                Notification.show("Failed exporting! Check application log!");
            }
        }
        else{
            Notification.show("Export source not selected!");
        }
    }
}
