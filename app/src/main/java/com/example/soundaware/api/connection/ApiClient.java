package com.example.soundaware.api.connection;

import com.example.soundaware.api.models.audio.AudioResponse;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8000/"; // Cambia por tu URL
    private static Retrofit retrofit = null;

    public interface ApiService {
        @Multipart
        @POST("audio")
        Call<AudioResponse> uploadAudio(
                @Part MultipartBody.Part file
        );
    }

    // Configuración de Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Método para subir audio
    public static Call<AudioResponse> uploadAudioFile(MultipartBody.Part audioFile) {
        ApiService apiService = getClient().create(ApiService.class);
        return apiService.uploadAudio(audioFile);
    }
}