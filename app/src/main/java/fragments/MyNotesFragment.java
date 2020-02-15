package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.HomeActivity;
import com.example.notehub.MainActivity;
import com.example.notehub.NoteActivity;
import com.example.notehub.R;
import com.example.notehub.UploadActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import adapters.NoteRecyclerViewAdapter;
import models.CardView;
import models.Note;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyNotesFragment extends Fragment implements UploadActivity.CardHolder {
    private ApiInterface apiService;
    private ArrayList<CardView> cards = new ArrayList<>();

    // RecyclerView
    View view;
    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    //private SharedPreferences savePreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = MainActivity.buildHTTP();
       // SharedPreferences savePreferences = getActivity().getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_notes_fragment, container, false);

        createCardsList();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bottom_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void createCardsList() {
        // Create Cards
        cards = new ArrayList<>();

        Call<List<Note>> call = apiService.getNotes(null, null, null, null, null);

        final SharedPreferences savePreferences = getActivity().getSharedPreferences("NoteHub", Context.MODE_PRIVATE);

        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.errorBody() == null) {
                    List<Note> notes = response.body();
                    for (int i = 0; i < notes.size(); i++) {

                        /*if (savePreferences.getString("TOKEN", null) == notes.get(i))*/
                            cards.add(new CardView(notes.get(i).getId(), notes.get(i).getTitle(), "School: " + notes.get(i).getUniversityName(),
                                    "Course: " + notes.get(i).getCourse(), "Name: " + notes.get(i).getAuthorUsername(), R.drawable.ic_favorite_star));
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
        recyclerView = view.findViewById(R.id.my_notes_recycler);
        // recyclerView.setHasFixedSize(true); // need to remove later IMPORTANT
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new NoteRecyclerViewAdapter(getActivity(), cards);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemCLickListener(new NoteRecyclerViewAdapter.onItemClickListener() {
            // Click on card changes text
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
                Intent intent = new Intent(getContext(), NoteActivity.class);
                startActivity(intent);
            }

            // Card clicked on gets sent to home
            @Override
            public void onFavoriteClick(int position) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.putExtra("Example item", cards.get(position));
                startActivity(intent);
            }

            @Override
            public void onCommentClick() {
            }

            @Override
            public void onReportClick() {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Report Note")
                        .setMessage("Do you want to report this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new MaterialAlertDialogBuilder(getContext())
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
                new MaterialAlertDialogBuilder(getContext())
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

    @Override
    public void insertCard(CardView cardView) {
        insertItem(cards.size(), cardView);
    }


    // Insert item in recycle view
    public void insertItem(int position, CardView card) {
        adapter.addItem(position, card);
        //adapter.notifyDataSetChanged(); // don't use this because will refresh the list and not show animation
    }

    // Remove item in recycle view
    public void removeItem(final int position) {
       final SharedPreferences savePreferences = getActivity().getSharedPreferences("NoteHub", Context.MODE_PRIVATE);

        Call<Note> call = apiService.deleteNote("Token " + savePreferences.getString("TOKEN", null), cards.get(position).getNoteId());

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

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(getContext())
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }

}
