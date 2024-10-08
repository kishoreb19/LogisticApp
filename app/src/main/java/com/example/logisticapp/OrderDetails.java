package com.example.logisticapp;

import java.io.Serializable;

public class OrderDetails implements Serializable {
    int length,breadth,height,weight;
    double sender_lat,sender_lng,receiver_lat,receiver_lng;
    int pickupStartHour,pickupStartMinute,pickupEndHour,pickupEndMinute,deliveryStartHour,deliveryStartMinute,deliveryEndHour,deliveryEndMinute;
    String date,phone;
    Boolean deliveryStatus;
    String user_name;

    public OrderDetails() {
        // Default constructor
    }

    public OrderDetails(int length, int breadth, int height, int weight, double sender_lat, double sender_lng, double receiver_lat, double receiver_lng, int pickupStartHour, int pickupStartMinute, int pickupEndHour, int pickupEndMinute, int deliveryStartHour, int deliveryStartMinute, int deliveryEndHour, int deliveryEndMinute, String date, String phone, Boolean deliveryStatus, String user_name) {
        this.length = length;
        this.breadth = breadth;
        this.height = height;
        this.weight = weight;
        this.sender_lat = sender_lat;
        this.sender_lng = sender_lng;
        this.receiver_lat = receiver_lat;
        this.receiver_lng = receiver_lng;
        this.pickupStartHour = pickupStartHour;
        this.pickupStartMinute = pickupStartMinute;
        this.pickupEndHour = pickupEndHour;
        this.pickupEndMinute = pickupEndMinute;
        this.deliveryStartHour = deliveryStartHour;
        this.deliveryStartMinute = deliveryStartMinute;
        this.deliveryEndHour = deliveryEndHour;
        this.deliveryEndMinute = deliveryEndMinute;
        this.date = date;
        this.phone = phone;
        this.deliveryStatus = deliveryStatus;
        this.user_name = user_name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getBreadth() {
        return breadth;
    }

    public void setBreadth(int breadth) {
        this.breadth = breadth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getSender_lat() {
        return sender_lat;
    }

    public void setSender_lat(double sender_lat) {
        this.sender_lat = sender_lat;
    }

    public double getSender_lng() {
        return sender_lng;
    }

    public void setSender_lng(double sender_lng) {
        this.sender_lng = sender_lng;
    }

    public double getReceiver_lat() {
        return receiver_lat;
    }

    public void setReceiver_lat(double receiver_lat) {
        this.receiver_lat = receiver_lat;
    }

    public double getReceiver_lng() {
        return receiver_lng;
    }

    public void setReceiver_lng(double receiver_lng) {
        this.receiver_lng = receiver_lng;
    }

    public int getPickupStartHour() {
        return pickupStartHour;
    }

    public void setPickupStartHour(int pickupStartHour) {
        this.pickupStartHour = pickupStartHour;
    }

    public int getPickupStartMinute() {
        return pickupStartMinute;
    }

    public void setPickupStartMinute(int pickupStartMinute) {
        this.pickupStartMinute = pickupStartMinute;
    }

    public int getPickupEndHour() {
        return pickupEndHour;
    }

    public void setPickupEndHour(int pickupEndHour) {
        this.pickupEndHour = pickupEndHour;
    }

    public int getPickupEndMinute() {
        return pickupEndMinute;
    }

    public void setPickupEndMinute(int pickupEndMinute) {
        this.pickupEndMinute = pickupEndMinute;
    }

    public int getDeliveryStartHour() {
        return deliveryStartHour;
    }

    public void setDeliveryStartHour(int deliveryStartHour) {
        this.deliveryStartHour = deliveryStartHour;
    }

    public int getDeliveryStartMinute() {
        return deliveryStartMinute;
    }

    public void setDeliveryStartMinute(int deliveryStartMinute) {
        this.deliveryStartMinute = deliveryStartMinute;
    }

    public int getDeliveryEndHour() {
        return deliveryEndHour;
    }

    public void setDeliveryEndHour(int deliveryEndHour) {
        this.deliveryEndHour = deliveryEndHour;
    }

    public int getDeliveryEndMinute() {
        return deliveryEndMinute;
    }

    public void setDeliveryEndMinute(int deliveryEndMinute) {
        this.deliveryEndMinute = deliveryEndMinute;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Boolean deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }
}
