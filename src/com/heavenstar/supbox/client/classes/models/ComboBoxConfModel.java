package com.heavenstar.supbox.client.classes.models;

import com.heavenstar.supbox.dao.ConfigurationDAO;
import com.heavenstar.supbox.entities.Configuration;

import javax.swing.*;
import java.util.ArrayList;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 20/05/13
 */
public class ComboBoxConfModel extends AbstractListModel implements ComboBoxModel {

    Configuration selected = null;
    private ArrayList<Configuration> confList = new ArrayList<>();
    private ConfigurationDAO confDAO = null;
    private int PreviousSelected = 0;

    /**
     * Initialise le modéle
     * @param confDAO Dao de configuration
     */
    public ComboBoxConfModel(ConfigurationDAO confDAO) {
        this.confDAO = confDAO;
        try {
            this.confList = confDAO.getConfList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }

    /**
     * récupére l'item sélectionné
     * @return Configuration sélectionnée
     */
    @Override
    public Object getSelectedItem() {
        return selected;
    }

    /**
     * Permet de définir l'item séléctionné
     * @param anItem sera le nouvel item sélectionné
     */
    @Override
    public void setSelectedItem(Object anItem) {
        selected = (Configuration) anItem;
    }


    /**
     * Donne la taille de l'arraylist utilisé pour la combobox
     * @return nombre des éléments de la combobox
     */
    @Override
    public int getSize() {
        return confList.size();
    }

    /**
     * Retourne un élément précidé dans l'arraylist
     * @param index de l'arraylist
     * @return Configuration pour l'index précisé
     */
    @Override
    public Object getElementAt(int index) {
        return confList.get(index);
    }

    /**
     * Méthode ajoutée, permet d'update l'arrayList depuis le DAO
     */
    public void update() {
        PreviousSelected = confList.indexOf(getSelectedItem());
        confList.clear();
        try {
            this.confList = confDAO.getConfList();
            try {
                Configuration tempConf = confList.get(PreviousSelected);
                this.setSelectedItem(tempConf);
            } catch (Exception e) {
                this.setSelectedItem(confList.get(0));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }
    }
}
