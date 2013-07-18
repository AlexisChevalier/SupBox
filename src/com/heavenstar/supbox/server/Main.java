package com.heavenstar.supbox.server;

import com.heavenstar.supbox.server.classes.ServerConfig;
import com.heavenstar.supbox.server.classes.ServerSocketConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    /**
     * Effectue toutes les vérifications/initialisations et lance le serveur
     * @param args
     */
    public static void main(String[] args) {
        checkServerConf();
        ServerSocket listen;
        Socket sock;
        try {
            listen = new ServerSocket(Integer.parseInt(ServerConfig.get("serverPort")), Integer.parseInt(ServerConfig.get("maxAllowedSimultaneousConnections")));
            ServerSocketConnection.setALLOW_SIGNUP(ServerConfig.get("allowNewUsers"));
            ServerSocketConnection.setWELCOME_MESSAGE(ServerConfig.get("serverMOTD"));
            System.out.println("[INFO] => SupBox Server running on port" + listen.getLocalPort() + " ! ");
            System.out.println("[INFO] => Waiting for connections !");
            while (true) {
                sock = listen.accept();
                ServerSocketConnection conn = new ServerSocketConnection(sock);
                new Thread(conn).start();
            }
        } catch (Exception e) {
            System.out.println("[ERROR] -> Server Failed to start ! Check the trace !");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Vérifie les fichies de configuration du serveur, les initialise si besoin
     */
    public static void checkServerConf() {
        File supBoxServerConfiguration = new File("SupBoxServerConfiguration.conf");
        if (supBoxServerConfiguration.exists()) {
            if (ServerConfig.get("useXmlAsUsersDatabase").equals("true") && ServerConfig.get("useMysqlAsUsersDatabase").equals("true")) {
                System.out.println("[ERROR] => You can't use Xml and Mysql simultaneously as User's Database !");
                System.exit(0);
            }

            if (ServerConfig.get("useXmlAsUsersDatabase").equals("false") && ServerConfig.get("useMysqlAsUsersDatabase").equals("false")) {
                System.out.println("[ERROR] => You have to choose between Xml or Mysql for the User's Database !");
                System.exit(0);
            }
        } else {
            ServerConfig.initalize();
        }

        if (ServerConfig.get("useXmlAsUsersDatabase").equals("true")) {
            System.out.println("[INFO] => Using Xml as Users's Database !");

            File supBoxServerUsersDatabase = new File("SupBoxServerUsersDatabase.xml");

            if (supBoxServerUsersDatabase.exists()) {
                System.out.println("[INFO] => Xml User's Database found at " + supBoxServerUsersDatabase.getAbsolutePath());
            } else {
                System.out.println("[INFO] => Xml User's Database file not foud, Initalizing...");
                InitializeXmlUsersDatabase(supBoxServerUsersDatabase);
            }
        }
        if (ServerConfig.get("useMysqlAsUsersDatabase").equals("true")) {
            System.out.println("[INFO] => Using Mysql as Users's Database !");
        }
    }

    /**
     * Initialise le fichier XML de stockage des utilisateurs
     * @param file fichier a utiliser
     */
    public static void InitializeXmlUsersDatabase(File file) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("SupBoxUsersDatabase");
            doc.appendChild(rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);

            System.out.println("[INFO] => User's Database " + file.getAbsolutePath() + " Initializated !");

        } catch (Exception e) {
            System.out.println("[ERROR] -> Failed to initialize XML User's Database ! Check the trace or use Mysql !");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
