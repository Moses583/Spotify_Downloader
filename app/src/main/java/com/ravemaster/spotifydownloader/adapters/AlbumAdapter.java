package com.ravemaster.spotifydownloader.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ravemaster.spotifydownloader.R;
import com.ravemaster.spotifydownloader.modelsalbum.Song;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private Context context;
    private ArrayList<Song> songs = new ArrayList<>();

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_item_two,parent,false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        String title = songs.get(position).title;
        String artist = songs.get(position).artist;
        String album = songs.get(position).album;
        String downloadUrl = songs.get(position).downloadLink;
        String coverUrl = songs.get(position).cover;

        holder.title.setText(title);
        holder.artist.setText(artist);
        if (album.isEmpty()){
        holder.album.setText("Unavailable");
        }else{
            holder.album.setText(album);
        }
        holder.title.setSelected(true);
        holder.artist.setSelected(true);
        holder.album.setSelected(true);

        Glide.with(context)
                .asBitmap()
                .load(coverUrl)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.error_image))
                .into(holder.cover);
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Downloading song", Toast.LENGTH_LONG).show();
                downloadSong(downloadUrl,title);
            }
        });
    }

    private void downloadSong(String downloadUrl,String title) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(title);
        request.setDescription("Downloading file...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+title+".mp3");
        request.setMimeType("audio/mpeg");

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
    public int calculateAverageColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;

        long red = 0;
        long green = 0;
        long blue = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                red += Color.red(pixel);
                green += Color.green(pixel);
                blue += Color.blue(pixel);
            }
        }

        int avgRed = (int) (red / size);
        int avgGreen = (int) (green / size);
        int avgBlue = (int) (blue / size);

        return Color.rgb(avgRed, avgGreen, avgBlue);
    }

}
class AlbumViewHolder extends RecyclerView.ViewHolder {
    TextView title,artist,album;
    ImageView cover,download;
    CardView cardView;
    public AlbumViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.trackTitleAlbum);
        artist = itemView.findViewById(R.id.trackArtistAlbum);
        album = itemView.findViewById(R.id.trackAlbumAlbum);
        cover = itemView.findViewById(R.id.imgTrackCoverAlbum);
        download = itemView.findViewById(R.id.imgDownloadTrackAlbum);
        cardView = itemView.findViewById(R.id.albumCardView);
    }
}
