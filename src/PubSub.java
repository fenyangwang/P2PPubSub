public interface PubSub {

    // Disseminate message from user command
    public void disseminate(Message msg);

    // Update the subscription list
    public void updateSubList();

}
