import java.util.List;

public interface PubSub {

    // Disseminate message from user command
    public void disseminate(Message msg);

    // Update the subscription list
    public void updateSubList(List<Category> categoryList, boolean subAction);

    // Add the new category
    public void addCategory(List<Category> newCategoryList);
}
