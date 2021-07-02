package main.java.insurance.gateway;


//import org.apache.activemq.broker.Connection;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageSenderGateway {

    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageProducer producer;


    public MessageSenderGateway(String channelName){
       try{
           Properties props = new Properties();
           props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
           props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
           props.put(("queue." + channelName), channelName);
           Context jndiContext = new InitialContext(props);
           ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
           connection = connectionFactory.createConnection();
           session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
           // connect to the sender destination
           destination = (Destination) jndiContext.lookup(channelName);
           producer = session.createProducer(destination);
           connection.start();
       }catch (JMSException | NamingException e){e.printStackTrace();}
    }

    public Message createTextMessage(String body){
        Message msg = null;
        try {
            msg = session.createTextMessage(body);
        } catch (JMSException e) {  e.printStackTrace();}
        return msg;
    }

    public Message createTextMessage(String correlationID, String body) {
        Message msg = null;
        try {
            msg = session.createTextMessage(body);
            msg.setJMSCorrelationID(correlationID);
        } catch (JMSException e) {  e.printStackTrace();}
        return msg;
    }

    public void send(Message msg){
        try {
            producer.send(msg);
            System.out.println("[INFO] Sent Message to Queue: " + destination);
        } catch (JMSException e) {  e.printStackTrace();}
    }

    public void send(Destination destinationQueue, Message msg){
        try {
            producer = session.createProducer(destinationQueue);
            producer.send(msg);
            System.out.println("[INFO] Sent Message to Queue: " + destinationQueue);
        } catch (JMSException e) {  e.printStackTrace();}
    }

//    public void send(Message msg, Destination... destinationQueue){
//        Destination temp = destination;
//        try {
//            if(destinationQueue != null && destinationQueue[0] != null){
//                producer = session.createProducer(destinationQueue[0]);
//                temp = destinationQueue[0];
//            }
//            producer.send(msg);
//            System.out.println("[INFO] Sent Message to Queue: " + temp);
//        } catch (JMSException e) {  e.printStackTrace();}
//    }

}
