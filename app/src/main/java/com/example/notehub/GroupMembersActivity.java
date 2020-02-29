package com.example.notehub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupMembersRecyclerViewAdapter;
import models.Group;
import models.Invitation;
import models.Membership;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class GroupMembersActivity extends AppCompatActivity {
    private int groupID;
    private Group group;
    private String groupName;

    private RecyclerView recyclerView;
    private GroupMembersRecyclerViewAdapter adapter;

    private ApiInterface apiService;
    private ArrayList<Membership> members = new ArrayList<>();

    private FloatingActionButton addButton;
    private EditText groupMemberNameEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        apiService = MainActivity.buildHTTP();
        groupID = getIntent().getIntExtra("groupID", -1);
        groupName = getIntent().getStringExtra("groupName");
        Call<Group> call = apiService.getGroup(MainActivity.getToken(GroupMembersActivity.this), groupID);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if(response.errorBody() == null)
                    group = response.body();
                createMembersList();
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {

            }
        });

        addButton = findViewById(R.id.add_group_member_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadGroupInvitation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title);

        // Style title for top app bar and hide it
        TextView actionBarTitle = findViewById(R.id.action_bar_search_title);
        actionBarTitle.setText("Group Members");

        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    // Back Button on app bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Load group list
    public void createMembersList() {
        members = new ArrayList<>();

        Call<List<Membership>> call = apiService.getGroupMemberships(MainActivity.getToken(this), groupID);
        buildRecyclerView();

        call.enqueue(new Callback<List<Membership>>() {
            @Override
            public void onResponse(Call<List<Membership>> call, Response<List<Membership>> response) {
                // Success; store group members
                if (response.errorBody() == null) {
                    //groups = response.body();
                    for (Membership member : response.body())
                        adapter.addItem(member);
                } else
                    showAlertMessage("Error, could not load users groups.", "Ok");
            }

            @Override
            public void onFailure(Call<List<Membership>> call, Throwable t) {

            }
        });
    }

    // Initialize recycler
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.group_members_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        adapter = new GroupMembersRecyclerViewAdapter(this, members, group);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(adapter);

        // Adapter listener on click
        adapter.setOnClickListener(new GroupMembersRecyclerViewAdapter.onItemClickListener() {
            final Context context = GroupMembersActivity.this;
            final String token = MainActivity.getToken(context);

            @Override
            public void onDeleteClick(final int position) {
                final Context context = GroupMembersActivity.this;
                final String token = MainActivity.getToken(context);
                final Membership member = members.get(position);
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Kick member")
                        .setMessage("Do you want to leave kick " + member.getUsername() + " from the group?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Membership> call = apiService.deleteGroupMembership(token, member.getGroup(), member.getId());
                                call.enqueue(new Callback<Membership>() {
                                    @Override
                                    public void onResponse(Call<Membership> call, Response<Membership> response) {
                                        if (response.errorBody() == null) {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Successfully kicked " + member.getUsername() + " from the group.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                            adapter.removeItem(position);
                                        } else {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Failed to kick " + member.getUsername() + " from the group.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Membership> call, Throwable t) {
                                        new MaterialAlertDialogBuilder(context)
                                                .setMessage("Failed to kick " + member.getUsername() + " from the group.")
                                                .setPositiveButton("Done", null)
                                                .show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void uploadGroupInvitation() {
        // Creates pop up dialog with input field and submit button
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.invite_user, null);
        createDialog.setView(dialogView);

        MaterialButton addMemberButton;
        groupMemberNameEdit = dialogView.findViewById(R.id.invite_username);

        addMemberButton = dialogView.findViewById(R.id.invite_user_button);
        final AlertDialog showDialog = createDialog.show();

        // Click create button
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<List<User>> userCall = apiService.getUsers(groupMemberNameEdit.getText().toString());
                userCall.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> userCall, Response<List<User>> response) {
                        if(response.errorBody() == null) {
                            List<User> users = response.body();
                            int userID = -1;
                            if(users.size() == 1)
                                userID = users.get(0).getId();
                            Invitation invitation = new Invitation();
                            invitation.setUser(userID);
                            Call<Invitation> call = apiService.uploadGroupInvitation(MainActivity.getToken(GroupMembersActivity.this), groupID, invitation);
                            call.enqueue(new Callback<Invitation>() {
                                @Override
                                public void onResponse(Call<Invitation> call, Response<Invitation> response) {
                                    if (response.errorBody() == null) {
                                        showAlertMessage("Invitation was successfully sent!", "Ok");
                                        showDialog.dismiss();
                                    }
                                    else {
                                        if (groupMemberNameEdit.getText().toString().trim().isEmpty())
                                            groupMemberNameEdit.setError("Please fill out this field.");

                                        else
                                            showAlertMessage("Failed to add user.", "Ok");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Invitation> call, Throwable t) {
                                    Log.d("TEST", "Super Failure");
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {

                    }
                });

            }
        });
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(GroupMembersActivity.this)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
