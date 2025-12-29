package com.mdb.media_data_gateway_service.movie;

import com.mdb.media_data_gateway_service.grpc.GetMovieRequest;
import com.mdb.media_data_gateway_service.grpc.GetMovieResponse;
import com.mdb.media_data_gateway_service.grpc.MovieServiceGrpc;
import com.mdb.media_data_gateway_service.grpc.SaveSampleResponseRequest;
import com.mdb.media_data_gateway_service.grpc.SaveSampleResponseResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class MovieGrpcService extends MovieServiceGrpc.MovieServiceImplBase {

    private final MovieDataService movieDataService;

    @Override
    public void saveSampleResponse(SaveSampleResponseRequest request, StreamObserver<SaveSampleResponseResponse> responseObserver) {
        try {
            int count = movieDataService.saveMoviesFromJson(request.getJsonContent());

            SaveSampleResponseResponse response = SaveSampleResponseResponse.newBuilder()
                    .setMessage("Successfully saved " + count + " movies.")
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error saving movies", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getMovie(GetMovieRequest request, StreamObserver<GetMovieResponse> responseObserver) {
        String json = movieDataService.getMovieJson(request.getImdbId());

        if (json == null) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Movie with IMDB ID " + request.getImdbId() + " not found.")
                    .asRuntimeException());
            return;
        }

        GetMovieResponse response = GetMovieResponse.newBuilder()
                .setMovieJson(json)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
