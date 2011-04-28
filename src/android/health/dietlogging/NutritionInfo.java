package android.health.dietlogging;

import java.io.File;

/**
 * This Class takes the values from the repository of information
 * and creates an object that contains the various pieces of
 * nutrition info needed for reference by the user.
 * 
 * @author John Mauldin
 */
public class NutritionInfo {
	
	/**
	 * This constructs a NutritionInfo based off of the specified nutrient amounts.
	 */
	public NutritionInfo(double protein, double carbs, double fiber, double sugar, double calcium,
			double iron, double magnesium, double phosphorus, double potassium, double sodium,
			double zinc, double vit_c, double vit_b6, double vit_b12, double vit_aiu,
			double vit_arae, double vit_e, double vid_d, double vit_k, double cholesterol,
			double calories, String allInfo, String name){
		this.protein = protein;
		this.carbohydrates = carbs;
		this.fiber = fiber;
		this.sugar = sugar;
		this.calcium = calcium;
		this.iron = iron;
		this.magnesium = magnesium;
		this.phosphorus = phosphorus;
		this.potassium = potassium;
		this.sodium = sodium;
		this.zinc = zinc;
		this.vit_c = vit_c;
		this.vit_b6 = vit_b6;
		this.vit_b12 = vit_b12;
		this.vit_a_iu = vit_aiu;
		this.vit_a_rae = vit_arae;
		this.vit_e = vit_e;
		this.vit_k = vit_k;
		this.cholesterol = cholesterol;
		this.calories = calories;
		
		this.allInfo = allInfo;
		this.name = name;
	}
	
	//keeps track of the amount of protein
	double protein;
	
	//keeps track of the amount of carbohydrates
	double carbohydrates;
	
	//keeps track of the amount of fiber
	double fiber;
	
	//keeps track of the amount of sugar
	double sugar;
	
	//keeps track of the amount of calcium
	double calcium;
	
	//keeps track of the amount of iron
	double iron;
	
	//keeps track of the amount of magnesium
	double magnesium;
	
	//keeps track of the amount of phosphorus
	double phosphorus;
	
	//keeps track of the amount of Potassium
	double potassium;
	
	//keeps track of the amount of Sodium
	double sodium;
	
	//keeps track of the amount of Zinc
	double zinc;
	
	//keeps track of the amount of Vit C
	double vit_c;
	
	//keeps track of the amount of Vit B6
	double vit_b6;
	
	//keeps track of the amount of Vit B12
	double vit_b12;
	
	//keeps track of the amount of Vit A IU
	double vit_a_iu;
	
	//keeps track of the amount of Vit A RAE
	double vit_a_rae;
	
	//keeps track of the amount of Vit E
	double vit_e;
	
	//keeps track of the amount of Vit D
	double vit_d;
	
	//keeps track of the amount of Vit K
	double vit_k;
	
	//keeps track of the amount of cholesterol
	double cholesterol;
	
	//keeps track of the amount of calories in a specific food
	double calories;
	
	//String that has all of the data in one string
	String allInfo;
	
	//String that has the name of the food in it
	String name;

	/**
	 * Array of constants that keep track of the recommended daily value for
	 * for each piece of nutrition info.
	 * Each value in terms of grams, except for Calories
	 * 0 - protein
	 * 1 - carbs
	 * 2 - fiber
	 * 3 - suger
	 * 4 - calcium
	 * 5 - iron
	 * 6 - magnesium
	 * 7 - phosphorus
	 * 8 - potassium
	 * 9 - sodium
	 * 10 - zinc
	 * 11 - vit_c
	 * 12 - vit_b6
	 * 13 - vit_b12
	 * 14 - vit_a_iu
	 * 15 - vit_a_rae
	 * 16 - vit_e
	 * 17 - vit_d
	 * 18 - vid_k
	 * 19 - cholesterol
	 * 20 - calories
	 * Each value will be defined from standards established by the  USDA
	 * Each value is based on a 2000 Calorie Intake
	 */
	final double Daily[] = { 50,300,25,40,10,.18,4,10,35,24,.015,.6,.02,.0006,
			5000, 5000, 30, 400, .008, .03, 2000};
	
	/**
	 * Returns the nutritional info condensed into one string
	 * @return All nutrition information stored in a single string for simplicity
	 */
	String getInfo(){
		return allInfo;
	}
	
	/**
	 * Returns a specified piece of nutrition info
	 * 
	 * @param instance - The index of nutrient information to return.
	 * @return The nutrient information specified. -1 if this index does not exist.
	 */
	double getVariable(int instance){
		if(instance == 0)
			return this.protein;
		if(instance == 1)
			return this.carbohydrates;
		if(instance == 2)
			return this.fiber;
		if(instance == 3)
			return this.sugar;
		if(instance == 4)
			return this.calcium;
		if(instance == 5)
			return this.iron;
		if(instance == 6)
			return this.magnesium;
		if(instance == 7)
			return this.phosphorus;
		if(instance == 8)
			return potassium;
		if(instance == 9)
			return sodium;
		if(instance == 10)
			return zinc;
		if(instance == 11)
			return vit_c;
		if(instance == 12)
			return vit_b6;
		if(instance == 13)
			return vit_b12;
		if(instance == 14)
			return vit_a_iu;
		if(instance == 15)
			return vit_a_rae;
		if(instance == 16)
			return vit_e;
		if(instance == 17)
			return vit_d;
		if(instance == 18)
			return vit_k;
		if(instance == 19)
			return cholesterol;
		if(instance == 20)
			return calories;
			
		else return -1;
	}

	double getSodium(){return this.sodium;}
	double getCholesterol(){return this.cholesterol;}
	double getCarbohydrates(){return this.carbohydrates;}
	double getFiber(){return this.fiber;}
	double getPotassium(){return this.potassium;}
	double getProtein(){return this.protein;}
	double getCalories(){return this.calories;}
	double getSuger(){return this.sugar;}
	double getCalcium(){return this.calcium;}
	double getIron(){return this.iron;}
	double getMagnesium(){return this.magnesium;}
	double getPhosphorus(){return this.phosphorus;}
	double getZinc(){return this.zinc;}
	double getVitC(){return this.vit_c;}
	double getVitB6(){return this.vit_b6;}
	double getVitB12(){return this.vit_b12;}
	double getVitAIU(){return this.vit_a_iu;}
	double getVitARAE(){return this.vit_a_rae;}
	double getVitE(){return this.vit_e;}
	double getVitD(){return this.vit_d;}
	double getVitK(){return this.vit_k;}
	
	/**
	 * Returns the daily percentage of a specific variable that corresponds to the
	 * list of daily values
	 * 
	 * @param int should correspond to which piece of nutrition info is needed
	 * 
	 * returns the recommended daily amount divided by the amount within the piece of food
	 */
	int getDailyPercent(int variable){
		int percent = 0;
		if(variable >= 0 && variable <= 7)
			percent = (int)((this.getVariable(variable)/Daily[variable])*100);
		return percent;
	}
}

