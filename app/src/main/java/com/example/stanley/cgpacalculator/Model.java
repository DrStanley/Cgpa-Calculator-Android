package com.example.stanley.cgpacalculator;

/**
 * Created by Stanley on 2019/01/12.
 */
public class Model {
    public String name, phone_number, email, image, usd;

    public Model(String image, String name, String num, String ema, String usd) {
        this.image = image;
        this.name = name;
        this.phone_number = num;
        this.email = ema;
        this.usd = usd;
    }

    public Model() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsd() {
        return usd;
    }

    public void setUsd(String usd) {
        this.usd = usd;
    }
}
