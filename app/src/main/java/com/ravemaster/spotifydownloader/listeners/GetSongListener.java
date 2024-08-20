package com.ravemaster.spotifydownloader.listeners;

import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

public interface GetSongListener {
    void didFetch(SongApiResponse response, String message);
    void didError(String message);
}
