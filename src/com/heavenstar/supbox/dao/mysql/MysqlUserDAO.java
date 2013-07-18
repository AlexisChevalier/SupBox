package com.heavenstar.supbox.dao.mysql;

import com.heavenstar.supbox.dao.UserDAO;
import com.heavenstar.supbox.entities.User;

import java.sql.*;

public class MysqlUserDAO implements UserDAO {

    private Connection connection = null;

    public MysqlUserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Permet de créer un utilisateur si il n'existe pas déja
     * @param user créé
     * @return Long|null Long (Mysql ID), null si l'user existe déja
     * @throws Exception
     */
    @Override
    public Long create(User user) throws Exception {
        if (user == null) {
            throw new Exception("User not defined");
        }
        Long id = null;
        ResultSet result = null;

        try {
            PreparedStatement psCheck = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            psCheck.setString(1, user.getUsername());
            result = psCheck.executeQuery();

            if (result.first()) {
                return null;
            }

            PreparedStatement ps = this.connection.prepareStatement(
                    "INSERT INTO users (username, password) " +
                            "VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();

            result = ps.getGeneratedKeys();
            result.first();
            id = result.getLong(1);

        } catch (SQLException e) {
            throw new SQLException("SQL ERROR", e);
        } catch (Exception e) {
            throw new SQLException("Unknown Exception", e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    throw new SQLException("SQL ERROR", e);
                }
            }
        }

        return id;
    }

    /**
     * Retourne l'utilisateur décrit pas le pass et le nom précisés
     * @param username username précisé
     * @param password password précisé
     * @return User|null soit l'user demandé, soit null si non trouvé
     * @throws Exception
     */
    @Override
    public User findByUsernameAndPassword(String username, String password) throws Exception {
        User u = null;
        ResultSet result = null;
        try {
            PreparedStatement ps = this.connection.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ?",
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();

            if (result.first()) {
                u = new User();
                u.setId(result.getInt("id"));
                u.setUsername(result.getString("username"));
                u.setPassword(result.getString("password"));
            }

        } catch (SQLException e) {
            throw new SQLException("SQL ERROR", e);
        } catch (Exception e) {
            throw new SQLException("Unknown Exception", e);
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    throw new SQLException("SQL ERROR", e);
                }
            }
        }
        return u;
    }
}
