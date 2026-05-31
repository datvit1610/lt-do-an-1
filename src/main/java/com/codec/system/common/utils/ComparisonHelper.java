package com.codec.system.common.utils;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class ComparisonHelper {
    private String operator;   // Toán tử nếu là so sánh đơn
    private String value;      // Giá trị nếu là so sánh đơn

    private int min;           // Giá trị nhỏ hơn trong khoảng
    private int max;           // Giá trị lớn hơn trong khoảng
    private boolean isRange;   // Đánh dấu biểu thức dạng khoảng

    private ComparisonHelper() {
    }

    public static ComparisonHelper parse(String expression) {
        ComparisonHelper helper = new ComparisonHelper();
        expression = expression.trim().replaceAll("\\s+", "");

        // Kiểm tra biểu thức dạng khoảng, ví dụ: 100<500, 0<=150
        Pattern rangePattern = Pattern.compile("^(\\d+)(<=|<|>=|>)(\\d+)$");
        Matcher matcher = rangePattern.matcher(expression);
        if (matcher.find()) {
            int left = Integer.parseInt(matcher.group(1));
            int right = Integer.parseInt(matcher.group(3));

            helper.operator = matcher.group(2); // Lưu lại loại so sánh giữa hai giá trị
            helper.min = Math.min(left, right);
            helper.max = Math.max(left, right);
            helper.isRange = true;
            return helper;
        }

        // Kiểm tra các biểu thức đơn, ví dụ: >=300, <150
        String[] operators = {">=", "<=", ">", "<", "="};
        for (String op : operators) {
            if (expression.startsWith(op)) {
                helper.operator = op;
                helper.value = expression.substring(op.length()).trim();
                helper.isRange = false;
                return helper;
            }
        }

        // Nếu không có toán tử, mặc định là so sánh bằng "="
        helper.operator = "=";
        helper.value = expression;
        helper.isRange = false;
        return helper;
    }

    public boolean compare(long actual) {
        if (isRange) {
            // Dạng khoảng: kiểm tra actual nằm trong khoảng
            return switch (operator) {
                case "<", ">" -> actual > min && actual < max;
                case "<=", ">=" -> actual >= min && actual <= max;
                default -> false;
            };
        } else {
            // Dạng đơn: dùng giá trị value
            long expected;
            try {
                expected = Long.parseLong(value);
            } catch (NumberFormatException e) {
                return false;
            }
            return switch (operator) {
                case ">" -> actual > expected;
                case "<" -> actual < expected;
                case "<=" -> actual <= expected;
                case ">=" -> actual >= expected;
                case "=" -> actual == expected;
                default -> false;
            };
        }
    }

    public int getRecommended(){
        if(isRange){
            return switch(operator){
                case "<", ">" -> min + 2;
                case "<=", ">=" -> min;
                default -> min;
            };

        }
        else {
            return Integer.parseInt(value);
        }
    }
}
