package com.ravemaster.spotifydownloader;

import android.content.Context;

import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;
import com.ravemaster.spotifydownloader.listeners.GetAlbumListener;
import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;
import com.ravemaster.spotifydownloader.listeners.GetPlaylistListener;
import com.ravemaster.spotifydownloader.listeners.GetSongListener;
import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class RequestManager {
    Context context;
    int currentKeyIndex = 0;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://spotify-downloader9.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public RequestManager(Context context) {
        this.context = context;
    }

    public void downloadSong(String songId, GetSongListener listener){
        GetSong getSong = retrofit.create(GetSong.class);
        Call<SongApiResponse> call = getSong.getSong(songId,getApiKey(),"spotify-downloader9.p.rapidapi.com");
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
        Call<PlaylistApiResponse> call = getPlayList.getPlaylist(playlistId,getApiKey(),"spotify-downloader9.p.rapidapi.com");
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
        Call<AlbumApiResponse> call = getAlbum.getAlbum(albumId,getApiKey(),"spotify-downloader9.p.rapidapi.com");
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
        Call<SongApiResponse> getSong(
                @Query("songId") String songId,
                @Header("x-rapidapi-key") String apiKey,
                @Header("x-rapidapi-host") String apiHost
        );
    }
    private interface GetPlayList{
        @GET("downloadPlaylist")
        Call<PlaylistApiResponse> getPlaylist(
                @Query("playlistId") String playlistId,
                @Header("x-rapidapi-key") String apiKey,
                @Header("x-rapidapi-host") String apiHost
        );
    }
    private interface GetAlbum{
        @GET("downloadAlbum")
        Call<AlbumApiResponse> getAlbum(
                @Query("albumId") String albumId,
                @Header("x-rapidapi-key") String apiKey,
                @Header("x-rapidapi-host") String apiHost
        );
    }
    private String getApiKey(){
        String apikey = "";
        switch (currentKeyIndex){
            case 0:
                apikey = "ed4b9641acmshe6e3944254ccdf8p12c249jsn32c2d44e1c9b";
                break;
            case 1:
                apikey = "cf8e818c30mshc3a90bc6ac3c539p10a09bjsn3c104522f764";
                break;
            case 2:
                apikey = "9514810a91mshaa1e82f038a194dp192b4djsn80ef1486dc10";
                break;
            case 3:
                apikey = "7a9a8d4846mshcfaa4b403a596e8p1d45b5jsneca71b63bb58";
                break;
            case 4:
                apikey = "190ecbd632mshf9627c5d4b4afc0p13af00jsnaa0e7ec1b323";
                break;
            case 5:
                apikey = "cee03e6aabmsh2fa043ef9ad519ap17e59bjsn695eb9e6b3fb";
                break;
            case 6:
                apikey = "ab9a345b1dmsh95573e64e14301dp11f08cjsnbb669399ee87";
                break;
            case 7:
                apikey = "7e3d2f10bdmsh70e6fefa71835adp16c240jsnb41f1b9c1073";
                break;
        }
        currentKeyIndex = (currentKeyIndex + 1) % 8;
        return apikey;
    }
}
