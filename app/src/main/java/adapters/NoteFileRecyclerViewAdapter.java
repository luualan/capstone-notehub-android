package adapters;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import models.NoteFile;

// Comment Recycler View Adapter
public class NoteFileRecyclerViewAdapter extends RecyclerView.Adapter<NoteFileRecyclerViewAdapter.ViewHolder>  {
    private Context context;
    private List<NoteFile> noteFiles;

    public NoteFileRecyclerViewAdapter(Context context, List<NoteFile> noteFiles) {
        this.context = context;
        this.noteFiles = noteFiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.note_file_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Picasso.with(holder.itemView.getContext()).load(noteFiles.get(position).getFile()).fit().centerCrop().into(holder.fileImage);
    }

    @Override
    public int getItemCount() {
        return noteFiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fileImage;
        private MaterialButton download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileImage = itemView.findViewById(R.id.note_file_image);
            download = itemView.findViewById(R.id.note_file_download);
        }
    }


}
