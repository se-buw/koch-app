package de.buw.se4de;

public class Rezept {
    String name;
    String[] zutaten;
    String personen;
    String[] kategorien;
    String zeit;
    String zubereitung;

    public Rezept(String name, String[] zutaten, String personen, String[] kategorien, String zeit, String zubereitung) {
        this.name = name;
        this.zutaten = zutaten;
        this.personen = personen;
        this.kategorien = kategorien;
        this.zeit = zeit;
        this.zubereitung = zubereitung;
    }

    public String toString(){ 
        return name;  
    }  
}