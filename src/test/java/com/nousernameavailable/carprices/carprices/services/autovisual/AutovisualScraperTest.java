package com.nousernameavailable.carprices.carprices.services.autovisual;

import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import com.nousernameavailable.carprices.carprices.services.LaCentrale.LaCentraleScraper;
import com.nousernameavailable.carprices.carprices.services.Scraper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

public class AutovisualScraperTest {


    @Test
    public void should_get_all_files_browsing(){
        String site = "AutoVisual";
        //String url = "https://www.lacentrale.fr/listing?energies=ess&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&gearbox=MANUAL";
        String url = "file:////home/mat/IdeaProjects/car-prices/autovisual.json";
        String baseUrl = "https://www.autovisual.com";
        String model = "SCIROCCO";
        String brand = "VOLKSWAGEN";
        Scraper scraper = new AutovisualScraper(site, url, baseUrl, model, brand, null);
        scraper.findPagesWithCarListing();
    }

}