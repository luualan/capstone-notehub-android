package fragments;

import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapters.CommentRecyclerViewAdapter;
import adapters.NoteFileRecyclerViewAdapter;
import models.Comment;
import models.NoteFile;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteFilesFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private List<NoteFile> noteFiles;
    private ApiInterface apiService;

    // Constructor
    public NoteFilesFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // Log.e("dssadaddadas", Integer.toString(noteActivity.getNoteId()));
        noteFiles = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.note_files_fragment, container, false);
        apiService = MainActivity.buildHTTP();

        NoteActivity noteActivity = (NoteActivity) getActivity();

        Call<List<NoteFile>> call = apiService.getNoteFiles(noteActivity.getNoteId());
        call.enqueue(new Callback<List<NoteFile>>() {
            @Override
            public void onResponse(Call<List<NoteFile>> call, Response<List<NoteFile>> response) {
                if(response.errorBody() == null) {
                    noteFiles = response.body();
                    buildRecyclerView();
                }
                else {
                }
            }

            @Override
            public void onFailure(Call<List<NoteFile>> call, Throwable t) {

            }
        });

        return view;
    }

    public void buildRecyclerView() {
        recyclerView = view.findViewById(R.id.note_files_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        NoteFileRecyclerViewAdapter recyclerViewAdapter = new NoteFileRecyclerViewAdapter(getActivity(),noteFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(recyclerViewAdapter);
    }

}
