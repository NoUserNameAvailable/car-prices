//package com.nousernameavailable.carprices.carprices;
//
//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.*;
//import com.nousernameavailable.carprices.carprices.repository.CarRepository;
//import fr.matlap.ScrapCars.model.Car;
//import fr.matlap.ScrapCars.model.CarIdentifier;
//import fr.matlap.ScrapCars.model.JobLog;
//import fr.matlap.ScrapCars.repository.CarRepository;
//import org.apache.commons.io.FileUtils;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Service
//class Scrapper {
//
//    private static final Logger log = LoggerFactory.getLogger(Scrapper.class);
//
//    @PersistenceContext
//    private EntityManager em;
//
//    @Autowired
//    private CarRepository carRepository;
//
//    private String site = "LaCentrale";
//    private String url = "https://www.lacentrale.fr/listing?energies=ess&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&gearbox=MANUAL";
//    private String baseUrl = "https://www.lacentrale.fr";
//    private String model = "SCIROCCO";
//    private String brand = "VOLKSWAGEN";
//    private String batchName = setBatchName(site, model);
//
//    private String setBatchName(String site, String model) {
//        return site + "_" + model;
//    }
//
//    HtmlPage getHtmlPage() throws Exception {
//        WebClient client = new WebClient();
//        client.getOptions().setCssEnabled(false);
//        client.getOptions().setJavaScriptEnabled(false);
//        try {
//            String searchUrl = url;
//            HtmlPage page = client.getPage(searchUrl);
//            return page;
//        } catch (Exception e) {
//            throw new Exception("Impossible de prendre la page web " + e.getMessage());
//        }
//    }
//
////    List<HtmlPage> getAllPages(List<HtmlPage> parsedPages, int lastPageParsed) throws IOException {
////        int pageToFind = lastPageParsed + 1;
////        HtmlPage page = parsedPages.get(0);
////        HtmlAnchor element = page.getFirstByXPath("//html/body/section/section/div/div/div/section/section[3]/div[2]/section[1]/div/ul/li/a[text()='" + pageToFind + "']");
////
////        if (element != null) {
////            parsedPages.add(element.click());
////            return getAllPages(parsedPages, pageToFind);
////        } else {
////            return parsedPages;
////        }
////
////    }
//
//    List<HtmlPage> getAllListingPages() {
//
//        return  null;
//    }
//
//    List<CarIdentifier> findLink(HtmlPage page) {
//        List<CarIdentifier> carIdentifiers = new ArrayList<>();
//
//        List<DomElement> elements = page.getByXPath("//div[contains(@class, 'adContainer')]");
//        elements.forEach(item -> {
//            try {
//                DomElement anchor = item.getFirstElementChild();
//                String id = anchor.getId();
//                URL url = null;
//                url = new URL(baseUrl + anchor.getAttribute("href"));
//                DomElement priceSpan = item.getFirstElementChild().getFirstByXPath("//div[contains(@class, 'adContainer')]/a[@id='" + id + "']//nobr/span[2]");
//                Double price = Double.parseDouble(priceSpan.getFirstChild().toString().replaceAll("\\s+", "").replaceAll(",", ".").replace("€", ""));
//                carIdentifiers.add(new CarIdentifier(id, url, price));
//
//                Document document = Jsoup.connect(page.getUrl().toString()).get();
//                FileUtils.writeStringToFile(new File(page.getTitleText()), document.outerHtml());
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });
//
//        return carIdentifiers;
//    }
//
//    List<CarIdentifier> findNewCars(List<CarIdentifier> carIdentifiers) {
//        List<CarIdentifier> carToInsert = new ArrayList<>();
//
//        carIdentifiers.forEach(item -> {
//            if (!carRepository.existsByCarIdSite(item.getId())) {
//                carToInsert.add(item);
//            }
//        });
//
//        return carToInsert;
//    }
//
//    List<CarIdentifier> findCarsToUpdate(List<CarIdentifier> carIdentifiers) {
//        List<CarIdentifier> carsToUpdate = new ArrayList<>();
//        carIdentifiers.forEach(item -> {
//            Optional<Car> car = carRepository.findByCarIdSiteAndModifiedIsNullAndDeletedIsNull(item.getId());
//
//            if (car.isPresent() && Double.compare(car.get().getPrice(), item.getPrice()) != 0) {
//                carsToUpdate.add(item);
//            }
//        });
//
//        return carsToUpdate;
//    }
//
//
//    List<Car> findNoLongerAvailablesCars(List<CarIdentifier> carIdentifiers) {
//        List<Car> carsToDelete = new ArrayList<>();
//
//        List<String> carIdSites = carIdentifiers.stream().map(CarIdentifier::getId).collect(Collectors.toList());
//        carRepository.findByNotCarIdSiteAndBatchAndModifiedIsNull(carIdSites, batchName).ifPresent(carsToDelete::addAll);
//
//        return carsToDelete;
//    }
//
//    void carsToInsert(List<CarIdentifier> carIdentifiers) {
//        carIdentifiers.forEach(item -> {
//            try {
//                carRepository.save(findCarDescription((item)));
//            } catch (IOException e) {
//                log.error("Impossible to open the car link (" + item.getUrl() + ") " + e.getMessage());
//            }
//        });
//    }
//
//    Car findCarDescription(CarIdentifier carIdentifier) throws IOException {
//
//
//        Car car = new Car(carIdentifier.getId());
//
//        WebClient client = new WebClient();
//        client.getOptions().setCssEnabled(false);
//        client.getOptions().setJavaScriptEnabled(false);
//
//        HtmlPage page = client.getPage(carIdentifier.getUrl());
//
//        HtmlUnorderedList ul = page.getFirstByXPath("//ul[contains(@class, 'infoGeneraleTxt column2')]");
//        ul.getChildElements().forEach(li -> {
//
//            String categorie = li.getElementsByTagName("h4").get(0).getTextContent();
//            String value = li.getElementsByTagName("span").get(0).getTextContent().trim();
//            switch (categorie.substring(0, categorie.length() - 2)) {
//                case "Année":
//                    car.setYear(Integer.decode(value));
//                    break;
//                case "Kilométrage":
//                    car.setKmAge(Integer.decode(value.replaceAll(" ", "").replaceAll("km", "")));
//                    break;
//                case "Puissance fiscale":
//                    car.setFiscalPower(Integer.decode(value.replaceAll("CV", "").replaceAll("\\s+", "").replaceAll("[^0-9]", "")));
//                    break;
//                case "Puissance din":
//                    car.setPower(Integer.decode(value.replaceAll("CV", "").replaceAll("\\s+", "").replaceAll("[^0-9]", "")));
//                    break;
////                case "Boîte de vitesse":
////
////                case "Énergie":
//
//                case "Mise en circulation":
//                    value = value.replaceAll("\\p{javaSpaceChar}", "");
//                    String part[] = value.split("/");
//                    car.setEntryIntoService(LocalDate.of(Integer.parseInt(part[2]), Integer.parseInt(part[1]), Integer.parseInt(part[0])));
//                    break;
//                case "Couleur intérieure":
//                    car.setInteriorColor(value.trim());
//                    break;
//                case "Couleur extérieure":
//                    car.setExteriorColor(value.trim());
//                    break;
//                case "Garantie":
//                    try {
//                        car.setWarranty(Integer.decode(value.replaceAll("[^0-9]", "").replaceAll(" ", "")));
//                    } catch (NumberFormatException e) {
//                        car.setWarranty(0);
//                    }
//                    break;
//                default:
//                    break;
//            }
//
//        });
//
//        DomText price = page.getFirstByXPath("//strong[contains(@class, 'sizeD lH35 inlineBlock vMiddle')]/text()");
//        car.setPrice(Double.parseDouble(price.getTextContent().replaceAll("\\s+", "").replaceAll("[^0-9]", "")));
//
//        DomText brandAndModel = page.getFirstByXPath("/html/body/section/section/section[2]/article/div[1]/div[1]/div[1]/div[1]/h1/div[1]/text()");
//        car.setBrand(brandAndModel.getData().replaceAll("\n", "").split(" {24}")[1].trim());
//        car.setModel(brandAndModel.getData().replaceAll("\n", "").split(" {24}")[2].trim());
//
//        car.setCarIdSite(carIdentifier.getId());
//        car.setUrl(carIdentifier.getUrl().toString());
//
//        DomText version = page.getFirstByXPath("//div[contains(@class, 'versionTxt txtGrey7C sizeC mB10 hiddenPhone')]/text()");
//        car.setVersion(version.getTextContent().replaceAll("\n", "").trim());
//
//        car.setAdded(LocalDateTime.now());
//
//        car.setBatch(batchName);
//
//        return car;
//    }
//
//    List<Car> modifyCarDescription(CarIdentifier carIdentifier) {
//
//        Optional<Car> carOptional = carRepository.findByCarIdSite(carIdentifier.getId());
//
//        if (!carOptional.isPresent()) {
//            throw new IllegalArgumentException("A car should be updated but no found !" + carIdentifier);
//        }
//
//        LocalDateTime localDateTime = LocalDateTime.now();
//
//        Car oldCar = carOptional.get();
//        oldCar.setModified(localDateTime);
//
//        Car newCar = null;
//        try {
//            newCar = findCarDescription(carIdentifier);
//            newCar.setAdded(localDateTime);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return new ArrayList<Car>(Arrays.asList(oldCar, newCar));
//    }
//
//    public void carsToModify(List<CarIdentifier> carIdentifiers) {
//        carIdentifiers.forEach(item -> {
//            modifyCarDescription(item).forEach(i -> carRepository.save(i));
//        });
//    }
//
//    public void carsToDelete(List<Car> cars) {
//        cars.forEach(car -> {
//            carRepository.delete(car);
//        });
//    }
//
//
//    //    @Scheduled(fixedRate = 5000000)
//    public void test() throws MalformedURLException {
//        URL url = new URL("https://www.lacentrale.fr/auto-occasion-annonce-87101130398.html");
//        CarIdentifier carIdentifier = new CarIdentifier("W101130398", url, 7777.0);
//        List<Car> cars = modifyCarDescription(carIdentifier);
//        System.out.println(cars);
//
//    }
//
//    @Scheduled(fixedRate = 5000000)
//    public void rien() {
//        LocalDateTime startDT = LocalDateTime.now();
//        try {
//            List<HtmlPage> browserPages = new ArrayList<>();
//            List<CarIdentifier> carIdentifiers = new ArrayList<>();
//
//            browserPages.add(getHtmlPage());
//
////            browserPages = getAllPages(browserPages, 1);
////
//            browserPages.forEach(page -> {
//                carIdentifiers.addAll(findLink(page));
//                try {
//                    TimeUnit.SECONDS.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            carIdentifiers.forEach(System.out::println);
//
//            List<CarIdentifier> newCars = findNewCars(carIdentifiers);
//            log.info("New cars : " + newCars.size());
//
//            List<CarIdentifier> modifiedCars = findCarsToUpdate(carIdentifiers);
//            log.info("Modified cars : " + modifiedCars.size());
//
//            List<Car> deletedCars = findNoLongerAvailablesCars(carIdentifiers);
//            log.info("Deleted cars : " + deletedCars.size());
//
////            //Laucnh db operations
////            carsToInsert(newCars);
////            carsToModify(modifiedCars);
////            carsToDelete(deletedCars);
//
//
//            LocalDateTime endDT = LocalDateTime.now();
//            JobLog jobLog = new JobLog(model, startDT, endDT, "DONE", newCars.size(), modifiedCars.size(), deletedCars.size(), site, brand, url);
//
//        } catch (Exception e1) {
//            log.warn(e1.getMessage());
//        }
//    }
//
//
//}
