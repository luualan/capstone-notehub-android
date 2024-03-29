package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;

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

public class SearchActivity extends AppCompatActivity implements UploadActivity.CardHolder {
    private ArrayList<CardView> cards;
    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter adapter; // bridge from recycle view to data; provides as many items as we currently need. (can use custom adapter)
    private RecyclerView.LayoutManager layoutManager; // aligning single items on list

    private ApiInterface apiService;
    private AnimationDrawable animationToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiService = MainActivity.buildHTTP();
        createCardsList();
    }

    @Override
    public void insertCard(CardView cardView) {
        insertItem(cards.size(), cardView);
    }

    // Adds the tool bar icons to bottom navigation bar
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.top_navigation, menu);

        // Style title for top app bar and hide it
        getSupportActionBar().setDisplayOptions(DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_title);
        getSupportActionBar().getCustomView().setVisibility(View.GONE);

        // display back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Fix
      /*  getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.animation_background));

        int actionBarId = getResources().getIdentifier("action_bar", "id", "android");
        View actionBar = findViewById(actionBarId);
        Log.e("AAAAAAAAAAAAAA", Integer.toString(actionBarId));
        Log.e("ALANAALANANA", actionBar.toString());
        Drawable actionBarBackground = actionBar.getBackground();
        animationToolBar = (AnimationDrawable) actionBarBackground;
        // animationToolBar = (AnimationDrawable) getActionBarView().getBackground();

       // animationToolBar = (AnimationDrawable) getSupportActionBar().getCustomView().getBackground();

        //Time changes
         animationToolBar.setEnterFadeDuration(5000);
         animationToolBar.setExitFadeDuration(3000);

         animationToolBar.start();*/

        // Search
        final MenuItem searchItem = menu.findItem(R.id.nav_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.requestFocus();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Expand Search hide title
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().getCustomView().setVisibility(View.GONE);
            }
        });

        // Close Search show title
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportActionBar().getCustomView().setVisibility(View.VISIBLE);
                return false;
            }
        });

        // Used for filter search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // What ever we type will filter search
            @Override
            public boolean onQueryTextChange(String newText) {
                // NewText then goes to our filter
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    // Back Button on app bar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void createCardsList() {
        // Create Cards
        cards = new ArrayList<>();

        Call<List<Note>> call = apiService.getNotes(getToken(), null, null, null, null, null);

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
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), notes.get(count).getAvgRating(),
                                                notes.get(count).isAuthor(), notes.get(count).isModerator(), R.drawable.ic_favorite_star, "Type: Public"));
                                    }
                                    else {
                                        adapter.addItem(new CardView(notes.get(count).getId(), notes.get(count).getTitle(), "School: " + notes.get(count).getUniversityName(),
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), notes.get(count).getAvgRating(),
                                                notes.get(count).isAuthor(), notes.get(count).isModerator(), R.drawable.ic_favorite_toggle_on, "Type: Public"));
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
        recyclerView = findViewById(R.id.search_recycler_view);
        // recyclerView.setHasFixedSize(true); // need to remove later IMPORTANT
        layoutManager = new LinearLayoutManager(this);
        adapter = new NoteRecyclerViewAdapter(SearchActivity.this, cards);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new NoteRecyclerViewAdapter.onItemClickListener() {
            // Click on card redirects to NoteActivity and sends the data to it
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Intent intent = new Intent(SearchActivity.this, NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle());
                startActivity(intent);
            }

            // Card clicked on gets sent to home
            @Override
            public void onFavoriteClick(final int position) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);


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
                Intent intent = new Intent(SearchActivity.this, NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle())
                        .putExtra("startComment", true);
                startActivity(intent);
            }

            @Override
            public void onReportClick(final int position) {
                final Context context = SearchActivity.this;
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
                final Context context = SearchActivity.this;
                final int noteID = cards.get(position).getNoteId();
                final String token = MainActivity.getToken(context);
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Delete Note")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Note> call = apiService.deleteNote(token, noteID);
                                call.enqueue(new Callback<Note>() {
                                    @Override
                                    public void onResponse(Call<Note> call, Response<Note> response) {
                                        if(response.errorBody() == null) {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Successfully deleted note.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                            adapter.removeItem(position);
                                        } else {
                                            new MaterialAlertDialogBuilder(context)
                                                    .setMessage("Failed to delete note.")
                                                    .setPositiveButton("Done", null)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Note> call, Throwable t) {
                                        new MaterialAlertDialogBuilder(context)
                                                .setMessage("Failed to delete note.")
                                                .setPositiveButton("Done", null)
                                                .show();
                                    }
                                });
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

    // Retrieve action bar id
    public View getActionBarView() {
        Window window = getWindow();
        View v = window.getDecorView();
        int resId = getResources().getIdentifier("action_bar_container", "id", "android");
        return v.findViewById(resId);
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

    private String getToken() {
        return MainActivity.getToken(this);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if (item != exception)
                item.setVisible(visible);
        }
    }

    public void home(View v) {
        Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public void signOut() {
        Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(SearchActivity.this)
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
