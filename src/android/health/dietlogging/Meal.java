package android.health.dietlogging;

import java.util.LinkedList;

/*
 * This class is essentially a linked list responsible for storing, managing,
 * and allowing access to the food objects stored within a specific meal object.
 * 
 *  @author John Mauldin
 */

public class Meal {

	/*
	 * An LinkedList that holds a collections of all the specific food objects that
	 * make up said meal
	 */
	LinkedList<Food> FoodList;
	
	//May be unnecessary 
	/*
	 * Keeps track of how many calories are contained in said meal
	 */
	//int calories;
	
	
	/*
	 * Adds a food object to the list of food objects within a meal
	 */
	void addFood(Food food){
		this.FoodList.add(food);
	}
	
	/*
	 * Removes a food object from the list
	 * 
	 * @param food instance of food item to be deleted
	 */
	void removeFood(int food){
		this.FoodList.remove(this.FoodList.get(food));
	}
	
	/*
	 * Returns specified food item from the list
	 * 
	 * @param instance location of food item within the array
	 */
	Food getFood(int instance){
		return this.FoodList.get(instance);
	}
	
	/*
	 * Returns the added calorie total of every food item in the array
	 */
	int getMealCalories(){
		//TODO: For loop may effect speed
		int total = 0;
		for(int i = 0; i < FoodList.size(); i++){
			total += FoodList.get(i).calories;
		}
		//calories = total;
		return total;
	}
}
