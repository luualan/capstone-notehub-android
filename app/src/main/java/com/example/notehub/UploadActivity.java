package com.example.notehub;

//import android.R.layout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
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
    View view;
    private List<String> filePaths;
    private List<File> files;
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
    private TextView showFileName;
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
        files = new ArrayList<File>();
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
        showFileName = view.findViewById(R.id.show_file_path);

        Call<List<University>> call = apiService.getUniversities(null, null, null);
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                if (response.errorBody() == null) {
                    universities = response.body();
                    Toast.makeText(getActivity(), universities.get(1).toString(), Toast.LENGTH_SHORT).show();
                    arrayAdapter = new ArrayAdapter<University>(getActivity(), android.R.layout.simple_list_item_1, universities.toArray(new University[universities.size()]));
                    schoolDropDown.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(getActivity(), "Could not load universities.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(getActivity(), "this is an actual network failure :frowning: inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                    // logging probably not necessary
                }
                else {
                    Toast.makeText(getActivity(), "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                    // todo log to some central bug tracking service
                }
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
                verifyStoragePermissions(getActivity());
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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        else {
            uploadFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadFile();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    // get the uri file path
    public String getRealPathFromURI(Uri contentUri) {
        if (!contentUri.getAuthority().equals("media")) {
            return contentUri.getPath();
        }

        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    // Method is called when we get file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if user image exist and user picks it
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            // contains the uri of the image picked; will later upload to server
            imageURI = data.getData();

            Log.e("34", imageURI.getAuthority());

            //pass it like this
            File file = new File(getRealPathFromURI(imageURI));
            files.add(file);

            String concat = "";

            for (File path : files)
                concat += path.getName() + "\n";

            Toast.makeText(getActivity(), file.toString(), Toast.LENGTH_SHORT).show();

            showFileName.setText(concat);
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

                    // call note
                    callNote.enqueue(new Callback<Note>() {
                        @Override
                        public void onResponse(Call<Note> call, Response<Note> response) {
                            if (response.errorBody() == null) {
                                // set the note's id
                                note.setId(response.body().getId());

                                for(int i = 0; i < files.size(); i++) {
                                    Toast.makeText(getActivity(), Integer.toString(note.getId()), Toast.LENGTH_SHORT).show();
                                    // set file
                                    RequestBody requestfile = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(imageURI)), files.get(i));
//                                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestfile);
//                                RequestBody index = RequestBody.create(okhttp3.MultipartBody.FORM, "1");

                                    MultipartBody.Builder builder = new MultipartBody.Builder();
                                    builder.setType(MultipartBody.FORM);

                                    builder.addFormDataPart("index", Integer.toString(i));
                                    builder.addFormDataPart("file", files.get(i).getName(), requestfile);

                                    Call<NoteFile> callNoteFile = apiService.uploadNoteFile("Token " + savePreferences.getString("TOKEN", null), note.getId(), builder.build());

                                    // call note file
                                    callNoteFile.enqueue(new Callback<NoteFile>() {
                                        @Override
                                        public void onResponse(Call<NoteFile> call, Response<NoteFile> response) {
                                            if (response.errorBody() == null) {
                                                Toast.makeText(getActivity(), "Upload successful!", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Toast.makeText(getActivity(), "Upload failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<NoteFile> call, Throwable t) {
                                            if (t instanceof IOException) {
                                                t.printStackTrace();
                                                Toast.makeText(getActivity(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                // logging probably not necessary
                                            } else {
                                                Toast.makeText(getActivity(), "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
                                                // todo log to some central bug tracking service
                                            }
                                        }
                                    });
                                }
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






    }
}
