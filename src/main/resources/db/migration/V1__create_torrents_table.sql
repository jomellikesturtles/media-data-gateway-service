-- SQL script to create the torrents table for TorrentEntity
CREATE TABLE IF NOT EXISTS torrents (
    imdb_id VARCHAR(255) PRIMARY KEY,
    data JSONB NOT NULL
);

-- Optional: Add an index on the JSONB column for better performance if querying specific fields
-- CREATE INDEX idx_torrents_data ON torrents USING GIN (data);
docker exec -it mdb-postgres psql -U postgres -d mdb_prod
