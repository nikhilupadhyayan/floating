package android.health.dietlogging;

/**
 * Class is a food object, which contains the information regarding each
 * item added to a Meal. Denoted by a String name, and the nutrition info attached
 * with said string
 * 
 * @author John Mauldin
 */
public class Food {

	//String denoting the name of a specific food
	String name;
	
	//Integer denoting the total calories of a specific food
	int calories;
	
	//the attached Nutrition Info for a specific piece of food
	NutritionInfo info;
	
	/**
	 * Constructor for a Food object: requires specific name
	 * which corresponds to a NutritionInfo object within
	 * the pre-made repository of food items 
	 */
	public Food(String name, NutritionInfo info, int calories){
		this.name = name;
		this.info = info;
		this.calories = calories;
	}
	
	/**
	 * Returns the {@link NutritionInfo} object for this food
	 * @return The NutritionInfo item for this food
	 */
	String getNutritionInfo(){
		return (this.info).getInfo();
	}
	
	/**
	 * Returns the number of calories within this food
	 * @return The number of calories this food contains.
	 */
	int getCalories(){
		return this.calories;
	}
	
	/**
	 * Returns the name of this Food item
	 * @return The name/description of this Food item
	 */
	String getFood(){
		return name;
	}
}
