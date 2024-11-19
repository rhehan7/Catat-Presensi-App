package project.catatpresensi.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import project.catatpresensi.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static Retrofit retrofit;

    public static ApiEndpoint getClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .addConverterFactory(GsonConverterFactory.create())
                  .client(okHttpClient)
                  .build();
        }
        return retrofit.create(ApiEndpoint.class);
    }


} // end class ApiClient
