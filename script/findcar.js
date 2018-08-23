const puppeteer = require("puppeteer");
const fse = require('fs-extra'); // v 5.0.0
const program = require('commander');

program.version("0.1.0")
    .option("-l, --link [value]'", "Announce link")
    .option("-bn, --bashName [value]'", "Bash for parsing")
    .option("-d, --date [value]'", "Date")
    .option("-d, --carIdSite [value]'", "Date")
    .parse(process.argv);

let json = {};
let pageNumber = 1;

const getHtmlFile = async () => {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    await page.goto(program.link);

    let bodyHTML = await page.evaluate(() => document.body.innerHTML);
    let filename = formatJsonEntry(program.bashName, program.date, program.carIdSite);
    fse.outputFile(filename, bodyHTML);
    json.filename = filename;

    await browser.close();
    console.log(JSON.stringify(json));
};

function formatJsonEntry(bashName, date, page) {
    return bashName + "_" + date + "_" + page + ".html";
}

getHtmlFile();




