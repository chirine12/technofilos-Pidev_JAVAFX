package org.example.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;






public class EmailUtil {
    public static void sendEmail(String recipient, String subject, String body) throws MessagingException{
        // Configurer les propriétés pour la session SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Remplacez par le serveur SMTP approprié
        props.put("mail.smtp.port", "587"); // Remplacez par le port SMTP approprié

        // Créer une session SMTP authentifiée
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Ebanking.Society@gmail.com", "ypbuklkwyqlktqmi"); // Remplacez par vos identifiants de messagerie
            }
        });

        try {
            // Créer un message MIME
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mohamedamine.benkhelifa@esprit.tn")); // Remplacez par votre adresse e-mail
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            // Envoyer le message
            Transport.send(message);

            System.out.println("E-mail envoyé avec succès à : " + recipient);
        } catch (MessagingException e) {
            throw new MessagingException("Erreur lors de l'envoi de l'e-mail : " + e.getMessage());
        }
    }
}
