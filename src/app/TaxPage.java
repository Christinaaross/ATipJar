package app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import appOutput.AppData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TaxPage {

	@SuppressWarnings("unused")
	private final AppData appData;
	
	public TaxPage(AppData appData) {
		this.appData = appData;
		
	}
	
	public Node getView() {
        VBox taxContent = new VBox(20);
        taxContent.setPadding(new Insets(20));
        taxContent.setAlignment(Pos.TOP_CENTER);
        taxContent.setStyle("-fx-background-color: #f0f0f0;"); // Light background for the whole page

        // Title 
        Text taxTitle = new Text("Tax Overview");
        taxTitle.setFont(Font.font("Serif", FontWeight.LIGHT, 24));
       
        //Add Image
        //Image image = new Image(getClass().getResource("/resources/uncSam.png").toExternalForm());
        Image image = new Image("file:/C:/Users/chris/OneDrive/Advanced Software Proj/sam.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(700); 
        imageView.setPreserveRatio(true);

        //you owe: label
        Label savingsLabel = new Label("Loading tax data...");
        savingsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        savingsLabel.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: black; -fx-border-width: 2;");

        
        // tax data
        new Thread(() -> {
            String savingsData = taxBox();
            Platform.runLater(() -> savingsLabel.setText(savingsData));
        }).start();
        
        Button resetButton = new Button("Reset Tax For New Year");
        resetButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        resetButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 20; -fx-border-radius: 5;");
        resetButton.setOnAction(event -> resetTaxData(savingsLabel));
        
        LocalDate currentDate = LocalDate.now();
        int currentYear = LocalDate.now().getYear();

        if (currentDate.isBefore(LocalDate.of(currentYear, 1, 1))) {
            resetButton.setDisable(true);
        }

        resetButton.setOnAction(event -> {
            if (currentDate.isBefore(LocalDate.of(currentYear + 1, 1, 1))) {
                savingsLabel.setText("Reset is only allowed on or after January 1st.");
            } else {
                resetTaxData(savingsLabel);
            }
        });
        
        // Blinking animation 
        new Thread(() -> {
            String savingsData = taxBox();
            Platform.runLater(() -> {
                savingsLabel.setText(savingsData);

                // only text
                Timeline blink = new Timeline(
                    new KeyFrame(Duration.seconds(0.9), event -> savingsLabel.setStyle
                    ("-fx-background-color: white; -fx-padding: 15; -fx-border-color: black; -fx-border-width: 2; -fx-text-fill: transparent;")),
                   
                    new KeyFrame(Duration.seconds(1.5), event -> savingsLabel.setStyle
                    ("-fx-background-color: white; -fx-padding: 15; -fx-border-color: black; -fx-border-width: 2; -fx-text-fill: black;"))
                );
                blink.setCycleCount(Timeline.INDEFINITE);
                blink.play();
            });
        }).start();

         
        // Txt box over img
        StackPane imageStack = new StackPane();
        imageStack.getChildren().addAll(imageView, savingsLabel);
        StackPane.setAlignment(savingsLabel, Pos.CENTER_RIGHT);
        StackPane.setMargin(savingsLabel, new Insets(193, 250, 40, 0));
        											// top , right , bottom, left
        //add all contents
        taxContent.getChildren().addAll(taxTitle, imageStack, resetButton);
        return taxContent;
    }
	
	private void resetTaxData(Label savingsLabel) {
        String resetQuery = "DELETE tr FROM tip_record tr " +
                            "JOIN shift s ON tr.shiftId = s.shiftId " +
                            "WHERE YEAR(s.date) < YEAR(CURDATE());"; // deletes all data from previous years 
        					// Will fix later!

        try (Connection conn = AppData.getConnection();
             PreparedStatement pre = conn.prepareStatement(resetQuery)) {

            int rowsAffected = pre.executeUpdate();

            Platform.runLater(() -> {
                savingsLabel.setText("Tax data reset! Removed " + rowsAffected + " records.");
                System.out.println("Reset successful. Records removed: " + rowsAffected);
            });

        } catch (SQLException e) {
            e.printStackTrace();
            Platform.runLater(() -> savingsLabel.setText("Error resetting tax data."));
        }
    }

	public String taxBox() {
		String taxQuery = "SELECT "
				+ "SUM(tr.cashTip + tr.cardTip) * 0.014 AS SuggestedSavings "+
				"FROM tip_record tr " +
				"JOIN shift s ON tr.shiftId = s.shiftId " +
				"WHERE s.userId = 1; " ;
		
		try (Connection conn = AppData.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(taxQuery)) {

	            if (rs.next()) {
	                double suggestedSavings = rs.getDouble("SuggestedSavings");
	                return String.format("You Owe: $%.2f", suggestedSavings);
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return "Error loading tax data.";
	        }

	        return "No tax data available.";
	    }	
		
	}
	

