package com.heavenstar.supbox.client.classes.views;

import com.heavenstar.supbox.client.MainGUI;
import com.heavenstar.supbox.client.classes.gui.DragDropListener;
import com.heavenstar.supbox.client.classes.models.ComboBoxConfModel;
import com.heavenstar.supbox.client.classes.models.ListConfModel;
import com.heavenstar.supbox.dao.ConfigurationDAO;
import com.heavenstar.supbox.dao.DaoFactory;
import com.heavenstar.supbox.entities.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.util.List;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 19/05/13
 */

public class MainWindow extends JFrame {

    //Panels
    final private JPanel filesPanel = new JPanel();
    final private JPanel bottomPanel = new JPanel();
    final private JPanel bottomButtonsPanel = new JPanel();
    final private JPanel confsPanel = new JPanel();
    final private JPanel addConfPanel = new JPanel();
    final private JPanel listConfPanel = new JPanel();
    final private JPanel removeConfPanel = new JPanel();
    final private JTabbedPane tabbedPane = new JTabbedPane();
    //Menu
    final private JMenuBar menuBar = new JMenuBar();
    final private JMenu menu = new JMenu("Menu");
    final private JMenuItem menuItemClose = new JMenuItem("Close");
    final private JMenuItem menuItemHelp = new JMenuItem("Help");
    final private JLabel dropMeSomething = new JLabel("Drag me a/some file(s) !", SwingConstants.CENTER);
    final private JButton sendFiles = new JButton("Send !");
    final private JButton cancelFiles = new JButton("Clear list");
    //Conf Tab
    final private JTextField nameTextField = new JTextField("Name");
    final private JTextField ipTextField = new JTextField("Ip/Domain");
    final private JTextField portTextField = new JTextField("Port");
    final private JButton addConfButton = new JButton("Create configuration !");
    final private JButton removeConfButton = new JButton("Remove configuration");
    //DAO
    private ConfigurationDAO confDAO = null;
    //Files tab
    private ComboBoxConfModel filesConfListModel = null;
    private ComboBoxConfModel removeConfListModel = null;
    private JComboBox<Object> filesConfigurationList = null;
    private JPopupMenu filesListPopupMenu = new JPopupMenu();
    private JMenuItem removeFilesPopupMenu = new JMenuItem("Remove Selected Files");
    private DefaultListModel FileListModel = new DefaultListModel();
    private JLabel fileListLabel = new JLabel("Files in queue", SwingConstants.CENTER);
    private JList bufferFileList = new JList(FileListModel);
    private JTable confListJtable = null;
    private ListConfModel tableConfModel = null;
    private JComboBox<Object> removeConfigurationList = null;

