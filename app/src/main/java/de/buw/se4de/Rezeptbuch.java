package de.buw.se4de;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import java.awt.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Rezeptbuch {

    ArrayList<Rezept> rezepte = new ArrayList<Rezept>();

    void init() {
        System.out.println("Rezeptbuch geöffnet");

        // Rezepte laden
        rezepte.addAll(load("src/main/resources/rezeptbuch.csv"));

        initWindow();
    }

    ArrayList<Rezept> load(String pfad) {
        ArrayList<Rezept> temp = new ArrayList<Rezept>();
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

                    Rezept rezept = new Rezept(name, zutaten, personen, kategorien, zeit, zubereitung);
                    temp.add(rezept);
                }
		} catch (IOException e) {
			e.printStackTrace();
		}
        return temp;
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

    boolean initWindow() {
        JFrame rezeptWindow = new JFrame("Rezeptbuch");
		rezeptWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		rezeptWindow.setSize(1280, 720);
        rezeptWindow.setResizable(false);
		
        JPanel auswahlPanel = new JPanel(new GridLayout(0, 5, 20, 20));

        for (Rezept rezept : rezepte) {
            JButton tempButton = new JButton(rezept.name);
            // knopf soll Fenster öffnen, mit dem man das Rezept bearbeiten kann

            tempButton.setPreferredSize(new Dimension(0, 200));
            auswahlPanel.add(tempButton);
        }

        auswahlPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JScrollPane auswahlScrollPanel = new JScrollPane(auswahlPanel);
        auswahlScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        auswahlScrollPanel.setPreferredSize(new Dimension(1280, 500));

        auswahlScrollPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel suchPanel = new JPanel();
        JLabel suchNameLabel = new JLabel("Name:");
        JLabel suchKategorieLabel = new JLabel("Kategorien:");
        JTextField suchNameTextField = new JTextField();
        JTextField suchKategorieTextField = new JTextField();
        JButton suchButton = new JButton("Suchen");
        // Button soll Suche starten

        suchNameTextField.setColumns(45);
        suchKategorieTextField.setColumns(45);

        suchKategorieTextField.setToolTipText("Man kann mehrere Kategorien mit \",\" angeben");

        suchPanel.add(suchNameLabel);
        suchPanel.add(suchNameTextField);
        suchPanel.add(suchKategorieLabel);
        suchPanel.add(suchKategorieTextField);
        suchPanel.add(suchButton);
        
        rezeptWindow.getContentPane().add(suchPanel, BorderLayout.NORTH);
        rezeptWindow.getContentPane().add(auswahlScrollPanel);
        
		rezeptWindow.setVisible(true);

        return true;
    }
}