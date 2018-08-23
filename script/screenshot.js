const puppeteer = require("puppeteer");
const fse = require('fs-extra'); // v 5.0.0
const program = require('commander');

program.version("0.1.0")
    .option("-l, --link [value]'", 'First listing page')
    .option("-bn, --bashName [value]'", "Bash for parsing")
    .option("-d, --date [value]'", "Date")
    .parse(process.argv);

let json =[];
let pageNumber = 1;

const getHtmlFiles = async () => {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto("https://www.lacentrale.fr/listing?energies=ess&gearbox=MANUAL&makesModelsCommercialNames=VOLKSWAGEN%3ASCIROCCO&options=&page=1");

    let bodyHTML = await page.evaluate(() => document.body.innerHTML);
    let filename = formatJsonEntry(program.bashName, program.date, pageNumber);
    fse.outputFile(filename, bodyHTML);
    json.push(formatJsonLine(filename));

    while ((await page.$("#recherche-react > div > div > section > section.mainCol > div:nth-child(2) > section > div > ul > li.arrow-btn.disabled > a > i.pictoArrowR") !== null) === false) {
        pageNumber++;
        await page.click("#recherche-react > div > div > section > section.mainCol > div:nth-child(2) > section:nth-child(3) > div > ul > li:nth-child(7) > a");
        await page.waitFor(3000);
        filename = formatJsonEntry(program.bashName, program.date, pageNumber);
        fse.outputFile(filename, await page.evaluate(() => document.body.innerHTML));
        json.push(formatJsonLine(filename));
    }

    await browser.close();
    console.log(JSON.stringify(json));
};

function formatJsonLine(filename){
    return {"filename": filename};
}

function formatJsonEntry(bashName, date, page) {
    return bashName + "_" + date + "_" + page + ".html";
}

getHtmlFiles();




