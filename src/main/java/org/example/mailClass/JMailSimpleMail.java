package org.example.mailClass;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class JMailSimpleMail {

    static String host = "u2.tech.hepl.local";

    public static void main(String[] args) {
        String exp = "mbangema@u2.tech.hepl.local";
        String dest = "mbangema@u2.tech.hepl.local";
        String sujet = "Bun, l'apologie du chetal!";
        String texte = "Essai d'un envoie de mail avec javax.mail!";

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", host);
        Session sess = Session.getDefaultInstance(prop, null);

        try {
            MimeMessage msg = new MimeMessage(sess);
            msg.setFrom(new InternetAddress(exp));
            msg.setRecipient(Message.RecipientType.TO,new InternetAddress(dest));
            msg.setSubject(sujet);
            msg.setText(texte);

            System.out.println("Envoie du message!");
            Transport.send(msg);
            System.out.println("Message envoyé!");
        }
        catch (MessagingException e) {
            System.out.println("Erreur lors de : " + e.getMessage());
        }
    }
}
