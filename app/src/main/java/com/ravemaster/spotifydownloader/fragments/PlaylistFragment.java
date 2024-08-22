package com.ravemaster.spotifydownloader.fragments;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.ravemaster.spotifydownloader.MainActivity;
import com.ravemaster.spotifydownloader.R;
import com.ravemaster.spotifydownloader.RequestManager;
import com.ravemaster.spotifydownloader.listeners.GetPlaylistListener;
import com.ravemaster.spotifydownloader.modelsplaylist.PlaylistApiResponse;


public class PlaylistFragment extends Fragment {
    RequestManager manager;
    Button search,paste;
    TextInputLayout enterLink;
    EditText editText;
    TextView txtLoading;
    ProgressBar progressBar;
    Dialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        manager = new RequestManager(getActivity());

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        initViews(view);
        showProgressDialog();
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

        return view;
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
            getPlaylist(s);
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

    private void getPlaylist(String s) {
        manager.downLoadPlaylist(s,playlistListener);
    }

    private final GetPlaylistListener playlistListener = new GetPlaylistListener() {
        @Override
        public void didFetch(PlaylistApiResponse response, String message) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), response.data.playlistDetails.artist , Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    };

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
        search = view.findViewById(R.id.btnFindPlaylist);
        paste = view.findViewById(R.id.btnPasteLink2);
        enterLink = view.findViewById(R.id.enterPlaylistUrl);
    }
}