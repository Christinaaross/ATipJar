package app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TipPredictionBox {

    private TipAnalytics tipAnalytics;
    private VBox predictionBox;
    private Label bubbleLabel;

    public TipPredictionBox(TipAnalytics tipAnalytics) {
        this.tipAnalytics = tipAnalytics;
    }

    public VBox createPredictionBox() {
        predictionBox = new VBox(10);
        predictionBox.setAlignment(Pos.CENTER);
        predictionBox.setPadding(new Insets(10));
        predictionBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;-fx-background-color: #f9f9f9"); //-fx-background-color: #f9f9f9;

        Label predictionLabel = new Label("Tip Prediction");
        predictionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        String bestDays = tipAnalytics.getBestDaysToWork();
        
        double averageEarnings = tipAnalytics.calculateAverageEarningsPerHour();

        bubbleLabel = new Label("The best days to work are " + bestDays + ", where you make $" 
                                      + String.format("%.2f", averageEarnings) + " per hour.");
        
        //updated data
        updatedPrediction();
        
        predictionBox.getChildren().addAll(predictionLabel, bubbleLabel);

        return predictionBox;
    }

	private void updatedPrediction() {
		
		String bestDays = tipAnalytics.getBestDaysToWork();
        double averageEarnings = tipAnalytics.calculateAverageEarningsPerHour();

        // update the label
        Platform.runLater(() -> {
            bubbleLabel.setText("The best days to work are " + bestDays + ", where you make $" 
                                + String.format("%.2f", averageEarnings) + " per hour.");
        });
		
	}
}
