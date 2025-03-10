package model;

public class Plate {
    private final String name;
    private final int calories;
    private final int carbohydrates;
    private final int protein;
    private final int fats;
    private final int sugar;
    private final int sodium;
    private final float price;
    private final int type;

    public Plate(String name, int calories, int carbohydrates,
                 int protein, int fats, int sugar, int sodium, float price, int type) {
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fats = fats;
        this.sugar = sugar;
        this.sodium = sodium;
        this.price = price;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public int getProtein() {
        return protein;
    }

    public int getFats() {
        return fats;
    }

    public int getSugar() {
        return sugar;
    }

    public int getSodium() {
        return sodium;
    }

    public float getPrice() {
        return price;
    }

    public int getType() {
        return type;
    }
}
