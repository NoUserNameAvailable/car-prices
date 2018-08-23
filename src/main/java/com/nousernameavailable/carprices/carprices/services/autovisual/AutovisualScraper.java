package com.nousernameavailable.carprices.carprices.services.autovisual;

import com.nousernameavailable.carprices.carprices.model.Car;
import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import com.nousernameavailable.carprices.carprices.services.Scraper;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutovisualScraper extends Scraper {

    private static final Logger log = LoggerFactory.getLogger(AutovisualScraper.class);

    private Path path;
    private File file;
    private List<CarIdentifier> carIdentifiers;
    private Map<String, Car> cars;

    public AutovisualScraper(String site, String url, String baseUrl, String model, String brand, CarRepository carRepository) {
        super(site, url, baseUrl, model, brand, carRepository);
        init();
        try {
            completeCarIdentifiers();
        } catch (IOException e) {
            System.out.println("*** test");
            e.printStackTrace();
        }

    }

    private void init() {
        path = Paths.get(getSite());
        try {
            if(!Files.isDirectory(path)){
                Files.createDirectory(path);
            }
        } catch (IOException e) {
            log.error("Impossible to create directory " + e.getMessage());
        }
        file = new File(getSite() +"/"+getBatchName()+"_"+super.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"))+".json");
        try {
            FileUtils.copyURLToFile(new URL(getUrl()), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void completeCarIdentifiers() throws IOException {
        carIdentifiers = new ArrayList<>();
        cars = new HashMap<>();
        JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
        JSONArray jsonCars = jsonObject.getJSONArray("cars");

        for(int i=0; i< jsonCars.length(); i++){
            JSONObject jsonCar = jsonCars.getJSONObject(i);
            carIdentifiers.add(setCarIdentifier(jsonCar));
            cars.put(jsonCar.optString("id"), setCar(jsonCar));
        }
    }

    private Car setCar(JSONObject jsonCar) {
        Car car = new Car(jsonCar.optString("id"));
        car.setYear(jsonCar.getInt("annee"));
        car.setKmAge(jsonCar.getInt("km"));
        car.setFiscalPower(jsonCar.optInt("cv"));
        car.setPower(jsonCar.optInt("puissance"));
        car.setGearboxType(findGearBoxType(jsonCar));
        car.setFuelType(findFuelType(jsonCar));
        car.setEntryIntoService(findEntryIntoService(jsonCar));
        car.setPrice(findPrice(jsonCar));
        car.setBrand(super.getBrand());
        car.setModel(super.getModel());
        car.setUrl(jsonCar.optString("url"));
        car.setVersion(jsonCar.optString("libelle"));
        car.setAdded(super.getLocalDateTime());
        car.setBatch(super.getBatchName());

        return car;
    }

    private Double findPrice(JSONObject jsonCar) {
        JSONArray prices = jsonCar.getJSONArray("prix");
        return prices.optDouble(prices.length()-1);
    }

    private LocalDate findEntryIntoService(JSONObject jsonCar) {
        String dateServcie = jsonCar.optString("dt_mec");
        if(dateServcie != null){
            return LocalDate.parse(dateServcie, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.000z"));
        }else {
            return null;
        }
    }

    private String findFuelType(JSONObject jsonCar) {
        switch (jsonCar.optString("id_energie")) {
            case "1":
                return "DIESEL";
            case "3":
                return "ESSENCE";
            default:
                return null;
        }
    }

    private String findGearBoxType(JSONObject jsonCar) {
        switch (jsonCar.optString("id_b")) {
            case "1":
                return "AUTOMATIQUE";
            case "2":
                return "MANUELLE";
            default:
                return null;
        }
    }

    private CarIdentifier setCarIdentifier(JSONObject jsonObject) {
        CarIdentifier carIdentifier = null;
        try {
            carIdentifier = new CarIdentifier(
                    jsonObject.optString("id"),
                    new URL(jsonObject.getString("url")),
                    findPrice(jsonObject)
            );
        } catch (MalformedURLException e) {
            log.error("Impossible to read ads url - " + e.getMessage());
        }
        return carIdentifier;
    }

    @Override
    public List<CarIdentifier> findPagesWithCarListing() {
        return carIdentifiers;
    }

    @Override
    public Car getCarDescription(CarIdentifier carIdentifier) {
        return cars.get(carIdentifier.getId());
    }


}
