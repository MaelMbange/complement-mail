package org.example.gui;

import org.example.mailClass.Mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class miniOuthlook extends JFrame{
    private JTextField textFieldAddressMail;
    private JPasswordField textFieldMDP;
    private JTextField textFieldUsername;
    private JRadioButton normalRadioButton;
    private JRadioButton multipartRadioButton;
    private JButton envoyerButton;
    private JPanel panelBase;
    private JTextArea textAreaCorps;
    private JButton selectionnerUnFichierButton;
    private JTextField textFieldSujet;
    private JTextField textFieldMailDestinataire;
    private JCheckBox gmailCheckBox;
    private JTabbedPane tabbedPaneCER;
    private JLabel object;
    private JLabel to;
    private JLabel from;
    private JLabel files;
    private JList listMailRecut;
    private JTextPane content;
    private JButton seConnecterButton;
    private JPanel PanelConnexion;
    private JPanel PanelEnvoyer;
    private JPanel PanelRecevoir;
    private JPanel panelMailInfo;
    private JCheckBox afficherTracesCheckBox;
    private JTextPane textPaneTrace;

    private ButtonGroup group;

    private File file;
    private threadNotification th;

    static String host = "u2.tech.hepl.local";

    private boolean isConnected = false;
    private String compte = null;
    private String mdp    = null;

    public miniOuthlook(){
        super("miniOutlook");
        setContentPane(panelBase);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initcomponent();
        setSize(500,500);
    }

    private void initcomponent(){

        initConnexion();
        initEnvoit();
        initReception();
    }

    private void initEnvoit(){
        group = new ButtonGroup();

        group.add(normalRadioButton);
        group.add(multipartRadioButton);

        selectionnerUnFichierButton.setEnabled(false);

        normalRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionnerUnFichierButton.setEnabled(false);
            }
        });

        multipartRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionnerUnFichierButton.setEnabled(true);
            }
        });

        selectionnerUnFichierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isConnected){
                    JFileChooser fj = new JFileChooser();

                    int ret = fj.showOpenDialog(null);

                    if(ret == JFileChooser.APPROVE_OPTION){
                        file = fj.getSelectedFile();
                        System.out.println("path = " + file.getAbsolutePath());
                        System.out.println();
                    }
                }
                else
                    JOptionPane.showMessageDialog(miniOuthlook.this,"Vous devez vous connecter pour faire cela!");
            }
        });

        envoyerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(isConnected){
                    if(normalRadioButton.isSelected()){
                        if(gmailCheckBox.isSelected())
                            envoyerSimpleGMAIL();
                        else
                            envoyerSimpleHepl();
                    }
                    else
                    {
                        if(file != null){
                            if(gmailCheckBox.isSelected())
                                envoyerMultipleGMAIL();
                            else
                                envoyerMultipleHepl();
                        }
                        else
                            JOptionPane.showMessageDialog(miniOuthlook.this,"Please select a file to send!");
                    }
                }
                else
                    JOptionPane.showMessageDialog(miniOuthlook.this,"Vous devez vous connecter pour faire cela!");
            }
        });
    }

    private void initConnexion(){

        seConnecterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textFieldAddressMail.getText().isBlank() && !String.valueOf(textFieldMDP.getPassword()).isBlank()){
                    if(th != null) th.interrupt();
                    if(gmailCheckBox.isSelected()){
                        if(isGmailAccountValid(textFieldAddressMail.getText(),String.valueOf(textFieldMDP.getPassword()))){
                            compte = textFieldAddressMail.getText();
                            mdp    = String.valueOf(textFieldMDP.getPassword());
                            isConnected = true;
                            JOptionPane.showMessageDialog(miniOuthlook.this,"Vous etes connecté!");
                            th = new threadNotification(miniOuthlook.this);
                            th.start();
                        }
                        else
                            JOptionPane.showMessageDialog(miniOuthlook.this,"Information du compte Invalide!");
                    }
                    else
                    {
                        if(isHeplAccountValid(textFieldAddressMail.getText(),String.valueOf(textFieldMDP.getPassword()))){
                            compte = textFieldAddressMail.getText();
                            mdp    = String.valueOf(textFieldMDP.getPassword());
                            isConnected = true;
                            JOptionPane.showMessageDialog(miniOuthlook.this,"Vous etes connecté!");
                            th = new threadNotification(miniOuthlook.this);
                            th.start();
                        }
                        else
                            JOptionPane.showMessageDialog(miniOuthlook.this,"Information du compte Invalide!");
                    }
                }
                else
                    JOptionPane.showMessageDialog(miniOuthlook.this,"Vous devez remplir tous les champs pour vous connecter!");
            }
        });
    }

    private void initReception(){
        panelMailInfo.setVisible(false);
        DefaultListModel<Mail> dlm = new DefaultListModel<>();
        //dlm.addElement(new Mail("JeanClaude","ArthurD","NTM","test test",false));

        listMailRecut.setModel(dlm);

        listMailRecut.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                panelMailInfo.setVisible(true);
                Mail m = (Mail)listMailRecut.getSelectedValue();
                from.setText(m.getFrom());
                to.setText(m.getTo());
                object.setText(m.getObject());
                content.setText(m.getContent());
                if(m.hasFile())
                    files.setText("1 file: " + m.getFileName());
                else
                    files.setText("0 file");
                textPaneTrace.setText(m.getTrace());
            }
        });

        textPaneTrace.setVisible(false);
        afficherTracesCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textPaneTrace.setVisible(afficherTracesCheckBox.isSelected());
            }
        });
    }

    private void envoyerSimpleHepl(){
        //String exp = "mbangema@u2.tech.hepl.local";
        //String dest = "mbangema@u2.tech.hepl.local";

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", host);
        Session sess = Session.getDefaultInstance(prop, null);

        try {
            MimeMessage msg = new MimeMessage(sess);
            msg.setFrom(new InternetAddress(compte,textFieldUsername.getText()));
            msg.setRecipient(Message.RecipientType.TO,new InternetAddress(textFieldMailDestinataire.getText()));
            msg.setSubject(textFieldSujet.getText());
            msg.setText(textAreaCorps.getText());

            System.out.println("Envoie du message!");
            Transport.send(msg);
            System.out.println("Message envoyé!");
            JOptionPane.showMessageDialog(miniOuthlook.this,"Message envoyé!");
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            System.out.println("Erreur lors de : " + e.getMessage());
        }
        System.out.println();
    }

    private void envoyerMultipleHepl(){
        Properties prop = System.getProperties();
        //prop.put("mail.store.protocol", "pop3");
        prop.put("mail.smtp.host",host);
        //prop.put("mail.disable.top",true);
        Session sess = Session.getDefaultInstance(prop, null);

        try{
            System.out.println("Creating mail to be send!");
            Message message = new MimeMessage(sess);
            message.setFrom(new InternetAddress(compte,textFieldUsername.getText()));

            MimeMultipart multipart = new MimeMultipart();

            // Partie texte du message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(textAreaCorps.getText());
            multipart.addBodyPart(textPart);

            // Partie fichier attaché
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(file.getAbsoluteFile());
            multipart.addBodyPart(filePart);

            message.setRecipient(Message.RecipientType.TO,new InternetAddress(textFieldMailDestinataire.getText()));
            message.setSubject(textFieldSujet.getText());
            message.setContent(multipart);

            System.out.println("Sending message!");
            Transport.send(message);
            System.out.println("Done");
            JOptionPane.showMessageDialog(miniOuthlook.this,"Message Sent!");
        }
        catch (MessagingException | IOException a) {
            throw new RuntimeException(a);
        }
        System.out.println();
    }

    private void envoyerSimpleGMAIL(){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465"); // 25 sans chiffement / 587 chiffrement explicite
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(compte,mdp);
            }
        });

        try{
            System.out.println("Creating mail to be send!");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com",textFieldUsername.getText()));

            message.setRecipient(Message.RecipientType.TO,new InternetAddress(textFieldMailDestinataire.getText()));
            message.setSubject(textFieldSujet.getText());
            message.setText(textAreaCorps.getText());

            System.out.println("Sending message!");
            Transport.send(message);
            System.out.println("Done");
            JOptionPane.showMessageDialog(miniOuthlook.this,"Message envoyé!");
        }
        catch (MessagingException | UnsupportedEncodingException a) {
            throw new RuntimeException(a);
        }
    }

    private void envoyerMultipleGMAIL(){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465"); // 25 sans chiffement / 587 chiffrement explicite
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(compte,mdp);
            }
        });

        try{
            System.out.println("Creating mail to be send!");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com",textFieldUsername.getText()));

            MimeMultipart multipart = new MimeMultipart();

            // Partie texte du message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(textAreaCorps.getText());
            multipart.addBodyPart(textPart);

            // Partie fichier attaché
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(file.getAbsoluteFile());
            multipart.addBodyPart(filePart);

            message.setRecipient(Message.RecipientType.TO,new InternetAddress(textFieldMailDestinataire.getText()));
            message.setSubject(textFieldSujet.getText());
            message.setContent(multipart);

            System.out.println("Sending message!");
            Transport.send(message);
            System.out.println("Done");
            JOptionPane.showMessageDialog(miniOuthlook.this,"Message Sent!");
        }
        catch (MessagingException | IOException a) {
            throw new RuntimeException(a);
        }
    }

    public static boolean isGmailAccountValid(String email,String password){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465"); // 25 sans chiffement / 587 chiffrement explicite
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        try {
            Session session = Session.getInstance(prop, authenticator);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", email, password);
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isHeplAccountValid(String email,String password){
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);

        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        };

        try {
            Session session = Session.getInstance(properties, authenticator);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, email, password);
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    JList getListMailRecut() {
        return listMailRecut;
    }

    String getCompte(){
        return compte;
    }

    String getMdp(){
        return mdp;
    }

    boolean isGmail(){
        return gmailCheckBox.isSelected();
    }

    JTextPane getTextPaneTrace(){return textPaneTrace;}

    public static void main(String[] args){
        miniOuthlook m = new miniOuthlook();
        m.setVisible(true);
    }
}
