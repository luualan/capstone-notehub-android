package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.Group;
import models.Invitation;
import models.Membership;
import models.Note;
import models.University;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    TextView inputOne;
    TextView inputTwo;
    ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        apiService = MainActivity.buildHTTP();
        inputOne = findViewById(R.id.inputOne);
        inputTwo = findViewById(R.id.inputTwo);
    }

    public String getToken() {
        SharedPreferences savePreferences = this.getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
        return "Token " + savePreferences.getString("TOKEN", null);
    }

    public String getInputOne() {
        return inputOne.getText().toString();
    }


    public String getInputTwo() {
        return inputTwo.getText().toString();
    }

    public void createGroup(View v) {
        Group group = new Group();
        Random random = new Random();
        group.setName("Test Group " + (random.nextInt(10000)));
        Call<Group> call = apiService.createGroup(getToken(), group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.errorBody() == null) {
                    Group data = response.body();
                    Log.d("TEST", "ID: " + data.getId());
                    Log.d("TEST", "Moderator ID: " + data.getModerator());
                    Log.d("TEST", "Moderator Username: " + data.getModeratorUsername());
                    Log.d("TEST", "Group name: " + data.getName());
                } else {
                    Log.d("TEST", "Failed to create Group");
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

        public void getGroup(View v) {
        Group group = new Group();
        Random random = new Random();
        group.setName("Test Group " + (random.nextInt(10000)));
        Call<Group> call = apiService.getGroup(getToken(), Integer.valueOf(getInputOne()));
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.errorBody() == null) {
                    Group data = response.body();
                    Log.d("TEST", "ID: " + data.getId());
                    Log.d("TEST", "Moderator ID: " + data.getModerator());
                    Log.d("TEST", "Moderator Username: " + data.getModeratorUsername());
                    Log.d("TEST", "Group name: " + data.getName());
                } else {
                    Log.d("TEST", "Failed to get Group");
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

    public void deleteGroup(View v) {
        Group group = new Group();
        Random random = new Random();
        group.setName("Test Group " + (random.nextInt(10000)));
        Call<Group> call = apiService.deleteGroup(getToken(), Integer.valueOf(getInputOne()));
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.errorBody() == null) {
                    Log.d("TEST", "Delete SUCCESSFUL");
                } else {
                    Log.d("TEST", "Failed to delete Group");
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

    public void updateGroup(View v) {
        Group group = new Group();
        Random random = new Random();
        group.setName("Test Group " + (random.nextInt(10000)));
        Call<Group> call = apiService.updateGroup(getToken(), Integer.valueOf(getInputOne()), group);
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if (response.errorBody() == null) {
                    Group data = response.body();
                    Log.d("TEST", "ID: " + data.getId());
                    Log.d("TEST", "Moderator ID: " + data.getModerator());
                    Log.d("TEST", "Moderator Username: " + data.getModeratorUsername());
                    Log.d("TEST", "Group name: " + data.getName());
                } else {
                    Log.d("TEST", "Failed to delete Group");
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

        public void uploadGroupNote(View v) {
        Note note = new Note();
        Random random = new Random();
        note.setTitle("Test Note " + (random.nextInt(10000)));
        note.setCourse("CS " + (random.nextInt(600)));
        note.setUniversity(random.nextInt(1000));
        Call<Note> call = apiService.uploadGroupNote(getToken(), Integer.valueOf(getInputOne()), note);
        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.errorBody() == null) {
                    Note data = response.body();
                    Log.d("TEST", "ID: " + data.getId());
                    Log.d("TEST", "Author Username: " + data.getAuthorUsername());
                    Log.d("TEST", "Title: " + data.getTitle());
                    Log.d("TEST", "Course: " + data.getCourse());
                    Log.d("TEST", "Group name: " + data.getGroup());
                } else {
                    Log.d("TEST", "Failed to upload group note");
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

        public void uploadGroupInvitation(View v) {
        Invitation invitation = new Invitation();
        Random random = new Random();
        invitation.setUser(Integer.valueOf(getInputTwo()));
        Call<Invitation> call = apiService.uploadGroupInvitation(getToken(), Integer.valueOf(getInputOne()), invitation);
        call.enqueue(new Callback<Invitation>() {
            @Override
            public void onResponse(Call<Invitation> call, Response<Invitation> response) {
                if (response.errorBody() == null) {
                    Invitation data = response.body();
                    Log.d("TEST", "ID: " + data.getId());
                    Log.d("TEST", "Group ID: " + data.getGroup());
                    Log.d("TEST", "Group Name: " + data.getGroupName());
                    Log.d("TEST", "User ID: " + data.getUser());
                    Log.d("TEST", "Username: " + data.getUsername());
                } else {
                    Log.d("TEST", "Failed to upload invitation");
                }
            }

            @Override
            public void onFailure(Call<Invitation> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

    public void getGroupInvitations(View v) {
        Random random = new Random();
        Call<List<Invitation>> call = apiService.getGroupInvitations(getToken(), Integer.valueOf(getInputOne()));
        call.enqueue(new Callback<List<Invitation>>() {
            @Override
            public void onResponse(Call<List<Invitation>> call, Response<List<Invitation>> response) {
                if (response.errorBody() == null) {
                    List<Invitation> dataList = response.body();
                    for(Invitation data : dataList) {
                        Log.d("TEST", "ID: " + data.getId());
                        Log.d("TEST", "Group ID: " + data.getGroup());
                        Log.d("TEST", "Group Name: " + data.getGroupName());
                        Log.d("TEST", "User ID: " + data.getUser());
                        Log.d("TEST", "Username: " + data.getUsername());
                    }
                } else {
                    Log.d("TEST", "Failed to upload invitation");
                }
            }

            @Override
            public void onFailure(Call<List<Invitation>> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

        public void getGroupMemberships(View v) {
        Random random = new Random();
        Call<List<Membership>> call = apiService.getGroupMemberships(getToken(), Integer.valueOf(getInputOne()));
        call.enqueue(new Callback<List<Membership>>() {
            @Override
            public void onResponse(Call<List<Membership>> call, Response<List<Membership>> response) {
                if (response.errorBody() == null) {
                    List<Membership> dataList = response.body();
                    for(Membership data : dataList) {
                        Log.d("TEST", "ID: " + data.getId());
                        Log.d("TEST", "Group ID: " + data.getGroup());
                        Log.d("TEST", "Group Name: " + data.getGroupName());
                        Log.d("TEST", "User ID: " + data.getUser());
                        Log.d("TEST", "Username: " + data.getUsername());
                    }
                } else {
                    Log.d("TEST", "Failed to upload membership");
                }
            }

            @Override
            public void onFailure(Call<List<Membership>> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }

    public void getGroupNotes(View v) {
        Random random = new Random();
        Call<List<Note>> call = apiService.getGroupNotes(getToken(), Integer.valueOf(getInputOne()), null, null, null, null, null);
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.errorBody() == null) {
                    List<Note> dataList = response.body();
                    for(Note data : dataList) {
                        Log.d("TEST", "ID: " + data.getId());
                        Log.d("TEST", "Author Username: " + data.getAuthorUsername());
                        Log.d("TEST", "Title: " + data.getTitle());
                        Log.d("TEST", "Course: " + data.getCourse());
                        Log.d("TEST", "Group name: " + data.getGroup());
                    }
                } else {
                    Log.d("TEST", "Failed to upload note");
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                Log.d("TEST", "Super Failure");
            }
        });
    }


}
