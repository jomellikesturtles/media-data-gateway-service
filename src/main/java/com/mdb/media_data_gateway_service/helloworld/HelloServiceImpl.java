package com.mdb.media_data_gateway_service.helloworld;

import org.springframework.grpc.server.service.GrpcService;

import com.mdb.media_data_gateway_service.grpc.HelloRequest;
import com.mdb.media_data_gateway_service.grpc.HelloResponse;
import com.mdb.media_data_gateway_service.grpc.HelloServiceGrpc;

import io.grpc.stub.StreamObserver;

@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    public HelloServiceImpl() {

    }

    @Override
    public void getHello(HelloRequest request, StreamObserver<HelloResponse> response) {

        HelloResponse helloResponse = HelloResponse.newBuilder()
                .setGreeting("HEYY")
                .build();

        response.onNext(helloResponse);
        response.onCompleted();
    }

}
