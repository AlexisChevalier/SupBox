package com.heavenstar.supbox.dao;

import com.heavenstar.supbox.dao.mysql.MysqlConnexion;
import com.heavenstar.supbox.dao.mysql.MysqlUserDAO;
import com.heavenstar.supbox.dao.preferences.PreferencesConfigurationDAO;
import com.heavenstar.supbox.dao.xml.XmlUserDAO;
import com.heavenstar.supbox.dao.xml.XmlUserDatabaseFile;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 06/05/13
 */
public class DaoFactory {
    public enum DataSource {
        MYSQL, PREFERENCES, XML,
    }

    /**
     * Retourne l'userDAO selon la source demandée
     * @param source précisée
     * @return UserDao demandé
     * @throws Exception
     */
    public static UserDAO getUserDao(DataSource source) throws Exception {
        switch (source) {
            case MYSQL:
                return new MysqlUserDAO(MysqlConnexion.getConnexion());
            case XML:
                return new XmlUserDAO(XmlUserDatabaseFile.getDatabaseFile());
            default:
                return null;
        }
    }

    /**
     * Retourne la ConfigurationDAO demandée
     * @param source précisée
     * @return ConfigurationDao demandée
     * @throws Exception
     */
    public static ConfigurationDAO getConfigurationDao(DataSource source) throws Exception {
        switch (source) {
            case PREFERENCES:
                return new PreferencesConfigurationDAO();
            default:
                return null;
        }
    }
}
