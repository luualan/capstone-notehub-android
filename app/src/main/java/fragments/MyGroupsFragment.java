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

import adapters.GroupRecyclerViewAdapter;
import models.Group;
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
                }
                else
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
    }

    public void createGroup() {
        final AlertDialog.Builder createDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_group, null);
        createDialog.setView(dialogView);

        MaterialButton createGroupButton;
        groupNameEdit = dialogView.findViewById(R.id.create_group_name);
        createGroupButton = dialogView.findViewById(R.id.create_group_button);

        final AlertDialog showDialog = createDialog.show();
        // Click create button
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Store group name input into group object
                Group group = new Group();
                group.setName(groupNameEdit.getText().toString());

                showDialog.dismiss();
                Call<Group> call = apiService.createGroup(MainActivity.getToken(getActivity()), group);
                call.enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(Call<Group> call, Response<Group> response) {
                        // Success
                        if (response.errorBody() == null) {
                            final Group data = response.body();
                            recyclerViewAdapter.addItem(data);
                            showAlertMessage("Group was successfully created!", "Ok");
                        }
                            else {
                            if (groupNameEdit.getText().toString().trim().isEmpty())
                                groupNameEdit.setError("Please fill out this field.");

                            else
                                showAlertMessage("Failed to create Group", "Ok");
                        }
                    }

                    @Override
                    public void onFailure(Call<Group> call, Throwable t) {
                        Log.d("TEST", "Super Failure");
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


