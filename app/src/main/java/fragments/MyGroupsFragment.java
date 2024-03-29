package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.InnerGroupActivity;
import com.example.notehub.MainActivity;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupRecyclerViewAdapter;
import models.Group;
import models.Membership;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyGroupsFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    GroupRecyclerViewAdapter recyclerViewAdapter;
    private List<Group> groups;
    private ApiInterface apiService;
    private FloatingActionButton addGroupButton;
    private EditText groupNameEdit;
    private RelativeLayout emptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_groups_fragment, container, false);
        apiService = MainActivity.buildHTTP();

        createGroupList();

        addGroupButton = view.findViewById(R.id.add_group_button);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        // final GroupActivity noteActivity = (GroupActivity) getActivity();
        return view;
    }

    public void clear() {
        if(recyclerViewAdapter != null) {
            int size = groups.size();
            groups.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if(recyclerViewAdapter != null) {
            createGroupList();
        }
    }

    // Load group list
    public void createGroupList() {
        groups = new ArrayList<>();

        Call<List<Group>> call = apiService.getUserGroups(MainActivity.getToken(getContext()));
        buildRecyclerView();
        call.enqueue(new Callback<List<Group>>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                // Success; store groups
                if (response.errorBody() == null) {
                    //groups = response.body();
                    for (Group group : response.body())
                        recyclerViewAdapter.addItem(group);

                    // Display empty view when response body is empty
                    emptyView = view.findViewById(R.id.empty_view);
                    if (groups.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                } else
                    showAlertMessage("Error, could not load users groups.", "Ok");
            }

            @Override
            public void onFailure(Call<List<Group>> call, Throwable t) {
            }
        });
    }

    // Initialize recycler
    public void buildRecyclerView() {
        recyclerView = view.findViewById(R.id.my_groups_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        recyclerViewAdapter = new GroupRecyclerViewAdapter(getContext(), groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(recyclerViewAdapter);

        // Adapter listener on click
        recyclerViewAdapter.setOnClickListener(new GroupRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), InnerGroupActivity.class)
                        .putExtra("groupID", groups.get(position).getId())
                        .putExtra("groupName", groups.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onClickButton(final int position) {
                final Context context = getContext();
                final String token = MainActivity.getToken(context);
                final Group group = groups.get(position);

                // If moderator, enable delete group functionality
                if(groups.get(position).getIsModerator()) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Delete group")
                            .setMessage("Do you want to delete this group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<Group> call = apiService.deleteGroup(token, group.getId());
                                    call.enqueue(new Callback<Group>() {
                                        @Override
                                        public void onResponse(Call<Group> call, Response<Group> response) {
                                            if(response.errorBody() == null) {
                                                recyclerViewAdapter.removeItem(position);
                                                new MaterialAlertDialogBuilder(context)
                                                        .setMessage("Successfully deleted group.")
                                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if(groups.isEmpty())
                                                                    refresh();
                                                            }
                                                        })
                                                        .show();
                                            }
                                            else {
                                                new MaterialAlertDialogBuilder(context)
                                                        .setMessage("Failed to delete group.")
                                                        .setPositiveButton("Done", null)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Group> call, Throwable t) {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Failed to delete group.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        }
                                    });

                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }

                // Otherwise, enable leave functionality
                else {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Leave group")
                            .setMessage("Do you want to leave this group?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Group group = groups.get(position);
                                    Call<Membership> call = apiService.deleteGroupMembership(token, group.getId(), group.getMembershipID());
                                    call.enqueue(new Callback<Membership>() {
                                        @Override
                                        public void onResponse(Call<Membership> call, Response<Membership> response) {
                                            if(response.errorBody() == null) {
                                                recyclerViewAdapter.removeItem(position);
                                                new MaterialAlertDialogBuilder(context)
                                                        .setMessage("Successfully left group.")
                                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if(groups.isEmpty())
                                                                    refresh();
                                                            }
                                                        })
                                                        .show();
                                            } else {
                                                new MaterialAlertDialogBuilder(context)
                                                        .setMessage("Failed to leave group.")
                                                        .setPositiveButton("Done", null)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Membership> call, Throwable t) {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Failed to leave group.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
    });
}

    // Add Group
    public void createGroup() {
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_group, null);
        createDialog.setView(dialogView);
        final boolean isEmpty = groups.isEmpty();

        MaterialButton createGroupButton;
        groupNameEdit = dialogView.findViewById(R.id.create_group_name);

        createGroupButton = dialogView.findViewById(R.id.create_group_button);
        final AlertDialog showDialog = createDialog.show();

        // Click create button
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> callUser = apiService.getUser(MainActivity.getToken(getActivity()));

                callUser.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, final Response<User> userResponse) {
                        // Successfully get user
                        if(userResponse.errorBody() == null) {
                            // Store group name input into group object
                            Group group = new Group();
                            group.setName(groupNameEdit.getText().toString());

                            Call<Group> callGroup = apiService.createGroup(MainActivity.getToken(getActivity()), group);
                            callGroup.enqueue(new Callback<Group>() {
                                @Override
                                public void onResponse(Call<Group> call, final Response<Group> response) {
                                    // Successfully get group
                                    if (response.errorBody() == null) {
                                        final Group data = response.body();
                                        recyclerViewAdapter.addItem(data);

                                        new MaterialAlertDialogBuilder(getActivity())
                                                .setMessage("Group was successfully created!")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(isEmpty)
                                                            refresh();
                                                    }
                                                })
                                                .show();


                                        showDialog.dismiss();
                                    } else {
                                        if (groupNameEdit.getText().toString().trim().isEmpty())
                                            groupNameEdit.setError("Please fill out this field.");

                                        else if(!userResponse.body().isPremium() && groups.size() == 3)
                                            showAlertMessage("Failed to create group. You have exceeded the number of groups you can create with your free subscription.", "Ok");

                                        else
                                            showAlertMessage("Failed to create group.", "Ok");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Group> call, Throwable t) {
                                    Log.d("TEST", "Super Failure");
                                }
                            });
                        }
                        else {
                            showAlertMessage("Failed to get user.", "Ok");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
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


