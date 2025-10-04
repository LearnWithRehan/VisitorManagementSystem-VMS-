package com.example.visitormanagementsys.ActiveVisit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.ApiClient;
import com.example.visitormanagementsys.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder> {

    private List<VisitorModel> visitorList;
    private Context context;

    public VisitorAdapter(Context context, List<VisitorModel> visitorList) {
        this.context = context;
        this.visitorList = visitorList;
    }

    @NonNull
    @Override
    public VisitorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.visitor_card_item, parent, false);
        return new VisitorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorViewHolder holder, int position) {
        VisitorModel visitor = visitorList.get(position);

        holder.tvName.setText("Name: " + visitor.getName());
        holder.tvId.setText("ID: " + visitor.getVisitorId());
        holder.tvMobile.setText("Mobile: " + visitor.getMobile());
        holder.tvAddress.setText("Address: " + visitor.getAddress());
        holder.tvCompany.setText("Company: " + visitor.getCompany());
        holder.tvPurpose.setText("Purpose: " + visitor.getPurpose());
        holder.tvDepartment.setText("Department: " + visitor.getDepartment());
        holder.tvEmployee.setText("Employee: " + visitor.getEmployee());
        holder.tvEntryDate.setText("Entry Date: " + visitor.getEntryDate());

        holder.btnMarkInactive.setOnClickListener(v -> {
            int visitorId = Integer.parseInt(visitor.getVisitorId());

            VisitorApiService apiService = ApiClient.getClient()
                    .create(VisitorApiService.class);

            Call<ApiResponse> call = apiService.markVisitorInactive(visitorId);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                        Toast.makeText(context, "Visitor marked inactive", Toast.LENGTH_SHORT).show();
                        visitorList.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(context, "Failed: " + (response.body() != null ? response.body().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return visitorList.size();
    }

    static class VisitorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvMobile, tvAddress, tvCompany, tvPurpose, tvDepartment, tvEmployee, tvEntryDate;
        Button btnMarkInactive;

        public VisitorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVisitorName);
            tvId = itemView.findViewById(R.id.tvVisitorID);
            tvMobile = itemView.findViewById(R.id.tvVisitorMobile);
            tvAddress = itemView.findViewById(R.id.tvVisitorAddress);
            tvCompany = itemView.findViewById(R.id.tvVisitorCompany);
            tvPurpose = itemView.findViewById(R.id.tvVisitorPurpose);
            tvDepartment = itemView.findViewById(R.id.tvVisitorDepartment);
            tvEmployee = itemView.findViewById(R.id.tvVisitorEmployee);
            tvEntryDate = itemView.findViewById(R.id.tvVisitorEntryDate);
            btnMarkInactive = itemView.findViewById(R.id.btnMarkInactive);
        }
    }
}
