package com.ravemaster.spotifydownloader.fragments;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.ravemaster.spotifydownloader.R;
import com.ravemaster.spotifydownloader.RequestManager;
import com.ravemaster.spotifydownloader.adapters.AlbumAdapter;
import com.ravemaster.spotifydownloader.listeners.GetAlbumListener;
import com.ravemaster.spotifydownloader.modelsalbum.AlbumApiResponse;
import com.ravemaster.spotifydownloader.modelsalbum.Song;


import java.util.ArrayList;


public class AlbumFragment extends Fragment {

    RequestManager manager;
    Button search,paste,downloadAll;
    TextInputLayout enterLink;
    EditText editText;
    TextView txtLoading,title,artist,songs;
    ImageView cover;
    ProgressBar progressBar;
    Dialog progressDialog;
    boolean success = false;
    LinearLayout layout;
    RecyclerView recyclerView;
    ArrayList<Song> songsArraylist = new ArrayList<>();
    AlbumAdapter adapter;

    private int currentMethodIndex = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        manager = new RequestManager(getActivity());

        View view = inflater.inflate(R.layout.fragment_album, container, false);
        initViews(view);
        showProgressDialog();
        adapter = new AlbumAdapter(getActivity());
        editText = enterLink.getEditText();
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNull();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteLink();
            }
        });
        downloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAllSongs(songsArraylist);
            }
        });
        return view;
    }

    private void downloadAllSongs(ArrayList<Song> songsArraylist) {
        Toast.makeText(getActivity(), "Downloading "+String.valueOf(songsArraylist.size())+" songs.", Toast.LENGTH_LONG).show();
        for (Song song :
                songsArraylist) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song.downloadLink));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle(song.title);
            request.setDescription("Downloading file...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+song.title+".mp3");
            request.setMimeType("audio/mpeg");
            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
        }
    }

    private void checkNull() {
        String s = editText.getText().toString();
        if(s.isEmpty()){
            enterLink.setErrorEnabled(true);
            enterLink.setError("You need to enter link");
        }
        else{
            progressDialog.show();
            enterLink.setErrorEnabled(false);
            getAlbum(s);
        }
    }

    private void pasteLink() {
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()){
            ClipData data = clipboardManager.getPrimaryClip();
            if (data != null && data.getItemCount()>0){
                CharSequence link = data.getItemAt(0).getText().toString();
                editText.setText(link);
            }
        }
    }

    private void getAlbum(String url) {
        switch (currentMethodIndex){
            case 0:
                manager.downloadAlbum(url,listener);
                break;
            case 1:
                manager.downloadAlbum1(url,listener);
                break;
            case 2:
                manager.downloadAlbum2(url,listener);
                break;
            case 3:
                manager.downloadAlbum3(url,listener);
                break;
            case 4:
                manager.downloadAlbum4(url,listener);
                break;
        }

        // Increment the index and wrap around if necessary
        currentMethodIndex = (currentMethodIndex + 1) % 5;
    }

    private final GetAlbumListener listener = new GetAlbumListener() {
        @Override
        public void didFetch(AlbumApiResponse response, String message) {
            progressDialog.dismiss();
            if (response.data == null){
                Toast.makeText(getActivity(), "The url you have copied doesn't seem to work, try copying and pasting it one more time 🥺.", Toast.LENGTH_LONG).show();
            }else{
                success = response.success;
                showData(response);
            }

        }

        @Override
        public void didError(String message) {
            progressDialog.dismiss();
            if (message.contains("timeout")){
                switch (currentMethodIndex){
                    case 0:
                        manager.downloadAlbum(editText.getText().toString(),listener);
                        break;
                    case 1:
                        manager.downloadAlbum1(editText.getText().toString(),listener);
                        break;
                    case 2:
                        manager.downloadAlbum2(editText.getText().toString(),listener);
                        break;
                    case 3:
                        manager.downloadAlbum3(editText.getText().toString(),listener);
                        break;
                    case 4:
                        manager.downloadAlbum4(editText.getText().toString(),listener);
                        break;
                }

                // Increment the index and wrap around if necessary
                currentMethodIndex = (currentMethodIndex + 1) % 5;
            }else if(message.contains("unable")){
                Toast.makeText(getActivity(), "Looks like you might be offline, turn on mobile data or wifi to continue 😉.", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void showData(AlbumApiResponse response) {
        if (success){
            layout.setVisibility(View.VISIBLE);
        }
        Glide.with(getActivity())
                .load(response.data.albumDetails.cover)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.error_image))
                .into(cover);
        title.setText(response.data.albumDetails.title);
        artist.setText(response.data.albumDetails.artist);
        songs.setText(String.valueOf(response.data.songs.size())+" songs");
        showRecycler(response.data.songs);
    }

    private void showRecycler(ArrayList<Song> songs) {
        songsArraylist = songs;
        adapter.setSongs(songsArraylist);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setHasFixedSize(true);
    }

    private void showProgressDialog() {
        progressDialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_progress_layout,null);
        progressBar = view.findViewById(R.id.myProgressBar);
        txtLoading = view.findViewById(R.id.txtProgress);
        progressDialog.setContentView(view);
        int widthInDp = 250;

        final float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        progressDialog.getWindow().setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.getWindow().setBackgroundDrawable(getDrawable(getActivity(),R.drawable.dialog_background));
        progressDialog.setCancelable(false);
        txtLoading.setText("Loading album ⌛");
    }

    private void initViews(View view) {
        search = view.findViewById(R.id.btnFindAlbum);
        paste = view.findViewById(R.id.btnPasteLink3);
        downloadAll = view.findViewById(R.id.btnDownloadAll2);
        cover = view.findViewById(R.id.imgAlbumCover);
        title = view.findViewById(R.id.txtAlbumTitle);
        artist = view.findViewById(R.id.txtAlbumArtist);
        songs = view.findViewById(R.id.txtAlbumSongs);
        enterLink = view.findViewById(R.id.enterAlbumUrl);
        layout = view.findViewById(R.id.albumLayout);
        recyclerView = view.findViewById(R.id.recyclerSongs2);
    }
}