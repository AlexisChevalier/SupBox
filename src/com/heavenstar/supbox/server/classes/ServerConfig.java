package com.heavenstar.supbox.server.classes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 21/05/13
 */
public class ServerConfig {
    private static Properties configFile = null;

    /**
     * Classe non instanciable
     */
    private ServerConfig() {
    }

    /**
     * Retourne la valeur précisée parl'entrée
     * @param entry entrée précisée
     * @return valeur de l'entrée
     */
    public static String get(String entry) {
        if (configFile == null) {
            configFile = new Properties();
            try {
                File file = new File("SupBoxServerConfiguration.conf");
                configFile.load(file.toURI().toURL().openStream());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[ERROR] => Can't read SupBoxServerConfiguration.conf !");
                System.exit(0);
            }
        }
        String message = configFile.getProperty(entry);
        if (message == null)
            return "";
        return String.format(message);
    }

    /**
     * Remplace ou ajoute une clé/valeur
     * @param key clé a utiliser
     * @param value valeur a placer
     */
    public static void put(String key, String value) {
        if (configFile == null) {
            configFile = new Properties();
            try {
                File file = new File("SupBoxServerConfiguration.conf");
                configFile.loadFromXML(file.toURI().toURL().openStream());
            } catch (Exception e) {
            }
        }
        configFile.setProperty(key, value);
        try {
            configFile.store(new FileOutputStream("SupBoxServerConfiguration.conf"), null);
        } catch (IOException e) {
            System.out.println("[ERROR] => Can't save [" + key + "]=>" + value + " to the file SupBoxServerConfiguration.conf !");
        }
    }

    /**
     * Initialisation aux valeurs par défaut du fichier
     */
    public static void initalize() {
        if (configFile == null) {
            configFile = new Properties();
            try {
                File file = new File("SupBoxServerConfiguration.conf");
                configFile.loadFromXML(file.toURI().toURL().openStream());
            } catch (Exception e) {
            }
        }
        configFile.setProperty("serverPort", "9500");
        configFile.setProperty("maxAllowedSimultaneousConnections", "10");
        configFile.setProperty("serverMOTD", "Welcome on this SupBox Server !");
        configFile.setProperty("allowNewUsers", "true");
        configFile.setProperty("useMysqlAsUsersDatabase", "false");
        configFile.setProperty("useXmlAsUsersDatabase", "true");
        configFile.setProperty("mysqlIpOrDomain", "localhost");
        configFile.setProperty("mysqlPort", "8889");
        configFile.setProperty("mysqlDatabase", "SupBox");
        configFile.setProperty("mysqlUser", "root");
        configFile.setProperty("mysqlPassword", "root");
        try {
            configFile.store(new FileOutputStream("SupBoxServerConfiguration.conf"), null);
            System.out.println("[INFO] =>Configuration file created ! You can update it in your current directory !");
        } catch (IOException e) {
            System.out.println("[ERROR] => Can't initialize file SupBoxServerConfiguration.conf !");
            System.exit(0);
        }
    }
}
