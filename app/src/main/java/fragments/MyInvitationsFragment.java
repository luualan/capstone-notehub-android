package fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.MainActivity;
import com.example.notehub.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import adapters.InvitationRecyclerViewAdapter;
import models.Invitation;
import models.Membership;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInvitationsFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    InvitationRecyclerViewAdapter recyclerViewAdapter;
    private List<Invitation> invitations;
    private ApiInterface apiService;
    private RelativeLayout emptyView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_invitations_fragment, container, false);
        apiService = MainActivity.buildHTTP();

        createInvitationList();
        return view;
    }

    public void clear() {
        if (recyclerViewAdapter != null) {
            int size = invitations.size();
            invitations.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if (recyclerViewAdapter != null) {
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

                    // Display empty view when response body is empty
                    emptyView = view.findViewById(R.id.empty_view);
                    if (invitations.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                } else
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

                            new MaterialAlertDialogBuilder(getActivity())
                                    .setMessage("You successfully joined " + data.getGroupName() + "!")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            clear();
                                            refresh();
                                        }
                                    })
                                    .show();
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Membership> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onDeleteButton(final int position) {
                new MaterialAlertDialogBuilder(getActivity())
                        .setMessage("Do you want to decline this invitation?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Invitation> call = apiService.deleteGroupInvitation(MainActivity.getToken(getContext()), invitations.get(position).getGroup(), invitations.get(position).getId());

                                call.enqueue(new Callback<Invitation>() {
                                    @Override
                                    public void onResponse(Call<Invitation> call, Response<Invitation> response) {
                                        if (response.errorBody() == null) {
                                            recyclerViewAdapter.removeItem(position);
                                            if (invitations.isEmpty()) {
                                                clear();
                                                refresh();
                                            }
                                            new MaterialAlertDialogBuilder(getActivity())
                                                    .setMessage("Invitation was successfully declined.")
                                                    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    })
                                                    .show();
                                        } else {
                                            showAlertMessage("Could not delete invitation.", "Ok");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Invitation> call, Throwable t) {
                                        showAlertMessage("Could not delete invitation.", "Ok");
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
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
