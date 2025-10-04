package com.example.visitormanagementsys.ActiveVisit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.R;

import java.util.List;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder> {

    private List<VisitorModel> visitorList;

    public VisitorAdapter(List<VisitorModel> visitorList) {
        this.visitorList = visitorList;
    }

    @NonNull
    @Override
    public VisitorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visitor_card_item, parent, false);
        return new VisitorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorViewHolder holder, int position) {
        VisitorModel visitor = visitorList.get(position);

        holder.tvName.setText("Name: " + visitor.getName());
        holder.tvMobile.setText("Mobile: " + visitor.getMobile());
        holder.tvAddress.setText("Address: " + visitor.getAddress());
        holder.tvCompany.setText("Company: " + visitor.getCompany());
        holder.tvPurpose.setText("Purpose: " + visitor.getPurpose());
        holder.tvDepartment.setText("Department: " + visitor.getDepartment());
        holder.tvEmployee.setText("Employee: " + visitor.getEmployee());
        holder.tvEntryDate.setText("Entry Date: " + visitor.getEntryDate());
    }

    @Override
    public int getItemCount() {
        return visitorList.size();
    }

    static class VisitorViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMobile, tvAddress, tvCompany, tvPurpose, tvDepartment, tvEmployee, tvEntryDate;

        public VisitorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvVisitorName);
            tvMobile = itemView.findViewById(R.id.tvVisitorMobile);
            tvAddress = itemView.findViewById(R.id.tvVisitorAddress);
            tvCompany = itemView.findViewById(R.id.tvVisitorCompany);
            tvPurpose = itemView.findViewById(R.id.tvVisitorPurpose);
            tvDepartment = itemView.findViewById(R.id.tvVisitorDepartment);
            tvEmployee = itemView.findViewById(R.id.tvVisitorEmployee);
            tvEntryDate = itemView.findViewById(R.id.tvVisitorEntryDate);
        }
    }
}
