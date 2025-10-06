package com.example.visitormanagementsys.Report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.R;

import java.util.List;

public class InVisitorAdapter extends RecyclerView.Adapter<InVisitorAdapter.ViewHolder> {

    private List<InVisitor> visitorList;

    public InVisitorAdapter(List<InVisitor> visitorList) {
        this.visitorList = visitorList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.in_item_visitor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InVisitor visitor = visitorList.get(position);
        holder.tvVisitorId.setText("Visitor ID: " + visitor.getVisitorId());
        holder.tvName.setText("Name: " + visitor.getName());
        holder.tvMobile.setText("Mobile: " + visitor.getMobile());
        holder.tvAddress.setText("Address: " + visitor.getAddress());
        holder.tvCompany.setText("Company: " + visitor.getCompany());
        holder.tvPurpose.setText("Purpose: " + visitor.getPurpose());
        holder.tvDepartment.setText("Department: " + visitor.getDepartment());
        holder.tvEmployee.setText("Employee: " + visitor.getEmployee());
        holder.tvStatus.setText("Status: " + visitor.getStatus());
        holder.tvEntryDate.setText("Entry Date: " + visitor.getEntryDate());
    }

    @Override
    public int getItemCount() {
        return visitorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVisitorId, tvName, tvMobile, tvAddress, tvCompany, tvPurpose, tvDepartment, tvEmployee, tvStatus, tvEntryDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvVisitorId = itemView.findViewById(R.id.tvVisitorIdr);
            tvName = itemView.findViewById(R.id.tvNamer);
            tvMobile = itemView.findViewById(R.id.tvMobiler);
            tvAddress = itemView.findViewById(R.id.tvAddressr);
            tvCompany = itemView.findViewById(R.id.tvCompanyr);
            tvPurpose = itemView.findViewById(R.id.tvPurposer);
            tvDepartment = itemView.findViewById(R.id.tvDepartmentr);
            tvEmployee = itemView.findViewById(R.id.tvEmployeer);
            tvStatus = itemView.findViewById(R.id.tvStatusr);
            tvEntryDate = itemView.findViewById(R.id.tvEntryDater);
        }
    }
}