package api;

import com.google.gson.JsonElement;
import com.moveapps.asistenciaeclass.Utils;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by iopazog on 22-09-14.
 */
public class eClassAPI {

    private static final String TAG = eClassAPI.class.getSimpleName();
    private String datos;

    public String getDatos() {
        return datos;
    }

    public void setDatos(String datos) {
        this.datos = datos;
    }

    public eClassAPI(String _datos) {
        this.datos = _datos;
    }

    public interface UsuarioService {

        @FormUrlEncoded
        @POST("/usuarios/login.json")
        public void getUsuarioAsync(@Field("datos") String datos, Callback<JsonElement> cb);

        @FormUrlEncoded
        @POST("/alumnos_clases_sedes/asistencia.json")
        public void subirAsistencia(@Field("datos") String datos, Callback<JsonElement> cb);
    }

    public void getUsuarioData(Callback<JsonElement> callback) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept-Language", "en-us,en;q=0.5");
                request.addHeader("Accept-Charset", "utf-8");
                request.addHeader("consumer_key", Utils.KEY);
                request.addHeader("consumer_secret", Utils.SECRET);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                //.setLogLevel(RestAdapter.LogLevel.FULL) //Usado para el debug
                .setEndpoint(Utils.API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        UsuarioService service = restAdapter.create(UsuarioService.class);
        service.getUsuarioAsync(getDatos(), callback);
    }

    public void subirAsistencia(Callback<JsonElement> callback) {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Accept-Language", "en-us,en;q=0.5");
                request.addHeader("Accept-Charset", "utf-8");
                request.addHeader("consumer_key", Utils.KEY);
                request.addHeader("consumer_secret", Utils.SECRET);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                //.setLogLevel(RestAdapter.LogLevel.FULL) //Usado para el debug
                .setEndpoint(Utils.API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        UsuarioService service = restAdapter.create(UsuarioService.class);
        service.subirAsistencia(getDatos(), callback);
    }
}
