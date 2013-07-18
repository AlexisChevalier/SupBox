package com.heavenstar.supbox.client.classes.gui;

import com.heavenstar.supbox.client.MainGUI;
import com.heavenstar.supbox.client.classes.views.LoginSignupWindow;
import com.heavenstar.supbox.commonClasses.SocketConnection;
import com.heavenstar.supbox.entities.Configuration;
import com.heavenstar.supbox.entities.User;
import com.heavenstar.supbox.methods.GlobalMethods;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 20/05/13
 */
public class ClientSocketConnectionGui extends SocketConnection implements Runnable {
    private Configuration conf;
    private ArrayList<String> fileList;
    private LoginSignupWindow accountWnd;
    private Integer connID;

    public enum AuthMethod {
        LOGIN, SIGNUP,
    }

    /**
     * Initialise la connection
     * @param conf configuration utilisé pour la connexion
     * @param fileList liste de fichiers envoyée au moment de la création de la connection
     * @throws Exception
     */
    public ClientSocketConnectionGui(Configuration conf, ArrayList<String> fileList) throws Exception {
        this.conf = conf;
        this.fileList = new ArrayList<>(fileList);
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

        } catch (UnknownHostException e) {
            throw new Exception("[Error] => Configuration \"" + conf.getName() + "\" looks wrong ! Connection failed !");
        } catch (IOException e) {
            throw new Exception("[Error] => Connection to configuration \"" + conf.getName() + "\" failed !");
        }
    }

    /**
     * Initialise le process de transfert (affiche la fenêtre de login et lance la connection)
     * @param connID Id de la connection stockée dans MainGUI
     */
    public void Initialize(Integer connID) {
        this.connID = connID;
        //Starting loginWindow
        accountWnd = null;
        try {
            accountWnd = new LoginSignupWindow(connID);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(accountWnd,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            accountWnd = null;
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                accountWnd.setLocationRelativeTo(MainGUI.wnd);
                accountWnd.setMinimumSize(new Dimension(400, 300));
                accountWnd.setSize(400, 300);
                accountWnd.setModal(true);
                accountWnd.setVisible(true);
            }
        });
    }

    /**
     * Assure le process d'authentification
     * @param method login ou signup
     * @param username username saisi
     * @param password password saisi
     * @return true si l'authentification est bonne, false sinon
     * @throws Exception
     */
    public boolean AuthProcess(AuthMethod method, String username, String password) throws Exception {
        Boolean authentified = false;
        User user = new User();
        user.setUsername(username);
        user.setPassword(GlobalMethods.cryptWithMD5(password));
        Matcher regex_matcher_start;

        switch (method) {
            case LOGIN:
                sendMessage(prepareString(serializeToXML(user), "HEAD_LOGIN-METHOD_", "FOOT_LOGIN-METHOD_"));

                regex_matcher_start = Pattern.compile(HEADER_REGEX).matcher(recieveMessage());

                if (regex_matcher_start.matches()) {
                    switch (regex_matcher_start.group(1)) {
                        case "LOGIN-FAILURE":
                            throw new Exception(recieveMessage());
                        case "LOGIN-SUCCESS":
                            authentified = true;
                            JOptionPane.showMessageDialog(null,
                                    recieveMessage() + " File upload Started !",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                    }
                    break;
                } else {
                    throw new Exception("[Error] => Communication with the server threw an error !");
                }
            case SIGNUP:
                sendMessage(prepareString(serializeToXML(user), "HEAD_SIGNUP-METHOD_", "FOOT_SIGNUP-METHOD_"));

                regex_matcher_start = Pattern.compile(HEADER_REGEX).matcher(recieveMessage());

                if (regex_matcher_start.matches()) {
                    switch (regex_matcher_start.group(1)) {
                        case "SIGNUP-FAILURE":
                            throw new Exception(recieveMessage());
                        case "SIGNUP-SUCCESS":
                            authentified = true;
                            JOptionPane.showMessageDialog(null,
                                    recieveMessage() + " File upload Started !",
                                    "Error",
                                    JOptionPane.INFORMATION_MESSAGE);
                            break;
                    }
                    break;
                } else {
                    throw new Exception("[Error] => Communication with the server threw an error !");
                }
        }
        return authentified;
    }

    /**
     * Méthode executée par le thread (Envoi des fichiers)
     */
    @Override
    public void run() {
        try {
            sendFiles(fileList);
            JOptionPane.showMessageDialog(null,
                    "All files have been send to configuration \"" + conf.getName() + "\" !",
                    "Success !",
                    JOptionPane.INFORMATION_MESSAGE);
            MainGUI.endConn(connID);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(accountWnd,
                    e.getMessage(),
                    "Error !",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Permet de récupérer le Welcome Message
     * @return welcome message envoyé par le serveur
     * @throws Exception
     */
    public String getWelcomeMessage() throws Exception {
        try {
            if (recieveMessage().equals("HEAD_WELCOME_MESSAGE_")) {
                return recieveMessage();
            } else {
                throw new Exception("[Error] => Communication with the server threw an error !");
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
