import java.io.Serializable;

public class Category implements Serializable {
	// default valid categories: CAT, DOG, BIRD, RABBIT
    // The boot node should add them when initializing its valid category set
    private String category;

    public Category(String category) {
        this.category = category.toUpperCase();
    }

    @Override
    public String toString() {
        return category;
    }

    public String getCategory() {
        return category;
    }
}