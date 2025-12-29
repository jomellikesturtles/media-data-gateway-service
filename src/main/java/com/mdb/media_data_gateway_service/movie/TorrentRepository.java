package com.mdb.media_data_gateway_service.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorrentRepository extends JpaRepository<TorrentEntity, String> {
}