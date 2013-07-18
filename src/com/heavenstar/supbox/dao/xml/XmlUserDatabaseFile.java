package com.heavenstar.supbox.dao.xml;

import java.io.File;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 21/05/13
 */
public class XmlUserDatabaseFile {
    private static File DatabaseFile = null;

    /**
     * Returns l' XML Database File object et l'instancie si besoin
     * @return File database
     * @throws Exception si il y a un soucis de chargement
     */
    public static File getDatabaseFile() throws Exception {
        if (DatabaseFile == null) {
            DatabaseFile = new File("SupBoxServerUsersDatabase.xml");
        }
        return DatabaseFile;
    }

    /**
     * Empêche la classe d'être instanciée
     */
    private XmlUserDatabaseFile() {
    }
}
