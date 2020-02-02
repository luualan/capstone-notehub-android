package remote;

import java.util.List;

import models.CreateUser;
import models.Login;
import models.Note;
import models.NoteFile;
import models.Token;
import models.University;
import models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET("api/user/")
    Call<List<User>> getUsers();

    @POST("api/user/")
    Call<User> createUser(@Body CreateUser user);

    @POST("api/login/")
    Call<Token> loginUser(@Body Login login);

    @GET("api/notes/")
    Call<List<Note>> getNotes(@Query("username") String username, @Query("title") String title, @Query("university") int university,
                              @Query("course") String course, @Query("order_by") String order_by);

    @POST("api/notes/")
    public Call<Note> uploadNote(@Header("Authorization") String authKey, @Body Note note);

    // api/notes/<int:pk>/
    @GET("api/notes/{id}/")
    public Call<Note> getNote(@Path("id") int id);

    // api/notes/<int:note_id>/files
    @GET("api/notes/{id}/files/")
    public Call<List<NoteFile>> getNoteFiles(@Path("id") int id);

    // api/notes/<int:note_id>/files/<int:index>
    @GET("api/notes/{id}/files/{index}/")
    public Call<NoteFile> getNoteFile(@Path("id") int id, @Path("index") int index);

    // api/notes/<int:note_id>/files
    @POST("api/notes/{id}/files/")
    public Call<NoteFile> uploadNoteFile(@Header("Authorization") String authKey, @Path("id") int id, @Body NoteFile noteFile);

    @GET("api/universities")
    Call<List<University>> getUniversities(@Query("starts_with") String starts_with, @Query("contains") String contains, @Query("order_by") String order_by);

    // api/universities/<str:name>/
    @GET("api/universities/{name}/")
    Call<University> getUniversity(@Path("name") String name);
}
