package app;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import appOutput.AppData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TipJar {

   // @SuppressWarnings("unused")
	private AppData appData;
    private TipAnalytics tipAnalytics;
    private TipPredictionBox tipPredictionBox;
    private BorderPane appScreen = new BorderPane();
    private Stage primaryStage;
    private DatePicker datePicker;
    private TextField shiftField;
    private TextField cashTipsField;
    private TextField cardTipsField;
    private ShiftDataSaver shiftDataSaver;
    private AnalyticsPage analyticsPage;
    private TableView<Object[]> tableView;
    private TaxPage taxPage;


    public TipJar(AppData appData, Stage primaryStage) {
        this.appData = appData;
        this.primaryStage = primaryStage;
        this.tipAnalytics = new TipAnalytics(appData);
        this.tipPredictionBox = new TipPredictionBox(tipAnalytics);
        this.shiftDataSaver = new ShiftDataSaver(appData);
        this.analyticsPage = new AnalyticsPage(appData);
        this.taxPage = new TaxPage(appData);

        createHeader();
        appScreen.setLeft(addLeft());
        appScreen.setCenter(addCenter());
        exitGame();
    }
  
    // AND STYLE SHEET
    public void show() {
        Scene scene = new Scene(appScreen, 1000, 700);
        //Break up into two line for multiple scenes
        
        //DELETE THIS IS YOU NO LONGER USING STYLE SHEET 
        //String css = this.getClass().getResource("app.css").toExternalForm();
        //scene.getStylesheets().add(css);
     
        primaryStage.setTitle("TipJar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node addLeft() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);        //-fx-border-color: gold;
        vbox.setStyle("-fx-background-color: #f0f0f0;  -fx-border-width: 2; -fx-border-radius: 5; -fx-text-fill: black;");
        // gold outline
        Text title = new Text("Navigation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setFill(Color.DARKSLATEGRAY);
        vbox.getChildren().add(title);

        Hyperlink homeLink = new Hyperlink("Home");
        Hyperlink analyticsLink = new Hyperlink("Analytics");
        Hyperlink taxLink = new Hyperlink("Tax Calculation");

        homeLink.setOnAction(e -> loadHomePage());
        analyticsLink.setOnAction(e -> loadAnalyticsPage());
        taxLink.setOnAction(e -> loadTaxPage());

        vbox.getChildren().addAll(homeLink, analyticsLink, taxLink);
        return vbox;
    }

    private void loadHomePage() {
        appScreen.setCenter(addCenter());
    }

    private void loadAnalyticsPage() {
        appScreen.setCenter(analyticsPage.getView());
    }

    private void loadTaxPage() {
        appScreen.setCenter(taxPage.getView());
    }

    private Node addCenter() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 5;");

        vbox.getChildren().addAll(
                createDateAndShiftRow(),
                createTipRow(),
                createSubmitButtonRow(),
                tipPredictionBox.createPredictionBox()
            );

            // Add EarningsReportUI using SwingNode
           vbox.getChildren().add(createEarningsReportNode());
           // hehehbewbfwwbej

            return vbox;
    }

    @SuppressWarnings("unchecked")
	private Node createEarningsReportNode() {
    	tableView = new TableView<>();
    
    	TableColumn<Object[], String> tipIdCol = new TableColumn<>("tipID");
	    tipIdCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0].toString()));

	    TableColumn<Object[], String> nameCol = new TableColumn<>("Name");
	    nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1].toString()));

	    TableColumn<Object[], String> dateCol = new TableColumn<>("Date");
	    dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2].toString()));

	    TableColumn<Object[], String> shiftCol = new TableColumn<>("Shift Duration");
	    shiftCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3].toString()));

	    TableColumn<Object[], String> cashTipCol = new TableColumn<>("Cash Tip");
	    cashTipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4].toString()));

	    TableColumn<Object[], String> cardTipCol = new TableColumn<>("Card Tip");
	    cardTipCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[5].toString()));

	    tableView.getColumns().addAll(tipIdCol, nameCol, dateCol, shiftCol, cashTipCol, cardTipCol);

	 
	    
	    updateTable();
	   
	    // Delete Button
	    Button deleteButton = new Button("Delete Selected");
	    deleteButton.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
	    deleteButton.setOnAction(event -> {
	    	
	        //selected row
	        Object[] selectedItem = tableView.getSelectionModel().getSelectedItem();

	        if (selectedItem != null) {
	            // Delete the selected item from the database
	        	// using tipid in col 0
	            boolean success = deleteDataFromDatabase(selectedItem[0].toString()); 

	            if (success) {
	                // Remove the selected item 
	                tableView.getItems().remove(selectedItem);
	            } else {
	                //  error msg if delete fails
	                Alert alert = new Alert(Alert.AlertType.ERROR);
	                alert.setTitle("Error");
	                alert.setHeaderText(null);
	                alert.setContentText("Failed to delete the selected record.");
	                alert.showAndWait();
	            }
	        } else {
	            // error msg if no row is selected
	            Alert alert = new Alert(Alert.AlertType.WARNING);
	            alert.setTitle("No Selection");
	            alert.setHeaderText(null);
	            alert.setContentText("Please select a row to delete.");
	            alert.showAndWait();
	        }
	    });

	    // add all contents
	    VBox container = new VBox(10, tableView, deleteButton);
	    container.setPadding(new Insets(20));
	    container.setAlignment(Pos.CENTER);
	    container.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-background-color: #ffffff;");

	    return container;
	}
   

	private boolean deleteDataFromDatabase(String userId) {
		
		String deleteQuery = "DELETE FROM myTiPJar.tip_record WHERE tipID = ?";
	
	    try (Connection conn = AppData.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

	        stmt.setString(1, userId);
	        int rowsAffected = stmt.executeUpdate();

	        return rowsAffected > 0; 
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}

	private Node createDateAndShiftRow() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER);

        Label dateLabel = new Label("Date:");
        dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        datePicker = new DatePicker(LocalDate.now());

        Label shiftLabel = new Label("Shift Hours:");
        shiftLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        shiftField = new TextField();
        shiftField.setPromptText("Enter hours worked");

        hbox.getChildren().addAll(dateLabel, datePicker, shiftLabel, shiftField);
        container.getChildren().add(hbox);
        return container;
    }

    private Node createTipRow() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER);

        Label cashTipsLabel = new Label("Cash Tips:");
        cashTipsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        cashTipsField = new TextField();
        cashTipsField.setPromptText("Enter cash tip amount");

        Label cardTipsLabel = new Label("Card Tips:");
        cardTipsLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        cardTipsField = new TextField();
        cardTipsField.setPromptText("Enter card tip amount");

        hbox.getChildren().addAll(cashTipsLabel, cashTipsField, cardTipsLabel, cardTipsField);
        container.getChildren().add(hbox);
        return container;
    }

    private Node createSubmitButtonRow() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(10));
        Button submitButton = new Button("Submit");
        submitButton.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        submitButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8 15 8 15;");
        submitButton.setOnAction(e -> handleSubmit());
        hbox.getChildren().add(submitButton);
        return hbox;
    }

    private void handleSubmit() {
        LocalDate selectedDate = datePicker.getValue();
        String shiftHoursText = shiftField.getText();
        String cashTipsText = cashTipsField.getText();
        String cardTipsText = cardTipsField.getText();
        
        
        if ((shiftHoursText == null || shiftHoursText.trim().isEmpty()) &&
                (cashTipsText == null || cashTipsText.trim().isEmpty()) &&
                (cardTipsText == null || cardTipsText.trim().isEmpty())) {

                showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are empty. Please enter data before submitting.");
                return;  }
        if ((cashTipsText == null || cashTipsText.trim().isEmpty()) && 
                (cardTipsText == null || cardTipsText.trim().isEmpty())){ 	
        		showAlert(Alert.AlertType.ERROR, "Input Error", "Cash and Card Tips cannot be empty if Shift Hours is entered.");
                return;  }
        
        if (((cashTipsText != null && !cashTipsText.trim().isEmpty()) || 
                (cardTipsText != null && !cardTipsText.trim().isEmpty())) &&
                (shiftHoursText == null || shiftHoursText.trim().isEmpty())) {
               showAlert(Alert.AlertType.ERROR, "Input Error", "Shift Hours cannot be empty if Cash Tips or Card Tips are entered.");
               return; }
        
        try {
        BigDecimal shiftHours = new BigDecimal(shiftHoursText.isEmpty() ? "0" : shiftHoursText);
        BigDecimal cashTips = new BigDecimal(cashTipsText.isEmpty() ? "0" : cashTipsText);
        BigDecimal cardTips = new BigDecimal(cardTipsText.isEmpty() ? "0" : cardTipsText);
        boolean isSaved = shiftDataSaver.saveShiftData(selectedDate, shiftHours, cashTips, cardTips);

        if (isSaved) {
        	
            showAlert(Alert.AlertType.INFORMATION, "Data Entry Confirmed", "Shift data saved successfully!");
            datePicker.setValue(LocalDate.now());
            shiftField.clear();
            cashTipsField.clear();
            cardTipsField.clear();
            
            updateTable(); // update table on submit
 
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save shift data.");
        	}
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are blank.");
        }
    }

	private void updateTable() {
		if(tableView !=null) {
			tableView.getItems().clear();
			EarningsReport report = new EarningsReport(appData);
			List<Object[]> updatedData = report.getEarningsData();
			tableView.getItems().addAll(updatedData);
		}
	}

	private void createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        Text title = new Text("     TipJar");
        title.setFont(Font.font("Serif", FontWeight.LIGHT, 24));
        header.getChildren().add(title);
        appScreen.setTop(header);
    }

    private void exitGame() {
        VBox exitBox = new VBox(15);
        exitBox.setPadding(new Insets(20));
        exitBox.setAlignment(Pos.BASELINE_RIGHT);
        Button btnExit = new Button("Exit");
        btnExit.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnExit.setOnAction(e -> Platform.exit());
        exitBox.getChildren().add(btnExit);
        appScreen.setBottom(exitBox);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
