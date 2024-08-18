package org.example.mailClass;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

public class GmailReception {
    static String Username = "try2reachmoon@gmail.com";
    static String Password = "jiro gzvf fyln jrxh";

    public static void main(String[] args){

        Properties prop = new Properties();
        prop.put("mail.store.protocol", "pop3");
        prop.put("mail.pop3.host", "pop.gmail.com");
        prop.put("mail.pop3.port", "995");
        prop.put("mail.pop3.starttls.enable", "true");
        prop.put("mail.pop3.auth", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(Username,Password);
            }
        });

        try{
            Store st = session.getStore("pop3s");
            st.connect("pop.gmail.com", Username, Password);

            Folder fold = st.getFolder("INBOX");
            fold.open(Folder.READ_ONLY);

            Message[] msg = fold.getMessages();

            System.out.println("Total de messages : " + fold.getMessageCount());
            System.out.println("Nouveaux messages : " + fold.getNewMessageCount());
            System.out.println();

            System.out.println("Liste des messages :");
            for(int i = 0; i < msg.length; i++){
                Object content = msg[i].getContent();

                if(content instanceof String){
                    System.out.println("Author: " + msg[i].getFrom()[0]);
                    System.out.println("Subject: " + msg[i].getSubject());
                    System.out.println("Content: " + content);
                }

                System.out.println("======== Header ========");
                Enumeration e = msg[i].getAllHeaders();
                for(Header header = (Header)e.nextElement(); e.hasMoreElements(); header = (Header)e.nextElement()){
                    System.out.println(header.getName() + "-->" + header.getValue());
                }
            }

            fold.close(true);
            st.close();
        }
        catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
