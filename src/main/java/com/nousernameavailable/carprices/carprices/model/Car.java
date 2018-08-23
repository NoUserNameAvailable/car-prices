package com.nousernameavailable.carprices.carprices.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.StringJoiner;

@Entity
@Table(name = "car")
@Getter
@Setter
public class Car {

    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Double price;

    @Column(name = "car_id_site")
    private String carIdSite;

    private String url;

    private Integer year;

    @Column(name = "kmAge")
    private Integer kmAge;

    @Column(name = "fiscal_power")
    private Integer fiscalPower;

    private Integer power;

    @Column(name = "gearbox_type")
    private String gearboxType;

    @Column(name = "interior_color")
    private String interiorColor;

    @Column(name = "exterior_color")
    private String exteriorColor;

    private Integer warranty;
    private String version;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "entry_into_service")
    private LocalDate entryIntoService;

    @Column(name = "added")
    private LocalDateTime added;

    @Column(name = "modified")
    private LocalDateTime modified;

    @Column(name = "deleted")
    private LocalDateTime deleted;

    @Column(name = "batch")
    private String batch;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    protected Car() {
    }

    public Car(String brand, String model, Double price) {
        this.brand = brand;
        this.model = model;
        this.price = price;
    }

    public Car(String brand, String model, Double price, String carIdSite) {
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.carIdSite = carIdSite;
    }

    public Car(Long id, String brand, String model, Double price, String carIdSite) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.price = price;
        this.carIdSite = carIdSite;
    }

    public Car(String carIdSite) {
        this.carIdSite = carIdSite;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("brand='" + brand + "'")
                .add("model='" + model + "'")
                .add("price=" + price)
                .add("carIdSite='" + carIdSite + "'")
                .add("url='" + url + "'")
                .add("year=" + year)
                .add("kmAge=" + kmAge)
                .add("fiscalPower=" + fiscalPower)
                .add("power=" + power)
                .add("gearboxType='" + gearboxType + "'")
                .add("fuelType='" + fuelType + "'")
                .add("entryIntoService=" + entryIntoService)
                .add("interiorColor='" + interiorColor + "'")
                .add("exteriorColor='" + exteriorColor + "'")
                .add("warranty=" + warranty)
                .add("version='" + version + "'")
                .toString();
    }

}
