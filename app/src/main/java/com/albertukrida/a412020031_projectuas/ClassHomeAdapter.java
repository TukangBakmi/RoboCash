package com.albertukrida.a412020031_projectuas;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ClassHomeAdapter extends RecyclerView.Adapter<ClassHomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<ClassTransaction> transactionArrayList = new ArrayList<>();
    InterfaceTransaction interfaceTransaction;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView category, date, amount;
        public ImageView icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            category = itemView.findViewById(R.id.category);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }

    public ClassHomeAdapter(Context context, ArrayList<ClassTransaction> transactionArrayList, InterfaceTransaction interfaceTransaction) {
        this.context = context;
        this.transactionArrayList = transactionArrayList;
        this.interfaceTransaction = interfaceTransaction;
    }

    @NonNull
    @Override
    public ClassHomeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.cardview_item_home, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClassHomeAdapter.MyViewHolder holder, int position) {

        ClassTransaction transaction = transactionArrayList.get(position);

        String convertedAmount = convert(transaction.getType(), transaction.getAmount());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.US);

        TextView cTextView = holder.category;
        cTextView.setText(transaction.getCategory());
        TextView daTextView = holder.date;
        daTextView.setText(dateFormat.format(transaction.getDate()));
        TextView aTextView = holder.amount;
        aTextView.setText(convertedAmount);

        if(transaction.getType().equals("Income")){
            aTextView.setTextColor(Color.parseColor("#39B54A"));
        }else{
            aTextView.setTextColor(Color.parseColor("#ff4229"));
        }

        ImageView iconImage = holder.icon;
        switch (transaction.getCategory()) {
            case "Automotive":
                iconImage.setBackgroundResource(R.mipmap.icon_automotive);
                break;
            case "Beauty & Care":
                iconImage.setBackgroundResource(R.mipmap.icon_beauty_care);
                break;
            case "Books":
                iconImage.setBackgroundResource(R.mipmap.icon_books);
                break;
            case "Debt/Credit":
                iconImage.setBackgroundResource(R.mipmap.icon_debt_credit);
                break;
            case "Decoration":
                iconImage.setBackgroundResource(R.mipmap.icon_decoration);
                break;
            case "Electronic":
                iconImage.setBackgroundResource(R.mipmap.icon_electronic);
                break;
            case "Fashion":
                iconImage.setBackgroundResource(R.mipmap.icon_fashion);
                break;
            case "Food & Beverages":
                iconImage.setBackgroundResource(R.mipmap.icon_food_beverages);
                break;
            case "Gift":
                iconImage.setBackgroundResource(R.mipmap.icon_gift);
                break;
            case "Health":
                iconImage.setBackgroundResource(R.mipmap.icon_health);
                break;
            case "Home & Living":
                iconImage.setBackgroundResource(R.mipmap.icon_home_living);
                break;
            case "Sports":
                iconImage.setBackgroundResource(R.mipmap.icon_sports);
                break;
            case "Transportation":
                iconImage.setBackgroundResource(R.mipmap.icon_transportation);
                break;
            case "Others":
                iconImage.setBackgroundResource(R.mipmap.icon_others);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(transactionArrayList.size(), 3);
    }

    public String convert(String type, String amt){
        // Reverse
        String reverse = "";
        for (int i = 0; i < amt.length(); i++) {
            reverse = amt.charAt(i) + reverse;
        }
        // Join
        StringBuilder join = new StringBuilder();
        // Split
        String[] split = reverse.split("");
        // Join and add . every 3 char
        for (int i = 0; i < split.length; i++) {
            if(i != 0 && i%3 == 0){
                join.append(".");
            }
            join.append(split[i]);
        }
        // Reverse again
        String editedAmt = "";
        for (int i = 0; i < join.length(); i++) {
            editedAmt = join.charAt(i) + editedAmt;
        }
        // Add +IDR or -IDR
        if(type.equals("Income")){
            amt = "+IDR " + editedAmt;
        }else{
            amt = "-IDR " + editedAmt;
        }
        // Return
        return amt;
    }

    public void update(ArrayList<ClassTransaction> classTransactions){
        this.transactionArrayList = classTransactions;
        this.notifyDataSetChanged();
    }

}