package brunodsf05.employee.attendance.tracker.api;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase que simplifica el manejo de respuestas HTTP.
 * Se basa en que el servidor puede fallar o devolver:
 * <ol>
 *     <li>HTTP OK.<br>{@link #onOkResponse(Object)}</li>
 *     <li>Error HTTP, pero con un JSON que es válido.<br>{@link #onErrorResponse(Object)}</li>
 *     <li>Error HTTP con una respuesta no parseable.<br>{@link #onNullResponse()}</li>
 * </ol>
 *
 * Además como QoL, permite ejecutar lógica antes y después de manejar alguna de las respuestas anteriores.
 *
 * @param <T> El objeto cuando HTTP devuelve OK
 * @param <E> El objeto que contiene el error
 */
public abstract class ProCallback<T, E> implements Callback<T> {
    /**
     * @return La clase con los datos de error. Ejemplo: BadResponse.class.
     */
    protected abstract Class<E> getErrorClass();

    /**
     * Se ejecuta antes de manejar la respuesta.
     */
    public abstract void beforeResponse();

    /**
     * Se ejecuta después de manejar la respuesta.
     */
    public abstract void afterResponse();

    /**
     * Se ejecuta si el código HTTP es válido y se puede parsear el json a {@link T}.
     *
     * @param okBody El objeto {@link T} recibido.
     */
    public abstract void onOkResponse(@NonNull T okBody);

    /**
     * Se ejecuta cuando el código HTTP no es válido pero se puede parsear el json a {@link E}.
     *
     * @param errorBody El objeto {@link E}.
     */
    public abstract void onErrorResponse(@NonNull E errorBody);

    /**
     * Se ejecuta cuando el código HTTP no es válido y no se pudo parsear el json a {@link T}
     */
    public abstract void onNullResponse();

    /**
     * Si la petición al servidor fallo.
     *
     * @param t Un error.
     */
    public abstract void onFailure(Throwable t);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        beforeResponse();

        if (response.isSuccessful() && response.body() != null) {
            onOkResponse(response.body());
        }
        else {
            try (ResponseBody responseBody = response.errorBody()) {
                assert responseBody != null;
                E errorBody = new Gson().fromJson(responseBody.string(), getErrorClass());
                onErrorResponse(errorBody);
            }
            catch (Exception e) {
                onNullResponse();
            }
        }

        afterResponse();
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        beforeResponse();
        onFailure(t);
        afterResponse();
    }
}
