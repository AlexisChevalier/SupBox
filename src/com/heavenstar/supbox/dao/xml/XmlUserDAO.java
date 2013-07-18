package com.heavenstar.supbox.dao.xml;

import com.heavenstar.supbox.dao.UserDAO;
import com.heavenstar.supbox.entities.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.UUID;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 21/05/13
 */
public class XmlUserDAO implements UserDAO {

    private File XmlDatabase = null;

    public XmlUserDAO(File XmlDatabase) {
        this.XmlDatabase = XmlDatabase;
    }

    /**
     * Permet de créer un utilisateur si il n'existe pas déja
     * @param user créé
     * @return String|null String (UUID), null si l'user existe déja
     * @throws Exception
     */
    @Override
    public String create(User user) {
        try {
            if (user == null) {
                throw new Exception("User not defined");
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(XmlDatabase);
            Node root = document.getFirstChild();

            NodeList listUsers = document.getElementsByTagName("user");
            Integer a;
            for (a = 0; a < listUsers.getLength(); a++) {
                Element actualElem = (Element) listUsers.item(a);

                String usernameXml = actualElem.getFirstChild().getTextContent();
                if (usernameXml.equals(user.getUsername())) {
                    return null;
                }
            }

            user.setId(String.valueOf(UUID.randomUUID()));
            Element userElement = document.createElement("user");
            root.appendChild(userElement);
            userElement.setAttribute("id", String.valueOf(user.getId()));

            Element nameElement = document.createElement("name");
            nameElement.appendChild(document.createTextNode(user.getUsername()));
            userElement.appendChild(nameElement);

            Element passwordElement = document.createElement("password");
            passwordElement.appendChild(document.createTextNode(user.getPassword()));
            userElement.appendChild(passwordElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(XmlDatabase);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(user.getId());
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
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(XmlDatabase);
        User user = null;

        NodeList listUsers = document.getElementsByTagName("user");
        Integer a;
        for (a = 0; a < listUsers.getLength(); a++) {
            Element actualElem = (Element) listUsers.item(a);

            String usernameXml = actualElem.getFirstChild().getTextContent();
            String passwordXml = actualElem.getLastChild().getTextContent();
            if (usernameXml.equals(username) && passwordXml.equals(password)) {
                user = new User();
                user.setId(actualElem.getAttribute("id"));
                user.setUsername(usernameXml);
                user.setPassword(passwordXml);
                break;
            }
        }

        return user;
    }
}
