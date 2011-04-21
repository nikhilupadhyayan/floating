package android.health.dietlogging;

import java.util.LinkedList;

/*
 * This class is responsible for storing, managing, and allowing access to the history
 * of all the user's meals recorded during a day.
 * 
 *  @author John Mauldin
 */

public class MealList {
	
	/*
	 * Keeps track of the date in which said meal list was created (one meal list
	 * per day)
	 */
	int date;
	
	//Keeps track of how many calories are in the total list of meals
	int calories = 0;
	
	/*
	 * An LinkedList that holds all individuals meals for a meal list
	 */
	LinkedList<Meal> MealList;
	
	
	/*
	 * constructor for a new MealList for a specific date
	 */
	public MealList(int date){
		this.date = date;
	}
	
	/*
	 * Adds a meal to the array
	 * 
	 * @param meal object denoting the meal to be added to the list
	 */
	void addMeal(Meal meal){
		this.MealList.add(meal);
		calories = calories + meal.calories;
	}
	/*
	 * Removes a specific meal from the list
	 * 
	 * @param meal integer denoting the instance of the meal object to
	 *  be removed from the list
	 */
	void removeMeal(int meal){
		this.MealList.remove(this.MealList.get(meal));
	}
	
	
	/*
	 * Returns the desired meal object from the list
	 * 
	 * @param instance integer referring to a specific spot in the list
	 */
	Meal getMeal(int instance){
		return this.MealList.get(instance);
	}
}

