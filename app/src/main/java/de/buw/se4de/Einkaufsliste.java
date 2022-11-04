package de.buw.se4de;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Einkaufsliste {
    String item;
    private static final String LISTE = "liste.csv";

    public static void main(String[] args) {
        write();
    }

    public static void write(){
        File f = new File("/Users/anastasiakozlova/Desktop/Koch-App/koch-app/app/src/main/resources/"+LISTE);
        try {//result is ignored because the existence of the file matters only
            f.createNewFile();
            System.out.print("Datei wurde erstellt.");
        } catch (IOException e) {
            System.err.print("Datei konnte nicht erstellt werden.");
            throw new RuntimeException(e);
        }
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("Item", "Menge"));) {
            System.out.print("Daten wurden erstellt.");
            csvPrinter.printRecord("Gurke", "2");
            csvPrinter.printRecord("Tomaten", "3");
            csvPrinter.printRecord("Käse", "1");
            // Hier speichern lassen
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

