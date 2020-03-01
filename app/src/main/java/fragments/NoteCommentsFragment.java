package fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.MainActivity;
import com.example.notehub.NoteActivity;
import com.example.notehub.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import adapters.CommentRecyclerViewAdapter;
import models.Comment;
import models.CommentReport;
import models.NoteReport;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteCommentsFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    CommentRecyclerViewAdapter recyclerViewAdapter;
    private List<Comment> comments;
    private TextInputEditText textBox;
    private MaterialButton button;
    private ApiInterface apiService;

    // Store data in list
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        comments = new ArrayList<>();
    }

    // Display xml page
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.note_comments_fragment, container, false);

        apiService = MainActivity.buildHTTP();

        textBox = view.findViewById(R.id.input_text_comment);
        button = view.findViewById(R.id.add_comment_button);

        createCommentsList();

        return view;
    }

    public void createCommentsList() {
        final NoteActivity noteActivity = (NoteActivity) getActivity();
        Call<List<Comment>> call = apiService.getNoteComments(getToken(), noteActivity.getNoteId());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.errorBody() == null)
                    comments = response.body();
                else {
                    showAlertMessage("All users comments for this note is unable to load.", "Ok");
                }
                recyclerView = view.findViewById(R.id.note_comments_recycler);

                // Defines the list displaying in recycler view and set layout to linear
                recyclerViewAdapter = new CommentRecyclerViewAdapter(getContext(), comments);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                // Set recycler view to use custom comment adapter
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.setOnItemClickListener(new CommentRecyclerViewAdapter.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }

                    @Override
                    public void onReportClick(int position) {
                        final Context context = getContext();
                        final int commentID = comments.get(position).getId();
                        final int noteID = comments.get(position).getNote();
                        new MaterialAlertDialogBuilder(context)
                                .setTitle("NoteReport Note")
                                .setMessage("Do you want to report this note?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Call<CommentReport> call = apiService.createNoteCommentReport(MainActivity.getToken(context), noteID, commentID);
                                        call.enqueue(new Callback<CommentReport>() {
                                            @Override
                                            public void onResponse(Call<CommentReport> call, Response<CommentReport> response) {
                                                if (response.errorBody() == null) {
                                                    new MaterialAlertDialogBuilder(context)
                                                            .setMessage("Comment successfully reported.")
                                                            .setPositiveButton("Done", null)
                                                            .show();
                                                } else {
                                                    new MaterialAlertDialogBuilder(context)
                                                            .setMessage("Already reported comment.")
                                                            .setPositiveButton("Done", null)
                                                            .show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<CommentReport> call, Throwable t) {

                                                new MaterialAlertDialogBuilder(context)
                                                        .setMessage("Failed to report comment.")
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
                    public void onDeleteClick(int position) {

                    }
                });
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Comment comment = new Comment(0, null, textBox.getText().toString());

                Call<Comment> call = apiService.uploadNoteComment(getToken(), noteActivity.getNoteId(), comment);
                call.enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.errorBody() == null) {
                            showAlertMessage("Comment was successfully added!", "Ok");
                            recyclerViewAdapter.addItem(response.body());
                        } else {
                            showAlertMessage("Please add text to your comment.", "Ok");
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {

                    }
                });
            }
        });
    }

    void insertItem(Comment comment) {
        recyclerViewAdapter.addItem(comment);
    }

    public void clear() {
        if(recyclerViewAdapter != null) {
            int size = comments.size();
            comments.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if(recyclerViewAdapter != null) {
            createCommentsList();
        }
    }


    private String getToken() {
        SharedPreferences savePreferences = getActivity().getSharedPreferences("NoteHub", Context.MODE_PRIVATE);
        return "Token " + savePreferences.getString("TOKEN", null);
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(getActivity())
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
