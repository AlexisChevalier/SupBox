package com.heavenstar.supbox.commonClasses;

import com.heavenstar.supbox.entities.User;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 12/05/13
 */
public class SocketConnection {
    protected static String HEADER_REGEX = "^HEAD_(.*)_$";
    protected static String FOOTER_REGEX = "^FOOT_(.*)_$";
    protected Socket sock;

    /* Utilisé pour les fichiers */
    protected DataOutputStream outDataWriter;
    protected DataInputStream inDataReader;
    protected BufferedInputStream inBufferedReader;
    protected BufferedOutputStream outBufferedWriter;

    /* Utilisé pour les commandes */
    protected PrintWriter writer;
    protected InputStreamReader inStrReader;
    protected BufferedReader reader;

    /**
     * Parse une string XML valide en user
     * @param xml String xml valide
     * @return User parsé depuis la chaine
     */
    protected User XMLToUser(String xml) {
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xml.getBytes()));
        return (User) decoder.readObject();
    }

    /**
     * Parse un user en string XML valide
     * @param user a parser
     * @return string parsée de puis l'objet user
     */
    protected String serializeToXML(User user) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (XMLEncoder encoder = new XMLEncoder(buffer)) {
            encoder.writeObject(user);
            encoder.flush();
        }
        return buffer.toString();
    }

    /**
     * Envoi un message a l'autre pair de la connection
     * @param message message a envoyer
     * @throws Exception
     */
    protected void sendMessage(String message) throws Exception {
        try {
            writer.println(message);
            writer.flush();
        } catch (Exception e) {
            throw new Exception("[Erreur] => Erreur de connection !");
        }
    }

    /**
     * Attend un message de l'autre pair de la connection
     * @return le message recu, si il arrive
     * @throws Exception
     */
    protected String recieveMessage() throws Exception {
        try {
            return reader.readLine();
        } catch (Exception e) {
            throw new Exception("[Erreur] => Erreur de connection !");
        }
    }

    /**
     * Process de recu de fichiers multiples
     * @param user utilisateur qui recoit les fichiers
     * @throws Exception
     */
    protected void recieveFiles(User user) throws Exception {
        int filesCount = inDataReader.readInt();
        System.out.println("[INFO] -> " + filesCount + " Files in queue !");

        File dir = new File(user.getUsername());
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        for (int i = 0; i < filesCount; i++) {
            Long fileLength = inDataReader.readLong();
            String fileName = inDataReader.readUTF();

            File outFile = new File(user.getUsername() + "/" + fileName);
            if (outFile.exists()) {
                outFile = new File(user.getUsername() + "/ copy-" + UUID.randomUUID() + "-" + fileName);
            }
            FileOutputStream fos = new FileOutputStream(outFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] theByte = new byte[1];
            for (int j = 0; j < fileLength; j++) {
                inBufferedReader.read(theByte);
                bos.write(theByte);
            }
            System.out.println("[INFO] -> File " + outFile.getName() + " recieved !");

            bos.close();
        }
    }

    /**
     * Process d'envoi de fichiers multiples
     * @param files arraylist de chemins de fichiers a envoyer
     * @throws Exception
     */
    protected void sendFiles(ArrayList<String> files) throws Exception {
        outDataWriter.writeInt(files.size());

        for (String file : files) {
            File fichier = null;
            if (System.getProperty("os.name").toLowerCase().contains("win")) { //IS ON WINDOWS
                URL url = new URL("file:///" + file.replaceAll("\"", ""));
                fichier = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            } else { //IS ON UNIX
                URL url = new URL("file:///" + file.replaceAll("\\\\ ", "%20"));
                fichier = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            }
            if (fichier.exists()) {
                long length = fichier.length();
                outDataWriter.writeLong(length);
                String name = fichier.getName();
                outDataWriter.writeUTF(name);

                FileInputStream fis = new FileInputStream(fichier);

                BufferedInputStream bis = new BufferedInputStream(fis);
                byte[] theByte = new byte[1];
                while ((bis.read(theByte)) != -1) {
                    outBufferedWriter.write(theByte);
                }
                bis.close();
            }
        }
        outBufferedWriter.flush();
    }

    /**
     * Permet de préparer une string pour matcher mon protocole personnel
     * Le protocole est constitué d'un header optionnel, d'un message, et d'un footer optionnel
     * Il est flexible et permet d'envoyer tous les messages de communication a l'autre pair de la connection
     * qu'il s'agisse d'une info, d'une erreur,
     * d'un user parsé en XML (pour le login, ou le signup, différencié via le header et arrêté par le footer)
     * @param message message à envoyer
     * @param header header du message (optionel)
     * @param footer footer du message (optionel)
     * @return
     */
    protected String prepareString(String message, String header, String footer) {
        String result = "";
        if (header != null) {
            result += (header + "\n");
        }
        result += (message);
        if (footer != null) {
            result += ("\n" + footer);
        }
        return result;
    }
}
