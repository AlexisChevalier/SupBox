package com.heavenstar.supbox.client;

import com.heavenstar.supbox.client.classes.cli.ClientSocketConnectionCli;
import com.heavenstar.supbox.dao.ConfigurationDAO;
import com.heavenstar.supbox.dao.DaoFactory;
import com.heavenstar.supbox.entities.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainCLI {
    //PREFERENCES DAO
    private static ConfigurationDAO confDAO;

    private static String REGEX_FILES_WINDOWS = "([^\\s\"+]+|\"([^\"]*)\")";
    private static String REGEX_FILES_UNIX = "((\\\\ |[^ ])+)";

    /**
     * Initialise le client CLI
     * @param args arguments de la console
     */
    public static void main(String[] args) {
        try {
            confDAO = DaoFactory.getConfigurationDao(DaoFactory.DataSource.PREFERENCES);
        } catch (Exception e) {
            System.out.println("[Error] -> Can't load preferences, please check your Java installation !");
        }

        String input = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        showHelp();

        do {
            System.out.print("> ");
            try {
                input = br.readLine();
            } catch (IOException e) {
                System.out.println("[Error] -> Please type a valid option !");
            }

            if (input.equals("exit")) {
                System.out.println("[Info] -> Closing...");
                break;
            }

            try {
                parseInput(input);
            } catch (Exception e) {
                System.out.println("[Error] -> Please type a valid option !");
            }

        } while (true);

        try {
            br.close();
        } catch (IOException e) {
            System.out.println("[Error] -> Please type a valid option !");
        }
    }

    /**
     * Affiche l'aide
     */
    private static void showHelp() {
        System.out.println("Available Options :");
        System.out.println("createconf {[name] [ip/domain] [port]}: Create new configuration");
        System.out.println("deleteconf [name]: Delete specified configuration");
        System.out.println("getconf [name]: Show specified configuration details");
        System.out.println("listconfs: List all configurations");
        System.out.println("editconf [name] [host] [port]: Edit specified configuration with specified details");
        System.out.println("use [name] [file1] {[file2] [file3]} : Send all files with the specified configuration");
        System.out.println("exit: Quit this program");
        System.out.println("help: Show program help");
    }

    /**
     * Parse l'entrée clavier et appelle la bonne fonction si besoin
     * @param input entrée clavier de l'utilisateur
     * @throws Exception
     */
    private static void parseInput(String input) throws Exception {
        ArrayList<String> arguments = new ArrayList<>();

        //Match de tous les arguments (y compris les chemins de fichiers avec espace)
        Matcher regex_matcher = null;
        if (System.getProperty("os.name").toLowerCase().contains("win")) { //IS ON WINDOWS
            regex_matcher = Pattern.compile(REGEX_FILES_WINDOWS).matcher(input);
        } else { //IS ON UNIX
            regex_matcher = Pattern.compile(REGEX_FILES_UNIX).matcher(input);
        }

        while (regex_matcher.find()) {
            arguments.add(regex_matcher.group(0));
        }
        if (arguments.size() <= 0) {
            System.out.println("[Error] -> Unrecognized Command !");
        } else {
            switch (arguments.get(0).toLowerCase()) {
                case "createconf":

                    if (arguments.size() == 1) {
                        createConfWithoutParams();
                    } else if (arguments.size() == 4) {
                        createConfWithParams(arguments);
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "deleteconf":
                    if (arguments.size() == 1) {
                        removeConfWithoutParams();
                    } else if (arguments.size() == 2) {
                        removeConfWithParams(arguments);
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "getconf":
                    if (arguments.size() == 1) {
                        getConfWithoutParams();
                    } else if (arguments.size() == 2) {
                        getConfWithParams(arguments);
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "listconfs":
                    if (arguments.size() == 1) {
                        listConfs();
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "editconf":
                    if (arguments.size() == 1) {
                        editConfWithoutParams();
                    } else if (arguments.size() == 4) {
                        editConfWithParams(arguments);
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "use":
                    if (arguments.size() == 1) {
                        useWithoutParams();
                    } else if (arguments.size() >= 3) {
                        try {
                            useWithParams(arguments);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                case "help":
                    if (arguments.size() == 1) {
                        showHelp();
                    } else {
                        System.out.println("[Error] -> Unrecognized Command !");
                    }

                    break;
                default:
                    System.out.println("[Error] -> Unrecognized Command !");
                    break;
            }
        }
        arguments.clear();
    }

    /**
     * Utilitaire pour créer une conf (arguments déja saisis)
     * @param arguments arguments saisis par l'user
     */
    private static void createConfWithParams(ArrayList<String> arguments) {
        if (!arguments.get(1).equals("") && !arguments.get(2).equals("") && !arguments.get(3).equals("")) {
            Configuration conf = new Configuration();
            conf.setName(arguments.get(1));
            conf.setServer(arguments.get(2));
            conf.setPort(arguments.get(3));
            try {
                confDAO.createConf(conf);
                System.out.println("[Info] -> Configuration " + conf.getName() + " created successfully !");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("[Error] -> Wrong parameters, try 'createconf' instead !");
        }
    }

    /**
     * Utilitaire pour créer une conf (Propose la saisie)
     */
    private static void createConfWithoutParams() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String name = "", server = "", port = "";
        System.out.println("Please type the configuration's name :");
        do {
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name ! !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name !");
            }
        } while (true);

        System.out.println("Please type an ip/domain name for the configuration :");
        do {
            System.out.print("> ");
            try {
                server = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid ip/domain name !");
            }
            if (server.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid ip/domain name !");
            }
        } while (true);

        System.out.println("Please type the port of the configuration :");
        do {
            System.out.print("> ");
            try {
                port = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid port !");
            }
            if (port.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid port !");
            }
        } while (true);

        Configuration conf = new Configuration();
        conf.setName(name);
        conf.setServer(server);
        conf.setPort(port);

        try {
            confDAO.createConf(conf);
            System.out.println("[Info] -> Configuration " + conf.getName() + " created successfully !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Utilitaire pour lister les confs
     */
    private static void listConfs() {
        try {
            ArrayList<Configuration> confs = confDAO.getConfList();
            System.out.println("---> Liste des configurations :");
            for (Configuration config : confs) {
                System.out.println("[" + config.getName() + "] => " + config.getServer() + ":" + config.getPort());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Utilitaire pour supprimer une conf (arguments déja saisis)
     * @param args arguments saisis par l'user
     */
    private static void removeConfWithParams(ArrayList<String> args) {
        if (!args.get(1).equals("")) {
            Configuration conf = new Configuration();
            conf.setName(args.get(1));
            try {
                confDAO.removeConf(conf);
                System.out.println("[Info] -> Configuration " + conf.getName() + " removed successfully !");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("[Error] -> Wrong parameters, try 'removeconf' instead !");
        }
    }

    /**
     * Utilitaire pour supprimer une conf (Propose la saisie)
     */
    private static void removeConfWithoutParams() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String name = "";
        System.out.println("Please type the name of the configuration you want to delete :");
        do {
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name!");
            }
        } while (true);

        Configuration conf = new Configuration();
        conf.setName(name);
        try {
            confDAO.removeConf(conf);
            System.out.println("[Info] -> Configuration " + conf.getName() + " removed successfully !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Utilitaire pour éditer une conf (arguments déja saisis)
     * @param args arguments saisis par l'user
     */
    private static void editConfWithParams(ArrayList<String> args) {
        if (!args.get(1).equals("") && !args.get(2).equals("") && !args.get(3).equals("")) {
            Configuration conf = new Configuration();
            conf.setName(args.get(1));
            conf.setServer(args.get(2));
            conf.setPort(args.get(3));
            try {
                confDAO.updateConf(conf);
                System.out.println("[Info] -> Configuration " + conf.getName() + " updated successfully !");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("[Error] -> Wrong parameters, try 'editconf' instead !");
        }
    }

    /**
     * Utilitaire pour éditer une conf (Propose la saisie)
     */
    private static void editConfWithoutParams() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String name = "", server = "", port = "";
        System.out.println("Please type the name of the configuration you want to edit :");
        do {
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name ! !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name !");
            }
        } while (true);

        System.out.println("Please type the new ip/domain name of the configuration :");
        do {
            System.out.print("> ");
            try {
                server = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid ip/domain name !");
            }
            if (server.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid ip/domain name !");
            }
        } while (true);

        System.out.println("Please type the new port of the configuration :");
        do {
            System.out.print("> ");
            try {
                port = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid port !");
            }
            if (port.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid port !");
            }
        } while (true);

        Configuration conf = new Configuration();
        conf.setName(name);
        conf.setServer(server);
        conf.setPort(port);

        try {
            confDAO.updateConf(conf);
            System.out.println("[Info] -> Configuration " + conf.getName() + " updated successfully !");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Utilitaire pour récupérer une conf (arguments déja saisis)
     * @param args arguments saisis par l'user
     */
    private static void getConfWithParams(ArrayList<String> args) {
        try {
            Configuration conf = confDAO.findConfByName(args.get(1));
            System.out.println("[Info] -> Configuration " + conf.getName() + " found successfully !");
            System.out.println("[" + conf.getName() + "] => " + conf.getServer() + ":" + conf.getPort());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Utilitaire pour récupérer une conf (Propose la saisie)
     */
    private static void getConfWithoutParams() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String name = "";
        System.out.println("Please type the name of the configuration you want :");
        do {
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name!");
            }
        } while (true);

        try {
            Configuration conf = confDAO.findConfByName(name);
            System.out.println("[Info] -> Configuration " + conf.getName() + " found successfully !");
            System.out.println("[" + conf.getName() + "] => " + conf.getServer() + ":" + conf.getPort());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Utilitaire pour utiliser une conf (arguments déja saisis)
     * @param args arguments saisis par l'user
     */
    private static void useWithParams(ArrayList<String> args) throws Exception {
        Configuration conf;
        ClientSocketConnectionCli conn;
        //Test de l'existance de la configuration
        try {
            conf = confDAO.findConfByName(args.get(1));
        } catch (Exception e) {
            throw new Exception("[Error] => Configuration " + args.get(1) + " doesn't exists !");
        }

        //Test de l'existance des fichiers
        for (int i = 2; i < args.size(); i++) {
            File f = null;
            if (System.getProperty("os.name").toLowerCase().contains("win")) { //IS ON WINDOWS
                URL url = new URL("file:///" + args.get(i).replaceAll("\"", ""));
                f = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            } else { //IS ON UNIX
                URL url = new URL("file:///" + args.get(i).replaceAll("\\\\ ", "%20"));
                f = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            }
            if (!f.exists()) {
                throw new Exception("[Error] => File " + args.get(i) + " doesn't exists !");
            } else if (f.isDirectory()) {
                throw new Exception("[Error] => File " + args.get(i) + " is a folder !");
            }
        }

        //Test de la connection
        conn = new ClientSocketConnectionCli(conf, args);

        //Run du thread
        if (conn.getAuth()) {
            System.out.println("File uploading in progress !");
            new Thread(conn).start();
        } else {
            System.out.println("[Error] => File uploading cancelled !");
        }
    }

    /**
     * Utilitaire pour utiliser une conf (Propose la saisie)
     */
    private static void useWithoutParams() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String name = "";
        String files = "";
        System.out.println("Please type the name of the configuration you want :");
        do {
            System.out.print("> ");
            try {
                name = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name !");
            }
            if (name.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name!");
            }
        } while (true);

        System.out.println("Please type or drop the files you want to upload on the server :");
        do {
            System.out.print("> ");
            try {
                files = br.readLine();
            } catch (IOException e) {
                System.out.println("Please type a valid name !");
            }
            if (files.length() > 0) {
                break;
            } else {
                System.out.println("Please type a valid name!");
            }
        } while (true);

        ArrayList<String> arguments = new ArrayList<>();

        //C'est un peu crade, mais ca marche parfaitement.

        arguments.add("use");
        arguments.add(name);

        //Match de tous les arguments (y compris les chemins de fichiers avec espace)
        Matcher regex_matcher = null;
        if (System.getProperty("os.name").toLowerCase().contains("win")) { //IS ON WINDOWS
            regex_matcher = Pattern.compile(REGEX_FILES_WINDOWS).matcher(files);
        } else { //IS ON UNIX
            regex_matcher = Pattern.compile(REGEX_FILES_UNIX).matcher(files);
        }


        Integer a = 2;
        while (regex_matcher.find()) {
            arguments.add(a, regex_matcher.group(0));
            a++;
        }

        useWithParams(arguments);
    }
}
