package com.ravemaster.spotifydownloader.listeners;

import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;

public interface GetAlbumListener {
    void didFetch(AlbumApiResponse response, String message);
    void didError(String message);
}
