package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.GroupActivity;
import com.example.notehub.LoginActivity;
import com.example.notehub.MainActivity;
import com.example.notehub.NoteActivity;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import adapters.InvitationRecyclerViewAdapter;
import models.Invitation;
import models.Membership;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsNotificationFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    InvitationRecyclerViewAdapter recyclerViewAdapter;
    private List<Invitation> invitations;
    private ApiInterface apiService;
    private EditText groupNameEdit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.groups_notification_fragment, container, false);
        apiService = MainActivity.buildHTTP();

        createInvitationList();
        return view;
    }

    public void clear() {
        if(recyclerViewAdapter != null) {
            int size = invitations.size();
            invitations.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if(recyclerViewAdapter != null) {
            createInvitationList();
        }
    }


    // Load invitation list
    public void createInvitationList() {
        invitations = new ArrayList<>();

        Call<List<Invitation>> call = apiService.getUserInvitations(MainActivity.getToken(getContext()));
        buildRecyclerView();
        call.enqueue(new Callback<List<Invitation>>() {
            @Override
            public void onResponse(Call<List<Invitation>> call, Response<List<Invitation>> response) {
                // Success; store invitations
                if (response.errorBody() == null) {
                    //invitations = response.body();
                    for (Invitation invitation : response.body())
                        recyclerViewAdapter.addItem(invitation);
                }
                else
                    showAlertMessage("Error, could not load users invitations.", "Ok");

            }

            @Override
            public void onFailure(Call<List<Invitation>> call, Throwable t) {
            }
        });
    }

    // Initialize recycler
    public void buildRecyclerView() {
        recyclerView = view.findViewById(R.id.groups_notification_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        recyclerViewAdapter = new InvitationRecyclerViewAdapter(getContext(), invitations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new InvitationRecyclerViewAdapter.onItemClickListener() {

            @Override
            public void onClickButton(final int position) {
                Membership membership = new Membership();
                Call<Membership> call = apiService.uploadGroupMembership(MainActivity.getToken(getContext()), invitations.get(position).getGroup(), membership);
                call.enqueue(new Callback<Membership>() {

                    @Override
                    public void onResponse(Call<Membership> call, Response<Membership> response) {
                        if (response.errorBody() == null) {
                            recyclerViewAdapter.removeItem(position);
                            Membership data = response.body();
                            showAlertMessage("You successfully joined " + data.getGroupName() + "!", "Ok");
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Membership> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onDeleteClick() {

            }
        });
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(getActivity())
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
