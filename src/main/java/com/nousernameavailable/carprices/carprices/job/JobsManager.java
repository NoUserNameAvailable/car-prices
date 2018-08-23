package com.nousernameavailable.carprices.carprices.job;

import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import com.nousernameavailable.carprices.carprices.services.LaCentrale.LaCentraleScraper;
import com.nousernameavailable.carprices.carprices.services.Scraper;
import com.nousernameavailable.carprices.carprices.services.autovisual.AutovisualScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class JobsManager {

    @Autowired
    private CarRepository carRepository;

//    @Scheduled(fixedRate = 500000)
//    public void run() {
//        String site = "LaCentrale";
//        String url = "https://www.lacentrale.fr/listing?energies=ess&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&gearbox=MANUAL";
//        String baseUrl = "https://www.lacentrale.fr";
//        String model = "SCIROCCO";
//        String brand = "VOLKSWAGEN";
//        Scraper scraper = new LaCentraleScraper(site, url, baseUrl, model, brand, carRepository);
////        scraper.setSite(site);
////        scraper.setUrl(url);
////        scraper.setBaseUrl(baseUrl);
////        scraper.setModel(model);
////        scraper.setBrand(brand);
//        scraper.runJob();
//    }

    @Scheduled(fixedRate = 5000000)
    public void runAutovisual(){
        String site = "AutoVisual";
        String url = "https://www.autovisual.com/fr/json-more-modele/Volkswagen/Scirocco/Scirocco--Coupé/fr--75000--Paris/9382";
//        String url = "file:////home/mat/IdeaProjects/car-prices/autovisual.json";
        String baseUrl = "https://www.autovisual.com";
        String model = "SCIROCCO";
        String brand = "VOLKSWAGEN";
        Scraper scraper = new AutovisualScraper(site, url, baseUrl, model, brand, carRepository);
        scraper.runJob();
    }

}
