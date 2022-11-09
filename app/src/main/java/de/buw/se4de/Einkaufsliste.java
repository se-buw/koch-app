package de.buw.se4de;

import org.apache.commons.csv.CSVFormat; //I added libraries by maven to the project structure
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;

public class Einkaufsliste {
    private static final String LISTE = "src/main/resources/liste.csv";

    public static void einkaufen() {
        print();
        add_items();
        print();
        delete_items();
        print();
    }

    public static void print() {
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
    public static void add_items(){
        File f = new File(LISTE);
        //checking if this file is already there, otherwise error
        try {//result is ignored because the existence of the file matters only
            f.createNewFile();
        } catch (IOException e) {
            System.err.print("File could not be created.");
            throw new RuntimeException(e);
        }
        //opening the writer to write into the csv file
        try(
                BufferedWriter writer = new BufferedWriter(new FileWriter(f, true));
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)){
            //opening the scanner for user's input
            Scanner object = new Scanner(System.in);
            do {
                //asking the user for input
                System.out.println("Would you like to add something to your grocery list? (If no, then type 'no')");
                //scanning his answer
                String item = object.nextLine();
                //breaking the while loop if the user does not want to add any items
                if (item.equalsIgnoreCase("no")) { break; }
                //asking the user how much he wants to add of the item
                System.out.println("How much of it would you like to add?");
                String menge = object.nextLine();
                //recording those values in the csv file
                csvPrinter.printRecord(item, menge);
            } while (true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void delete_items() {
        File f = new File(LISTE);
        //creates array with all ingredients to rewrite the file after deleting an item
        List<String> list = new ArrayList<>();
        //reads csv file
        try ( BufferedReader reader = new BufferedReader(new FileReader(f));
              CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT)){
            //creates scanner for user's input
            Scanner object = new Scanner(System.in);
            //starts the option to 'delete' an item
            do {
                //Scanning user's input for deleting an item
                System.out.println("Would you like to delete something? (If no, then type 'no')");
                String item = object.nextLine();
                //breaks while loop if nothing should be deleted
                if (item.equalsIgnoreCase("no")) { break; }
                //adds all items and amounts to the array
                for (CSVRecord csvRecord : csvParser) {
                    list.addAll(Arrays.asList(csvRecord.get(0), csvRecord.get(1)));
                }
                //case if item is not on the list
                if (!list.contains(item)) {
                    System.out.println("This item is not on your list.");
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
                    }
                }
            } while (true);
            }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}

