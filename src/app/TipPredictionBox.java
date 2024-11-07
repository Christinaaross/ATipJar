package app;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TipPredictionBox {
//
    private TipAnalytics tipAnalytics;

    public TipPredictionBox(TipAnalytics tipAnalytics) {
        this.tipAnalytics = tipAnalytics;
    }

    public VBox createPredictionBox() {
        VBox predictionBox = new VBox(10);
        predictionBox.setAlignment(Pos.CENTER);
        predictionBox.setPadding(new Insets(10));
        predictionBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        Label predictionLabel = new Label("Tip Prediction");
        predictionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Fetch prediction data from TipAnalytics
        String bestDays = tipAnalytics.getBestDaysToWork();
        double averageEarnings = tipAnalytics.calculateAverageEarningsPerHour();

        Label bubbleLabel = new Label("The best days to work are " + bestDays + ", where you make $" 
                                      + String.format("%.2f", averageEarnings) + " per hour.");
        bubbleLabel.setStyle("-fx-background-color: lightblue; -fx-border-radius: 20; -fx-background-radius: 20; "
                           + "-fx-padding: 10; -fx-font-size: 14; -fx-font-family: Arial;");
        bubbleLabel.setWrapText(true);

        // Add labels to the prediction box
        predictionBox.getChildren().addAll(predictionLabel, bubbleLabel);

        return predictionBox;
    }
}
