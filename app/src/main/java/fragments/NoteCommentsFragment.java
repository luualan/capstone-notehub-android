package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.R;

import java.util.ArrayList;
import java.util.List;

import adapters.CommentRecyclerViewAdapter;
import models.Comment;

public class NoteCommentsFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private List<Comment> comments;

    // Constructor
    public NoteCommentsFragment() {

    }

    // Store data in list
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        comments = new ArrayList<>();
        comments.add(new Comment("Mom", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks." +
                "blah, blah bla, blah bldffadadadadadadaddsadadadadaadaaddsdadadadadadadadadadadadadadadadadadadadaadadada.", R.drawable.anime));
        comments.add(new Comment("Dudedsadasdadadadadadadadadadadadadadadadadadaad", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));
        comments.add(new Comment("Dude", "This project is taking forever holy sheep shit and now I got to write an long as description to check whether the padding looks good. This sucks.", R.drawable.anime));

    }

    // Display xml page
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.note_comments_fragment, container, false);
        recyclerView = view.findViewById(R.id.note_comments_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        CommentRecyclerViewAdapter recyclerViewAdapter = new CommentRecyclerViewAdapter(getContext(),comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
}
