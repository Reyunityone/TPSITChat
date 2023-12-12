package Client;

import javax.swing.*;
import java.awt.*;

public class login     extends JFrame{
    private JPanel panel1;
    private JButton REGISTRATIButton;
    private JButton LOGINButton;
    private JTextField textField1;
    private JPasswordField passwordField1;

    private JFrame frame;

    public login(){
        frame = new JFrame("Login");
        frame.setMinimumSize(new Dimension(660,460));
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.add(panel1);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        new login();
    }
}


