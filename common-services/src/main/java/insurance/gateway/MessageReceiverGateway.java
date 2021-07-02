package main.java.insurance.gateway;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageReceiverGateway {
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer;
    private TemporaryQueue temporaryQueue;


    public MessageReceiverGateway(String channelName, boolean... useTemporaryQueue){
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            props.put(("queue." + channelName), channelName);

            Context jndiContext = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if((useTemporaryQueue.length >= 1 && useTemporaryQueue[0])){
                // connect to the receiver destination
                temporaryQueue = session.createTemporaryQueue();
                consumer = session.createConsumer(temporaryQueue);
            }else{
                // connect to the receiver destination
                destination = (Destination) jndiContext.lookup(channelName);
                consumer = session.createConsumer(destination);
            }
            connection.start();
        } catch (JMSException | NamingException e) {  e.printStackTrace();}

    }

    public TemporaryQueue getTemporaryQueue() {
        return temporaryQueue;
    }

    public void setListener(MessageListener ml){
        try {
            consumer.setMessageListener(ml);
        } catch (JMSException e) {  e.printStackTrace();}
    }
}
