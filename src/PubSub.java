import java.util.List;

public interface PubSub {

    public final static double DISS_MSG_GAMMA = 0.5;
    public final static double DISS_NEW_CATEGORY_GAMMA = 1.0;

    // Disseminate message from user command
    public void disseminate(Request request, boolean isCategoryDiss, double gamma);

    // Update the subscription list
    public void updateSubList(List<Category> categoryList, boolean subAction);

    // Add the new category
    public void addCategory(List<Category> newCategoryList);
}
