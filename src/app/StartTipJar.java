package app;

import javafx.application.Application;
import javafx.stage.Stage;

import appOutput.AppData;

public class StartTipJar extends Application {

    private AppData appData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        appData = new AppData();

        TipJar tipJar = new TipJar(appData, primaryStage);
        tipJar.show();

        System.out.println("TipJar application started.");
    }
}

