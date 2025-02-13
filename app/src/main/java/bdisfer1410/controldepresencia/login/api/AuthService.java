package bdisfer1410.controldepresencia.login.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);
}
