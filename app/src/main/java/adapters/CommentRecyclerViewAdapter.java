package adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.MainActivity;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        void onOverflowClick(int position, PopupMenu menu);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(comments.get(position).getUsername());
        holder.description.setText(comments.get(position).getText());
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        DateFormat dateReadableFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            date = dateFormat.parse(comments.get(position).getCreatedAt());
        } catch (Exception ParseException) {
            date = new Date();
        }
        holder.date.setText(dateReadableFormat.format(date));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
        holder.image.setImageResource(R.drawable.blank);
        Call<List<User>> call = MainActivity.buildHTTP().getUsers(comments.get(position).getUsername());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.errorBody() == null && response.body().size() == 1 && response.body().get(0).getAvatar() != null) {
                    String imageUrl = response.body().get(0).getAvatar();
                    Picasso.with(context).load(imageUrl).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(holder.image);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        //if(!comments.get(position).isAuthor())
        //  holder.menu.getMenu().findItem(R.id.deleteGroup).setVisible(false);
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
        private TextView date;
        private ImageView overflow;
        //public ImageView report;
        //public ImageView deleteImage;

        private CardView container;

        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.note_username);
            description = itemView.findViewById(R.id.note_user_description);
            image = itemView.findViewById(R.id.note_user_img);
            date = itemView.findViewById(R.id.note_date);
            overflow = itemView.findViewById(R.id.note_overflow);
            container = itemView.findViewById(R.id.comment_container);

            //report = itemView.findViewById(R.id.image_report);
            //deleteImage = itemView.findViewById(R.id.image_delete);


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
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        final PopupMenu menu = new PopupMenu(v.getContext(), v);
                        MenuInflater inflater = menu.getMenuInflater();
                        inflater.inflate(R.menu.overflow_menu, menu.getMenu());
                        listener.onOverflowClick(getAdapterPosition(), menu);

                    }
                }
            });

            // When users click on delete icon
/*            deleteImage.setOnClickListener(new View.OnClickListener() {
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

 */

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
