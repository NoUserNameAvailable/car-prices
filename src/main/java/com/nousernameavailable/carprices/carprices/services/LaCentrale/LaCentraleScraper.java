package com.nousernameavailable.carprices.carprices.services.LaCentrale;

import com.nousernameavailable.carprices.carprices.model.Car;
import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.services.Scraper;

import java.util.List;

public class LaCentraleScraper extends Scraper {

    public LaCentraleScraper(String site, String url, String baseUrl, String model, String brand) {
        super(site, url, baseUrl, model, brand);
    }

    @Override
    public List<CarIdentifier> findPagesWithCarListing() {
        return null;
    }

    @Override
    public Car getCarDescription() {
        return null;
    }

}
