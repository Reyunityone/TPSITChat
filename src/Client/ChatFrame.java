package Client;

import javax.swing.*;
import java.awt.*;

public class ChatFrame extends JFrame{
    private JPanel panel1;
    private JTextArea textArea1;
    private JButton button1;
    private JButton button2;

    private JFrame frame;

    public ChatFrame(){
        frame = new JFrame("Chat");
        frame.setMinimumSize(new Dimension(1024,720));
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
        new ChatFrame();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}