package com.heavenstar.supbox.client.classes.views;

import com.heavenstar.supbox.client.MainGUI;
import com.heavenstar.supbox.client.classes.gui.ClientSocketConnectionGui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @User: CHEVALIER Alexis <Alexis.Chevalier@supinfo.com>
 * @Date: 20/05/13
 */
public class LoginSignupWindow extends JDialog {
    final JPanel welcomeHeaderPanel = new JPanel();
    final JPanel welcomeMessagePanel = new JPanel();
    final JPanel headerPanel = new JPanel();
    final JPanel topPanel = new JPanel();
    final JPanel loginPanel = new JPanel();
    final JPanel signupPanel = new JPanel();
    final JPanel buttonsPanel = new JPanel();
    final JPanel bottomPanel = new JPanel();
    //WelcomeMessage
    private JLabel welcomeMessageLabel = new JLabel("", SwingConstants.CENTER);
    //Header
    final private JLabel headerLoginLabel = new JLabel("Login", SwingConstants.CENTER);
    final private JLabel headerSignupLabel = new JLabel("Signup", SwingConstants.CENTER);
    //Login
    final private JLabel loginAccountLabel = new JLabel("Account Name", SwingConstants.CENTER);
    final private JTextField loginAccountField = new JTextField("", SwingConstants.CENTER);
    final private JLabel loginPasswordLabel = new JLabel("Password", SwingConstants.CENTER);
    final private JPasswordField loginPasswordField = new JPasswordField("", SwingConstants.CENTER);
    //Signup
    final private JLabel signupAccountLabel = new JLabel("Account Name", SwingConstants.CENTER);
    final private JTextField signupAccountField = new JTextField("", SwingConstants.CENTER);
    final private JLabel signupPasswordLabel = new JLabel("Password", SwingConstants.CENTER);
    final private JPasswordField signupPasswordField = new JPasswordField("", SwingConstants.CENTER);
    final private JLabel signupConfirmPasswordLabel = new JLabel("Confirm Password", SwingConstants.CENTER);
    final private JPasswordField signupConfirmPasswordField = new JPasswordField("", SwingConstants.CENTER);
    final private JButton loginButton = new JButton("Login !");

    //Buttons
    final private JButton signupButton = new JButton("Signup !");
    //Bottom
    final private JButton cancelButton = new JButton("Cancel");

    private Integer ConnID;

    /**
     * Initialise la fenêtre de login/signup
     * @param connId id de la connection stockée dans MainGUI
     * @throws Exception
     */
    public LoginSignupWindow(Integer connId) throws Exception {
        this.ConnID = connId;

        welcomeMessageLabel.setText(MainGUI.getConn(ConnID).getWelcomeMessage());

        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                LoginSignupWindow.this.setVisible(false);
                LoginSignupWindow.this.dispose();
            }
        });

        headerLoginLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLoginLabel.setForeground(Color.blue);
        headerSignupLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerSignupLabel.setForeground(Color.blue);

        InitializePanels();

        InitializeLoginPanel();

        InitializeSignupPanel();

        InitializeButtonsPanel();

        InitializeBottomPanel();

        AssembleComponents();
    }

    /**
     * Initialise tous les panneaux
     */
    private void InitializePanels() {
        this.getContentPane().setLayout(new BorderLayout());
        GridBagConstraints c = new GridBagConstraints();
        welcomeHeaderPanel.setLayout(new BorderLayout());
        welcomeMessagePanel.setLayout(new FlowLayout());
        headerPanel.setLayout(new GridLayout(1, 2));
        topPanel.setLayout(new GridBagLayout());
        loginPanel.setLayout(new GridLayout(4, 1));
        signupPanel.setLayout(new GridLayout(6, 1));
        buttonsPanel.setLayout(new GridLayout(1, 2));
        bottomPanel.setLayout(new BorderLayout());


        welcomeHeaderPanel.add(welcomeMessageLabel, BorderLayout.NORTH);
        welcomeHeaderPanel.add(headerPanel,BorderLayout.CENTER);
        this.add(welcomeHeaderPanel, BorderLayout.NORTH);
        this.add(topPanel, BorderLayout.CENTER);

        headerPanel.add(headerLoginLabel);
        headerPanel.add(headerSignupLabel);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(0, 25, 0, 25);

        topPanel.add(loginPanel, c);

        c.gridx = 1;
        topPanel.add(signupPanel, c);


        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Initialise le loginpanel
     */
    private void InitializeLoginPanel() {
        loginPanel.add(loginAccountLabel);
        loginPanel.add(loginAccountField);
        loginPanel.add(loginPasswordLabel);
        loginPanel.add(loginPasswordField);
    }

    /**
     * Initialise le SignupPanel
     */
    private void InitializeSignupPanel() {
        signupPanel.add(signupAccountLabel);
        signupPanel.add(signupAccountField);
        signupPanel.add(signupPasswordLabel);
        signupPanel.add(signupPasswordField);
        signupPanel.add(signupConfirmPasswordLabel);
        signupPanel.add(signupConfirmPasswordField);
    }

    /**
     * Initialise les boutons et leurs actionlisteners
     */
    private void InitializeButtonsPanel() {
        buttonsPanel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loginAccountField.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please type your account name !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else if (String.valueOf(loginPasswordField.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(null, "Please type your password !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else {
                    try {
                        if (MainGUI.getConn(ConnID).AuthProcess(ClientSocketConnectionGui.AuthMethod.LOGIN, loginAccountField.getText().trim(), String.valueOf(loginPasswordField.getPassword()))) {
                            LoginSignupWindow.this.dispose();
                            MainGUI.runConn(ConnID);
                            MainGUI.emptyQueue();
                        }
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(LoginSignupWindow.this,
                                e1.getMessage(),
                                "Error !",
                                JOptionPane.ERROR_MESSAGE);
                        LoginSignupWindow.this.dispose();
                    }
                }
            }
        });

        buttonsPanel.add(signupButton);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupAccountField.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please type an account name !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else if (String.valueOf(signupPasswordField.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(null, "Please type a password !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else if (String.valueOf(signupConfirmPasswordField.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(null, "Please confirm your password !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else if (!String.valueOf(signupPasswordField.getPassword()).equals(String.valueOf(signupConfirmPasswordField.getPassword()))) {
                    JOptionPane.showMessageDialog(null, "Your passwords doesn't match !", "An error occured !", JOptionPane.PLAIN_MESSAGE);
                } else {
                    try {
                        if (MainGUI.getConn(ConnID).AuthProcess(ClientSocketConnectionGui.AuthMethod.SIGNUP, signupAccountField.getText().trim(), String.valueOf(signupPasswordField.getPassword()))) {
                            LoginSignupWindow.this.dispose();
                            MainGUI.runConn(ConnID);
                            MainGUI.emptyQueue();
                        }
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(LoginSignupWindow.this,
                                e1.getMessage(),
                                "Error !",
                                JOptionPane.ERROR_MESSAGE);
                        LoginSignupWindow.this.dispose();
                    }
                }
            }
        });
    }

    /**
     *  Initialize le panel du bas
     */
    private void InitializeBottomPanel() {
        bottomPanel.add(buttonsPanel, BorderLayout.NORTH);
        bottomPanel.add(cancelButton, BorderLayout.SOUTH);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginSignupWindow.this.dispose();
            }
        });
    }

    /**
     * Assemble tous les composants
     */
    private void AssembleComponents() {
        pack();
    }
}
