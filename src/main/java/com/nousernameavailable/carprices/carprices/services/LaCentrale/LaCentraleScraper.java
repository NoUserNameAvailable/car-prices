package com.nousernameavailable.carprices.carprices.services.LaCentrale;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.nousernameavailable.carprices.carprices.model.Car;
import com.nousernameavailable.carprices.carprices.model.CarIdentifier;
import com.nousernameavailable.carprices.carprices.model.NodeOutput;
import com.nousernameavailable.carprices.carprices.repository.CarRepository;
import com.nousernameavailable.carprices.carprices.services.Scraper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class LaCentraleScraper extends Scraper {

    private static final Logger log = LoggerFactory.getLogger(LaCentraleScraper.class);


    @Autowired
    private CarRepository carRepository;

    protected LaCentraleScraper() {
    }

    ;

    public LaCentraleScraper(String site, String url, String baseUrl, String model, String brand, CarRepository carRepository) {
        super(site, url, baseUrl, model, brand, carRepository);
    }

    @Override
    public List<CarIdentifier> findPagesWithCarListing() {
        List<HtmlPage> htmlPages = getListingPagesFromScript();
        List<CarIdentifier> carIdentifiers = new ArrayList<>();
        htmlPages.forEach(htmlPage -> carIdentifiers.addAll(findCarIdentifiers(htmlPage)));
        return carIdentifiers;
    }

    @Override
    public Car getCarDescription(CarIdentifier carIdentifier) {
        Car car = new Car(carIdentifier.getId());

        HtmlPage page = null;
        try {
            page = getAnoucementPage(carIdentifier);
        } catch (Exception e) {
            System.out.println("Impossible de récupèrer l'annonce.");
        }

        HtmlUnorderedList ul = page.getFirstByXPath("//ul[contains(@class, 'infoGeneraleTxt column2')]");
        ul.getChildElements().forEach(li -> {

            String categorie = li.getElementsByTagName("h4").get(0).getTextContent();
            String value = li.getElementsByTagName("span").get(0).getTextContent().trim();
            switch (categorie.substring(0, categorie.length() - 2)) {
                case "Année":
                    car.setYear(Integer.decode(value));
                    break;
                case "Kilométrage":
                    car.setKmAge(Integer.decode(value.replaceAll(" ", "").replaceAll("km", "")));
                    break;
                case "Puissance fiscale":
                    car.setFiscalPower(Integer.decode(value.replaceAll("CV", "").replaceAll("\\s+", "").replaceAll("[^0-9]", "")));
                    break;
                case "Puissance din":
                    car.setPower(Integer.decode(value.replaceAll("CV", "").replaceAll("\\s+", "").replaceAll("[^0-9]", "")));
                    break;
//                case "Boîte de vitesse":
//
//                case "Énergie":

                case "Mise en circulation":
                    value = value.replaceAll("\\p{javaSpaceChar}", "");
                    String part[] = value.split("/");
                    car.setEntryIntoService(LocalDate.of(Integer.parseInt(part[2]), Integer.parseInt(part[1]), Integer.parseInt(part[0])));
                    break;
                case "Couleur intérieure":
                    car.setInteriorColor(value.trim());
                    break;
                case "Couleur extérieure":
                    car.setExteriorColor(value.trim());
                    break;
                case "Garantie":
                    try {
                        car.setWarranty(Integer.decode(value.replaceAll("[^0-9]", "").replaceAll(" ", "")));
                    } catch (NumberFormatException e) {
                        car.setWarranty(0);
                    }
                    break;
                default:
                    break;
            }

        });

        DomText price = page.getFirstByXPath("//strong[contains(@class, 'sizeD lH35 inlineBlock vMiddle')]/text()");
        car.setPrice(Double.parseDouble(price.getTextContent().replaceAll("\\s+", "").replaceAll("[^0-9]", "")));

        car.setBrand(super.getBrand());
        car.setModel(super.getModel());

        car.setCarIdSite(carIdentifier.getId());
        car.setUrl(carIdentifier.getUrl().toString());

        DomText version = page.getFirstByXPath("//div[contains(@class, 'versionTxt txtGrey7C sizeC mB10 hiddenPhone')]/text()");
        car.setVersion(version.getTextContent().replaceAll("\n", "").trim());

        car.setAdded(super.getLocalDateTime());

        car.setBatch(super.getBatchName());

        return car;
    }


    public List<HtmlPage> getListingPagesFromScript() {
        List<HtmlPage> htmlPages = new ArrayList<>();
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        String message = "";

//        try {
//            // Execute command
//            String command = String.format("node script/screenshot --link %s --bashName %s --date %s",
//                    super.getUrl(), super.getBatchName(), super.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")));
//            Process process = Runtime.getRuntime().exec(command);
//
//            message = IOUtils.toString(process.getInputStream(), "UTF-8");
//
//            System.out.println("*** "+message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        message= "[{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_1.html\"},{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_2.html\"},{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_3.html\"},{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_4.html\"},{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_5.html\"}]";

        message= "[{\"filename\":\"LaCentrale_SCIROCCO_2018-08-15T19-57-31_1.html\"}]";
        List<NodeOutput> nodeOutputs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            nodeOutputs = objectMapper.readValue(message, new TypeReference<List<NodeOutput>>() {
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        nodeOutputs.forEach(nodeOutput -> {
            try {
                htmlPages.add(webClient.getPage(Paths.get(nodeOutput.getFilename()).toUri().toURL()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        return htmlPages;

    }

    private List<CarIdentifier> findCarIdentifiers(HtmlPage htmlPage) {
        List<CarIdentifier> carIdentifiers = new ArrayList<>();

        List<DomElement> elements = htmlPage.getByXPath("//div[contains(@class, 'adContainer')]");
        elements.forEach(item -> {
            try {
                System.out.println(htmlPage.getUrl()+" "+ item);
                DomNodeList<HtmlElement> anchors = item.getElementsByTagName("a");
                HtmlAnchor anchor = (HtmlAnchor) anchors.get(anchors.size()-1);
                String id = anchor.getId();
                URL url = new URL(super.getBaseUrl() + anchor.getAttribute("href"));
                DomElement priceSpan = item.getFirstElementChild().getFirstByXPath("//div[contains(@class, 'adContainer')]/a[@id='" + id + "']//nobr/span[2]");
                Double price = Double.parseDouble(priceSpan.getFirstChild().toString().replaceAll("\\s+", "").replaceAll("[^0-9]", ""));
                carIdentifiers.add(new CarIdentifier(id, url, price));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
        return carIdentifiers;
    }

    private HtmlPage getAnoucementPage(CarIdentifier carIdentifier) throws Exception {
        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        String message = "";

        try {
            // Execute command
            String command = String.format("node script/findcar --link %s --bashName %s --date %s --carIdSite %s",
                    carIdentifier.getUrl().toString(), super.getBatchName(), super.getLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")), carIdentifier.getId());

            Process process = Runtime.getRuntime().exec(command);

            message = IOUtils.toString(process.getInputStream(), "UTF-8");

            System.out.println("*** " + message);
        } catch (IOException e) {
            log.error("Impossible to get car ad : {}", e.getCause());
            e.printStackTrace();
        }

        //message ="[{\"filename\":\"oklm_3000_1.html\"},{\"filename\":\"oklm_3000_2.html\"},{\"filename\":\"oklm_3000_3.html\"},{\"filename\":\"oklm_3000_4.html\"},{\"filename\":\"oklm_3000_5.html\"}]\n";

        ObjectMapper objectMapper = new ObjectMapper();
        NodeOutput nodeOutput;
        try {
            nodeOutput = objectMapper.readValue(message, NodeOutput.class);
            return webClient.getPage(Paths.get(nodeOutput.getFilename()).toUri().toURL());
        } catch (IOException e) {
            throw new Exception("Impossible de récupèrer l'annonce");
        }


    }


}
