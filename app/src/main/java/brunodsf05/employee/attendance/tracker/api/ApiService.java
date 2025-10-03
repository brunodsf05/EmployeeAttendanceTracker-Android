package brunodsf05.employee.attendance.tracker.api;

import brunodsf05.employee.attendance.tracker.api.clock.ClockResponse;
import brunodsf05.employee.attendance.tracker.api.auth.AuthRequest;
import brunodsf05.employee.attendance.tracker.api.auth.AuthResponse;
import brunodsf05.employee.attendance.tracker.api.clock.send.ClockSendRequest;
import brunodsf05.employee.attendance.tracker.api.incidence.IncidenceRequest;
import brunodsf05.employee.attendance.tracker.api.incidence.IncidenceResponse;
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
    @POST("/incidencia")
    Call<IncidenceResponse> sendIncidence(@Header("Authorization") String token, @Body IncidenceRequest incidenceRequest);
}
