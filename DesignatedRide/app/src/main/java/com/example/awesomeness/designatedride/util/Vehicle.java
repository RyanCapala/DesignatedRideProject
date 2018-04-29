package com.example.awesomeness.designatedride.util;

import android.widget.TextView;

public class Vehicle {

    private TextView car_make, car_model, car_year;

    public Vehicle(TextView car_make, TextView car_model, TextView car_year) {
        this.car_make = car_make;
        this.car_model = car_model;
        this.car_year = car_year;
    }

    public TextView getCar_make() {
        return car_make;
    }

    public void setCar_make(TextView car_make) {
        this.car_make = car_make;
    }

    public TextView getCar_model() {
        return car_model;
    }

    public void setCar_model(TextView car_model) {
        this.car_model = car_model;
    }

    public TextView getCar_year() {
        return car_year;
    }

    public void setCar_year(TextView car_year) {
        this.car_year = car_year;
    }
}
