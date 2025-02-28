package bdisfer1410.controldepresencia.api;

import bdisfer1410.controldepresencia.api.clock.action.ClockActionResponse;
import bdisfer1410.controldepresencia.api.auth.AuthRequest;
import bdisfer1410.controldepresencia.api.auth.AuthResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);
    @GET("/fichar")
    Call<ClockActionResponse> getClockAction(@Header("Authorization") String token);
}
