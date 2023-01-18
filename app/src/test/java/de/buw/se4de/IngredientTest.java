package de.buw.se4de;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class IngredientTest {
	@Test
	void testConstructor(){
		int amount = -4;
		String unit = "g";
		String name = "Luft";
		Ingredient i = new Ingredient(amount, unit, name);

		assertEquals(amount, i.amount);
		assertEquals(unit, i.unit);
		assertEquals(name, i.name);
	}

	@Test
	void testToString(){
		int amount = 4;
		String unit = "g";
		String name = "Luft";
		Ingredient i = new Ingredient(amount, unit, name);

		assertEquals("4g Luft", i.toString());
	}

	@Test
	void testAdjustAmountNegative(){
		int amount = 4;
		String unit = "";
		String name = "Luft";
		Ingredient i = new Ingredient(amount, unit, name);

		i.adjustAmount(2, 1);
		assertEquals(8, i.amount);
	}

	@Test
	void testAdjustAmountNullDesired(){
		int amount = 2;
		String unit = "g";
		String name = "Schokolade";
		Ingredient i = new Ingredient(amount, unit, name);

		i.adjustAmount(0, 1);
		assertEquals(0, i.amount);
		assertEquals(unit, i.unit);
	}

	@Test
	void testAdjustAmountSame(){
		int amount = 2;
		String unit = "g";
		String name = "Schokolade";
		Ingredient i = new Ingredient(amount, unit, name);

		i.adjustAmount(1, 1);
		assertEquals(amount, i.amount);
		assertEquals(unit, i.unit);
		assertEquals(name, i.name);
	}

	@Test
	void testAdjustAmountInteger(){
		int amount = 2;
		String unit = "g";
		String name = "Schokolade";
		Ingredient i = new Ingredient(amount, unit, name);

		i.adjustAmount(300, 3);
		assertEquals(200, i.amount);
		assertEquals(unit, i.unit);
		assertEquals(name, i.name);
	}

	@Test
	void testAdjustAmountNullRecipe(){
		int amount = 2;
		String unit = "g";
		String name = "Schokolade";
		Ingredient i = new Ingredient(amount, unit, name);

		i.adjustAmount(1, 0);
		assertEquals(0, i.amount);
	}
}
