package de.buw.se4de;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Einkaufsliste {
    String item;

    public static void main (String[] args){
        String line = "";
        String splitBy = ",";
        try {
            BufferedReader a = new BufferedReader(new FileReader("liste.csv"));
            while ((line = a.readLine()) != null)   //returns a Boolean value
            {
                System.out.println(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

