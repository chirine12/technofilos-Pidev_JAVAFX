package com.example.test.controllers;

import com.twilio.Twilio;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;



public class sms {
    public static void sendSms(String to, String message) {
      

        Message sms = Message.creator(
                new PhoneNumber("+21655347204"),             // Le numéro du destinataire
                new PhoneNumber("+19062845671"),  // Le numéro Twilio envoyant le SMS
                message
        ).create();

        System.out.println("SMS envoyé : " + sms.getSid());
    }
}
