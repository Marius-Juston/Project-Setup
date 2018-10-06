package setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CmdOutputDisplay implements Initializable {

  public TextArea text;
  private BufferedReader stdInput;
  private BufferedReader stdError;

  public static void show(Process process) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    Parent p = fxmlLoader.load(CmdOutputDisplay.class.getResource("cmdOutputDisplay.fxml").openStream());
    CmdOutputDisplay controller = fxmlLoader.getController();
    controller.setProcess(process);

    Stage stage = new Stage();
    stage.setScene(new Scene(p));
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.show();
  }


  public void setProcess(Process process) {
    stdInput = new BufferedReader(new
        InputStreamReader(process.getInputStream()));
    stdError = new BufferedReader(new
        InputStreamReader(process.getErrorStream()));

    Thread thread = new Thread(() -> {
//        System.out.println(s);
      stdInput.lines().forEach(this::append);

//        System.out.println(s);
      stdError.lines().forEach(this::append);

      text.appendText("\nFINISHED");
    });
    thread.start();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    text.textProperty().addListener(
        (ChangeListener<Object>) (observable, oldValue, newValue) -> text.setScrollTop(Double.MAX_VALUE));
  }


  private void append(String s) {
    text.appendText(String.format("%s%n", s));
  }
}
