package org.example.mailClass;

import javax.activation.MimeType;
import javax.mail.*;
import java.io.*;
import java.util.Properties;

public class JMailSimpleRecieve {

    static String host = "u2.tech.hepl.local";
    //static String username = "mbangema";
    static String username = "mbangema";
    static String password = "alkatraz";

    public static void main(String[] args){

        Properties prop = System.getProperties();
        prop.put("mail.pop3.host",host);
        prop.put("mail.disable.top",true);

        Session session = Session.getInstance(prop, null);

        try{
            Store st = session.getStore("pop3");
            st.connect(host , username, password);

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
                    System.out.println();
                }
                else if(content instanceof Multipart)
                {
                    Multipart content1 = (Multipart)content;
                    System.out.println("Mail composed by " + content1.getCount() + " components.");
                    System.out.println("Author: " + msg[i].getFrom()[0]);
                    System.out.println("Subject: " + msg[i].getSubject());
                    System.out.println();

                    for(int j = 0; j < content1.getCount(); j++){
                        System.out.println("Component " + j + ": ");
                        Part p = content1.getBodyPart(j);
                        if(p.isMimeType("text/plain")){
                            System.out.println("text:");
                            System.out.println(p.getContent().toString());
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
                        }
                        System.out.println();
                    }
                    //System.out.println();
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
