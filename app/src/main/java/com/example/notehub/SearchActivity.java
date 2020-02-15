package com.example.notehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapters.NoteRecyclerViewAdapter;
import models.CardView;
import models.Favorite;
import models.Note;
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
        getSupportActionBar().setCustomView(R.layout.action_bar_title_search);
        getSupportActionBar().getCustomView().setVisibility(View.GONE);

        // display back button
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Search
        final MenuItem searchItem = menu.findItem(R.id.nav_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

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


        Call<List<Note>> call = apiService.getNotes(null, null, null, null, null);

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
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), R.drawable.ic_favorite_star));
                                    }
                                    else {
                                        adapter.addItem(new CardView(notes.get(count).getId(), notes.get(count).getTitle(), "School: " + notes.get(count).getUniversityName(),
                                                "Course: " + notes.get(count).getCourse(), "Name: " + notes.get(count).getAuthorUsername(), R.drawable.ic_favorite_toggle_on));
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

        adapter.setOnItemCLickListener(new NoteRecyclerViewAdapter.onItemClickListener() {
            // Click on card changes text
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Intent intent = new Intent(SearchActivity.this, NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId());
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
            public void onCommentClick() {
            }

            @Override
            public void onReportClick() {
                new MaterialAlertDialogBuilder(SearchActivity.this)
                        .setTitle("Report Note")
                        .setMessage("Do you want to report this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new MaterialAlertDialogBuilder(SearchActivity.this)
                                        .setMessage("Note successfully reported.")
                                        .setPositiveButton("Done", null)
                                        .show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }

            // Click on delete image deletes card
            @Override
            public void onDeleteClick(final int position) {
                new MaterialAlertDialogBuilder(SearchActivity.this)
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

    private String getToken() {
        SharedPreferences savePreferences = this.getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
        return "Token " + savePreferences.getString("TOKEN", null);
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
