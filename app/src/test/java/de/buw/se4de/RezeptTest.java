package de.buw.se4de;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RezeptTest {
	@Test
	void testIngredientStringEmpty(){
		Rezept r = new Rezept(null, new ArrayList<>(), null, null, null, null);

		assertEquals("", r.ingredientString(false));
		assertEquals("", r.ingredientString(true));
	}

	@Test
	void testIngredientStringOne(){
		Rezept r = new Rezept(null, new ArrayList<>(), null, null, null, null);
		r.ingredients.add(new Ingredient(0, null, "Schokolade"));

		assertNotEquals("", r.ingredientString(false));
	}
}
