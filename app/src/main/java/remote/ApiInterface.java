package remote;

import java.util.List;

import models.Comment;
import models.CreateUser;
import models.Favorite;
import models.Group;
import models.Invitation;
import models.Login;
import models.Membership;
import models.Note;
import models.NoteFile;
import models.Rating;
import models.Token;
import models.University;
import models.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {
    @GET("api/user/")
    Call<User> getUser(@Header("Authorization") String authKey);

    @GET("api/user/notes/")
    Call<List<Note>> getUserFiles(@Header("Authorization") String authKey);

    @GET("api/user/groups/")
    Call<List<Group>> getUserGroups(@Header("Authorization") String authKey);

    @GET("api/user/invitations/")
    Call<List<Invitation>> getUserInvitations(@Header("Authorization") String authKey);

    @GET("api/user/favorites/")
    Call<List<Note>> getUserFavorites(@Header("Authorization") String authKey);

    @GET("api/users/")
    Call<List<User>> getUsers();

    @POST("api/users/")
    Call<User> createUser(@Body CreateUser user);

    @POST("api/login/")
    Call<Token> loginUser(@Body Login login);

    @GET("api/notes/")
    Call<List<Note>> getNotes(@Query("username") String username, @Query("title") String title, @Query("university") String university,
                              @Query("course") String course, @Query("order_by") String order_by);

    @POST("api/notes/")
    public Call<Note> uploadNote(@Header("Authorization") String authKey, @Body Note note);

    @GET("api/notes/{id}/")
    public Call<Note> getNote(@Path("id") int id);

    @DELETE("api/notes/{id}/")
    public Call<Note> deleteNote(@Header("Authorization") String authKey, @Path("id") int id);

    @GET("api/notes/{id}/files/")
    public Call<List<NoteFile>> getNoteFiles(@Path("id") int id);

    @POST("api/notes/{id}/files/")
    public Call<NoteFile> uploadNoteFile(@Header("Authorization") String authKey, @Path("id") int id, @Body RequestBody file);

    @GET("api/notes/{id}/files/{index}/")
    public Call<NoteFile> getNoteFile(@Path("id") int id, @Path("index") int index);

    @GET("api/notes/{id}/ratings/")
    public Call<List<Rating>> getNoteRatings(@Path("id") int id);

    @POST("api/notes/{id}/ratings/")
    public Call<Rating> uploadNoteRating(@Header("Authorization") String authKey, @Path("id") int id, @Body Rating rating);

    @GET("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> getNoteRating(@Path("noteId") int noteId, @Path("id") int id);

    @PATCH("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> updateNoteRating(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id, @Body Rating rating);

    @DELETE("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> deleteNoteRating(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{id}/comments/")
    public Call<List<Comment>> getNoteComments(@Path("id") int id);

    @POST("api/notes/{id}/comments/")
    public Call<Comment> uploadNoteComment(@Header("Authorization") String authKey, @Path("id") int id, @Body Comment comment);

    @GET("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> getNoteComment(@Path("noteId") int noteId, @Path("id") int id);

    @PATCH("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> updateNoteComment(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id, @Body Comment comment);

    @DELETE("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> deleteNoteComment(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{id}/favorites/")
    public Call<List<Favorite>> getNoteFavorites(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/favorites/")
    public Call<Favorite> uploadNoteFavorite(@Header("Authorization") String authKey, @Path("id") int id, @Body Favorite favorite);

    @GET("api/notes/{noteId}/favorites/{id}/")
    public Call<Favorite> getNoteFavorite(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @DELETE("api/notes/{noteId}/favorites/{id}/")
    public Call<Favorite> deleteNoteFavorite(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @POST("api/groups/")
    public Call<Note> createGroup(@Header("Authorization") String authKey, @Body Group group);

    @GET("api/groups/{id}/")
    public Call<Group> getGroup(@Header("Authorization") String authKey, @Path("id") int id);

    @PATCH("api/groups/{id}/")
    public Call<Group> updateGroup(@Header("Authorization") String authKey, @Path("id") int id, @Body Group group);

    @DELETE("api/groups/{id}/")
    public Call<Group> deleteGroup(@Header("Authorization") String authKey, @Path("id") int id);

    @GET("api/groups/{groupId}/notes/")
    public Call<List<Note>> getGroupNotes(@Header("Authorization") String authKey, @Path("groupId") int groupId,
                                          @Query("username") String username, @Query("title") String title, @Query("university") String university,
                                          @Query("course") String course, @Query("order_by") String order_by);

    @POST("api/groups/{groupId}/notes/")
    public Call<Note> uploadGroupNote(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Body Note note);

    @GET("api/groups/{groupId}/memberships/")
    public Call<List<Membership>> getGroupMemberships(@Header("Authorization") String authKey, @Path("groupId") int groupId);

    @POST("api/groups/{groupId}/memberships/")
    public Call<Membership> uploadGroupMembership(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Body Membership membership);

    @GET("api/groups/{groupId}/memberships/{id}/")
    public Call<Membership> getGroupMembership(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Path("id") int id);

    @DELETE("api/groups/{groupId}/memberships/{id}/")
    public Call<Membership> deleteGroupMembership(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Path("id") int id);

    @GET("api/groups/{groupId}/invitations/")
    public Call<List<Invitation>> getGroupInvitations(@Header("Authorization") String authKey, @Path("groupId") int groupId);

    @POST("api/groups/{groupId}/invitations/")
    public Call<Invitation> uploadGroupInvitation(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Body Invitation invitation);

    @GET("api/groups/{groupId}/invitatons/{id}/")
    public Call<Membership> getGroupInvitation(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Path("id") int id);

    @DELETE("api/groups/{groupId}/memberships/{id}/")
    public Call<Membership> deleteGroupInvitation(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Path("id") int id);

    @GET("api/universities/")
    Call<List<University>> getUniversities(@Query("starts_with") String starts_with, @Query("contains") String contains, @Query("order_by") String order_by);

    @GET("api/universities/{name}/")
    Call<University> getUniversity(@Path("name") String name);
}
