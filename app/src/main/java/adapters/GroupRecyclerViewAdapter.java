package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Group;

// Group Recycler View Adapter
public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Group> groups;
    private onItemClickListener listener;

    // Group Constructor
    public GroupRecyclerViewAdapter(Context context, List<Group> groups ) {
        this.context = context;
        this.groups = groups;
    }

    public interface onItemClickListener {
        void onClickButton(int position);
        void onItemClick(int position);
        void onDeleteClick(int posiiton);
    }

    public void setOnClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.group_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groupName.setText(groups.get(position).getName());
        holder.moderator.setText(groups.get(position).getModeratorUsername());
       // holder.image.setImageResource(groups.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.groups.size();
    }

    public void addItem(Group group) {
        if (group != null) {
            groups.add(group);
            notifyItemInserted(groups.size() - 1);
        }
    }

    public void removeItem(int position) {
        if (groups.size() != 0)
            groups.remove(position);
        notifyItemRemoved(position);
    }

    // Group View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private TextView moderator;
        private MaterialButton button;
        private CircleImageView image;


        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            groupName = itemView.findViewById(R.id.group_name_text);
            moderator = itemView.findViewById(R.id.group_moderator_text);
            button = itemView.findViewById(R.id.group_button);
            button.setText("Leave");
            button.setBackgroundColor(0xffff4444);

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

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClickButton(position);
                        }
                    }
                }
            });
        }
    }
}
