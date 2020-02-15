package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.MainActivity;
import com.example.notehub.NoteActivity;
import com.example.notehub.R;
import com.example.notehub.UploadActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import adapters.NoteRecyclerViewAdapter;
import models.CardView;
import models.Favorite;
import models.Note;
import models.Rating;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment implements UploadActivity.CardHolder {
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
        view = inflater.inflate(R.layout.favorite_fragment, container, false);
        createCardsList();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.bottom_navigation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        //createCardsList();
    }

    private String getToken() {
        return MainActivity.getToken(getActivity());
    }

    public void createCardsList() {
        // Create Cards
        cards = new ArrayList<>();
        Call<List<Note>> call = apiService.getUserFavorites(getToken());

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
        recyclerView = view.findViewById(R.id.favorite_recycler);
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
                Intent intent = new Intent(getActivity(), NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle())
                        .putExtra("startComment", false);
                startActivity(intent);
            }

            // Card clicked on gets sent to home
            @Override
            public void onFavoriteClick(final int position) {
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
                                            cards.remove(position);
                                            adapter.notifyItemRemoved(position);
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
                Intent intent = new Intent(getActivity(), NoteActivity.class)
                        .putExtra("noteID", cards.get(position).getNoteId())
                        .putExtra("noteTitle", cards.get(position).getTitle())
                        .putExtra("startComment", true);
                startActivity(intent);
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

    @Override
    public void insertCard(CardView cardView) {
        insertItem(cards.size(), cardView);
    }


    // Insert item in recycle view
    public void insertItem(int position, CardView card) {
        adapter.addItem(card);
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
