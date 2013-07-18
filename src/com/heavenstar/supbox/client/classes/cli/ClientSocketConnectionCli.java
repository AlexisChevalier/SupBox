package com.heavenstar.supbox.client.classes.cli;

import com.heavenstar.supbox.commonClasses.SocketConnection;
import com.heavenstar.supbox.entities.Configuration;
import com.heavenstar.supbox.entities.User;
import com.heavenstar.supbox.methods.GlobalMethods;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 09/05/13
 */
public class ClientSocketConnectionCli extends SocketConnection implements Runnable {

    private Boolean auth = false;
    private ArrayList<String> fileList = new ArrayList<>();

    /**
     * Initialise la connection
     * @param conf configuration utilisé pour la connection
     * @param arguments liste brute d'arguments envoyée par le client (contient les fichiers)
     * @throws Exception
     */
    public ClientSocketConnectionCli(Configuration conf, ArrayList<String> arguments) throws Exception {
        try {
            InetAddress address = InetAddress.getByName(conf.getServer());
            sock = new Socket(address, Integer.parseInt(conf.getPort()));
            sock.setSoTimeout(10000);

            outBufferedWriter = new BufferedOutputStream(sock.getOutputStream());

            inDataReader = new DataInputStream(sock.getInputStream());
            outDataWriter = new DataOutputStream(outBufferedWriter);

            writer = new PrintWriter(sock.getOutputStream());

            inStrReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(inStrReader);

            for (int a = 2; a < arguments.size(); a++) {
                fileList.add(arguments.get(a));
            }

            auth = AuthProcess();

        } catch (UnknownHostException e) {
            throw new Exception("[Error] => Configuration \"" + conf.getName() + "\" seems corrupted ! Connection has failed !");
        } catch (IOException e) {
            throw new Exception("[Error] => Connection to configuration \"" + conf.getName() + "\" has failed !");
        }
    }

    /**
     * Assure le process d'authentification
     * @return true si l'auth est valide
     * @throws Exception
     */
    private boolean AuthProcess() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String option = "", name = "", md5pass = "", pass, passverif;
        User user;
        Boolean authentified = false;

        // wait for Welcome Message
        System.out.println();
        if (recieveMessage().equals("HEAD_WELCOME_MESSAGE_")) {
            System.out.println(recieveMessage());
        } else {
            throw new Exception("[Error] => Client-Server communication error !");
        }
        System.out.println();

        System.out.println("Choose an option :");
        System.out.println("[1] - Login with an existing account");
        System.out.println("[2] - Create an account on this server");
        do { //BOUCLE DES OPTIONS
            System.out.print("> ");
            try {
                option = br.readLine();
            } catch (IOException e) {
                System.out.println("[Error] => Please type a valid option !");
            }
            if (option.equals("1")) { //Login

                user = promptForLogin();
                sendMessage(prepareString(serializeToXML(user), "HEAD_LOGIN-METHOD_", "FOOT_LOGIN-METHOD_"));

                Matcher regex_matcher_start = Pattern.compile(HEADER_REGEX).matcher(recieveMessage());

                if (regex_matcher_start.matches()) {
                    switch (regex_matcher_start.group(1)) {
                        case "LOGIN-FAILURE":
                            authentified = false;
                            break;
                        case "LOGIN-SUCCESS":
                            authentified = true;
                            break;
                    }
                    System.out.println(recieveMessage());
                    break;
                } else {
                    throw new Exception("[Error] => Client-Server communication error !");
                }

            } else if (option.equals("2")) { //Create account

                user = promptForSignup();
                sendMessage(prepareString(serializeToXML(user), "HEAD_SIGNUP-METHOD_", "FOOT_SIGNUP-METHOD_"));

                Matcher regex_matcher_start = Pattern.compile(HEADER_REGEX).matcher(recieveMessage());

                if (regex_matcher_start.matches()) {
                    switch (regex_matcher_start.group(1)) {
                        case "SIGNUP-FAILURE":
                            authentified = false;
                            break;
                        case "SIGNUP-SUCCESS":
                            authentified = true;
                            break;
                    }
                    System.out.println(recieveMessage());
                    break;
                } else {
                    throw new Exception("[Error] => Client-Server communication error !");
                }

            } else {
                System.out.println("[Error] => Please type a valid option !");
            }
        } while (true);
        return authentified;
    }

    /**
     * Propose a l'utilisateur de saisir ses données pour se connecter
     * @return user saisi par le client
     */
    private User promptForLogin() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String option = "", name = "", md5pass = "", pass;
        User user = new User();
        System.out.println("Type your account name");
        do { // BOUCLE DU NOM DE COMPTE POUR LOGIN
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("[Error] => Please type a valid account name !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("[Error] => Please type a valid account name !");
            }
        } while (true);


        System.out.println("Type your password");
        do { // BOUCLE DU PASSWORD POUR LOGIN
            try {
                pass = new String(System.console().readPassword());
                if (pass.length() > 0) {
                    md5pass = GlobalMethods.cryptWithMD5(pass);
                    break;
                } else {
                    System.out.println("[Error] => Please type a valid password !");
                }
            } catch (Exception e) {
                System.out.println("[Error] => Please type a valid password !");
            }
        } while (true);

        user.setPassword(md5pass);
        user.setUsername(name);
        return user;
    }

    /**
     * Propose a l'utilisateur de saisir ses données pour s'inscrire
     * @return user saisi par le client
     */
    private User promptForSignup() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String name = "", md5pass = "", pass, passverif;
        User user = new User();

        System.out.println("Type your account name");
        do { // BOUCLE DU NOM DE COMPTE POUR REGISTER
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("[Error] => Please type a valid account name !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("[Error] => Please type a valid account name !");
            }
        } while (true);


        System.out.println("Type your password");
        do { // BOUCLE DU PASSWORD POUR REGISTER
            try {
                pass = new String(System.console().readPassword());
                if (pass.length() > 0) {
                    break;
                } else {
                    System.out.println("[Error] => Please type a valid password !");
                }
            } catch (Exception e) {
                System.out.println("[Error] => Please type a valid password !");
            }
        } while (true);

        System.out.println("Confirm your password");
        do { // BOUCLE DU PASSWORD VERIF POUR REGISTER
            try {
                passverif = new String(System.console().readPassword());
                if (passverif.length() > 0) {
                    break;
                } else {
                    System.out.println("[Error] => Please confirm with a valid password");
                }
            } catch (Exception e) {
                System.out.println("[Error] => Please confirm with a valid password");
            }
        } while (true);

        if (passverif.equals(pass)) {
            try {
                md5pass = GlobalMethods.cryptWithMD5(pass);
                System.out.println("Authentification...");

                user.setPassword(md5pass);
                user.setUsername(name);
            } catch (Exception e) {
                System.out.println("[Error] => Invalid password !");
            }
        } else {
            System.out.println("[Error] => Passwords doesn't match !");
        }
        return user;
    }

    /**
     * Getter du résultat de l'authentification
     * @return boolean
     */
    public Boolean getAuth() {
        return auth;
    }


    /**
     * Méthode executée par le thread (Envoi des fichiers)
     */
    @Override
    public void run() {
        try {
            sendFiles(fileList);
            System.out.println("[Info] => File upload was successful !");
            System.out.print("> ");
        } catch (Exception e) {
            System.out.println("[Error] => Connection Interrupted !");
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
            }
        }
    }
}
