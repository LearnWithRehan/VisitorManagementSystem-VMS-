package com.example.visitormanagementsys.ActiveVisit;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveVisitor extends AppCompatActivity {
    private static final String TAG = "ActiveVisitor";
    private RecyclerView recyclerView;
    private VisitorAdapter adapter;
    private List<VisitorModel> visitorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_visitor);

        recyclerView = findViewById(R.id.recyclerActiveVisitors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        adapter = new VisitorAdapter(this, visitorList);
        recyclerView.setAdapter(adapter);

        fetchActiveVisitors();
    }

    private void fetchActiveVisitors() {
        VisitorApiService apiService = ApiClient.getClient().create(VisitorApiService.class);
        Call<VisitorApiResponse> call = apiService.getActiveVisitors();

        call.enqueue(new Callback<VisitorApiResponse>() {
            @Override
            public void onResponse(Call<VisitorApiResponse> call, Response<VisitorApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        List<VisitorModel> visitors = response.body().getData();
                        if (visitors.isEmpty()) {
                            Toast.makeText(ActiveVisitor.this, "No visitor found", Toast.LENGTH_SHORT).show();
                        } else {
                            // Clear the old list and add new data
                            visitorList.clear();
                            visitorList.addAll(visitors);

                            // Notify adapter about data changes
                            adapter.notifyDataSetChanged();
                        }
                        Log.d(TAG, "Visitors size: " + visitors.size());
                    } else {
                        Toast.makeText(ActiveVisitor.this, "API returned failure", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "API returned failure");
                    }
                } else {
                    Toast.makeText(ActiveVisitor.this, "Response failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Response failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<VisitorApiResponse> call, Throwable t) {
                Toast.makeText(ActiveVisitor.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure", t);
            }
        });
    }
}
