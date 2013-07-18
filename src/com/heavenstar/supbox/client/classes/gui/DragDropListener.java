package com.heavenstar.supbox.client.classes.gui;

import com.heavenstar.supbox.client.MainGUI;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.File;
import java.util.List;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 19/05/13
 */
public class DragDropListener implements DropTargetListener {

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {}

    /**
     * Gére le drag dans la zone de drag and drop
     * @param dtde evenement de drag and drop
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        JLabel label = (JLabel) dtde.getDropTargetContext().getComponent();
        label.setText("Drop it :)");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}

    /**
     * Gére la sortie du drag de la zone de drag and drop
     * @param dte evenement de drag and drop
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        JLabel label = (JLabel) dte.getDropTargetContext().getComponent();
        label.setText("Drag me a/some file(s) !");
    }

    /**
     * Gére le drop d'éléments dans la zone de drag and drop
     * @param dtde evenement de drag and drop
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        dtde.acceptDrop(DnDConstants.ACTION_COPY);
        Transferable transferable = dtde.getTransferable();
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (DataFlavor flavor : flavors) {

            try {
                if (flavor.isFlavorJavaFileListType()) {
                    List files = (List) transferable.getTransferData(flavor);

                    for (Object file : files) {
                        MainGUI.addFile(((File) file).getPath());
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        dtde.dropComplete(true);
        JLabel label = (JLabel) dtde.getDropTargetContext().getComponent();
        label.setText("Drag me a/some file(s) !");
    }
}
