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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;

// Comment Recycler View Adapter
public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
    Context context;
    List<Comment> comments;

    // Comment Constructor
    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.note_comment_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(comments.get(position).getName());
        holder.description.setText(comments.get(position).getDescription());
        holder.image.setImageResource(comments.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }

    // Comment View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView description;
        private CircleImageView image;


        // View Constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.note_username);
            description = itemView.findViewById(R.id.note_user_description);
            image = itemView.findViewById(R.id.note_user_img);
        }
    }

}
