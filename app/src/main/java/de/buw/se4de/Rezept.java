package de.buw.se4de;

import java.util.ArrayList;

public class Rezept {
    String name;
    ArrayList<Ingredient> ingredients;
    String personen;
    String[] kategorien;
    String zeit;
    String zubereitung;
    String rating; // allows recipes to be rated

    public Rezept(String name, ArrayList<Ingredient> zutaten, String personen, String[] kategorien, String zeit, String zubereitung,String r) {
        this.name = name;
        this.ingredients = zutaten;
        this.personen = personen;
        this.kategorien = kategorien;
        this.zeit = zeit;
        this.zubereitung = zubereitung;
        this.rating = r;
    }

    // Wir brauchen die toString() Methode um die Namen im Listenmodus für den Export anzuzeigen
    public String toString(){
        return name;
    }
}