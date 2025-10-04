package com.example.visitormanagementsys.Report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visitormanagementsys.R;

import java.util.List;

public class VisitorReportAdapter extends RecyclerView.Adapter<VisitorReportAdapter.ViewHolder> {

    private Context context;
    private List<ReportModel> visitorList;

    public VisitorReportAdapter(Context context, List<ReportModel> visitorList) {
        this.context = context;
        this.visitorList = visitorList;
    }

    @NonNull
    @Override
    public VisitorReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_visitor_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorReportAdapter.ViewHolder holder, int position) {
        ReportModel visitor = visitorList.get(position);

        holder.tvVisitorId.setText("Visitor ID: " + visitor.getVisitor_id());
        holder.tvName.setText("Name: " + visitor.getName());
        holder.tvAddress.setText("Address: " + visitor.getAddress());
        holder.tvMobile.setText("Mobile: " + visitor.getMobile());
        holder.tvDepartment.setText("Department: " + visitor.getDepartment());
        holder.tvEmployee.setText("Employee: " + visitor.getEmployee());
        holder.tvPurpose.setText("Purpose: " + visitor.getPurpose());
        holder.tvCompany.setText("Company: " + visitor.getCompany());
        holder.tvEntryDate.setText("Entry Date: " + visitor.getEntry_Date());
        holder.tvOutDate.setText("Out Date: " + visitor.getOut_Date());
    }

    @Override
    public int getItemCount() {
        return visitorList != null ? visitorList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVisitorId, tvName, tvAddress, tvMobile, tvDepartment, tvEmployee,
                tvPurpose, tvCompany, tvEntryDate, tvOutDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVisitorId = itemView.findViewById(R.id.tvVisitorIdr);
            tvName = itemView.findViewById(R.id.tvNamer);
            tvAddress = itemView.findViewById(R.id.tvAddressr);
            tvMobile = itemView.findViewById(R.id.tvMobiler);
            tvDepartment = itemView.findViewById(R.id.tvDepartmentr);
            tvEmployee = itemView.findViewById(R.id.tvEmployeer);
            tvPurpose = itemView.findViewById(R.id.tvPurposer);
            tvCompany = itemView.findViewById(R.id.tvCompanyr);
            tvEntryDate = itemView.findViewById(R.id.tvEntryDater);
            tvOutDate = itemView.findViewById(R.id.tvOutDater);
        }
    }
}
