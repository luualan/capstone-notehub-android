package adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.ContentResolver;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.List;

import models.Group;
import models.NoteFile;
import retrofit2.http.Url;

// Comment Recycler View Adapter
public class UploadNoteFileRecyclerViewAdapter extends RecyclerView.Adapter<UploadNoteFileRecyclerViewAdapter.ViewHolder>  {
    private Context context;
    private List<NoteFile> noteFiles;
    private onItemClickListener listener;

    public UploadNoteFileRecyclerViewAdapter(Context context, List<NoteFile> noteFiles) {
        this.context = context;
        this.noteFiles = noteFiles;
    }

    public interface onItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.upload_note_file_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = Uri.parse(noteFiles.get(position).getFile());

        holder.fileTitle.setText(queryName(context.getContentResolver(), uri));
        Picasso.with(holder.itemView.getContext()).load(noteFiles.get(position).getFile()).fit().centerCrop().into(holder.fileImage);
    }

    @Override
    public int getItemCount() {
        return noteFiles.size();
    }

    public void addItem(NoteFile noteFile) {
        if (noteFile != null) {
            noteFiles.add(noteFile);
            int size = (noteFiles.size() - 1);

            noteFiles.get(size).setFile(noteFile.getFile());
            notifyItemInserted(size);
        }
    }

    public void removeItem(int position) {
        if (noteFiles.size() != 0)
            noteFiles.remove(position);
        notifyItemRemoved(position);
    }

    // Convert uri to file name
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView fileImage;
        private TextView fileTitle;
        private ImageView trashIcon;

        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            fileImage = itemView.findViewById(R.id.upload_note_file_image);
            fileTitle = itemView.findViewById(R.id.upload_note_file_title);
            trashIcon = itemView.findViewById(R.id.upload_delete_icon);

        /*    trashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });*/
        }
    }
}
