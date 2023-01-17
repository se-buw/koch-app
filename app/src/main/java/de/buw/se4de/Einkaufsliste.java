package de.buw.se4de;

import org.apache.commons.csv.CSVFormat; //I added libraries by maven to the project structure
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;

public class Einkaufsliste {
    private static final String LISTE = "./src/main/resources/liste.csv";

    String set_inhalt(){

        String inhalt = "";
        File f = new File(LISTE);
        //opening the reader and parser for the csv file
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
              CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT) ){
            //looping through the lines in the csv file
            for (CSVRecord csvRecord : csvParser) {
                String item = csvRecord.get(0);
                String menge = csvRecord.get(1);
                //printing each line
                inhalt = inhalt.concat(item + " " + menge + "\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return inhalt;
    }

    void save(String inhalt){
        File f = new File(LISTE);
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)){
            for (String zeile : inhalt.split("\n")) {
                String[] temp = zeile.split(" ");
                String item = temp[0];
                String menge = temp[1];
                csvPrinter.printRecord(item, menge);
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
}

