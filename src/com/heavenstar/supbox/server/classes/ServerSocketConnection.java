package com.heavenstar.supbox.server.classes;

import com.heavenstar.supbox.commonClasses.SocketConnection;
import com.heavenstar.supbox.dao.DaoFactory;
import com.heavenstar.supbox.dao.UserDAO;
import com.heavenstar.supbox.entities.User;

import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 09/05/13
 */

public class ServerSocketConnection extends SocketConnection implements Runnable {
    private static String WELCOME_MESSAGE;

    private static String ALLOW_SIGNUP;

    private User user;

    /**
     * Initialisation de la nouvelle connection
     * @param sock socket utilisé pour la connection
     * @throws Exception
     */
    public ServerSocketConnection(Socket sock) throws Exception {
        try {
            System.out.println("[INFO] -> New connection from " + sock.getInetAddress());
            this.sock = sock;
            this.sock.setSoTimeout(60000);

            inBufferedReader = new BufferedInputStream(sock.getInputStream());

            inDataReader = new DataInputStream(inBufferedReader);
            outDataWriter = new DataOutputStream(sock.getOutputStream());

            writer = new PrintWriter(sock.getOutputStream());
            inStrReader = new InputStreamReader(sock.getInputStream());

            reader = new BufferedReader(inStrReader);
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Thread lancé, on suit la procédure suivante : (1, envoi du message de bienvenue 2, authentification 3, reception des fichiers)
     */
    @Override
    public void run() {
        System.out.println("[INFO] -> New thread running !");

        //Welcome Message
        try {
            sendMessage(prepareString(WELCOME_MESSAGE, "HEAD_WELCOME_MESSAGE_", null));
        } catch (Exception e) {
            System.out.println("[ERROR] -> Connection interrupted !");
            System.out.println("[INFO] -> Thread stopped");
            return;
        }

        //Authprocess
        try {
            if (!AuthProcess()) {
                System.out.println("[ERROR] -> Authentification failed !");
                return;
            }
            System.out.println("[INFO] -> Authentification succeed !");
        } catch (Exception e) {
            System.out.println("[ERROR] -> Connection interrupted !");
            System.out.println("[INFO] -> Thread stopped");
            return;
        }

        try {

            recieveFiles(user);
            System.out.println("[INFO] -> Thread stopped");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ERROR] -> Connection interrupted !");
            System.out.println("[INFO] -> Thread stopped");
            return;
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
                System.out.println("[ERROR] -> Failed to close socket !");
            }
        }
    }

    /**
     * @return true si l'authentification à réussi, retourne faux dans le cas contraire.
     * @throws Exception
     */
    private boolean AuthProcess() throws Exception {
        String value, content = "", method = null;
        UserDAO userDao = null;
        if (ServerConfig.get("useXmlAsUsersDatabase").equals("true")) {
            userDao = DaoFactory.getUserDao(DaoFactory.DataSource.XML);
        } else {
            userDao = DaoFactory.getUserDao(DaoFactory.DataSource.MYSQL);
        }
        Boolean waitForContent = false, authentified = false;
        while (true) {
            value = recieveMessage();
            if (value == null) {
                break;
            }
            Matcher regex_matcher_start = Pattern.compile(HEADER_REGEX).matcher(value);
            Matcher regex_matcher_end = Pattern.compile(FOOTER_REGEX).matcher(value);

            //SCAN FOR REQUEST HEADERS
            if (regex_matcher_start.matches()) {
                waitForContent = true;
                switch (regex_matcher_start.group(1)) {
                    case "LOGIN-METHOD":
                        method = "LOGIN";
                        break;

                    case "SIGNUP-METHOD":
                        method = "SIGNUP";
                        break;
                }

            } else if (regex_matcher_end.matches()) {
                break;
            } else { // REQUEST CONTENT
                if (waitForContent) {
                    content += value;
                } else {
                    throw new Exception("[ERROR] -> Request malformed !");
                }
            }
        }
        User user = XMLToUser(content);
        switch (method) {
            case "LOGIN":
                user = userDao.findByUsernameAndPassword(user.getUsername(), user.getPassword());
                if (user == null) {
                    sendMessage(prepareString("Accound not found !", "HEAD_LOGIN-FAILURE_", null));
                    authentified = false;
                } else {
                    this.user = user;
                    sendMessage(prepareString("Welcome back " + user.getUsername() + " ! You are now logged.", "HEAD_LOGIN-SUCCESS_", null));
                    authentified = true;
                }
                break;
            case "SIGNUP":
                if (ALLOW_SIGNUP.equals("true")) {
                    try {
                        if (userDao.create(user) == null) {
                            sendMessage(prepareString("There's already an account with this username on this server !", "HEAD_SIGNUP-FAILURE_", null));
                            authentified = false;
                        } else {
                            this.user = user;
                            sendMessage(prepareString("Your account has been created with success ! You are now logged.", "HEAD_SIGNUP-SUCCESS_", null));
                            authentified = true;
                        }
                    } catch (Exception e) {
                        authentified = false;
                    }
                } else {
                    sendMessage(prepareString("You are not allowed to create a new account on this server, please contact the server's owner.", "HEAD_SIGNUP-FAILURE_", null));
                    authentified = false;
                }
                break;
        }
        return authentified;
    }

    /**
     * Setter du ALLOW_SIGNUP, permet de savoir si on a le droit de s'inscrire ou non sur le serveur
     * @param ALLOW_SIGNUP autorisation ou non ("true"|"false")
     */
    public static void setALLOW_SIGNUP(String ALLOW_SIGNUP) {
        ServerSocketConnection.ALLOW_SIGNUP = ALLOW_SIGNUP;
    }

    /**
     * Setter du WELCOME_MESSAGE, permet de connaitre le welcome message a envoyer
     * @param WELCOME_MESSAGE Message a envoyer
     */
    public static void setWELCOME_MESSAGE(String WELCOME_MESSAGE) {
        ServerSocketConnection.WELCOME_MESSAGE = WELCOME_MESSAGE;
    }
}
