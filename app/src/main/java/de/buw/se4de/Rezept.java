package de.buw.se4de;

public class Rezept {
    String name;
    String[] zutaten;
    String personen;
    String[] kategorien;
    String zeit;
    String zubereitung;
    String rating = "Unbewertet"; // allows recipes to be rated

    public Rezept(String name, String[] zutaten, String personen, String[] kategorien, String zeit, String zubereitung) {
        this.name = name;
        this.zutaten = zutaten;
        this.personen = personen;
        this.kategorien = kategorien;
        this.zeit = zeit;
        this.zubereitung = zubereitung;
    }

    // Wir brauchen die toString() Methode um die Namen im Listenmodus für den Export anzuzeigen
    public String toString(){ 
        return name;  
    }  
}