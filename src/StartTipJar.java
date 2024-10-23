import app.TipJar;
import app.WelcomePage;
import appOutput.AppData;
import javafx.application.Application;
import javafx.stage.Stage;

public class StartTipJar extends Application {
	
	private AppData appData;
	
	@Override
	public void start(Stage Stage) throws Exception {
		appData = new AppData();
		new WelcomePage();
		//new TipJar();
		appData.close();
	}
	public static void main(String[] args) {
		launch(args);

	}
}
