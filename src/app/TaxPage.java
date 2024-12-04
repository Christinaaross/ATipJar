package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import appOutput.AppData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

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
       
        Label savingsLabel = new Label("Loading tax data...");
        savingsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        // tax data
        new Thread(() -> {
            String savingsData = taxBox();
            Platform.runLater(() -> savingsLabel.setText(savingsData));
        }).start();

        taxContent.getChildren().addAll(taxTitle, savingsLabel);
        return taxContent;
    }
	
	public String taxBox() {
		String taxQuery = "SELECT "
				+ "SUM(tr.cashTip + tr.cardTip) * 0.20 AS SuggestedSavings "+
				"FROM tip_record tr " +
				"JOIN shift s ON tr.shiftId = s.shiftId " +
				"WHERE s.userId = 1; " ;
		
		try (Connection conn = AppData.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(taxQuery)) {

	            if (rs.next()) {
	                double suggestedSavings = rs.getDouble("SuggestedSavings");
	                return String.format("Suggested Savings: $%.2f", suggestedSavings);
	            }

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return "Error loading tax data.";
	        }

	        return "No tax data available.";
	    }	
		
	}
	

