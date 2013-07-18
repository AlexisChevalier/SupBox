package com.heavenstar.supbox.dao;

import com.heavenstar.supbox.entities.User;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 06/05/13
 */
public interface UserDAO {
    /**
     * Permet de créer un utilisateur si il n'existe pas déja
     * @param user créé
     * @return Long|String|null Long si il s'agit de Mysql, String si il s'agit de XML (car UUID utilisé), null si l'user existe déja
     * @throws Exception
     */
    public Object create(User user) throws Exception;

    /**
     * Retourne l'utilisateur décrit pas le pass et le nom précisés
     * @param username username précisé
     * @param password password précisé
     * @return User|null soit l'user demandé, soit null si non trouvé
     * @throws Exception
     */
    public User findByUsernameAndPassword(String username, String password) throws Exception;
}
