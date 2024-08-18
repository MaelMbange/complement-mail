package org.example.mailClass;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

public class GmailSimple {
    static String Username = "mail@gmail.com";
    static String Password = "mailpassword";

    static String pseudo = "DART";

    static String sujet = "Test n°" + new Random().nextInt(1000);
    static String texte = "Ceci est un test d'envoie de mail!";

    public static void main(String[] args){

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465"); // 25 sans chiffement / 587 chiffrement explicite
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Username,Password);
            }
        });

        try{
            System.out.println("Creating mail to be send!");

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com",pseudo));

            message.setRecipient(Message.RecipientType.TO,new InternetAddress(Username));
            message.setSubject(sujet);
            message.setText(texte);

            System.out.println("Sending message!");
            Transport.send(message);
            System.out.println("Done");
        }
        catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
