package remote;

import java.util.List;
import java.util.Map;

import models.Comment;
import models.CommentReport;
import models.CreateUser;
import models.Favorite;
import models.Group;
import models.Invitation;
import models.Login;
import models.Membership;
import models.Note;
import models.NoteFile;
import models.NoteReport;
import models.Rating;
import models.Token;
import models.University;
import models.UpdatePasswordRequest;
import models.UpdatePasswordResponse;
import models.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("api/user/")
    Call<User> getUser(@Header("Authorization") String authKey);

    @GET("api/user/notes/")
    Call<List<Note>> getUserNotes(@Header("Authorization") String authKey);

    @GET("api/user/groups/")
    Call<List<Group>> getUserGroups(@Header("Authorization") String authKey);

    @GET("api/user/invitations/")
    Call<List<Invitation>> getUserInvitations(@Header("Authorization") String authKey);

    @GET("api/user/favorites/")
    Call<List<Note>> getUserFavorites(@Header("Authorization") String authKey);

    @PUT("api/user/change_password/")
    Call<UpdatePasswordResponse> updatePassword(@Header("Authorization") String authKey, @Body UpdatePasswordRequest request);

    @GET("api/users/")
    Call<List<User>> getUsers(@Query("username") String username);

    @POST("api/users/")
    Call<User> createUser(@Body CreateUser user);

    @POST("api/login/")
    Call<Token> loginUser(@Body Login login);

    @GET("api/notes/")
    Call<List<Note>> getNotes(@Header("Authorization") String authKey, @Query("username") String username, @Query("title") String title, @Query("university") String university,
                              @Query("course") String course, @Query("order_by") String order_by);

    @POST("api/notes/")
    public Call<Note> uploadNote(@Header("Authorization") String authKey, @Body Note note);

    @GET("api/notes/{id}/")
    public Call<Note> getNote(@Header("Authorization") String authKey, @Path("id") int id);

    @DELETE("api/notes/{id}/")
    public Call<Note> deleteNote(@Header("Authorization") String authKey, @Path("id") int id);

    @GET("api/notes/{id}/files/")
    public Call<List<NoteFile>> getNoteFiles(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/files/")
    public Call<NoteFile> uploadNoteFile(@Header("Authorization") String authKey, @Path("id") int id, @Body RequestBody file);

    @GET("api/notes/{id}/files/{index}/")
    public Call<NoteFile> getNoteFile(@Header("Authorization") String authKey, @Path("id") int id, @Path("index") int index);

    @GET("api/notes/{id}/ratings/")
    public Call<List<Rating>> getNoteRatings(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/ratings/")
    public Call<Rating> uploadNoteRating(@Header("Authorization") String authKey, @Path("id") int id, @Body Rating rating);

    @GET("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> getNoteRating(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @PATCH("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> updateNoteRating(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id, @Body Rating rating);

    @DELETE("api/notes/{noteId}/ratings/{id}/")
    public Call<Rating> deleteNoteRating(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{id}/comments/")
    public Call<List<Comment>> getNoteComments(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/comments/")
    public Call<Comment> uploadNoteComment(@Header("Authorization") String authKey, @Path("id") int id, @Body Comment comment);

    @GET("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> getNoteComment(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @PATCH("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> updateNoteComment(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id, @Body Comment comment);

    @DELETE("api/notes/{noteId}/comments/{id}/")
    public Call<Comment> deleteNoteComment(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{noteId}/comments/{id}/report/")
    public Call<List<CommentReport>> getNoteCommentReport(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @POST("api/notes/{noteId}/comments/{id}/report/")
    public Call<CommentReport> createNoteCommentReport(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{id}/favorites/")
    public Call<List<Favorite>> getNoteFavorites(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/favorites/")
    public Call<Favorite> uploadNoteFavorite(@Header("Authorization") String authKey, @Path("id") int id, @Body Favorite favorite);

    @GET("api/notes/{noteId}/favorites/{id}/")
    public Call<Favorite> getNoteFavorite(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @DELETE("api/notes/{noteId}/favorites/{id}/")
    public Call<Favorite> deleteNoteFavorite(@Header("Authorization") String authKey, @Path("noteId") int noteId, @Path("id") int id);

    @GET("api/notes/{id}/report/")
    public Call<List<NoteReport>> getNoteReport(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/notes/{id}/report/")
    public Call<NoteReport> createNoteReport(@Header("Authorization") String authKey, @Path("id") int id);

    @POST("api/groups/")
    public Call<Group> createGroup(@Header("Authorization") String authKey, @Body Group group);

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
    public Call<Invitation> deleteGroupInvitation(@Header("Authorization") String authKey, @Path("groupId") int groupId, @Path("id") int id);

    @GET("api/universities/")
    Call<List<University>> getUniversities(@Query("starts_with") String starts_with, @Query("contains") String contains, @Query("order_by") String order_by);

    @GET("api/universities/{name}/")
    Call<University> getUniversity(@Path("name") String name);
}
