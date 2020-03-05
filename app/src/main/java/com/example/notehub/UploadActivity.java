package com.example.notehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import adapters.UploadNoteFileRecyclerViewAdapter;
import models.CardView;
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

public class UploadActivity extends DialogFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private View view;
    private Note note;
    private List<University> universities;
    private ArrayAdapter<University> arrayAdapter;
    private University container;
    private Button buttonChooseImage;
    private Button buttonUpload;
    private ImageView imageView;
    private ProgressBar progessBar;
    private AutoCompleteTextView schoolDropDown;
    private TextView showFileName;
    private EditText title;
    private EditText course;
    private Uri imageURI;
    private List<Uri> uris;
    private ApiInterface apiService;
    private Call call;
    private ImageButton xIcon;
    private ImageButton refreshIcon;
    private CardHolder cardHolder;
    private int groupID;
    private RelativeLayout toolbar;
    private AnimationDrawable animationToolBar;
    private RecyclerView noteFilesRecyclerView;
    private UploadNoteFileRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<NoteFile> noteFiles = new ArrayList<>();
    private TextView uploadFilesText;

    public interface CardHolder {
        void insertCard(CardView card);
    }

    static UploadActivity newInstance() {
        return new UploadActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
        uris = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CardHolder) {
            cardHolder = (CardHolder) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (call != null) {
            call.cancel();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_upload, container, false);

        // Tool Bar Background Animation
        toolbar = view.findViewById(R.id.upload_tool_bar_layout);
        animationToolBar = (AnimationDrawable) toolbar.getBackground();
        animationToolBar.setEnterFadeDuration(5000);
        animationToolBar.setExitFadeDuration(3000);
        animationToolBar.start();

        apiService = MainActivity.buildHTTP();

        buttonChooseImage = view.findViewById(R.id.choose_image_button);
        buttonUpload = view.findViewById(R.id.upload_image_button);
        schoolDropDown = view.findViewById(R.id.upload_school);
        title = view.findViewById(R.id.upload_title);
        course = view.findViewById(R.id.upload_course);
        uploadFilesText = view.findViewById(R.id.upload_files_text);

        xIcon = view.findViewById(R.id.fullscreen_dialog_close);
        refreshIcon = view.findViewById(R.id.fullscreen_dialog_refresh);

        if(getArguments() == null)
            groupID = -1;
        else
            groupID = getArguments().getInt("groupID");

        // Get list of Universities from API
        call = apiService.getUniversities(null, null, null);
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                if (response.errorBody() == null) {
                    universities = response.body();
                    // Toast.makeText(getContext(), universities.get(1).toString(), Toast.LENGTH_SHORT).show();

                    arrayAdapter = new ArrayAdapter<University>(getContext(), android.R.layout.simple_list_item_1, universities.toArray(new University[universities.size()]));
                    schoolDropDown.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(getContext(), "Could not load universities.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {

            }
        });

        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions();
            }
        });

        xIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.getText().clear();
                schoolDropDown.getText().clear();
                course.getText().clear();
                noteFiles.clear();
                uris.clear();
                uploadFilesText.setVisibility(View.GONE);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(UploadActivity.this);
                ft.attach(UploadActivity.this);
                ft.commit();
            }
        });

        // RecyclerView
        buildRecyclerView();
        return view;
    }

    public void buildRecyclerView() {
        noteFilesRecyclerView = view.findViewById(R.id.upload_files_recycler_view);
        recyclerViewAdapter = new UploadNoteFileRecyclerViewAdapter(getActivity(), noteFiles);
        noteFilesRecyclerView.setAdapter(recyclerViewAdapter);
        // Horizontal layout
        noteFilesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerViewAdapter.setOnItemClickListener(new UploadNoteFileRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                recyclerViewAdapter.removeItem(position);
                uris.remove(position);

                if(uris.isEmpty())
                    uploadFilesText.setVisibility(View.GONE);
            }
        });
    }

    private void openFileChooser() {
        String[] mimeTypes = {"image/jpeg", "image/png"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/jpeg|image/png")
                .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Checks if the app has permission to write to device storage
    // If the app does not has permission then the user will be prompted to grant permissions
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            uploadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty. Permission was granted.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadFile();
                }
                // permission denied
                else {

                }
                return;
            }
        }
    }

    // Method is called when user selects a file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if user image exist and user picks it
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            // contains the uri of the image picked; will later upload to server
            imageURI = data.getData();

            // Log.e("34", imageURI.getAuthority());

            //pass it like this
            //File file = new File(getRealPathFromURI(imageURI));
            //files.add(file);

            uris.add(imageURI);

            NoteFile noteFile = new NoteFile();
            noteFile.setFile(imageURI.toString());
            recyclerViewAdapter.addItem(noteFile);

            uploadFilesText.setVisibility(View.VISIBLE);

        /*    String concat = "";

            // Print file names
            for (Uri path : uris)
                concat += queryName(getContext().getContentResolver(), path) + "\n";

            // Toast.makeText(getContext(), file.toString(), Toast.LENGTH_SHORT).show();

            showFileName.setText(concat);*/
           // Picasso.with(view.getContext()).load(imageURI).into(imageView);
        }
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor = resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private File createTempFile(String name) {
        File file = null;
        try {
            file = File.createTempFile(name, null, getContext().getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void uploadFile() {
        note = new Note();
        Call<University> callUniversity = apiService.getUniversity(schoolDropDown.getText().toString());

        final boolean titleCheckEmpty = title.getText().toString().trim().isEmpty();
        final boolean schoolCheckEmpty = schoolDropDown.getText().toString().trim().isEmpty();
        final boolean courseCheckEmpty = course.getText().toString().trim().isEmpty();

        if (titleCheckEmpty)
            title.setError("Please fill out this field.");

        if (schoolCheckEmpty)
            schoolDropDown.setError("Please fill out this field.");

        if (courseCheckEmpty)
            course.setError("Please fill out this field.");

        callUniversity.enqueue(new Callback<University>() {
            @Override
            public void onResponse(Call<University> call, Response<University> response) {
                if (response.errorBody() == null) {
                    // Toast.makeText(getContext(), Integer.toString(response.body().getId()), Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), response.body().getName(), Toast.LENGTH_SHORT).show();

                    container = response.body();
                    note.setUniversity(container.getId());
                    note.setTitle(title.getText().toString());
                    note.setCourse(course.getText().toString());

                    final Call<Note> callNote;
                    if(groupID == -1)
                        callNote = apiService.uploadNote(MainActivity.getToken(getActivity()), note);
                    else
                        callNote = apiService.uploadGroupNote(MainActivity.getToken(getActivity()), groupID, note);

                    // call note
                    callNote.enqueue(new Callback<Note>() {
                        @Override
                        public void onResponse(Call<Note> call, Response<Note> response) {
                            if (response.errorBody() == null) {
                                // set the note's id
                                note = response.body();

                                // card holder
                                CardView cardView;

                                if(groupID == -1) {
                                    cardView = new CardView(note.getId(), note.getTitle(), note.getUniversityName(), note.getCourse(), note.getAuthorUsername(),
                                            note.getAvgRating(), note.isAuthor(), note.isModerator(), R.drawable.ic_favorite_star, "Type: Public");
                                }
                                else {
                                    cardView = new CardView(note.getId(), note.getTitle(), note.getUniversityName(), note.getCourse(), note.getAuthorUsername(),
                                            note.getAvgRating(), note.isAuthor(), note.isModerator(), R.drawable.ic_favorite_star, "Type: Private");
                                }

                                if (cardHolder != null)
                                    cardHolder.insertCard(cardView);

                                for (int i = 0; i < uris.size(); i++) {
                                    // set file
                                    final File tempFile = createTempFile("temp file");

                                    try {
                                        InputStream in = getContext().getContentResolver().openInputStream(uris.get(i));
                                        OutputStream out = new FileOutputStream(tempFile);
                                        byte[] buf = new byte[1024];
                                        int len;
                                        while ((len = in.read(buf)) > 0) {
                                            out.write(buf, 0, len);
                                        }
                                        out.close();
                                        in.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    RequestBody requestFile = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(uris.get(i))), tempFile);

                                    MultipartBody.Builder builder = new MultipartBody.Builder();
                                    builder.setType(MultipartBody.FORM);

                                    builder.addFormDataPart("index", Integer.toString(i));
                                    builder.addFormDataPart("file", queryName(getContext().getContentResolver(), uris.get(i)), requestFile);

                                    Call<NoteFile> callNoteFile = apiService.uploadNoteFile(MainActivity.getToken(getActivity()), note.getId(), builder.build());

                                    // call note file
                                    callNoteFile.enqueue(new Callback<NoteFile>() {
                                        @Override
                                        public void onResponse(Call<NoteFile> call, Response<NoteFile> response) {
                                            if (response.errorBody() == null) {

                                            } else {
                                                showAlertMessage("Upload failed.");
                                                // Toast.makeText(getContext(), "Upload failed.", Toast.LENGTH_SHORT).show();
                                            }
                                            tempFile.delete();
                                        }

                                        @Override
                                        public void onFailure(Call<NoteFile> call, Throwable t) {
                                            tempFile.delete();
                                        }
                                    });
                                }
                                // showAlertMessage("Upload successful!");

                                new MaterialAlertDialogBuilder(getContext())
                                        .setMessage("Upload successful!")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(groupID == -1)
                                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                                else
                                                    dismiss();
                                            }
                                        })
                                        .show();
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Note> call, Throwable t) {

                        }
                    });

                } else {
                    if (!titleCheckEmpty && !schoolCheckEmpty && !courseCheckEmpty)
                        showAlertMessage("Enter a valid university.");
                }
            }

            @Override
            public void onFailure(Call<University> call, Throwable t) {

            }
        });
    }

    private void showAlertMessage(String message) {
        new MaterialAlertDialogBuilder(getContext())
                .setMessage(message)
                .setPositiveButton("Ok", null)
                .show();
    }

  /*  private void showToastMessage(String text) {
        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }*/
}
