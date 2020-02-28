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
        import models.Invitation;

// Invitation Recycler View Adapter
public class InvitationRecyclerViewAdapter extends RecyclerView.Adapter<InvitationRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Invitation> invitations;
    private onItemClickListener listener;

    // Invitation Constructor
    public InvitationRecyclerViewAdapter(Context context, List<Invitation> invitations ) {
        this.context = context;
        this.invitations = invitations;
    }

    public interface onItemClickListener {
        void onClickButton(int position);
        void onItemClick(int position);
        void onDeleteClick();
    }

    public void setOnItemClickListener(onItemClickListener listener) { this.listener = listener; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.invitation_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groupName.setText(invitations.get(position).getGroupName());
        holder.username.setText("Moderator: " + invitations.get(position).getModeratorUsername());
        // holder.image.setImageResource(invitations.get(position).getPhoto());
    }

    @Override
    public int getItemCount() {
        return this.invitations.size();
    }

    public void addItem(Invitation invitation) {
        if (invitation != null) {
            invitations.add(invitation);
            notifyItemInserted(invitations.size() - 1);
        }
    }

    public void removeItem(int position) {
        if (invitations.size() != 0)
            invitations.remove(position);
        notifyItemRemoved(position);
    }

    // Invitation View Holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView groupName;
        private TextView username;
        private MaterialButton button;
        private CircleImageView image;


        // View Constructor
        public ViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            groupName = itemView.findViewById(R.id.invitation_group_name_text);
            username = itemView.findViewById(R.id.invitation_username_text);
            button = itemView.findViewById(R.id.invitation_button);
            button.setText("Join");
            button.setBackgroundColor(Color.GREEN);

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
