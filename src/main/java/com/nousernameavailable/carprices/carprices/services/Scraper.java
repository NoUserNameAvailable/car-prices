package com.nousernameavailable.carprices.carprices.services;

import com.nousernameavailable.carprices.carprices.model.Car;
import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public abstract class Scraper {

    @Autowired
    private CarRepository carRepository;

    private String site;
    private String url;
    private String baseUrl;
    private String model;
    private String brand;
    private String batchName;
    private LocalDateTime localDateTime;

    private static final Logger log = LoggerFactory.getLogger(Scraper.class);

    protected Scraper(){

    }

    public Scraper(String site, String url, String baseUrl, String model, String brand) {
        this.site = site;
        this.url = url;
        this.baseUrl = baseUrl;
        this.model = model;
        this.brand = brand;
        this.batchName = setBatchName(this.site, this.model);
        this.localDateTime = LocalDateTime.now();
    }

    private String setBatchName(String site, String model) {
        return site + "_" + model;
    }

    public abstract List<CarIdentifier> findPagesWithCarListing();

    private List<Car> findNewCars(List<CarIdentifier> carIdentifiers){
        List<Car> carsToInsert = new ArrayList<>();

        carIdentifiers.forEach(carIdentifier -> {
            if (!carRepository.existsByCarIdSite(carIdentifier.getId())) {
                carsToInsert.add(getCarDescription());
            }
        });
        return carsToInsert;
    }

    private List<Car> findUpdatedCars(List<CarIdentifier> carIdentifiers){
        List<Car> carsToUpdate = new ArrayList<>();

        carIdentifiers.forEach(carIdentifier -> {
            Optional<Car> currentCar = carRepository.findByCarIdSiteAndModifiedIsNullAndDeletedIsNull(carIdentifier.getId());
            if (currentCar.isPresent() && Double.compare(currentCar.get().getPrice(), carIdentifier.getPrice()) != 0) {
                Car oldCar = currentCar.get();
                oldCar.setModified(localDateTime);
                carsToUpdate.add(oldCar);

                Car newCar = getCarDescription();
                newCar.setAdded(localDateTime);
                carsToUpdate.add(newCar);
            }
        });

        return carsToUpdate;
    }

    List<Car> findDeletedCar(List<CarIdentifier> carIdentifiers){
        List<Car> carsToDelete = new ArrayList<>();

        // get all cardIdSites find in listing
        List<String> carIdSites = carIdentifiers.stream().map(CarIdentifier::getId).collect(Collectors.toList());
        carRepository.findByNotCarIdSiteAndBatchAndModifiedIsNull(carIdSites, batchName).ifPresent(carsToDelete::addAll);

        return carsToDelete;
    }

    public abstract Car getCarDescription();

    void upsertCars(List<Car> cars){
        carRepository.saveAll(cars);
    }

    void deleteCars(List<Car> cars){
        carRepository.deleteAll(cars);
    }

    public void job(){
        List<CarIdentifier> carIdentifiers = findPagesWithCarListing();

        List<Car> newCars = findNewCars(carIdentifiers);
        List<Car> modifiedCars = findUpdatedCars(carIdentifiers);
        List<Car> deletedCars = findDeletedCar(carIdentifiers);

        upsertCars(newCars);
        upsertCars(modifiedCars);
        deleteCars(deletedCars);
    }



}
