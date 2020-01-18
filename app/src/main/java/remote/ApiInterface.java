package remote;

import java.util.List;

import models.CreateUser;
import models.Login;
import models.Token;
import models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("api/user/")
    Call<List<User>> getUsers();

    @POST("api/user/")
    Call<User> createUser(@Body CreateUser user);

    @POST("api/login/")
    Call<Token> loginUser(@Body Login login);
}
