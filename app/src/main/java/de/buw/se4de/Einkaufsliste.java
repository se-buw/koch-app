package de.buw.se4de;
import java.io.*;

import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Einkaufsliste {
    private static final String LISTE = "liste.csv";

    public static void main(String[] args) {
        load();
        write();
    }

    public static void load() {
        File f = new File("/Users/anastasiakozlova/Desktop/Koch-App/koch-app/app/src/main/resources/"+LISTE);
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
        CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT) ){
        for (CSVRecord csvRecord : csvParser) {
            String item = csvRecord.get(0);
            String menge = csvRecord.get(1);
            System.out.println(item + " " + menge);
        }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void write(){
        File f = new File("/Users/anastasiakozlova/Desktop/Koch-App/koch-app/app/src/main/resources/"+LISTE);
        try {//result is ignored because the existence of the file matters only
            f.createNewFile();
        } catch (IOException e) {
            System.err.print("Datei konnte nicht erstellt werden.");
            throw new RuntimeException(e);
        }
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("Item", "Menge"));) {
            Scanner object = new Scanner(System.in);
            String item;
            do {
                System.out.println("Was möchtest du zur Einkaufsliste hinzufügen? (Wenn nichts, dann schreib 'nichts')");
                item = object.nextLine();
                if (item.equalsIgnoreCase("nichts")) { break; }
                System.out.println("Wie viel möchtest du davon hinzufügen?");
                String menge = object.nextLine();
                csvPrinter.printRecord(item, menge);
                // Hier muss noch gespeichert werden
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

