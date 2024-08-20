package com.ravemaster.spotifydownloader;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ravemaster.spotifydownloader.listeners.GetAlbumListener;
import com.ravemaster.spotifydownloader.listeners.GetPlaylistListener;
import com.ravemaster.spotifydownloader.listeners.GetSongListener;
import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;
import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;
import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

public class MainActivity extends AppCompatActivity {

    Button song,playList,album;
    RequestManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        manager = new RequestManager(this);

        song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSong();
            }
        });
        playList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               getPlaylist();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAlbum();
            }
        });
    }

    private void getAlbum() {
        manager.downloadAlbum("https://open.spotify.com/album/3kS42vslfpYnxWkGN4JvlW",albumListener);
    }

    private void getPlaylist() {
        manager.downLoadPlaylist("https%3A%2F%2Fopen.spotify.com%2Fplaylist%2F2erlPnqkQL4KNA8HtJ5D0Q",playlistListener);
    }

    private void getSong() {
        manager.downloadSong("https%3A%2F%2Fopen.spotify.com%2Ftrack%2F7jT3LcNj4XPYOlbNkPWNhU",listener);
    }

    private final GetPlaylistListener playlistListener = new GetPlaylistListener() {
        @Override
        public void didFetch(PlaylistApiResponse response, String message) {
            Toast.makeText(MainActivity.this, response.data.playlistDetails.artist , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final GetSongListener listener = new GetSongListener() {
        @Override
        public void didFetch(SongApiResponse response, String message) {
            Toast.makeText(MainActivity.this, response.data.artist, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private final GetAlbumListener albumListener = new GetAlbumListener() {
        @Override
        public void didFetch(AlbumApiResponse response, String message) {
            Toast.makeText(MainActivity.this, response.data.albumDetails.artist, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void initViews(){
        song = findViewById(R.id.testSong);
        playList = findViewById(R.id.testPlaylist);
        album = findViewById(R.id.testAlbum);
    }
}