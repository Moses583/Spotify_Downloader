package com.ravemaster.spotifydownloader;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.ravemaster.spotifydownloader.adapters.ViewPagerAdapter;
import com.ravemaster.spotifydownloader.listeners.GetAlbumListener;
import com.ravemaster.spotifydownloader.listeners.GetPlaylistListener;
import com.ravemaster.spotifydownloader.listeners.GetSongListener;
import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;
import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;
import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;
    RequestManager manager;
    Dialog permissionsDialog;
    Button request;

    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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

        SplashScreen.installSplashScreen(this);
        initViews();
        manager = new RequestManager(this);
        viewPager2.setAdapter(new ViewPagerAdapter(this));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                if (bottomNavigationView.getMenu().getItem(position).getTitle().equals("Song")){
                    toolbar.setTitle("Song");
                }else{
                    toolbar.setTitle(bottomNavigationView.getMenu().getItem(position).getTitle());
                }
            }
        });
        bottomNavigationView.setOnItemSelectedListener(bottomListener);
        if (hasPermissions()){
//            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
        }else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                showRationaleDialog();
            }else {
                requestPermissionLauncher.launch(permissions);
            }
        }
    }
    private void showRationaleDialog() {
        permissionsDialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_request_permissions,null);
        request = view.findViewById(R.id.btnRequestPermissions);
        permissionsDialog.setContentView(view);
        permissionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        permissionsDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        permissionsDialog.setCancelable(false);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(permissions);
                permissionsDialog.dismiss();
            }
        });
        permissionsDialog.show();
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> o) {

        }
    });

    private boolean hasPermissions(){
        return
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

    private void getAlbum() {
        manager.downloadAlbum("https://open.spotify.com/album/3kS42vslfpYnxWkGN4JvlW",albumListener);
    }

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

    private final NavigationBarView.OnItemSelectedListener bottomListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.idSong){
                viewPager2.setCurrentItem(0);
                toolbar.setTitle("Song");
                return true;
            } else if (item.getItemId() == R.id.idPlaylist){
                viewPager2.setCurrentItem(1);
                toolbar.setTitle("PlayList");
                return true;
            }
            else if (item.getItemId() == R.id.idAlbum){
                viewPager2.setCurrentItem(2);
                toolbar.setTitle("Album");
                return true;
            }
            return false;
        }
    };

    private void initViews(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.myViewPager);
        toolbar = findViewById(R.id.mainToolBar);
    }
}