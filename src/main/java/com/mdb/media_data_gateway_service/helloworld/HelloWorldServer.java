//package com.mdb.media.media_data_gateway_service.helloworld;
//
//import io.grpc.Grpc;
//import io.grpc.InsecureServerCredentials;
//import io.grpc.Server;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class HelloWorldServer {
//    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldServer.class);
//
//    private Server server;
//    private void start() throws IOException {
//        int port = 50051;
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
////        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
////                .executor(executorService)
////                .addService(new GreeterImpl())
////                .build()
////                .start();
//    }
//
//    static class GreeterImpl  {
////        public sayHello(HelloRequest);
//    }
//
//}
