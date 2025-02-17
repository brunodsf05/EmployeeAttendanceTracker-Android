package bdisfer1410.controldepresencia;

import android.annotation.SuppressLint;
import android.content.Context;

public class Util {
    /**
     * Obtiene el texto
     *
     * @param context Contexto de la aplicación.
     * @param key La clave del recurso de texto que se desea obtener.
     *
     * @return El mensaje que guarda {@code key}. Si no lo encuentra devuelve el texto de {@code key}.
     */
    public static String getMessage(Context context, String key) {
        @SuppressLint("DiscouragedApi")
        int resId = context.getResources().getIdentifier(key, "string", context.getPackageName());

        return (resId != 0)
                ? context.getString(resId)
                : key;
    }
}
