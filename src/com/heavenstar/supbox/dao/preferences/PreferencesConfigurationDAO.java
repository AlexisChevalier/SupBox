package com.heavenstar.supbox.dao.preferences;

import com.heavenstar.supbox.dao.ConfigurationDAO;
import com.heavenstar.supbox.entities.Configuration;

import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 08/05/13
 */
public class PreferencesConfigurationDAO implements ConfigurationDAO {
    private final String DEFAULT_NODE = "supbox";
    private final String CONFIGURATIONS_NODE = "configurations";

    /**
     * Retourne une configuration par son nom
     * @param name nom de la configuration
     * @return Configuration demandée
     * @throws Exception si conf non trouvée
     */
    @Override
    public Configuration findConfByName(String name) throws Exception {
        Configuration conf = new Configuration();
        try {
            if (Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).nodeExists(name)) {
                Preferences preferences = Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).node(name);
                conf.setName(preferences.get("name", null));
                conf.setServer(preferences.get("server", null));
                conf.setPort(preferences.get("port", null));

                return conf;
            } else {
                throw new Exception("Configuration inexistante !");
            }
        } catch (BackingStoreException e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * retourne la liste des configurations
     * @return une liste de configurations
     * @throws Exception si aucune conf enregistrée
     */
    @Override
    public ArrayList<Configuration> getConfList() throws Exception {
        ArrayList<Configuration> configurations = new ArrayList<>();
        try {
            Preferences preferences = Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE);
            String[] keys = preferences.childrenNames();
            for (int i = 0; i < keys.length; i++) {
                Configuration conf = new Configuration();
                Preferences actualPrefs = preferences.node(keys[i]);
                conf.setName(actualPrefs.get("name", null));
                conf.setServer(actualPrefs.get("server", null));
                conf.setPort(actualPrefs.get("port", null));
                configurations.add(conf);
            }
            if (configurations.size() == 0) {
                throw new Exception("Aucune configuration enregistrée !");
            } else {
                return configurations;
            }
        } catch (BackingStoreException e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * Supprime la configuration précisée
     * @param conf configuratin précisée
     * @throws Exception si conf non trouvée
     */
    @Override
    public void removeConf(Configuration conf) throws Exception {
        try {
            if (Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).nodeExists(conf.getName())) {
                Preferences preferences = Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).node(conf.getName());
                preferences.removeNode();
            } else {
                throw new Exception("Configuration inexistante !");
            }
        } catch (BackingStoreException e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * Met a jour la configuration précisée
     * @param conf configuratin précisée
     * @throws Exception si conf non trouvée
     */
    @Override
    public void updateConf(Configuration conf) throws Exception {
        try {
            if (Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).nodeExists(conf.getName())) {
                Preferences preferences = Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).node(conf.getName());
                preferences.put("server", conf.getServer());
                preferences.put("port", conf.getPort());
            } else {
                throw new Exception("Configuration inexistante !");
            }
        } catch (BackingStoreException e) {
            throw new BackingStoreException(e);
        }
    }

    /**
     * Crée la conf précisée
     * @param conf conf précisée
     * @throws Exception si impossible ou si le nom est déja pris
     */
    @Override
    public void createConf(Configuration conf) throws Exception {
        try {
            if (Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).nodeExists(conf.getName())) {
                throw new Exception("Il y a déja une configuration avec ce nom !");
            } else {
                Preferences preferences = Preferences.userRoot().node(DEFAULT_NODE).node(CONFIGURATIONS_NODE).node(conf.getName());
                preferences.put("name", conf.getName());
                preferences.put("server", conf.getServer());
                preferences.put("port", conf.getPort());
            }
        } catch (BackingStoreException e) {
            throw new BackingStoreException(e);
        }
    }
}