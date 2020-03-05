package fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import adapters.CommentRecyclerViewAdapter;
import models.Comment;
import models.CommentReport;
import models.NoteReport;
import models.User;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteCommentsFragment extends Fragment {

    View view;
    CommentRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private List<Comment> comments;
    private TextInputEditText textBox;
    private ImageView avatar;
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
        avatar = view.findViewById(R.id.note_user_img);
        avatar.setImageResource(R.drawable.blank);
        Call<User> call = apiService.getUser(MainActivity.getToken(getContext()));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.errorBody() == null && response.body().getAvatar() != null)
                    Picasso.with(getContext()).load(MainActivity.getBaseUrl() + response.body().getAvatar()).fit().placeholder(R.drawable.blank).centerCrop().noFade().into(avatar);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        textBox = view.findViewById(R.id.input_text_comment);
        button = view.findViewById(R.id.add_comment_button);

        createCommentsList();

        return view;
    }

    public void onDeleteClick(final int position, PopupMenu menu) {
        final Context context = getContext();
        final int noteID = comments.get(position).getNote();
        final int commentID = comments.get(position).getId();
        final String token = MainActivity.getToken(context);
        new MaterialAlertDialogBuilder(context)
                .setTitle("Delete Comment")
                .setMessage("Do you want to delete this comment?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Comment> call = apiService.deleteNoteComment(token, noteID, commentID);
                        call.enqueue(new Callback<Comment>() {
                            @Override
                            public void onResponse(Call<Comment> call, Response<Comment> response) {
                                if (response.errorBody() == null) {
                                    recyclerViewAdapter.removeItem(position);
                                    new MaterialAlertDialogBuilder(context)
                                            .setMessage("Successfully deleted note.")
                                            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (comments.isEmpty())
                                                        refresh();
                                                }
                                            })
                                            .show();
                                } else {
                                    new MaterialAlertDialogBuilder(context)
                                            .setMessage("Failed to delete note.")
                                            .setPositiveButton("Done", null)
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Comment> call, Throwable t) {
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

    public void onReportClick(int position, PopupMenu menu) {
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
                    public void onOverflowClick(final int position, final PopupMenu menu) {
                        if(!comments.get(position).isAuthor())
                            menu.getMenu().findItem(R.id.overflow_delete).setVisible(false);
                        else
                            menu.getMenu().findItem(R.id.overflow_report).setVisible(false);
                        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.overflow_delete:
                                        onDeleteClick(position, menu);
                                        return true;
                                    case R.id.overflow_report:
                                        onReportClick(position, menu);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        menu.show();
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

        textBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    button.callOnClick();
                    closeKeyboard();
                }
                return true;
            }
        });
    }

    void insertItem(Comment comment) {
        recyclerViewAdapter.addItem(comment);
    }

    public void clear() {
        if (recyclerViewAdapter != null) {
            int size = comments.size();
            comments.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if (recyclerViewAdapter != null) {
            createCommentsList();
        }
    }

    // Close keyboard when open
    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
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