    /**
     * Initialise la MainWindow
     */
    public MainWindow() {
        super("SupBox");
        try {
            confDAO = DaoFactory.getConfigurationDao(DaoFactory.DataSource.PREFERENCES);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "An error occured !", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        }

        //initialisation des contenus dynamiques
        filesConfListModel = new ComboBoxConfModel(confDAO);
        removeConfListModel = new ComboBoxConfModel(confDAO);
        filesConfigurationList = new JComboBox<Object>(filesConfListModel);
        removeConfigurationList = new JComboBox<Object>(removeConfListModel);
        tableConfModel = new ListConfModel(confDAO);
        confListJtable = new JTable(tableConfModel);
        updateConfList();

        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.this.setVisible(false);
                MainWindow.this.dispose();
            }
        });

        InitializePanels();

        InitializeMenu();

        InitializeFilesTab();

        InitializeConfTab();

        AssembleComponents();
    }

    /**
     * Initialise les panneaux
     */
    private void InitializePanels() {
        //Layouts
        confsPanel.setLayout(new BorderLayout());
        filesPanel.setLayout(new BorderLayout());
        bottomPanel.setLayout(new BorderLayout());
        bottomButtonsPanel.setLayout(new FlowLayout());
        addConfPanel.setLayout(new FlowLayout());
        listConfPanel.setLayout(new BorderLayout());
        removeConfPanel.setLayout(new FlowLayout());

        //Organisation

        //Files
        filesPanel.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(bottomButtonsPanel, BorderLayout.SOUTH);

        //Configurations
        confsPanel.add(addConfPanel, BorderLayout.NORTH);
        confsPanel.add(listConfPanel, BorderLayout.CENTER);
        confsPanel.add(removeConfPanel, BorderLayout.SOUTH);

        //Tabs
        tabbedPane.addTab("Send files", null, filesPanel);
        tabbedPane.addTab("Configurations", null, confsPanel);
    }

    /**
     * Initialise le menu et ses actionsListeners
     */
    private void InitializeMenu() {

        final String helpText = "This is the SupBox's help panel ! \nYou can easily send files to a SupBox server. \nIn order to use it, you have to create a configuration for your server, \nthen drop some files using one of your configurations on the 'Send Files' tab.\n\n\nCopyright Alexis Chevalier (2013)";
        //CLOSE ACTION LISTENER
        menuItemClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.dispose();
            }
        });
        //HELP ACTION LISTENER
        menuItemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainWindow.this, helpText, "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });
        menu.add(menuItemHelp);
        menu.addSeparator();
        menu.add(menuItemClose);
        menuBar.add(menu);
    }

    /**
     * Initialise l'onglet des fichiers et ses ActionsListeners
     */
    private void InitializeFilesTab() {
        filesPanel.add(filesConfigurationList, BorderLayout.NORTH);
        DragDropListener dropListener = new DragDropListener();
        new DropTarget(dropMeSomething, dropListener);
        filesPanel.add(dropMeSomething, BorderLayout.CENTER);
        bottomPanel.add(new JScrollPane(bufferFileList), BorderLayout.CENTER);
        bottomPanel.add(fileListLabel, BorderLayout.NORTH);
        bottomButtonsPanel.add(sendFiles);
        bottomButtonsPanel.add(cancelFiles);

        sendFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (FileListModel.size() == 0) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Please add some files first.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        MainGUI.startNewConn((Configuration) filesConfListModel.getSelectedItem(), MainGUI.getFileList());
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(null,
                                e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        });

        cancelFiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmptyFileQueue();
                MainGUI.emptyQueue();
            }
        });

        filesListPopupMenu.add(removeFilesPopupMenu);
        bufferFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent Me) {
                if (SwingUtilities.isRightMouseButton(Me)) {
                    filesListPopupMenu.show(Me.getComponent(), Me.getX(), Me.getY());
                }
            }
        });
        removeFilesPopupMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List selectedValuesList = bufferFileList.getSelectedValuesList();
                for (Object value : selectedValuesList) {
                    String path = (String) value;
                    RemoveFileInQueue(path);
                }
            }
        });
    }

    /**
     * Initialise l'onglet de configurations et ses ActionListeners
     */
    private void InitializeConfTab() {
        nameTextField.setPreferredSize(new Dimension(90, 24));
        ipTextField.setPreferredSize(new Dimension(90, 24));
        portTextField.setPreferredSize(new Dimension(70, 24));

        addConfPanel.add(nameTextField);
        addConfPanel.add(ipTextField);
        addConfPanel.add(portTextField);
        addConfPanel.add(addConfButton);

        addConfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameTextField.getText();
                String ip = ipTextField.getText();
                String port = portTextField.getText();
                boolean valid = true;
                if (name.equals("") || name.equals("Name")) {
                    valid = false;
                }
                if (ip.equals("") || ip.equals("Ip/Domain")) {
                    valid = false;
                }
                if (port.equals("") || port.equals("Port")) {
                    valid = false;
                }

                if (valid == false) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Some fields are not correctly filled !",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    Configuration conf = new Configuration();
                    conf.setName(name);
                    conf.setServer(ip);
                    conf.setPort(port);
                    try {
                        confDAO.createConf(conf);
                        tableConfModel.addRow(conf);
                        updateConfList();
                        JOptionPane.showMessageDialog(MainWindow.this,
                                "Configuration Successfully Created !",
                                "Success !",
                                JOptionPane.INFORMATION_MESSAGE);
                        nameTextField.setText("Name");
                        ipTextField.setText("Ip/Domain");
                        portTextField.setText("Port");
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                                e1.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        confListJtable.getTableHeader().setReorderingAllowed(false);
        listConfPanel.add(confListJtable.getTableHeader(), BorderLayout.NORTH);
        listConfPanel.add(new JScrollPane(confListJtable), BorderLayout.CENTER);
        removeConfPanel.add(removeConfigurationList);

        removeConfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    confDAO.removeConf((Configuration) removeConfListModel.getSelectedItem());
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Configuration Successfully Deleted !",
                            "Success !",
                            JOptionPane.INFORMATION_MESSAGE);
                    updateConfList();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            e1.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        removeConfPanel.add(removeConfButton);
    }

    /**
     * Assemble tous les composants
     */
    private void AssembleComponents() {
        this.setJMenuBar(menuBar);
        add(tabbedPane);
        pack();
    }

    /**
     * Vide la queue de fichiers dans le modéle de la liste
     */
    public void EmptyFileQueue() {
        FileListModel.clear();
    }

    /**
     * Ajoute un item dans la liste du modéle
     * @param item item a ajouter
     */
    public void AddItemInFileQueue(String item) {
        FileListModel.addElement(item);
    }

    /**
     * Supprime le fichier de la liste du modéle
     * @param path a supprimer
     */
    public void RemoveFileInQueue(String path) {
        FileListModel.removeElement(path);
        MainGUI.removeFile(path);
    }

    /**
     * Met a jour la liste (1: Update DAO, 2: Revalidation des components, 3: UI Rebuilding)
     */
    public void updateConfList() {
        tableConfModel.update();
        confListJtable.revalidate();
        confListJtable.repaint();
        confListJtable.updateUI();
        filesConfListModel.update();
        removeConfListModel.update();
        filesConfigurationList.revalidate();
        filesConfigurationList.repaint();
        filesConfigurationList.updateUI();
        removeConfigurationList.revalidate();
        removeConfigurationList.repaint();
        removeConfigurationList.updateUI();
    }
}
