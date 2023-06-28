/**
 * by Jakub Wawak
 * kubawawak@gmail.com
 * all rights reserved
 */
package com.jakubwawak.website.website_content_objects.vault_components;

import com.jakubwawak.support_objects.Vault;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Object for creating component for showing vault details
 */
public class VaultDetailsComponent {
    public Dialog main_dialog;
    VerticalLayout main_layout;

    Vault vault;

    /**
     * Constructor
     * @param noteit_vault_id
     */
    public VaultDetailsComponent(int noteit_vault_id){

    }
}
