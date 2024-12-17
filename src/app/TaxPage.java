package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import appOutput.AppData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
        StackPane.setMargin(savingsLabel, new Insets(193, 200, 40, 0));
        											//top , right , bottom, left
        //add all contents
        taxContent.getChildren().addAll(taxTitle, imageStack);
        return taxContent;
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
	

