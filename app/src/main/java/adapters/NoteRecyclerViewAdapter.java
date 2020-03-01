package adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import models.CardView;
import remote.ApiInterface;

import com.example.notehub.MainActivity;
import com.example.notehub.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// An adapter for notes
public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> implements Filterable {
    private ArrayList<CardView> list;
    private ArrayList<CardView> listFull; // copy
    private List<CardView> filteredList;
    private onItemClickListener listener;

    Context context;

    public interface onItemClickListener {
        void onItemClick(int position);

        void onFavoriteClick(int position);

        void onCommentClick(int position);

        void onReportClick(int position);

        void onDeleteClick(int position);

        void onRatingClick(int position, float score);
    }

    public void setOnItemCLickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView university;
        public TextView course;
        public TextView name;
        public TextView type;
        public ImageView favorite;
        public ImageView comment;
        public ImageView report;
        public ImageView deleteImage;
        public RatingBar ratingBar;

        // Animation reference
        public RelativeLayout container;

        public ViewHolder(final View itemView, final onItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.text_title);
            university = itemView.findViewById(R.id.text_university);
            course = itemView.findViewById(R.id.text_course);
            name = itemView.findViewById(R.id.text_name);
            type = itemView.findViewById(R.id.text_type);
            favorite = itemView.findViewById(R.id.image_favorite);
            comment = itemView.findViewById(R.id.image_comment);
            report = itemView.findViewById(R.id.image_report);
            deleteImage = itemView.findViewById(R.id.image_delete);
            ratingBar = itemView.findViewById(R.id.star_rating);
            container = itemView.findViewById(R.id.action_note);

            // when users click on whole card
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            // when users click on favorite icon
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onFavoriteClick(position);
                        }
                    }
                }
            });

            // When users click on comment icon
            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onCommentClick(position);
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

            // When users click on rating
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    // send to api and save in server
                    if (listener != null && fromUser) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRatingClick(position, rating);
                        }
                    }
                }
            });
        }
    }

    // Constructor with ArrayList
    public NoteRecyclerViewAdapter(Context context, ArrayList<CardView> list) {
        this.context = context;
        this.list = list;
        this.listFull = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_view, parent, false);
        ViewHolder evh = new ViewHolder(v, listener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Animation for Images and card
        //holder.favorite.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        //holder.deleteImage.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));

        CardView currentItem = list.get(position);

        holder.favorite.setImageResource(currentItem.getimageFavorite());
        holder.title.setText(currentItem.getTitle());
        holder.university.setText(currentItem.getUniversity());
        holder.course.setText(currentItem.getCourse());
        holder.name.setText(currentItem.getName());
        holder.type.setText(currentItem.getType());
        holder.ratingBar.setStepSize((float)0.25);
        holder.ratingBar.setRating(currentItem.getAvgRating());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public void addItem(CardView card) {
        if (list != null)
            list.add(card);

        if (filteredList != null)
            filteredList.add(card);

        if (listFull != null)
            listFull.add(card);

        notifyItemInserted(list.size() - 1);
    }

    public void removeItem(int position) {
        if (list != null)
            list.remove(position);

        if (filteredList != null)
            filteredList.remove(position);

        if (listFull != null)
            listFull.remove(position);

        notifyItemRemoved(position);
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList = new ArrayList<>();

            // Constraint length means input field
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            }
            // Something typed in search field
            else {
                String filterInput = constraint.toString().toLowerCase().trim();

                for (CardView item : listFull) {
                    // Check notes by title, school, course, or name
                    if (item.getTitle().toLowerCase().contains(filterInput) || item.getUniversity().toLowerCase().contains(filterInput)
                            || item.getCourse().toLowerCase().contains(filterInput) || item.getName().toLowerCase().contains(filterInput)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values); // list contains only filtered items
            notifyDataSetChanged();
        }
    };
}
