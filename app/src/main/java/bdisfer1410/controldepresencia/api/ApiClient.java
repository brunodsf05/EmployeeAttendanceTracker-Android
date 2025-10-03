package bdisfer1410.controldepresencia.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provides a retrofit instance that can be used with the EmployeeAttendanceTracker-Backend.
 * It works as a singleton because the url can be changed.
 * Please note that the url is blank and that you must set it before doing your first request.
 */
public class ApiClient {
    private static final Gson myGson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // ISO 8601
            .create();

    public static String url = "";

    public static Retrofit getRetrofit() {
        boolean urlChanged = !url.equals(lastUrl);

        if (urlChanged) {
            lastUrl = url;
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(myGson))
                    .build();
        }

        return retrofit;
    }

    //region Singleton manager
    private static String lastUrl = null;
    private static Retrofit retrofit;
    //endregion
}

