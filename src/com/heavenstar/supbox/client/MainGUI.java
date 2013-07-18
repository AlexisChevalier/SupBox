package com.heavenstar.supbox.client;

import com.heavenstar.supbox.client.classes.views.MainWindow;
import com.heavenstar.supbox.client.classes.gui.ClientSocketConnectionGui;
import com.heavenstar.supbox.entities.Configuration;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MainGUI {

    public static final MainWindow wnd = new MainWindow();
    private static ArrayList<String> fileList = new ArrayList<>();
    private static ArrayList<ClientSocketConnectionGui> connList = new ArrayList<>();

    /**
     * Lance la ManWindow
     * @param args arguments de la console
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                wnd.setMinimumSize(new Dimension(600, 500));
                wnd.setSize(600, 500);
                wnd.setVisible(true);
            }
        });
    }

    /**
     * Ajoute un fichier a la liste
     * @param filepath chemin du fichier a ajouter
     */
    public static void addFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (!file.isDirectory()) {
                fileList.add(filepath);
                wnd.AddItemInFileQueue(filepath);
            }
        }
    }

    /**
     * Supprime un fichier
     * @param filepath chemin du fichier a retirer
     */
    public static void removeFile(String filepath) {
        fileList.remove(filepath);
    }

    /**
     * Vide la queue des fichiers
     */
    public static void emptyQueue() {
        fileList.clear();
        wnd.EmptyFileQueue();
    }

    /**
     * Apelle la méthode d'update des confs de la MainWindow
     */
    public static void updateConfLists() {
        wnd.updateConfList();
    }

    /**
     * Initialise une nouvelle connection avec le serveur
     * @param conf configuration a utiliser
     * @param fileList liste de fichiers a envoyer
     * @throws Exception
     */
    public static void startNewConn(Configuration conf, ArrayList<String> fileList) throws Exception {
        try {
            ClientSocketConnectionGui conn = null;
            try {
                conn = new ClientSocketConnectionGui(conf, fileList);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(wnd,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            connList.add(conn);
            Integer index = connList.indexOf(conn);
            connList.get(index).Initialize(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Créé un nouveau thread pour l'envoi de fichiers avec la connection précisée
     * @param connID id de la connection stockée
     */
    public static void runConn(Integer connID) {
        new Thread(connList.get(connID)).start();
    }

    /**
     * Termine une connection (la retire de l'arraylist)
     * @param connID id de la connection stockée
     */
    public static void endConn(Integer connID) {
        connList.remove(connID);
    }

    /**
     * Récupére la liste de fichiers actuelle
     * @return l'arraylist des chemins de fichier
     */
    public static ArrayList<String> getFileList() {
        return fileList;
    }

    /**
     * Permet de récupérer la connection précisée
     * @param id id de la connection stockée
     * @return la connection demandée
     */
    public static ClientSocketConnectionGui getConn(Integer id) {
        return connList.get(id);
    }
}
