package bdisfer1410.controldepresencia.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://bdisfer1410.eu.pythonanywhere.com/";

    private static final Gson myGson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") // Formato ISO 8601
            .create();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(myGson))
            .build();
}

