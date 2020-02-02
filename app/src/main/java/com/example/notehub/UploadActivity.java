package com.example.notehub;

//import android.R.layout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import models.Note;
import models.NoteFile;
import models.University;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import remote.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class UploadActivity extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    File file;
    View view;
    private List<String> filePaths;

    private Note note;
    private List<University> universities;
    private ArrayAdapter<University> arrayAdapter;
    private University container;

    private Button buttonChooseImage;
    private Button buttonUpload;
    private Button textViewShowUploads;
    private EditText editTextFilename;
    private ImageView imageView;
    private ProgressBar progessBar;
    private AutoCompleteTextView schoolDropDown;
    private TextView showFilePath;
    private EditText title;
    private EditText course;

    private Uri imageURI;
    ApiInterface apiService;

    static UploadActivity newInstance() {
        return new UploadActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_upload, container, false);

        apiService = MainActivity.buildHTTP();

        filePaths = new ArrayList<String>();

        buttonChooseImage = view.findViewById(R.id.choose_image_button);
        buttonUpload = view.findViewById(R.id.upload_image_button);
        //textViewShowUploads = view.findViewById(R.id.show_image_button);
        imageView = view.findViewById(R.id.image_view);
        progessBar = view.findViewById(R.id.progress_bar);
        schoolDropDown = view.findViewById(R.id.school);
        title = view.findViewById(R.id.note_title);
        course = view.findViewById(R.id.course);

        Call<List<University>> call = apiService.getUniversities(null, null, null);
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                if (response.errorBody() == null) {
                    universities = response.body();
                    arrayAdapter = new ArrayAdapter<University>(getActivity(), android.R.layout.simple_list_item_1, universities.toArray(new University[universities.size()]));
                    schoolDropDown.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(getActivity(), "Could not load universities.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {

            }
        });

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Eror", "dd");
                openFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        //  textViewShowUploads.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //   public void onClick(View v) {

        //    }
        // });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Method is called when we get file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if user image exist and user picks it
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            // contains the uri of the image picked; will later upload to server
            imageURI = data.getData();

            //pass it like this
            file = new File(imageURI.getPath());

            String concat = "";
            filePaths.add(file.toString());

            for (String path : filePaths)
                concat += path + "\n";

            // showFilePath.setText(concat);
            // Toast.makeText(UploadActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
            Picasso.with(view.getContext()).load(imageURI).into(imageView);
        }
    }

    private void uploadFile() {
        note = new Note();

        final SharedPreferences savePreferences = getActivity().getSharedPreferences("NoteHub", Context.MODE_PRIVATE);

        Call<University> callUniversity = apiService.getUniversity(schoolDropDown.getText().toString());
        callUniversity.enqueue(new Callback<University>() {
            @Override
            public void onResponse(Call<University> call, Response<University> response) {
                if (response.errorBody() == null) {
                    Toast.makeText(getActivity(), Integer.toString(response.body().getId()), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), response.body().getName(), Toast.LENGTH_SHORT).show();
                    container = response.body();
                    note.setUniversity(container.getId());
                    note.setTitle(title.getText().toString());
                    note.setCourse(course.getText().toString());

                    Call<Note> callNote;
                    callNote = apiService.uploadNote("Token " + savePreferences.getString("TOKEN", null), note);
                    Toast.makeText(getActivity(), "Upload successful!", Toast.LENGTH_SHORT).show();

                    callNote.enqueue(new Callback<Note>() {
                        @Override
                        public void onResponse(Call<Note> call, Response<Note> response) {
                            if (response.errorBody() == null) {
                                note.setId(response.body().getId());
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Note> call, Throwable t) {

                        }
                    });

                } else {

                }
            }

            @Override
            public void onFailure(Call<University> call, Throwable t) {

            }
        });

        Call<NoteFile> callNoteFile;

        //for(String path: filePaths) {
        //   callNoteFile = apiService.uploadNoteFile( savePreferences.getString("TOKEN",null), path, null);
        //   }


    }
}
