package de.buw.se4de;

import org.apache.commons.csv.CSVFormat; //I added libraries by maven to the project structure
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Scanner;

import java.io.FileWriter;

public class Einkaufsliste {
    private static final String LISTE = "liste.csv";

    public static void einkaufen() {
        load();
        write();
        load();
    }

    public static void load() {
        File f = new File("src/main/resources/"+LISTE);
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
        /*try(
                FileWriter writer = new FileWriter(f);) {
            Scanner object = new Scanner(System.in);
            String item;
            do {
                System.out.println("Was möchtest du zur Einkaufsliste hinzufügen? (Wenn nichts, dann schreib 'nichts')");
                item = object.nextLine();
                if (item.equalsIgnoreCase("nichts")) { break; }
                System.out.println("Wie viel möchtest du davon hinzufügen?");
                String menge = object.nextLine();
                String line = item + "," + menge + "\n";
                writer.write(line);
                // Hier muss noch gespeichert werden
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/
        try(
                //BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
                //CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                //   .withHeader("Item", "Menge"));) {
                ){
            Scanner object = new Scanner(System.in);
            String item;
            do {
                //StringBuilder sb = new StringBuilder();
                System.out.println("Was möchtest du zur Einkaufsliste hinzufügen? (Wenn nichts, dann schreib 'nichts')");
                item = object.nextLine();
                if (item.equalsIgnoreCase("nichts")) { break; }
                System.out.println("Wie viel möchtest du davon hinzufügen?");
                String menge = object.nextLine();
                //String i = item + "," + menge + "\n";
                //writer.append(i);
                csvPrinter.printRecord(item, menge);
                // Hier muss noch gespeichert werden
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void delete() {
        File f = new File("src/main/resources/"+LISTE);
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
              CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT) ){
            Scanner object = new Scanner(System.in);
            String item;
            do {
                System.out.println("Möchtest du etwas löschen? (Wenn nein, dann schreib 'nein')");
                item = object.nextLine();
                if (item.equalsIgnoreCase("nein")) { break; }
                // here you'll delete
            } while (true);
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

