package com.example.todoapp.api;

import com.example.todoapp.models.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CategoryApi {

    @GET("categories")
    Call<List<Category>> getCategories(
            @Header("Authorization") String token
    );
}
