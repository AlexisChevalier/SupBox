package com.heavenstar.supbox.client.classes.models;


import com.heavenstar.supbox.client.MainGUI;
import com.heavenstar.supbox.dao.ConfigurationDAO;
import com.heavenstar.supbox.entities.Configuration;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 19/05/13
 */
public class ListConfModel implements TableModel {
    private ArrayList<Configuration> confs;
    private String[] titres = {"Name", "IP/Domain", "Port"};
    private ArrayList<TableModelListener> listeners = new ArrayList<>();
    private ConfigurationDAO confDAO = null;

    /**
     * Initialise le modéle
     * @param confDAO Dao de configuration
     */
    public ListConfModel(ConfigurationDAO confDAO) {
        this.confDAO = confDAO;
        update();
    }

    /**
     * Ajoute une configuration
     * @param conf configuration a ajouter
     */
    public void addRow(Configuration conf) {
        confs.add(conf);
    }

    /**
     * Donne la taille de l'arraylist utilisé pour la JTable
     * @return nombre des éléments de la JTable
     */

    @Override
    public int getRowCount() {
        return confs.size();
    }

    /**
     * Donne le nombre de colonnes
     * @return nombre de colonnes de la JTable
     */
    @Override
    public int getColumnCount() {
        return titres.length;
    }

    /**
     * Donne le nom d'une colonne précisée
     * @param columnIndex index de la colonne
     * @return string contenant le nom de la colonne
     */
    @Override
    public String getColumnName(int columnIndex) {
        return titres[columnIndex];
    }

    /**
     * Retourne la classe des items stockés dans une colonne
     * @param columnIndex index de la colonne
     * @return Classe des items
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    /**
     * permet de savoir ou non si une cellule est éditable (ici seules les cols 1 et 2 le sont)
     * @param rowIndex index de la ligne
     * @param columnIndex index de la colonne
     * @return true si éditable, false sinon
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Retourne ce qui est stocké dans l'endroit précisé
     * @param rowIndex index de la ligne
     * @param columnIndex index de la colonne
     * @return l'objet stocké a l'endroit précisé
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return confs.get(rowIndex).getName();
            case 1:
                return confs.get(rowIndex).getServer();
            case 2:
                return confs.get(rowIndex).getPort();
            default:
                return null;
        }
    }

    /**
     * Setter d'une cellule
     * @param aValue objet a placer dans la cellule
     * @param rowIndex Ligne a utiliser
     * @param columnIndex Colonne a utiliser
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
                confs.get(rowIndex).setServer((String) aValue);
                break;
            case 2:
                confs.get(rowIndex).setPort((String) aValue);
                break;
        }
        try {
            confDAO.updateConf(confs.get(rowIndex));
            MainGUI.updateConfLists();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * Permet d'ajouter un TableModelListener sur notre TableModel
     * @param l TableModelListener a ajouter
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    /**
     * Permet de supprimer un TableModelListener sur notre TableModel
     * @param l TableModelListener a retirer
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Méthode ajoutée, permet de mettre a jour le modéle depuis le DAO
     */
    public void update() {
        try {
            this.confs = confDAO.getConfList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }
}
