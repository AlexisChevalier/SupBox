package com.heavenstar.supbox.dao;

import com.heavenstar.supbox.entities.Configuration;

import java.util.ArrayList;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 08/05/13
 */
public interface ConfigurationDAO {
    /**
     * Retourne une configuration par son nom
     * @param name nom de la configuration
     * @return Configuration demandée
     * @throws Exception si conf non trouvée
     */
    public Configuration findConfByName(String name) throws Exception;

    /**
     * retourne la liste des configurations
     * @return une liste de configurations
     * @throws Exception si aucune conf enregistrée
     */
    public ArrayList<Configuration> getConfList() throws Exception;

    /**
     * Supprime la configuration précisée
     * @param conf configuratin précisée
     * @throws Exception si conf non trouvée
     */
    public void removeConf(Configuration conf) throws Exception;

    /**
     * Met a jour la configuration précisée
     * @param conf configuratin précisée
     * @throws Exception si conf non trouvée
     */
    public void updateConf(Configuration conf) throws Exception;

    /**
     * Crée la conf précisée
     * @param conf conf précisée
     * @throws Exception si impossible ou si le nom est déja pris
     */
    public void createConf(Configuration conf) throws Exception;
}
