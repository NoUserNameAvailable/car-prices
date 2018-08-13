package com.nousernameavailable.carprices.carprices.model;

import java.net.URL;
import java.util.StringJoiner;

public class CarIdentifier {

    private String id;
    private URL url;
    private Double price;

    public CarIdentifier(String id, URL url, Double price) {
        this.id = id;
        this.url = url;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CarIdentifier.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("url=" + url)
                .add("price=" + price)
                .toString();
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
