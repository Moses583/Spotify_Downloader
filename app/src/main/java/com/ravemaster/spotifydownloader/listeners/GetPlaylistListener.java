package com.ravemaster.spotifydownloader.listeners;

import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;

public interface GetPlaylistListener {
    void didFetch(PlaylistApiResponse response, String message);
    void didError(String message);
}
