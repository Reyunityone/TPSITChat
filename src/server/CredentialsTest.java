package server;

import java.util.ArrayList;

public class CredentialsTest {
    public static void main(String[] args) {
        try {
            CredentialsHandler handler = new CredentialsHandler();
            ArrayList<User> test = handler.readCredentials();
            for(User u : test){
                System.out.println("Username: " + u.getUsername() + " Password: " + u.getPassword());
            }
            handler.writeCredentials(new User("drappeggiojoe","squalo88"));
            } catch (Exception e) {
                System.err.println(e);
            }
    }
}
