package org.example.gui;

import org.example.mailClass.Mail;

import javax.mail.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

public class threadNotification extends Thread{
    Properties prop;
    private miniOuthlook Ui;
    int totalmessage = 0;

    public threadNotification(miniOuthlook ui){
        this.Ui = ui;
    }

    @Override
    public void run() {
        if(Ui.isGmail()){
            gmail();
        }
        else{
            hepl();
        }

    }

    private void hepl() {
        System.out.println("-->hepl");

        boolean run = true;

        while (run) {
            try {
                sleep(5000);

                prop = System.getProperties();
                prop.put("mail.pop3.host", "u2.tech.hepl.local");
                prop.put("mail.disable.top", true);

                Session session = Session.getInstance(prop, null);

                Store store = session.getStore("pop3");
                store.connect("u2.tech.hepl.local",Ui.getCompte().toString().split("@u2.tech.hepl.local")[0] , Ui.getMdp());

                Folder Inbox = store.getFolder("INBOX");
                Inbox.open(Folder.READ_ONLY);

                Message[] msg = Inbox.getMessages();

                System.out.println("Total de messages : " + Inbox.getMessageCount());

                if(totalmessage != Inbox.getMessageCount()){

                    totalmessage = Inbox.getMessageCount();
                    if(totalmessage != 0)
                        Notify();

                    {
                        JList list = Ui.getListMailRecut();
                        list.setModel(new DefaultListModel<Mail>());
                    }

                    System.out.println("Liste des messages :");
                    for(int i = 0; i < msg.length; i++){
                        Object content = msg[i].getContent();

                        // recuperation de tout les destinataires avant comme ca je peux generaliser pour les 2 cas
                        String Recipients = "";
                        Address[] recipients = msg[i].getRecipients(Message.RecipientType.TO);
                        for (Address recipient : recipients) {
                            Recipients += recipient.toString() + "/";
                        }

                        //debut de l'analyse
                        if(content instanceof String){
                            System.out.println("Author: " + msg[i].getFrom()[0]);
                            System.out.println("Object: " + msg[i].getSubject());
                            System.out.println("Content: " + content);
                            System.out.println();

                            // ==== recuperer les traces ====
                            String Trace = "";

                            Enumeration e = msg[i].getAllHeaders();
                            for(Header header = (Header)e.nextElement(); e.hasMoreElements(); header = (Header)e.nextElement()){
                                Trace += header.getName() + "-->" + header.getValue() + "\n";
                            }

                            JList list =  Ui.getListMailRecut();
                            DefaultListModel<Mail> ml = (DefaultListModel<Mail>)list.getModel();
                            ml.addElement(new Mail(msg[i].getFrom()[0].toString(),Recipients,msg[i].getSubject(),content.toString(),false,Trace));
                        }
                        else if(content instanceof Multipart)
                        {
                            Multipart content1 = (Multipart)content;
                            System.out.println("Mail composed by " + content1.getCount() + " components.");
                            System.out.println("Author: " + msg[i].getFrom()[0]);
                            System.out.println("Object: " + msg[i].getSubject());
                            System.out.println();

                            String plainText = "";
                            boolean hasFile = false;
                            String fileName = "";

                            for(int j = 0; j < content1.getCount(); j++){
                                System.out.println("Component " + j + ": ");
                                Part p = content1.getBodyPart(j);
                                if(p.isMimeType("text/plain")){
                                    System.out.println("text:");
                                    System.out.println(p.getContent().toString());
                                    plainText = p.getContent().toString();
                                }
                                else if(p.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)){
                                    String username = System.getProperty("user.name");
                                    File f = new File("C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                    if(!f.exists()){
                                        InputStream is = p.getInputStream();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        is.transferTo(baos);
                                        baos.flush();

                                        FileOutputStream fos = new FileOutputStream("C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                        baos.writeTo(fos);
                                        fos.close();
                                        System.out.println("File downloaded and stored in C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                    }
                                    else
                                        System.out.println("File already stored in C:/Users/"+username+"/Documents/Mail/" + p.getFileName());

                                    hasFile = true;
                                    fileName = p.getFileName();
                                }
                                System.out.println();
                            }
                            // ==== recuperer les traces ====
                            String Trace = "";

                            Enumeration e = msg[i].getAllHeaders();
                            for(Header header = (Header)e.nextElement(); e.hasMoreElements(); header = (Header)e.nextElement()){
                                Trace += header.getName() + "-->" + header.getValue() + "\n";
                            }

                            JList list =  Ui.getListMailRecut();
                            DefaultListModel<Mail> ml = (DefaultListModel<Mail>)list.getModel();
                            ml.addElement(new Mail(msg[i].getFrom()[0].toString(),Recipients,msg[i].getSubject(),plainText,hasFile,fileName,Trace));
                        }
                    }
                }

                Inbox.close(true);
                store.close();
            }
            catch (MessagingException | InterruptedException | IOException | AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void gmail(){
        System.out.println("-->gmail");

        boolean run = true;

        Properties prop = new Properties();
        prop.put("mail.store.protocol", "pop3s");
        prop.put("mail.pop3.host", "pop.gmail.com");
        prop.put("mail.pop3.port", "995");
        prop.put("mail.pop3.starttls.enable", "true");
        prop.put("mail.pop3.auth", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Ui.getCompte(),Ui.getMdp());
            }
        });

        while(run){
            try{
                sleep(5000);
                Store store = session.getStore("pop3s");
                store.connect("pop.gmail.com", Ui.getCompte(),Ui.getMdp());

                Folder Inbox = store.getFolder("INBOX");
                Inbox.open(Folder.READ_ONLY);

                Message[] msg = Inbox.getMessages();

                System.out.println("Total de messages : " + Inbox.getMessageCount());

                if(totalmessage != Inbox.getMessageCount()){
                    if(totalmessage != 0)
                        Notify();

                    totalmessage = Inbox.getMessageCount();

                    System.out.println("Liste des messages :");
                    for(int i = 0; i < msg.length; i++){
                        Object content = msg[i].getContent();

                        // recuperation de tout les destinataires avant comme ca je peux generaliser pour les 2 cas
                        String Recipients = "";
                        Address[] recipients = msg[i].getRecipients(Message.RecipientType.TO);
                        for (Address recipient : recipients) {
                            Recipients += recipient.toString() + "/";
                        }

                        //debut de l'analyse
                        if(content instanceof String){
                            System.out.println("Author: " + msg[i].getFrom()[0]);
                            System.out.println("Object: " + msg[i].getSubject());
                            System.out.println("Content: " + content);
                            System.out.println();

                            // ==== recuperer les traces ====
                            String Trace = "";

                            Enumeration e = msg[i].getAllHeaders();
                            for(Header header = (Header)e.nextElement(); e.hasMoreElements(); header = (Header)e.nextElement()){
                                Trace += header.getName() + "-->" + header.getValue() + "\n";
                            }

                            JList list =  Ui.getListMailRecut();
                            DefaultListModel<Mail> ml = (DefaultListModel<Mail>)list.getModel();
                            ml.addElement(new Mail(msg[i].getFrom()[0].toString(),Recipients,msg[i].getSubject(),content.toString(),false,Trace));
                        }
                        else if(content instanceof Multipart)
                        {
                            Multipart content1 = (Multipart)content;
                            System.out.println("Mail composed by " + content1.getCount() + " components.");
                            System.out.println("Author: " + msg[i].getFrom()[0]);
                            System.out.println("Object: " + msg[i].getSubject());
                            System.out.println();

                            String plainText = "";
                            boolean hasFile = false;
                            String fileName = "";

                            for(int j = 0; j < content1.getCount(); j++){
                                System.out.println("Component " + j + ": ");
                                Part p = content1.getBodyPart(j);
                                if(p.isMimeType("text/plain")){
                                    System.out.println("text:");
                                    System.out.println(p.getContent().toString());
                                    plainText = p.getContent().toString();
                                }
                                else if(p.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)){
                                    String username = System.getProperty("user.name");
                                    File f = new File("C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                    if(!f.exists()){
                                        InputStream is = p.getInputStream();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        is.transferTo(baos);
                                        baos.flush();

                                        FileOutputStream fos = new FileOutputStream("C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                        baos.writeTo(fos);
                                        fos.close();
                                        System.out.println("File downloaded and stored in C:/Users/"+username+"/Documents/Mail/" + p.getFileName());
                                    }
                                    else
                                        System.out.println("File already stored in C:/Users/"+username+"/Documents/Mail/" + p.getFileName());

                                    hasFile = true;
                                    fileName = p.getFileName();
                                }
                                System.out.println();
                            }
                            // ==== recuperer les traces ====
                            String Trace = "";

                            Enumeration e = msg[i].getAllHeaders();
                            for(Header header = (Header)e.nextElement(); e.hasMoreElements(); header = (Header)e.nextElement()){
                                Trace += header.getName() + "-->" + header.getValue() + "\n";
                            }

                            JList list =  Ui.getListMailRecut();
                            DefaultListModel<Mail> ml = (DefaultListModel<Mail>)list.getModel();
                            ml.addElement(new Mail(msg[i].getFrom()[0].toString(),Recipients,msg[i].getSubject(),plainText,hasFile,fileName,Trace));
                        }
                    }
                }

                Inbox.close(true);
                store.close();
            }
            catch(InterruptedException | NoSuchProviderException e){
                run = false;
            } catch (MessagingException | AWTException e) {
                throw new RuntimeException(e);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void Notify() throws AWTException {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage("Reception d'un nouveau Mail", "Vous venez de recevoir un nouveau mail!", TrayIcon.MessageType.INFO);
        } else {
            System.err.println("System tray not supported!");
        }
    }
}
