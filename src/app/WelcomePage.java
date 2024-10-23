package app;
import javax.swing.*;
import appOutput.AppData;
import javafx.application.Platform;
import user.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WelcomePage {

    private static User user;
    private static JFrame frame;
    private AppData appData;  
    
    public WelcomePage() {

        appData = new AppData();
        checkUserData(); 
    }

   
    private void checkUserData() {
        createWelcome();  
    }

    private void createWelcome() {
        frame = new JFrame("Welcome to TipJar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BorderLayout());

        if (user == null || user.getName() == null) {
            String userName = JOptionPane.showInputDialog(frame, "Please enter your name to use TipJar:");
            if (userName != null && !userName.trim().isEmpty()) {
                user = new User(1, userName);  
                saveUserToDatabase(userName); 
            } else {
                user = new User(0, "Guest"); 
            }
        }

        setupWelcomePageComponents();

        frame.setVisible(true);
    }

    private void saveUserToDatabase(String userName) {
        String insertUserQuery = "INSERT INTO user (firstName) VALUES (?)"; 

        try (PreparedStatement preparedStatement = appData.getConnection().prepareStatement(insertUserQuery)) {
            preparedStatement.setString(1, userName);  
            int rowsInserted = preparedStatement.executeUpdate();  

            if (rowsInserted > 0) {
                System.out.println("User successfully inserted into the database!");
            } else {
                System.out.println("Failed to insert user.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupWelcomePageComponents() {
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "! Your ID is: " + user.getId(), SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(welcomeLabel, BorderLayout.CENTER);

        JButton goToTipJarButton = new JButton("Go to TipJar");
        goToTipJarButton.setPreferredSize(new Dimension(200, 50));
        goToTipJarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTipJarPage();  
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(goToTipJarButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openTipJarPage() {
        if (frame != null) {
            frame.dispose(); 
        }
     
        Platform.runLater(() -> new TipJar());
    }
}
