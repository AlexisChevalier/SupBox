package com.heavenstar.supbox.entities;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 08/05/13
 */
public class Configuration {
    private String name;
    private String server;
    private String port;

    /**
     * Getter du nom
     * @return le nom de la connection
     */
    public String getName() {
        return name;
    }

    /**
     * Setter du nom
     * @param name de la connection
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter du domaine/ip
     * @return domaine/ip de la connection
     */
    public String getServer() {
        return server;
    }

    /**
     * Setter du domaine/ip
     * @param server de la connection
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Getter du port
     * @return port de la connection
     */
    public String getPort() {
        return port;
    }

    /**
     * Setter du port
     * @param port de la connection
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Overriding de toString, permet de d'afficher directement une connection
     * @return string formatée
     */
    @Override
    public String toString() {
        return name + " (" + server + ":" + port + ")";
    }
}
