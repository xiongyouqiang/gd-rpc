package io.goudai.network;

import io.goudai.commons.factory.NamedThreadFactory;
import io.goudai.net.context.Context;
import io.goudai.net.context.ContextHolder;
import io.goudai.net.handler.codec.Decoder;
import io.goudai.net.handler.codec.DefaultDecoder;
import io.goudai.net.handler.codec.DefaultEncoder;
import io.goudai.net.handler.codec.Encoder;
import io.goudai.net.handler.in.ChannelInHandler;
import io.goudai.net.handler.serializer.JavaSerializer;
import io.goudai.net.handler.serializer.Serializer;
import io.goudai.net.Connector;
import io.goudai.net.ReactorPool;
import io.goudai.net.session.Session;
import io.goudai.net.session.factory.DefaultSessionFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by freeman on 2016/1/14.
 */
public class Client {
    static {
        Serializer serializer = new JavaSerializer();
        Decoder<User> decoder = new DefaultDecoder<>(serializer);
        Encoder<User> encoder = new DefaultEncoder<>(serializer);
        ChannelInHandler<User> channelInHandler =  (session,request) -> { System.out.println("client received on server ");System.out.println(request);};
        ExecutorService executorService = Executors.newFixedThreadPool(20,new NamedThreadFactory());
        Context<User, User> context = new Context<>(decoder, encoder, channelInHandler, serializer,executorService);
        ContextHolder.registed(context);
    }
    public static void main(String[] args) throws Exception {
        DefaultSessionFactory sessionFactory = new DefaultSessionFactory();
        ReactorPool reactorPool = new ReactorPool(1, sessionFactory);
        reactorPool.startup();
        Connector connector = new Connector("connector-1", reactorPool);
        connector.start();
        Session session = connector.connect(new InetSocketAddress(8888),4000, sessionFactory);
        System.out.println(session);
        session.write(new User());




//        connector.shutdown();
    }
}