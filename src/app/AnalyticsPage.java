package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import appOutput.AppData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
        analyticsTitle.setFont(Font.font("Serif", FontWeight.LIGHT, 24));

        // Stats 
        HBox summaryStats = new HBox(20);
        summaryStats.setAlignment(Pos.CENTER);
        summaryStats.setPadding(new Insets(20));
       summaryStats.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-radius: 5;");
       populateSummaryStats(summaryStats);

        //Line Chart for Trends 
        LineChart<String, Number> trendsLineChart = createTrendsLineChart();
        
        // add all to frame
        analyticsContent.getChildren().addAll(analyticsTitle, summaryStats, trendsLineChart);
        return analyticsContent;
    }
    
    private LineChart<String, Number> createTrendsLineChart() {
    	
    	CategoryAxis xAxis = new CategoryAxis(); // using for axes bc date is string
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Earnings");
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis); // Creates LineChart
        lineChart.setTitle("Earnings Over Time");

        XYChart.Series<String, Number> series = new XYChart.Series<>(); // Create Data Series
        series.setName("Earnings");

        // Data for chart
        String query =  //Dont forget your spaces before " or it will error
        		"SELECT s.date AS shiftDate, SUM(tr.cashTip + tr.cardTip) AS totalEarnings "
        		+ "FROM shift s "
        		+ "JOIN tip_record tr ON s.shiftId = tr.shiftId "
        		+ "GROUP BY s.date "
        		+ "ORDER BY s.date ";
        		
        try (Connection conn = AppData.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String shiftDate = rs.getString("shiftDate");
                double totalEarnings = rs.getDouble("totalEarnings");
                series.getData().add(new XYChart.Data<>(shiftDate, totalEarnings));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Adds to chart
        lineChart.getData().add(series);
        lineChart.setPrefSize(600, 400);
        
        return lineChart;
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
        	createSummaryCircle("Total Cash", String.format("$%.2f", totalCash)),
            createSummaryCircle("Total Credit", String.format("$%.2f", totalCredit)),
            createSummaryCircle("Total Amount", String.format("$%.2f", totalEarned)),
            createSummaryCircle("Tip Average", String.format("$%.2f", tipAvgPerHour))
        );
    }
    
    // Styled summary circles
    private VBox createSummaryCircle(String text, String Value) {
        Label label = new Label(text);
        label.setFont(Font.font("Serif", FontWeight.NORMAL, 13));
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);

        
        Label valueLabel = new Label(Value); // added values for it to show
        valueLabel.setFont(Font.font("Serif", FontWeight.BOLD, 14));
        valueLabel.setAlignment(Pos.CENTER);
        valueLabel.setStyle("-fx-border-color: black; -fx-border-radius: 50%; -fx-padding: 15; -fx-background-radius: 50%;"); // -fx-background-color: #cce7ff;
        
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


