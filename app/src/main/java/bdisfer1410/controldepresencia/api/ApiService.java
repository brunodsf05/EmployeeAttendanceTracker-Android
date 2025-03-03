package bdisfer1410.controldepresencia.api;

import bdisfer1410.controldepresencia.api.clock.ClockResponse;
import bdisfer1410.controldepresencia.api.auth.AuthRequest;
import bdisfer1410.controldepresencia.api.auth.AuthResponse;
import bdisfer1410.controldepresencia.api.clock.send.ClockSendRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/login")
    Call<AuthResponse> login(@Body AuthRequest authRequest);
    @GET("/fichar")
    Call<ClockResponse> getClockAction(@Header("Authorization") String token);
    @POST("/fichar")
    Call<ClockResponse> sendClockRequest(@Header("Authorization") String token, @Body ClockSendRequest clockSendRequest);
}
