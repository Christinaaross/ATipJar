package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import appOutput.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class AnalyticsPage {
	
	@SuppressWarnings("unused")
	private final AppData appData;
	
	 public AnalyticsPage(AppData appData) {
	        this.appData = appData;
	    }

    public Node getView() {
        VBox analyticsContent = new VBox(20);
        analyticsContent.setPadding(new Insets(20));
        analyticsContent.setAlignment(Pos.TOP_CENTER);
        analyticsContent.setStyle("-fx-background-color: #f0f0f0;"); // Light background for the whole page

        // Title 
        Text analyticsTitle = new Text("Analytics Overview");
        analyticsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Tip amounts in bubbles
        /*
        VBox periodTips = new VBox(10);
        periodTips.setAlignment(Pos.CENTER);
        periodTips.setPadding(new Insets(10));
        periodTips.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");
        periodTips.getChildren().addAll(
            createTipLabel("Current Day:", "$100"),
            createTipLabel("Current Week:", "$500"),
            createTipLabel("Current Month:", "$2000"),
            createTipLabel("Current Year:", "$24000")
        );
        */

        // Stats 
        HBox summaryStats = new HBox(15);
        summaryStats.setAlignment(Pos.CENTER);
        summaryStats.setPadding(new Insets(15));
       summaryStats.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");
       
       populateSummaryStats(summaryStats);

        

        // Placeholder trends box finish later
        Label trendsPlaceholder = new Label("Trends");
        trendsPlaceholder.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        trendsPlaceholder.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: #e8e8e8; -fx-padding: 40; -fx-border-radius: 5;");
        trendsPlaceholder.setPrefSize(300, 150);
        trendsPlaceholder.setAlignment(Pos.CENTER);

        // add all to frame
        analyticsContent.getChildren().addAll(analyticsTitle, summaryStats, trendsPlaceholder);
        return analyticsContent;
    }
    
    public void populateSummaryStats(HBox summaryStats) {
        // Define your queries
        String totalCreditQuery = "SELECT SUM(cardTip) AS TotalCredit FROM tip_record";
        String totalEarnedQuery = "SELECT SUM(cashTip + cardTip) AS TotalEarned FROM tip_record";
        String avgPerHourQuery = "SELECT SUM(cashTip + cardTip) / SUM(totalDuration) AS TipAvgPerHour FROM shift s JOIN tip_record tr ON s.shiftId = tr.shiftId";
        String totalCashQuery = "SELECT SUM(cashTip) AS TotalCash FROM tip_record";

        // Fetch values using executeQueryForValue
        double totalCredit = executeQueryForValue(totalCreditQuery);
        double totalEarned = executeQueryForValue(totalEarnedQuery);
        double tipAvgPerHour = executeQueryForValue(avgPerHourQuery);
        double totalCash = executeQueryForValue(totalCashQuery);

        // Add to summaryStats
        summaryStats.getChildren().addAll(
            createSummaryCircle("Total CREDIT CARD AMOUNT", String.format("$%.2f", totalCredit)),
            createSummaryCircle("TOTAL AMOUNT EARNED", String.format("$%.2f", totalEarned)),
            createSummaryCircle("Tip Average per Hour", String.format("$%.2f", tipAvgPerHour)),
            createSummaryCircle("Total CASH AMOUNT", String.format("$%.2f", totalCash))
        );
    }


    // Helper method to create periodic tip labels
   /* private HBox createTipLabel(String period, String amount) {
        Label periodLabel = new Label(period);
        periodLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        
        Label amountLabel = new Label(amount);
        amountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        HBox hbox = new HBox(20, periodLabel, amountLabel);
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }
	*/
    
    // Helper method to create styled summary circles
    
    private VBox createSummaryCircle(String text, String Value) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        //label.setStyle("-fx-border-color: black; -fx-border-radius: 50%; -fx-padding: 20; -fx-background-radius: 50%; -fx-background-color: #cce7ff;");
        
        Label valueLabel = new Label(Value); // added values for it to show
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueLabel.setAlignment(Pos.CENTER);
        valueLabel.setStyle("-fx-border-color: black; -fx-border-radius: 50%; -fx-padding: 20; -fx-background-radius: 50%; -fx-background-color: #cce7ff;");
        
        VBox circle = new VBox(label, valueLabel);
        circle.setAlignment(Pos.CENTER);
        circle.setPrefSize(100, 100);
        return circle;
    }
    
    private double executeQueryForValue(String query) {
        try (Connection conn = AppData.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

}


