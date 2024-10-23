package app;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import user.User;

public class TipJar  {

	private BorderPane appScreen = new BorderPane();
	private Stage primaryStage = new Stage();
	private User user;
	
	public TipJar() {
		createHeader();
		showGame();
		exitGame();
		appScreen.setLeft(addVBox());
	}
	
	
	private Node addVBox() {
		VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);
        
        Text title = new Text("Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);
        
        Hyperlink options[] = new Hyperlink[] {
                new Hyperlink("Home Page"),
                new Hyperlink("Analytics"),
                new Hyperlink("Distribution"),
        };
        
        for (int i = 0; i < 3; i++) {
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }
		return vbox;
	}


	private void showGame() {
		//the usual stuff
		Scene scene = new Scene(appScreen, 1000, 700);
		primaryStage.setTitle(" TipJar");
		primaryStage.setScene(scene);
		primaryStage.show(); 	
	}
	
	private void createHeader() {
		Text text = new Text("Welcome" );
		text.setFont(Font.font(20));
		HBox header = new HBox(20);
		header.getChildren().add(text);
		header.setAlignment(Pos.CENTER);
		appScreen.setTop(header);
	}
	
	
	
	private void exitGame() {
		Button btnExit = new Button("Exit");
		btnExit.setOnAction(e -> Platform.exit()); // or //primaryStage.close();
		VBox exitBox = new VBox(btnExit);
	    exitBox.setAlignment(Pos.BASELINE_RIGHT);  
	    exitBox.setPadding(new Insets(30));
		appScreen.setBottom(exitBox);
	}

}
