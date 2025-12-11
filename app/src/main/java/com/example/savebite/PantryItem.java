package com.example.savebite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PantryItem {
    private int id;
    private String name;
    private String category;
    private String quantity;
    private String expiryDate;
    private int isConsumed; // 新增：0 = 未消耗, 1 = 已消耗

    // 更新构造函数
    public PantryItem(int id, String name, String category, String quantity, String expiryDate, int isConsumed) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.isConsumed = isConsumed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getQuantity() { return quantity; }
    public String getExpiryDate() { return expiryDate; }

    // 新增 Getter/Setter
    public int getIsConsumed() { return isConsumed; }
    public void setIsConsumed(int isConsumed) { this.isConsumed = isConsumed; }

    public String getStatus() {
        if (isConsumed == 1) return "consumed"; // 如果已消耗，直接返回状态

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // 注意日期格式要匹配输入
        try {
            // 尝试解析 dd/MM/yyyy 或 yyyy-MM-dd (兼容旧数据)
            Date expiry;
            if (this.expiryDate.contains("/")) {
                expiry = sdf.parse(this.expiryDate);
            } else {
                expiry = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this.expiryDate);
            }

            Date today = new Date();
            today.setHours(0); today.setMinutes(0); today.setSeconds(0);

            long diff = expiry.getTime() - today.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if (days < 0) return "expired";
            if (days <= 3) return "warning";
            return "good";
        } catch (ParseException e) {
            e.printStackTrace();
            return "good";
        }
    }
}