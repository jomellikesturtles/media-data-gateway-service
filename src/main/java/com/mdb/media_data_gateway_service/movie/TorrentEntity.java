package com.mdb.media_data_gateway_service.movie;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "torrents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorrentEntity {

    @Id
    @Column(name = "imdb_id")
    private String imdbId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "data", columnDefinition = "jsonb")
    private String data;
}