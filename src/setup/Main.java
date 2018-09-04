package setup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public final void start(Stage primaryStage) throws java.io.IOException {
    Parent root = FXMLLoader.load(getClass().getResource("projectSetupInterface.fxml"));
    primaryStage.setTitle("Setup Project");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();
  }
}
