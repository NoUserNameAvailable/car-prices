package com.nousernameavailable.carprices.carprices.scraper.LaCentrale;

import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.services.LaCentrale.LaCentraleScraper;
import org.junit.Test;

import java.util.List;

public class LaCentraleScraperTest {



    @Test
    public void should_get_all_files_browsing(){
        String site = "LaCentrale";
        String url = "https://www.lacentrale.fr/listing?energies=ess&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&gearbox=MANUAL";
        String baseUrl = "https://www.lacentrale.fr";
        String model = "SCIROCCO";
        String brand = "VOLKSWAGEN";
        LaCentraleScraper scraper = new LaCentraleScraper(site, url, baseUrl, model, brand, null);
        List<CarIdentifier> carIdentifiers = scraper.findPagesWithCarListing();
        scraper.getCarDescription(carIdentifiers.get(0));

    }



}