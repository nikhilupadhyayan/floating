package android.health.dietlogging;

import java.io.File;

/*
 * This Class takes the values from the repository of information
 * and creates an object that contains the various pieces of
 * nutrition info needed for reference, upon request, by the user
 * 
 * @author John Mauldin
 */
public class NutritionInfo {
	
	//TODO: Need to write code that will create NutritionInfo object from the NTInfo
	public NutritionInfo(File filename){
		
	}
	

	// track of the amount of sodium in a specific food
	double sodium;
	
	//keeps track of the amount fat in a specific food
	double totalFat;
	
	//keeps track of the amount of cholesterol in a specific food
	double cholesterol;
	
	//keeps track of the amount of carbohydrates in a specific food
	double carbohydrates;

	//keeps track of the amount of fiber in a specific food
	double fiber;

	//keeps track of the amount of sugar in a specific food
	double Potassium;

	//keeps track of the amount of protein in a specific food
	double protein;
	
	//keeps track of the amount of calories in a specific food
	double calories;

	/*
	 * Array of constants that keep track of the recommended daily value for
	 * for each piece of nutrition info.
	 * Each value in terms of grams, except for Calories
	 * 0 - sodium (in milligrams)
	 * 1 - totalFat
	 * 2 - cholesterol
	 * 3 - Carbohydrates
	 * 4 - Fiber
	 * 5 - Potassium
	 * 6 - Protein
	 * 7 - Calories
	 * Each value will be defined from standards established by the  USDA
	 * Each value is based on a 2000 Calorie Intake
	 */
	final double Daily[] = { 2.4, 65, 0.3, 300, 25, 3.5, 50, 2000};
	
	//Returns the nutritional info condensed into one string
	String getInfo(){
		//TODO:Condense and return the nutrition info into a string
		return null;
	}
	
	//TODO: May need to rethink the variable as an enumerator
	//Returns a specified piece of nutrition info
	//if instance is out of bounds, returns -1
	double getVariable(int instance){
		if(instance == 0)
			return this.sodium;
		if(instance == 1)
			return this.totalFat;
		if(instance == 2)
			return this.cholesterol;
		if(instance == 3)
			return this.carbohydrates;
		if(instance == 4)
			return this.fiber;
		if(instance == 5)
			return this.Potassium;
		if(instance == 6)
			return this.protein;
		if(instance == 7)
			return this.calories;
		else return -1;
	}

	double getSodium(){return this.sodium;}
	double getTotalFat(){return this.totalFat;}
	double getCholesterol(){return this.cholesterol;}
	double getCarbohydrates(){return this.carbohydrates;}
	double getFiber(){return this.fiber;}
	double getPotassium(){return this.Potassium;}
	double getProtein(){return this.protein;}
	double getCalories(){return this.calories;}
	
	
	//TODO:This may belong in another class
	/*
	 * Returns the daily percentage of a specific variabale that corresponds to the
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

