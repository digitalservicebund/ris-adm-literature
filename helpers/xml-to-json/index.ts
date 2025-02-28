import { argv, exit } from "node:process";
import fs from "node:fs";
import { convertXML } from "simple-xml-to-json";

const inputFilePath: string = argv[2];
if (!inputFilePath) {
  console.log("No input path given. Exit.");
  exit(1);
}

const xmlText = fs.readFileSync(inputFilePath, {
  encoding: "utf8",
});

const rawJson = convertXML(xmlText);

console.log(JSON.stringify(rawJson));

// define interfae for output (as required for "Periodika")
// map rawJson to intended interface
