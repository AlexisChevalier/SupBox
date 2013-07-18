package com.heavenstar.supbox.dao.mysql;

import com.heavenstar.supbox.server.classes.ServerConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlConnexion {

    private static Connection connection = null;

    /**
     * Retourne la connection Mysql (et l'instancie si besoin)
     * @return Connection mysql
     * @throws Exception si il y a un soucis de connection
     */
    public static Connection getConnexion() throws Exception {
        if (connection == null) {
            try {
                String connectionString = "jdbc:mysql://" + ServerConfig.get("mysqlIpOrDomain") + ":" + ServerConfig.get("mysqlPort") + "/" + ServerConfig.get("mysqlDatabase");
                String user = ServerConfig.get("mysqlUser");
                String password = ServerConfig.get("mysqlPassword");
                Class.forName(com.mysql.jdbc.Driver.class.getName());
                return connection = DriverManager.getConnection(connectionString, user, password);
            } catch (ClassNotFoundException e) {
                throw new ClassNotFoundException("Can’t load the Mysql Driver !", e);
            } catch (SQLException e) {
                throw new SQLException("Can't connect to the database !", e);
            }
        } else {
            return connection;
        }
    }

    /**
     * Empêche la classe d'être instanciée
     */
    private MysqlConnexion() {
    }

    /**
     * Ferme la connection mysql
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if (connection != null) connection.close();
        super.finalize();
    }
}