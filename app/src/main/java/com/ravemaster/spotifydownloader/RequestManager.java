package com.ravemaster.spotifydownloader;

import android.content.Context;

import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;
import com.ravemaster.spotifydownloader.listeners.GetAlbumListener;
import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;
import com.ravemaster.spotifydownloader.listeners.GetPlaylistListener;
import com.ravemaster.spotifydownloader.listeners.GetSongListener;
import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://spotify-downloader9.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void downloadSong(String songId, GetSongListener listener){
        GetSong getSong = retrofit.create(GetSong.class);
        Call<SongApiResponse> call = getSong.getSong(songId);
        call.enqueue(new Callback<SongApiResponse>() {
            @Override
            public void onResponse(Call<SongApiResponse> call, Response<SongApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.code()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<SongApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from on failure");
            }
        });
    }

    public void downLoadPlaylist(String playlistId, GetPlaylistListener listener){
        GetPlayList getPlayList = retrofit.create(GetPlayList.class);
        Call<PlaylistApiResponse> call = getPlayList.getPlaylist(playlistId);
        call.enqueue(new Callback<PlaylistApiResponse>() {
            @Override
            public void onResponse(Call<PlaylistApiResponse> call, Response<PlaylistApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<PlaylistApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from on failure");
            }
        });
    }

    public void downloadAlbum(String albumId, GetAlbumListener listener){
        GetAlbum getAlbum = retrofit.create(GetAlbum.class);
        Call<AlbumApiResponse> call = getAlbum.getAlbum(albumId);
        call.enqueue(new Callback<AlbumApiResponse>() {
            @Override
            public void onResponse(Call<AlbumApiResponse> call, Response<AlbumApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message()+" error from on response");
                    return;
                }
                listener.didFetch(response.body(), response.message());
            }

            @Override
            public void onFailure(Call<AlbumApiResponse> call, Throwable throwable) {
                listener.didError(throwable.getMessage()+" error from on failure");
            }
        });
    }

    private interface GetSong{
        @GET("downloadSong")
        @Headers({
                "x-rapidapi-key: 190ecbd632mshf9627c5d4b4afc0p13af00jsnaa0e7ec1b323",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<SongApiResponse> getSong(
                @Query("songId") String songId
        );
    }
    private interface GetPlayList{
        @GET("downloadPlaylist")
        @Headers({
                "x-rapidapi-key: 7e3d2f10bdmsh70e6fefa71835adp16c240jsnb41f1b9c1073",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<PlaylistApiResponse> getPlaylist(
                @Query("playlistId") String playlistId
        );
    }
    private interface GetAlbum{
        @GET("downloadAlbum")
        @Headers({
                "x-rapidapi-key: 7a9a8d4846mshcfaa4b403a596e8p1d45b5jsneca71b63bb58",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<AlbumApiResponse> getAlbum(
                @Query("albumId") String albumId
        );
    }
}
