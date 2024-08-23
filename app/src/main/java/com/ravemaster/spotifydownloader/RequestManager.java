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
                "x-rapidapi-key: ab9a345b1dmsh95573e64e14301dp11f08cjsnbb669399ee87",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<SongApiResponse> getSong(
                @Query("songId") String songId
        );
    }
    private interface GetPlayList{
        @GET("downloadPlaylist")
        @Headers({
                "x-rapidapi-key: ab9a345b1dmsh95573e64e14301dp11f08cjsnbb669399ee87",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<PlaylistApiResponse> getPlaylist(
                @Query("playlistId") String playlistId
        );
    }
    private interface GetAlbum{
        @GET("downloadAlbum")
        @Headers({
                "x-rapidapi-key: ab9a345b1dmsh95573e64e14301dp11f08cjsnbb669399ee87",
                "x-rapidapi-host: spotify-downloader9.p.rapidapi.com"
        })
        Call<AlbumApiResponse> getAlbum(
                @Query("albumId") String albumId
        );
    }
}
