package tn.esprit.service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
public class SMSService {
    // Remplacez par vos clés d'API Twilio
    public static final String ACCOUNT_SID = "ACe7fe7b5e1babf7da07a4e2966d1ad5af";
    public static final String AUTH_TOKEN = "b7f4ca2ba8643f85186c365a7a6761a1";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Votre méthode pour envoyer un SMS
    public void sendSms(String to, String from, String body) {
        Message message = Message.creator(
                new PhoneNumber(to),  // Numéro du destinataire
                new PhoneNumber(from),  // Numéro Twilio
                body                    // Contenu du SMS
        ).create();
        System.out.println("Sent message SID: " + message.getSid());
    }
}


