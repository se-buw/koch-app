package de.buw.se4de;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RezeptTest {
	@Test
	void testConstructor(){
		String name = "Eierkuchen";
		Ingredient eier = new Ingredient(1, "st", "Eier");
		Ingredient mehl = new Ingredient(150, "g", "Mehl");
		ArrayList<Ingredient> zutaten = new ArrayList<Ingredient>();
		zutaten.add(eier);
		zutaten.add(mehl);
		String personen = "2";
		String[] kategorien = null;
		String zeit = "30 min";
		String zubereitung = "backen";

		Rezept eierkuchen = new Rezept(name, zutaten, personen, kategorien, zeit, zubereitung,"Gut");

		assertEquals(name, eierkuchen.name);
		assertEquals(zutaten, eierkuchen.ingredients);
		assertEquals(personen, eierkuchen.personen);
		assertEquals(kategorien, eierkuchen.kategorien);
		assertEquals(zeit, eierkuchen.zeit);
		assertEquals(zubereitung, eierkuchen.zubereitung);
		assertEquals("Gut", eierkuchen.rating);
		}
	}
