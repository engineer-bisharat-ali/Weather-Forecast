package com.example.weatherforcast;

public class WeatherRVModel {

    private String time;
    private String temperature;
    private String windspeed;

    private String Icon;

    public WeatherRVModel(String time, String temperature, String Icon, String windspeed) {
        this.time = time;
        this.temperature = temperature;
        this.Icon = Icon;
        this.windspeed = windspeed;


    }


    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
