package fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notehub.GroupMembersActivity;
import com.example.notehub.MainActivity;
import com.example.notehub.NoteActivity;
import com.example.notehub.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
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
    private NoteFileRecyclerViewAdapter recyclerViewAdapter;
    private List<NoteFile> noteFiles;
    private ApiInterface apiService;
    private String imageUrl;

    // Constructor
    public NoteFilesFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteFiles = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.note_files_fragment, container, false);
        apiService = MainActivity.buildHTTP();

        createFilesList();

        return view;
    }

    public void createFilesList() {
        final NoteActivity noteActivity = (NoteActivity) getActivity();

        Call<List<NoteFile>> call = apiService.getNoteFiles(MainActivity.getToken(noteActivity), noteActivity.getNoteId());
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
    }

    public void buildRecyclerView() {
        recyclerView = view.findViewById(R.id.note_files_recycler);

        // Defines the list displaying in recycler view and set layout to linear
        recyclerViewAdapter = new NoteFileRecyclerViewAdapter(getActivity(),noteFiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set recycler view to use custom comment adapter
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new NoteFileRecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onDownloadClick(int position) {
                imageUrl = noteFiles.get(position).getFile();
                verifyStoragePermissions();
            }
        });
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
            download();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty. Permission was granted.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download();
                }
                // permission denied
                else {

                }
                return;
            }
        }
    }


    public void download() {
        Picasso.with(getContext())
                .load(imageUrl)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {
                                  String root = Environment.getExternalStorageDirectory().toString();

                                  File myDir = new File(root + "/myDirectory");

                                  if (!myDir.exists()) {
                                      myDir.mkdirs();
                                  }

                                  String name = new Date().toString() + ".jpg";
                                  myDir = new File(myDir, name);
                                  FileOutputStream out = new FileOutputStream(myDir);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                  out.flush();
                                  out.close();
                                  showAlertMessage("Download successful.", "Done");
                              } catch(Exception e){
                                  // some action
                              }
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                              showAlertMessage("Download failed.", "Done");
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );
    }

    public void clear() {
        if(recyclerViewAdapter != null) {
            int size = noteFiles.size();
            noteFiles.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, size);
        }
    }

    public void refresh() {
        if(recyclerViewAdapter != null) {
            createFilesList();
        }
    }

    private void showAlertMessage(String message, String buttonText) {
        new MaterialAlertDialogBuilder(getActivity())
                .setMessage(message)
                .setPositiveButton(buttonText, null)
                .show();
    }
}
