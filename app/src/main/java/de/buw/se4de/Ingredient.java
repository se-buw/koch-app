package de.buw.se4de;

// The intent of this class is to allow us to dynamically adjust the amount of ingredients needed to create a recipe
// This way the user can simply enter how many portions they would like, and the program adjusts the amounts accordingly

// IMPORTANT: for conversion to work, ingredients in csv must be written in 'amount/unit/name;' format and
// edited ingredients must be written in 'amountunit name' format
public class Ingredient {
    int amount;
    float amount;
    String unit;
    String name;
}


    // allows saving and parsing of ingredient names and amounts

    public Ingredient(float amount, String unit, String name) {
        this.amount = amount;
        this.unit = unit;
        this.name = name;
    }
    public String toString(){ // creates string for printing in jpanels
        return amount + unit + " " + name;
    }
    public String toSaveString() {  // String stores amount units and name in 'amount/unit/name;' format for csv saving ex.: '4/cups/flower'
        return amount + "/" + unit + "/" + name + ";";
    }

    public void adjustAmount(float desiredServings, float recipeServings) {      // we pass the amount the user desires, as well as the servings the original recipe can generate into the function
        // just to be safe we ensure that the user wants a non-zero number of servings
        if (desiredServings != 0.0f) {
            float singleServing = amount / recipeServings;
            amount = singleServing * desiredServings;
        }
    }
}