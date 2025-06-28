class FoodItem {
    String name;
    double price;
    int prepTime;

    FoodItem(String name, double price, int prepTime) {
        this.name = name;
        this.price = price;
        this.prepTime = prepTime;
    }

    @Override
    public String toString() {
        return name + " - $" + price + " - " + prepTime + "m";
    }
}