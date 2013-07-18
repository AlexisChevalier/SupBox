package com.heavenstar.supbox.entities;

import java.io.Serializable;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 06/05/13
 */
public class User implements Serializable {
    private Object id;
    private String username;
    private String password;

    /**
     * Getter de l'id
     * @return Long|String (long si Mysql (MysqlID), string si XML (UUID))
     */
    public Object getId() {
        return id;
    }

    /**
     * Setter de l'id
     * @param id Long|String (long si Mysql (MysqlID), string si XML (UUID))
     * @throws Exception
     */
    public void setId(Object id) throws Exception {
        if (id instanceof String || id instanceof Integer) {
            this.id = id;
        } else {
            throw new Exception("[Error !] User id must be an Integer or a String !");
        }
    }

    /**
     * Getter de l'username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter de l'username
     * @param username de l'user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter du password
     * @return password de l'user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter de l'username
     * @param password de l'user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Overriding de toString, permet de d'afficher directement un user
     * @return string formatée
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
