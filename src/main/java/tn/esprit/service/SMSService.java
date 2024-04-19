package tn.esprit.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {
    public void sendSms(String to, String from, String body) {
        String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
        String authToken = System.getenv("TWILIO_AUTH_TOKEN");

        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                body
        ).create();
        System.out.println("Sent message SID: " + message.getSid());
    }
}