package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import models.Note;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements UploadActivity.CardHolder {

    private FloatingActionButton floatingActionButton;
    private BottomAppBar bottomAppBar;
    private DialogFragment dialog;

    private ArrayList<CardView> cards;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter; // bridge from recycle view to data; provides as many items as we currently need. (can use custom adapter)
    private RecyclerView.LayoutManager layoutManager; // aligning single items on list

    private Button buttonInsert;
    private Button buttonRemove;

    private EditText editTextInsert;
    private EditText editTextRemove;

    private Menu menu;

    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        apiService = MainActivity.buildHTTP();

        createCardsList();
        setButtons();

        bottomAppBar = findViewById(R.id.bottomAppBar);
        // main line for setting menu in bottom app bar
        setSupportActionBar(bottomAppBar);

        // Click home icon to go to home screen
        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home(v);
            }
        });

        floatingActionButton = findViewById(R.id.fab);

        // Click floating action button opens full screen dialog screen
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog = UploadActivity.newInstance();
                dialog.show(ft, "dialog");
            }
        });
    }

    public void createCardsList() {
        cards = new ArrayList<>();

        //cards.add(new CardView(R.drawable.ic_favorite, "2", "4", "5", "4", null));
        Call<List<Note>> call = apiService.getNotes(null, null, null, null, null);

        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.errorBody() == null) {
                    List<Note> notes = response.body();
                    for (int i = 0; i < notes.size(); i++)
                        cards.add(new CardView(notes.get(i).getId(), notes.get(i).getTitle(), notes.get(i).getUniversityName(),
                                notes.get(i).getCourse(), notes.get(i).getAuthorUsername(), R.drawable.ic_favorite));
                    buildRecyclerView();
                    Log.e("dude", Integer.toString(cards.size()));
                } else {
                    Toast.makeText(SearchActivity.this, "Could not load notes to recycler view.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {

            }
        });
    }

    // Initialize recycle view
    public void buildRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        // recyclerView.setHasFixedSize(true); // need to remove later IMPORTANT
        layoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerViewAdapter(cards);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemCLickListener(new RecyclerViewAdapter.onItemClickListener() {
            // Click on card changes text
            @Override
            public void onItemClick(int position) {
                changeItem(position, "Clicked");
            }

            // Card clicked on gets sent to home
            @Override
            public void onFavoriteClick(int position) {
                Log.e("dude", Integer.toString(cards.size()));
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                intent.putExtra("Example item", cards.get(position));
                startActivity(intent);
            }

            // Click on delete image deletes card
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    // Set up the buttons' functionality
    public void setButtons() {
        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.text_insert);
        editTextRemove = findViewById(R.id.text_remove);

 /*       // Click insert
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(1, new CardView());
            }
        });*/

        // Click remove
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });
    }

    // Insert item in recycle view
    public void insertItem(int position, CardView card) {
        adapter.addItem(position, card);

        //adapter.notifyDataSetChanged(); // don't use this because will refresh the list and not show animation
    }

    // Remove item in recycle view
    public void removeItem(final int position) {
        //cards.remove(position);

        final SharedPreferences savePreferences = this.getSharedPreferences("NoteHub", Context.MODE_PRIVATE);

        Call<Note> call = apiService.deleteNote("Token " + savePreferences.getString("TOKEN", null), cards.get(position).getNoteId());

        call.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if(response.errorBody() == null) {
                    adapter.removeItem(position);
                }
                else {
                    Toast.makeText(SearchActivity.this, "Could not delete note.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void insertCard(CardView cardView) {
        insertItem(cards.size(), cardView);
    }

    // Adds the tool bar icons to bottom navigation bar
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation, menu);
        this.menu = menu;

        final MenuItem searchItem = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Used for filter search
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // Whatever we type will go to newText
            @Override
            public boolean onQueryTextChange(String newText) {
                // NewText then goes to our filter
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    /*    searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                setItemsVisibility(menu, item, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                setItemsVisibility(menu, item, true);
                return true;
            }
        });
*/
        return true;
    }

    // Clicks for icons located on toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.nav_search:
                floatingActionButton.hide();
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                item.expandActionView();
                setItemsVisibility(menu, item, false);
                return true;*/
            case R.id.nav_groups:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                Toast.makeText(this, "groups selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_settings:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                Toast.makeText(this, "settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_sign_out:
                bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
