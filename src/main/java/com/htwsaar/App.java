package com.htwsaar;


import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App
{
    public static void main(String[] args) throws Exception
    {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(simpleWebSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        String url = "ws://localhost:8080/chat";
        StompSessionHandler sessionHandler = new MyStompSessionHandler();

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("login", "user");
        connectHeaders.add("passcode", "user");

        StompSession session = stompClient.connect(url, (WebSocketHttpHeaders) null, connectHeaders, sessionHandler).get();

        session.subscribe("/app/get.user.by.name/user", new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return User.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                System.err.println(payload.toString());
            }
        });

        session.subscribe("/user/queue/messages", new StompFrameHandler()
        {
            @Override
            public Type getPayloadType(StompHeaders headers)
            {
                return Message[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload)
            {
                Message[] messages = (Message[]) payload;

                int[] messageIds = new int[messages.length];

                for (int i = 0; i < messages.length; i++)
                {
                    messageIds[i] = messages[i].id;
                }

                if (messageIds.length > 0)
                {
                    session.send("/app/received", messageIds);
                }
            }
        });


        session.send("/app/receive", "");


        Thread.sleep(100000000);
    }
}

