package de.buw.se4de;

import org.apache.commons.csv.CSVFormat; //I added libraries by maven to the project structure
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.BorderLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;

public class Einkaufsliste {
    private static final String LISTE = "./app/src/main/resources/liste.csv";

    private final Scanner scanner = new Scanner(System.in);

    private boolean running = false;
    private JFrame eWindow;
    /*
    public void einkaufen() {
        print();
        add_items();
        print();
        delete_items();
        print();
    }*/

    public JFrame init() {
        if (running) return eWindow;
        running = true;
        
        eWindow = new JFrame("Einkaufsliste");
		eWindow.setSize(400, 800);
		eWindow.setResizable(false);

        eWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                eWindow = null;
            }
        });

        JButton saveButton = new JButton("Speichern");
        JTextArea textArea = new JTextArea();

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

        textArea.setText(inhalt);

        saveButton.addActionListener(e -> {
            String neuerInhalt = textArea.getText();
            try(
            BufferedWriter writer = new BufferedWriter(new FileWriter(f));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)){
                for (String zeile : neuerInhalt.split("\n")) {
                    String[] temp = zeile.split(" ");
                    String item = temp[0];
                    String menge = temp[1];
                    csvPrinter.printRecord(item, menge);
                } 
            } catch (IOException err) {
                err.printStackTrace();
            }
        });

        eWindow.getContentPane().add(textArea);
        eWindow.getContentPane().add(saveButton, BorderLayout.NORTH);
        eWindow.setVisible(true);

        return eWindow;
    }

    public void print() {
        File f = new File(LISTE);
        //opening the reader and parser for the csv file
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
        CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT) ){
            //looping through the lines in the csv file
            for (CSVRecord csvRecord : csvParser) {
                String item = csvRecord.get(0);
                String menge = csvRecord.get(1);
                //printing each line
                System.out.println(item + " " + menge);
        }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Einkaufslistenelemente hinzufügen
    public void add_items(){
        File f = new File(LISTE);
        //checking if this file is already there, otherwise error
        try {//result is ignored because the existence of the file matters only
            f.createNewFile();
        } catch (IOException e) {
            System.err.print("Datei konnte nicht erstellt werden.");
            throw new RuntimeException(e);
        }
        //opening the writer to write into the csv file
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)){
            do {
                //asking the user for input
                System.out.println("Was möchten Sie noch zu Ihrer Einkaufsliste hinzufügen? (Falls nicht, schreiben Sie bitte 'nein')");
                //scanning his answer
                String item = scanner.nextLine();
                //breaking the while loop if the user does not want to add any items
                if (item.equalsIgnoreCase("nein")) { break; }
                //asking the user how much he wants to add of the item
                System.out.println("Wie viel brauchen Sie davon?");
                String menge = scanner.nextLine();
                //recording those values in the csv file
                csvPrinter.printRecord(menge, item);
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Einkaufslistenelemente löschen
    public void delete_items() {
        File f = new File(LISTE);
        //creates array with all ingredients to rewrite the file after deleting an item
        List<String> list = new ArrayList<>();
        //reads csv file
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
              CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT)){
            //starts the option to 'delete' an item
            do {
                //Scanning user's input for deleting an item
                System.out.println("Welchen Eintrag möchten Sie löschen? (Falls nicht, schreiben Sie bitte 'nein')");
                String item = scanner.nextLine();
                //breaks while loop if nothing should be deleted
                if (item.equalsIgnoreCase("nein")) { break; }
                //adds all items and amounts to the array
                for (CSVRecord csvRecord : csvParser) {
                    list.addAll(Arrays.asList(csvRecord.get(0), csvRecord.get(1)));
                }
                //case if item is not on the list
                if (!list.contains(item)) {
                    System.out.println("Dieser Eintrag befindet sich nicht auf der Liste.");
                }
                else {
                    //starting to overwrite the file
                    try (FileWriter writer = new FileWriter(f)) {
                        //all items except deleted one will be written into csv file
                        for (int i = 0; i < list.size() / 2; i++) {
                            String item2 = list.get(2 * i);
                            //if item2 is not users item, then write them into the csv file
                            if (!item2.equalsIgnoreCase(item)) {
                                String line = item2 + "," + list.get(2 * i + 1) + "\n";
                                writer.write(line);
                            }
                        }
                        //removes the item and amount from the array
                        int k = list.indexOf(item);
                        list.remove(k);
                        list.remove(k);
                        System.out.println(item + " wurde gelöscht");
                    }
                }
            } while (true);
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

