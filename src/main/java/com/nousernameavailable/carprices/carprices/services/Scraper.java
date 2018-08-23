package com.nousernameavailable.carprices.carprices.services;

import com.nousernameavailable.carprices.carprices.model.Car;
import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Scraper {

    private String site;
    private String url;
    private String baseUrl;
    private String model;
    private String brand;
    private String batchName;
    private LocalDateTime localDateTime;
    private CarRepository carRepository;

    private static final Logger log = LoggerFactory.getLogger(Scraper.class);

    protected Scraper(){ }


    public Scraper(String site, String url, String baseUrl, String model, String brand, CarRepository carRepository) {
        this.site = site;
        this.url = url;
        this.baseUrl = baseUrl;
        this.model = model;
        this.brand = brand;
        this.batchName = setBatchName(this.site, this.model);
        this.localDateTime = LocalDateTime.now();
        this.carRepository = carRepository;
    }

    private String setBatchName(String site, String model) {
        return site + "_" + model;
    }

    public abstract List<CarIdentifier> findPagesWithCarListing();

    private List<Car> findNewCars(List<CarIdentifier> carIdentifiers){
        List<Car> carsToInsert = new ArrayList<>();

        carIdentifiers.forEach(carIdentifier -> {
            if (!carRepository.existsByCarIdSite(carIdentifier.getId())) {
                carsToInsert.add(getCarDescription(carIdentifier));
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

                Car newCar = getCarDescription(carIdentifier);
                newCar.setAdded(localDateTime);
                carsToUpdate.add(newCar);
            }
        });

        return carsToUpdate;
    }

    private List<Car> findDeletedCar(List<CarIdentifier> carIdentifiers){
        List<Car> carsToDelete = new ArrayList<>();

        // get all cardIdSites find in listing
        List<String> carIdSites = carIdentifiers.stream().map(CarIdentifier::getId).collect(Collectors.toList());
        carRepository.findByNotCarIdSiteAndBatchAndModifiedIsNull(carIdSites, batchName).ifPresent(carsToDelete::addAll);

        return carsToDelete;
    }

    public abstract Car getCarDescription(CarIdentifier carIdentifier);

    private void upsertCars(List<Car> cars){
        carRepository.saveAll(cars);
    }

    private void deleteCars(List<Car> cars){
        cars.forEach(car -> {
            car.setDeleted(localDateTime);
            carRepository.save(car);
        });
    }

    public void runJob(){
        List<CarIdentifier> carIdentifiers = findPagesWithCarListing();
        log.info("Car ads found : " + carIdentifiers.size());

        System.out.println(carIdentifiers);

        List<Car> newCars = findNewCars(carIdentifiers);
        log.info("New cars found : {}", newCars.size() );
        List<Car> modifiedCars = findUpdatedCars(carIdentifiers);
        log.info("Modified cars found : {}", modifiedCars.size() );
        List<Car> deletedCars = findDeletedCar(carIdentifiers);
        log.info("Deleted cars found : {}", deletedCars.size() );

        upsertCars(newCars);
        upsertCars(modifiedCars);
        deleteCars(deletedCars);
    }



}
