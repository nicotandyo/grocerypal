package group4.tcss450.uw.edu.grocerypal450.models;

/**
 * This class is used to create Objects that represent a User.
 */
public class User {
    /**
     * User's name
     */
    private String name;
    /**
     * User's password
     */
    private String password;

    /**
     * Create a new user with given name and email.
     * @param name
      */
    public User(String name) {
        this.name = name;
    }

    /**
     * Get this user's name.
     * @return String name
     */
    public String getName() {
        return name;
    }


    /**
     * Set this user's password
     * @param thePassword
     */
    public void setPassword(String thePassword) {
        this.password = thePassword;
    }

    /**
     * Get this user's password
     * @return String password
     */
    public String getPassword() {
        return password;
    }


}
