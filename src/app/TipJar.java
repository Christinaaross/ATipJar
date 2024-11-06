package app;

import java.math.BigDecimal;
import java.time.LocalDate;

import appOutput.AppData;
import javafx.application.Platform;
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

    @SuppressWarnings("unused")
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

    public TipJar(AppData appData, Stage primaryStage) {
        this.appData = appData;
        this.primaryStage = primaryStage;
        this.tipAnalytics = new TipAnalytics(appData);
        this.tipPredictionBox = new TipPredictionBox(tipAnalytics);
        this.shiftDataSaver = new ShiftDataSaver(appData);
        this.analyticsPage = new AnalyticsPage();

        createHeader();
        appScreen.setLeft(addLeft());
        appScreen.setCenter(addCenter());
        exitGame();
    }

    public void show() {
        Scene scene = new Scene(appScreen, 1000, 700);
        primaryStage.setTitle("TipJar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Node addLeft() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: gold; -fx-border-width: 2; -fx-border-radius: 5; -fx-text-fill: black;");
        // gold outline
        Text title = new Text("Navigation");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setFill(Color.DARKSLATEGRAY);
        vbox.getChildren().add(title);

        Hyperlink homeLink = new Hyperlink("Home Page");
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
       //for later tax page 
    }

    private Node addCenter() {
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #d0d0d0; -fx-border-width: 1; -fx-border-radius: 5;");

        vbox.getChildren().addAll(createDateAndShiftRow(), createTipRow(), createSubmitButtonRow(), tipPredictionBox.createPredictionBox());
        return vbox;
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
        BigDecimal shiftHours = new BigDecimal(shiftField.getText().isEmpty() ? "0" : shiftField.getText());
        BigDecimal cashTips = new BigDecimal(cashTipsField.getText().isEmpty() ? "0" : cashTipsField.getText());
        BigDecimal cardTips = new BigDecimal(cardTipsField.getText().isEmpty() ? "0" : cardTipsField.getText());

        boolean isSaved = shiftDataSaver.saveShiftData(selectedDate, shiftHours, cashTips, cardTips);

        if (isSaved) {
            showAlert(Alert.AlertType.INFORMATION, "Data Entry Confirmed", "Shift data saved successfully!");
            datePicker.setValue(LocalDate.now());
            shiftField.clear();
            cashTipsField.clear();
            cardTipsField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save shift data. Please try again.");
        }
    }

    private void createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #333333;");
        Text title = new Text("Welcome to TipJar");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.GOLD);

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
