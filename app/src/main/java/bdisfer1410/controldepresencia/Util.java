package bdisfer1410.controldepresencia;

import android.annotation.SuppressLint;
import android.content.Context;

public class Util {
    /**
     * Obtiene un mensaje de ``strings.xml`` según la clave.
     *
     * @param context Contexto de la aplicación.
     * @param key La clave del recurso de texto que se desea obtener.
     * @param fallbackMessage El mensaje a mostrar cuando la clave no exista.
     *
     * @return El mensaje que guarda {@code key}. Si no lo encuentra devuelve {@code fallbackMessage}.
     */
    public static String getMessage(Context context, String key, String fallbackMessage) {
        @SuppressLint("DiscouragedApi")
        int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());

        return (resId != 0)
                ? context.getString(resId)
                : fallbackMessage;
    }

    /**
     * Obtiene un mensaje de ``strings.xml`` según la clave.
     *
     * @param context Contexto de la aplicación.
     * @param key La clave del recurso de texto que se desea obtener.
     * @param fallbackResId El código del mensaje a mostrar cuando la clave no exista.
     *
     * @return El mensaje que guarda {@code key}. Si no lo encuentra devuelve el mensaje de {@code fallbackResId}.
     */
    public static String getMessage(Context context, String key, int fallbackResId) {
        return getMessage(context, key, context.getString(fallbackResId));
    }

    /**
     * Obtiene un mensaje de ``strings.xml`` según la clave.
     *
     * @param context Contexto de la aplicación.
     * @param key La clave del recurso de texto que se desea obtener.
     *
     * @return El mensaje que guarda {@code key}. Si no lo encuentra devuelve el texto de {@code key}.
     */
    public static String getMessage(Context context, String key) {
        return getMessage(context, key, key);
    }
}
