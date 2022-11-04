package de.buw.se4de;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Rezeptbuch {

    ArrayList<Rezept> rezepte = new ArrayList<Rezept>();

    void init() {
        System.out.println("Rezeptbuch geöffnet");

        // Rezepte laden
        load("src/main/resources/rezeptbuch.csv");

        run();
    }

    void load(String pfad) {
		try (Reader reader = Files.newBufferedReader(Paths.get(pfad));
			@SuppressWarnings("deprecation")
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
                for (CSVRecord csvRecord : csvParser) {
                    String name = csvRecord.get("name");
                    String zutatenVoll = csvRecord.get("zutaten");
                    String personenStr = csvRecord.get("personen");
                    String kategorienVoll = csvRecord.get("kategorien");
                    String zeit = csvRecord.get("zeit");
                    String zubereitung = csvRecord.get("zubereitung").replaceAll(";", "\n");

                    String[] zutaten = zutatenVoll.split(";");
                    int personen = Integer.parseInt(personenStr);
                    String[] kategorien = kategorienVoll.split(";");

                    Rezept temp = new Rezept(name, zutaten, personen, kategorien, zeit, zubereitung);
                    rezepte.add(temp);
                }
		} catch (IOException e) {
			e.printStackTrace();
		}
    } 

    void run() {

        for (Rezept rezept : rezepte) {
            System.out.println(rezept.name);
            System.out.println(rezept.zubereitung);
            System.out.println("Für " + rezept.personen + " Personen!");
        }

        // Wahl ermöglichen
        // Rezepte zeigen

        // Rezepte ändern

        // Rezepte löschen

        // Rezepte importieren

        // Rezepte exportieren

        // Rezepte suchen
    }
}