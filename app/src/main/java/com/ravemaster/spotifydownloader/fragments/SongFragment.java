package com.ravemaster.spotifydownloader.fragments;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.ravemaster.spotifydownloader.MainActivity;
import com.ravemaster.spotifydownloader.R;
import com.ravemaster.spotifydownloader.RequestManager;
import com.ravemaster.spotifydownloader.listeners.GetSongListener;
import com.ravemaster.spotifydownloader.modelssongs.SongApiResponse;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SongFragment extends Fragment {

    Button button,download,paste;
    TextInputLayout enterSongUrl;
    EditText editText;
    RequestManager manager;
    ProgressBar progressBar;
    TextView txtLoading,songTitle,albumName,releaseDate,artistName;
    Dialog progressDialog;
    ImageView imgSongCover;
    LinearLayout details;
    String downloadUrl="";
    String title="";
    boolean success = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        initViews(view);
        editText = enterSongUrl.getEditText();
        manager = new RequestManager(getActivity());
        showProgressDialog();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNull();
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSong();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteLink();
            }
        });
        return view;
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

    private void checkNull() {
        String s = editText.getText().toString();
        if(s.isEmpty()){
            enterSongUrl.setErrorEnabled(true);
            enterSongUrl.setError("You need to enter link");
        }
        else{
            progressDialog.show();
            enterSongUrl.setErrorEnabled(false);
            getSong(s);
        }
    }

    private void downloadSong() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(title);
        request.setDescription("Downloading file...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+title+".mp3");
        request.setMimeType("audio/mpeg");

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(getActivity(), "Downloading song", Toast.LENGTH_SHORT).show();

    }

    private void getSong(String url) {
        manager.downloadSong(url,listener);
    }

    private final GetSongListener listener = new GetSongListener() {
        @Override
        public void didFetch(SongApiResponse response, String message) {
            progressDialog.dismiss();
            if (response.data == null){
                Toast.makeText(getActivity(), "Please check the url you have copied", Toast.LENGTH_SHORT).show();
            }else{
                success = response.success;
                showData(response);
            }
        }

        @Override
        public void didError(String message) {
            progressDialog.dismiss();
            if (message.contains("timeout")){
                manager.downloadSong(editText.getText().toString(),listener);
            }else if(message.contains("unable")){
                Toast.makeText(getActivity(), "Connect to the internet", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), "You have exceeded your maximum download requests for today. Come back tomorrow for more 😁👍!!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showData(SongApiResponse response) {
        if (success){
            details.setVisibility(View.VISIBLE);
        }
        Glide.with(getActivity())
                .load(response.data.cover)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.error_image))
                .into(imgSongCover);
        title = response.data.title;
        songTitle.setText(title);
        albumName.setText(response.data.album);
        artistName.setText(response.data.artist);
        releaseDate.setText(response.data.releaseDate);
        downloadUrl = response.data.downloadLink;
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
        txtLoading.setText("Finding song");
    }

    private void initViews(View view) {
        button = view.findViewById(R.id.btnFindSong);
        enterSongUrl = view.findViewById(R.id.enterSongUrl);
        download = view.findViewById(R.id.btnDownloadSong);
        paste = view.findViewById(R.id.btnPasteLink);
        artistName = view.findViewById(R.id.txtArtistName);
        albumName = view.findViewById(R.id.txtAlbumName);
        releaseDate = view.findViewById(R.id.txtReleaseDate);
        songTitle = view.findViewById(R.id.txtSongTitle);
        imgSongCover = view.findViewById(R.id.imgSongCover);
        details = view.findViewById(R.id.layoutDetails);

    }
}