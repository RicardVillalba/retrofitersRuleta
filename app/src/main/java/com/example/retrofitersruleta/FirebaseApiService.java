package com.example.retrofitersruleta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FirebaseApiService {

    @GET("jugadores/{id}.json")
    Call<Jugador> getJugador(@Path("id") String jugadorId);

    @GET("jugadores/{nombreJugador}")
    Call<PartidaRecord> getJugadorPorNombre(@Path("nombreJugador") String nombreJugador);

    @POST("jugadores.json")
    Call<Jugador> addJugador(@Body Jugador jugador);
}
