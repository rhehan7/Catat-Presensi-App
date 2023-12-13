package project.catatpresensi.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import project.catatpresensi.model.LoginRequest;
import project.catatpresensi.model.ServerResponse;
import project.catatpresensi.model.ServerResponseHistory;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiEndpoint {

   @POST("auth/login")
   Call<ServerResponse> loginResponse(
         @Body LoginRequest loginRequest
   );

   @Multipart
   @POST("attendances/tap")
   Call<ServerResponse> submitPresence(
           @Header("Authorization") String authorization,
           @Part("latitude") RequestBody latitude,
           @Part("longitude") RequestBody longitude,
           @Part MultipartBody.Part image,
           @Part("type") RequestBody jenisPresensi
           );

   @GET("attendances/histories")
   Call<ServerResponseHistory> getPresensiHistory(
           @Header("Authorization") String authorization,
           @Query("year") int tahun,
           @Query("month") int bulan
   );

   @GET("profile")
   Call<ServerResponse> getDataProfile(@Header("Authorization") String authorization);
}
