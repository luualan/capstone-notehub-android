package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import adapters.GroupMembersRecyclerViewAdapter;
import adapters.GroupRecyclerViewAdapter;
import models.Group;
import models.Membership;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class GroupMembersActivity extends AppCompatActivity {
    private int groupID;
    private String groupName;

    private RecyclerView recyclerView;
    private GroupMembersRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ApiInterface apiService;
    private ArrayList<Membership> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        apiService = MainActivity.buildHTTP();
        groupID = getIntent().getIntExtra("groupID", -1);
        groupName = getIntent().getStringExtra("groupName");
        createMembersList();
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
                }
                else
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
        adapter = new GroupMembersRecyclerViewAdapter(this, members);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(adapter);

        // Adapter listener on click
        adapter.setOnClickListener(new GroupMembersRecyclerViewAdapter.onItemClickListener() {
         /*   @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(GroupMembersActivity.this, InnerGroupActivity.class)
                        .putExtra("groupID", groups.get(position).getId())
                        .putExtra("groupName", groups.get(position).getName());
                startActivity(intent);
            }*/

   /*         @Override
            public void onClickButton(int position) {

            }

            @Override
            public void onDeleteClick(int position) {

            }*/
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_top_navigation, menu);

        getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_search);

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

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(GroupMembersActivity.this)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
