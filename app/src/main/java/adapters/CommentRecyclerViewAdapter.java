package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.CardView;
import models.Comment;

// Comment Recycler View Adapter
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;
    private onItemClickListener listener;

    // Comment Constructor
    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }


    public interface onItemClickListener {
        void onItemClick(int position);

        void onReportClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.note_comment_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(comments.get(position).getUsername());
        holder.description.setText(comments.get(position).getText());
        // holder.image.setImageResource(comments.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    public void addItem(Comment comment) {
        comments.add(comment);
        notifyItemInserted(comments.size() - 1);
    }

    public void removeItem(int position) {
        if (comments != null) {
            comments.remove(position);
            notifyItemRemoved(position);
        }
    }


    // Comment View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView description;
        private CircleImageView image;
        public ImageView report;
        public ImageView deleteImage;

        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.note_username);
            description = itemView.findViewById(R.id.note_user_description);
            image = itemView.findViewById(R.id.note_user_img);
            report = itemView.findViewById(R.id.image_report);
            deleteImage = itemView.findViewById(R.id.image_delete);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            //listener.onButtonClick();
                        }
                    }
                }
            });

            //  When users click on report icon
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onReportClick(position);
                        }
                    }
                }
            });

            // When users click on delete icon
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        /*    button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick();
                        }
                    }
                }
            });*/
        }
    }
}
