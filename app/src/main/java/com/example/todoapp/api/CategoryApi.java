package com.example.todoapp.api;

import com.example.todoapp.requests.CategoryRequest;
import com.example.todoapp.responses.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryApi {

    @GET("categories")
    Call<List<CategoryResponse>> getCategories();

    @POST("categories")
    Call<CategoryResponse> createCategory(
            @Body CategoryRequest request
    );

    @PUT("categories/{id}")
    Call<CategoryResponse> updateCategory(
            @Path("id") int categoryId,
            @Body CategoryRequest request
    );

    @DELETE("categories/{id}")
    Call<Void> deleteCategory(
            @Path("id") int categoryId
    );
}
