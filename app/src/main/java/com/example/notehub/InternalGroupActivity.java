package com.example.notehub;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import adapters.NoteRecyclerViewAdapter;
import models.CardView;
import models.Favorite;
import models.Note;
import models.NoteReport;
import models.Rating;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM;

public class InternalGroupActivity extends AppCompatActivity implements UploadActivity.CardHolder {

    private int groupID;
    private String groupName;

    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ApiInterface apiService;
    private ArrayList<CardView> cards = new ArrayList<>();
    private DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal);

        apiService = MainActivity.buildHTTP();
        groupID = getIntent().getIntExtra("groupID", -1);
        groupName = getIntent().getStringExtra("groupName");

        createCardsList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_top_navigation, menu);

        getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title_search);

        // Style title for top app bar and hide it
        TextView actionBarTitle = findViewById(R.id.action_bar_search_title);
        actionBarTitle.setText(groupName);

        // Display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.group_nav_invite:
                Toast.makeText(this, "Invite", Toast.LENGTH_SHORT).show();
               // Intent homeIntent = new Intent(this, SearchActivity.class);
                //startActivity(homeIntent);
                return true;
            case R.id.group_nav_upload_notes:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("groupID", groupID);
                dialogFragment = UploadActivity.newInstance();
                dialogFragment.setArguments(bundle);
                dialogFragment.show(ft, "dialog");
                return true;
            case R.id.group_nav_show_members:
                Toast.makeText(this, "Show members", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.group_nav_leave:
                //Call<Membership> call =
                //Call<Membership> call = apiService.deleteGroupMembership(getToken(), groupID, );
                Intent leaveIntent = new Intent(this, GroupActivity.class);
                startActivity(leaveIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dialogFragment.onActivityResult(requestCode, requestCode, data);
    }

    // Back Button on app bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String getToken() {
        return MainActivity.getToken(this);
    }

    public void createCardsList() {
        // Create Cards
        cards = new ArrayList<>();

        Call<List<Note>> call = apiService.getGroupNotes(getToken(), groupID, null, null, null, null, null);

        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.errorBody() == null) {
                    final List<Note> notes = response.body();
                    for (int i = 0; i < notes.size(); i++) {
                        final int count = i;
                        Call<List<Favorite>> favoriteCall = apiService.getNoteFavorites(getToken(), notes.get(i).getId());

                        favoriteCall.enqueue(new Callback<List<Favorite>>() {
                            @Override
                            public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                                if(response.errorBody() == null) {
                                    List<Favorite> favorites = response.body();

                                    if(favorites.size() == 0) {
                                        adapter.addItem(new CardView(notes.get(count).getId(), notes.get(count).getTitle(), "School: " + notes.get(count).getUniversityName(),
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), notes.get(count).getAvgRating(), R.drawable.ic_favorite_star));
                                    }
                                    else {
                                        adapter.addItem(new CardView(notes.get(count).getId(), notes.get(count).getTitle(), "School: " + notes.get(count).getUniversityName(),
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), notes.get(count).getAvgRating(), R.drawable.ic_favorite_toggle_on));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Favorite>> call, Throwable t) {

                            }
                        });

                    }
                    buildRecyclerView();
                } else {
                    showAlertMessage("Could not load notes to recycler view.", "Ok");
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {

            }
        });
    }

    // Initialize recycle view
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.internal_recycler);
        // recyclerView.setHasFixedSize(true); // need to remove later IMPORTANT
        layoutManager = new LinearLayoutManager(InternalGroupActivity.this);
        adapter = new NoteRecyclerViewAdapter(InternalGroupActivity.this, cards);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemCLickListener(new NoteRecyclerViewAdapter.onItemClickListener() {
            // Click on card redirects to NoteActivity and sends the data to it
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Intent intent = new Intent(InternalGroupActivity.this, NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle());
                startActivity(intent);
            }

            // Card clicked on gets sent to home
            @Override
            public void onFavoriteClick(final int position) {
                Intent intent = new Intent(InternalGroupActivity.this, HomeActivity.class);

                Call<List<Favorite>> callGet = apiService.getNoteFavorites(getToken(), cards.get(position).getNoteId());

                callGet.enqueue(new Callback<List<Favorite>>() {
                    @Override
                    public void onResponse(Call<List<Favorite>> call, Response<List<Favorite>> response) {
                        if(response.errorBody() == null) {
                            List<Favorite> favorites = response.body();

                            if(favorites.size() == 0) {
                                Call<Favorite> callUpload = apiService.uploadNoteFavorite(getToken(), cards.get(position).getNoteId(), new Favorite());

                                callUpload.enqueue(new Callback<Favorite>() {
                                    @Override
                                    public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                                        if(response.errorBody() == null) {
                                            cards.get(position).setImageFavorite(R.drawable.ic_favorite_toggle_on);
                                            //ImageView favorite = findViewById(R.id.image_favorite);
                                            //favorite.setImageResource(R.drawable.ic_favorite_toggle_on);
                                            adapter.notifyItemChanged(position);
                                        }
                                        else {
                                            showAlertMessage("Could not favorite note.", "Ok");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Favorite> call, Throwable t) {

                                    }
                                });
                            }
                            else {
                                Call<Favorite> callDelete = apiService.deleteNoteFavorite(getToken(), cards.get(position).getNoteId(), favorites.get(0).getId());
                                callDelete.enqueue(new Callback<Favorite>() {
                                    @Override
                                    public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                                        if(response.errorBody() == null) {
                                            // showAlertMessage("");
                                            cards.get(position).setImageFavorite(R.drawable.ic_favorite_star);
                                            //ImageView favorite = findViewById(R.id.image_favorite);
                                            //favorite.setImageResource(R.drawable.ic_favorite_star);
                                            adapter.notifyItemChanged(position);
                                        }
                                        else {
                                            showAlertMessage("Could not delete favorite note.", "Ok");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Favorite> call, Throwable t) {

                                    }
                                });
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Favorite>> call, Throwable t) {

                    }
                });



                //intent.putExtra("Example item", cards.get(position));
                //startActivity(intent);
            }

            @Override
            public void onCommentClick(int position) {
                Intent intent = new Intent(InternalGroupActivity.this, NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle())
                        .putExtra("startComment", true);
                startActivity(intent);
            }

            @Override
            public void onReportClick(int position) {
                final Context context = InternalGroupActivity.this;
                final int noteID = cards.get(position).getNoteId();
                new MaterialAlertDialogBuilder(context)
                        .setTitle("NoteReport Note")
                        .setMessage("Do you want to report this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<NoteReport> call = apiService.createNoteReport(MainActivity.getToken(context), noteID);
                                call.enqueue(new Callback<NoteReport>() {
                                    @Override
                                    public void onResponse(Call<NoteReport> call, Response<NoteReport> response) {
                                        if(response.errorBody() == null) {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Note successfully reported.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        } else {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Already reported note.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<NoteReport> call, Throwable t) {

                                        new MaterialAlertDialogBuilder(context)
                                                .setMessage("Failed to report note.")
                                                .setPositiveButton("Done", null)
                                                .show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            // Click on delete image deletes card
            @Override
            public void onDeleteClick(final int position) {
                new MaterialAlertDialogBuilder(InternalGroupActivity.this)
                        .setTitle("Delete Note")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            @Override
            public void onRatingClick(final int position, final float score) {
                Call<List<Rating>> callGet = apiService.getNoteRatings(getToken(), cards.get(position).getNoteId());
                callGet.enqueue(new Callback<List<Rating>>() {
                    @Override
                    public void onResponse(Call<List<Rating>> call, Response<List<Rating>> response) {
                        if(response.errorBody() == null) {
                            List<Rating> ratings = response.body();

                            if(ratings.size() == 0) {
                                Rating rating = new Rating();
                                rating.setScore(score);
                                Call<Rating> callUpload = apiService.uploadNoteRating(getToken(), cards.get(position).getNoteId(), rating);
                                callUpload.enqueue(new Callback<Rating>() {
                                    @Override
                                    public void onResponse(Call<Rating> call, Response<Rating> response) {
                                        if(response.errorBody() == null) {
                                        }
                                        else {
                                            showAlertMessage("Could not rating note.", "Ok");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Rating> call, Throwable t) {

                                    }
                                });
                            }
                            else {
                                Rating rating = response.body().get(0);
                                rating.setScore(score);
                                Call<Rating> callDelete = apiService.updateNoteRating(getToken(), cards.get(position).getNoteId(), rating.getId(), rating);
                                callDelete.enqueue(new Callback<Rating>() {
                                    @Override
                                    public void onResponse(Call<Rating> call, Response<Rating> response) {
                                        if(response.errorBody() == null) {
                                        }
                                        else {
                                            showAlertMessage("Could not delete rating note.", "Ok");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Rating> call, Throwable t) {

                                    }
                                });
                            }
                        }
                        else {

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Rating>> call, Throwable t) {

                    }
                });
            }
        });
    }

    // Remove item in recycle view
    public void removeItem(final int position) {
        //cards.remove(position);

        Call<Note> call = apiService.deleteNote(getToken(), cards.get(position).getNoteId());

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.errorBody() == null) {
                    adapter.removeItem(position);
                } else {
                    showAlertMessage("Cannot delete other user's note.", "Ok");
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {

            }
        });
    }

    // Change cards text
    public void changeItem(int position, String text) {
        cards.get(position).setTitle(text);
        adapter.notifyItemChanged(position);
    }

    // Insert item in recycle view
    public void insertItem(int position, CardView card) {
        adapter.addItem(card);
        //adapter.notifyDataSetChanged(); // don't use this because will refresh the list and not show animation
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(InternalGroupActivity.this)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }

    @Override
    public void insertCard(CardView card) {
            insertItem(cards.size(), card);
    }
}
