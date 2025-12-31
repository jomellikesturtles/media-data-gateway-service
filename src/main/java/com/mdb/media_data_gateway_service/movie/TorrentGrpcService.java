package com.mdb.media_data_gateway_service.movie;

import com.mdb.media_data_gateway_service.grpc.MovieTorrent;
import com.mdb.media_data_gateway_service.grpc.TorrentServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class TorrentGrpcService extends TorrentServiceGrpc.TorrentServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TorrentGrpcService.class);
    @Autowired
    private final TorrentDataService torrentDataService;

    @Override
    public void saveTorrent(MovieTorrent request, StreamObserver<MovieTorrent> responseObserver) {
        try {
            int count = torrentDataService.saveTorrentFromJson(request.getDataJson());
            LOGGER.info("Successfully saved " + count + " movies.");
            MovieTorrent response = MovieTorrent.newBuilder()
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
    public void getTorrent(MovieTorrent request, StreamObserver<MovieTorrent> responseObserver) {
        String json = torrentDataService.getTorrentJson(request.getImdbId());

        if (json == null) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription("Movie with IMDB ID " + request.getImdbId() + " not found.")
                    .asRuntimeException());
            return;
        }

        MovieTorrent response = MovieTorrent.newBuilder()
                .setDataJson(json)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
