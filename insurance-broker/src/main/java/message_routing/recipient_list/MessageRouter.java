package main.java.message_routing.recipient_list;

import common_services.gateway.MessageSenderGateway;

import javax.jms.Message;
import java.util.Arrays;
import java.util.Iterator;

public class MessageRouter {
    public static void sendToRecipientList(Message msg, MessageSenderGateway[] recipientList) {
        System.out.println("[INFO] Message Router ====================");

        Iterator<MessageSenderGateway> iterator = Arrays.asList(recipientList).iterator();

        while (iterator.hasNext()) {
            MessageSenderGateway sender = iterator.next();
            System.out.println("[INFO] Sending to route...");
            sender.send(msg);
        }
        System.out.println("-------------------------------------------");
    }
}
