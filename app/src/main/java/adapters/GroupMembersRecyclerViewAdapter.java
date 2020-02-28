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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Membership;

// Group Members Recycler View Adapter
public class GroupMembersRecyclerViewAdapter extends RecyclerView.Adapter<GroupMembersRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private static List<Membership> members;
    private onItemClickListener listener;

    // Group Members Constructor
    public GroupMembersRecyclerViewAdapter(Context context, List<Membership> members) {
        this.context = context;
        this.members = members;
    }

    public interface onItemClickListener {
       // void onClickButton(int position);
        // void onItemClick(int position);
       // void onDeleteClick(int position);
    }

    public void setOnClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.user_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(members.get(position).getUsername());
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        DateFormat dateReadableFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            date = dateFormat.parse(members.get(position).getJoinedAt());
        } catch (Exception ParseException) {
            date = new Date();
        }
        holder.joinDate.setText(dateReadableFormat.format(date));
       // holder.image.setImageResource(members.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.members.size();
    }

    public void addItem(Membership member) {
        if (member != null) {
            members.add(member);
            int size = (members.size() - 1);

            members.get(size).setUsername("Name: " +
                    members.get(size).getUsername());
            notifyItemInserted(size);
        }
    }

    public void removeItem(int position) {
        if (members.size() != 0)
            members.remove(position);
        notifyItemRemoved(position);
    }

    // Group Members View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView joinDate;
       // private ImageView remove;


        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.group_member_name);
            joinDate = itemView.findViewById(R.id.group_member_join_date);
          //  remove = itemView.findViewById(R.id.group_button);

            // hide remove button
          //  remove.setVisibility(View.GONE);

            int position = getAdapterPosition();

         /*   itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });*/

        /*    remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClickButton(position);
                        }
                    }
                }
            });*/
        }
    }
}
