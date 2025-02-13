package bdisfer1410.controldepresencia.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface AuthService {
    @POST("/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);
}
