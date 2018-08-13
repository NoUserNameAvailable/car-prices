package com.nousernameavailable.carprices.carprices.job;

import com.nousernameavailable.carprices.carprices.services.LaCentrale.LaCentraleScraper;
import com.nousernameavailable.carprices.carprices.services.Scraper;

public class JobsManager {

    public void run() {
        String site = "LaCentrale";
        String url = "https://www.lacentrale.fr/listing?energies=ess&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&gearbox=MANUAL";
        String baseUrl = "https://www.lacentrale.fr";
        String model = "SCIROCCO";
        String brand = "VOLKSWAGEN";
        Scraper scraper = new LaCentraleScraper(site, url, baseUrl, model, brand);
        scraper.job();
    }

}
