import app.EarningsReport;
import app.TipJar;
//import app.WelcomePage;
import appOutput.AppData;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartTipJar extends Application {
    
    private AppData appData;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        appData = new AppData();
        TipJar tipJar = new TipJar(appData, primaryStage); // database connection to tipjar
        tipJar.show();
        
        
        primaryStage.setOnCloseRequest(event -> {
        	
        });
    }

    public static void main(String[] args) {
    	AppData appData = new AppData();
        EarningsReport report = new EarningsReport(appData);
        report.writeCSVD("files/report.txt");
        
        launch(args);
    }
}

