package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.MainActivity;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Group;
import models.Membership;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Group Members Recycler View Adapter
public class GroupMembersRecyclerViewAdapter extends RecyclerView.Adapter<GroupMembersRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private static List<Membership> members;
    private onItemClickListener listener;
    private static Group group;

    // Group Members Constructor
    public GroupMembersRecyclerViewAdapter(Context context, List<Membership> members, Group group) {
        this.context = context;
        this.members = members;
        this.group = group;
    }

    public interface onItemClickListener {
       // void onClickButton(int position);
        // void onItemClick(int position);
       void onDeleteClick(int position);
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.name.setText(members.get(position).getUsername());
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");
        DateFormat dateReadableFormat = new SimpleDateFormat("MM-dd-yyyy");
        try {
            date = dateFormat.parse(members.get(position).getJoinedAt());
        } catch (Exception ParseException) {
            date = new Date();
        }
        holder.joinDate.setText("Joined: " + dateReadableFormat.format(date));
        holder.role.setText("Role: " + members.get(position).getRole());
        holder.avatar.setImageResource(R.drawable.blank);
        Call<List<User>> call = MainActivity.buildHTTP().getUsers(members.get(position).getUsername());
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.errorBody() == null && response.body().size() == 1 && response.body().get(0).getAvatar() != null) {
                    String imageUrl = response.body().get(0).getAvatar();
                    Picasso.with(context).load(imageUrl).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(holder.avatar);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
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
        private TextView role;
        private ImageView avatar;
        private ImageView remove;

        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            name = itemView.findViewById(R.id.group_member_name);
            joinDate = itemView.findViewById(R.id.group_member_join_date);
            role = itemView.findViewById(R.id.group_member_rank);
            avatar = itemView.findViewById(R.id.group_member_user_img);
            remove = itemView.findViewById(R.id.group_member_remove);

            // hide remove button
            if(!group.getIsModerator())
                remove.setVisibility(View.GONE);


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

            remove.setOnClickListener(new View.OnClickListener() {
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
        }
    }
}
