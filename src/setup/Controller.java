package setup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;

public class Controller implements Initializable {

  private static final String quickStart = "https://github.com/Open-RIO/GradleRIO/raw/master/Quickstart.zip";
  /**
   * Size of the buffer to read/write data
   */
  private static final int BUFFER_SIZE = 4096;
  public ChoiceBox<String> ideaSelection;
  @FXML
  private TextField projectName;
  @FXML
  private ChoiceBox<String> languageSelection;
  @FXML
  private TextField teamNumber;
  @FXML
  private Button download;
  private boolean textCorrect;
  private boolean teamCorrect = false;

  public static void replaceSelected(String fileLocation, String regexExpression, String replaceWith)
      throws IOException {
    // input the file content to the StringBuffer "input"
    StringBuilder inputBuffer = new StringBuilder(1277);

    try (BufferedReader file = new BufferedReader(new FileReader(fileLocation))) {
      String line;
      while ((line = file.readLine()) != null) {
        inputBuffer.append(line);

        inputBuffer.append(System.lineSeparator());
      }
    }

    String inputStr = inputBuffer.toString();

    Pattern matchPatter = Pattern.compile(regexExpression);
    Matcher matcher = matchPatter.matcher(inputStr);
    inputStr = matcher.replaceAll(replaceWith);

    // write the new String with the replaced line OVER the same file
    try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
        new FileOutputStream(fileLocation))) {

      bufferedOutputStream.write(inputStr.getBytes());
    }
  }

  @Override
  public final void initialize(URL location, ResourceBundle resources) {
    languageSelection.getItems().addAll("Java", "C++");
    languageSelection.setValue("Java");

    ideaSelection.getItems().addAll("Eclipse", "Idea", "CLion");
    ideaSelection.setValue("Idea");
  }

  private String download(String urlR, String projectName) {
    try {
      URL url = new URL(urlR);
      URLConnection conn = url.openConnection();
      projectName += ".zip";
      try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(projectName)) {
        byte[] b = new byte[1024];
        int count;
        while ((count = in.read(b)) >= 0) {
          out.write(b, 0, count);
        }
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
      projectName = null;
    }

    return projectName;
  }

  /**
   * Extracts a zip entry (file entry)
   */
  private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
      byte[] bytesIn = new byte[BUFFER_SIZE];
      int read;
      while ((read = zipIn.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }
  }

  /**
   * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory (will be created if does
   * not exists)
   */
  private void unzip(String zipFilePath, String destDirectory) {
    try {

      File destDir = new File(destDirectory);
      if (!destDir.exists()) {
        destDir.mkdir();
      }
      ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
      ZipEntry entry = zipIn.getNextEntry();
      // iterates over entries in the zip file
      while (entry != null) {
        String filePath = destDirectory + File.separator + entry.getName();
        if (entry.isDirectory()) {
          // if the entry is a directory, make the directory
          File dir = new File(filePath);
          dir.mkdir();
        } else {
          // if the entry is a file, extracts it
          extractFile(zipIn, filePath);
        }
        zipIn.closeEntry();
        entry = zipIn.getNextEntry();
      }
      zipIn.close();
    } catch (IOException ignored) {

    }
  }

  public final void setupProject(ActionEvent actionEvent) throws IOException {

    DirectoryChooser chooser = new DirectoryChooser();
    chooser.setTitle("Choose folder to place project into");
    File defaultDirectory = new File(System.getProperty("user.home"));
    chooser.setInitialDirectory(defaultDirectory);
    File selectedDirectory = chooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

    if (selectedDirectory != null && selectedDirectory.exists()) {

      String realFolder = selectedDirectory + File.separator + projectName.getText();
      String zipFileName = download(quickStart, realFolder);
      unzip(zipFileName, realFolder); //this was copied

      delete(new File(zipFileName));
      String language = languageSelection.getValue().equals("Java") ? "cpp" : "java";
      String deletedLanguageDirectory = realFolder + File.separator + language;
      language = language.equals("java") ? "cpp" : "java";
      String languageDirectory = realFolder + File.separator + language;

      delete(new File(deletedLanguageDirectory));

      File teamNamePath = Paths.get(languageDirectory, "src", "main", "java", "frc", "team0000").toFile();

      String teamNumberString = "team" + teamNumber.getText();
      File newTeamNamePath = Paths.get(languageDirectory, "src", "main", "java", "frc", teamNumberString)
          .toFile();

      teamNamePath.renameTo(newTeamNamePath);

      if (language.equals("java")) {
        String gradleBuildFile = Paths.get(realFolder, language, "build.gradle").toString();

        replaceSelected(gradleBuildFile, "5333", teamNumber.getText());
        replaceSelected(gradleBuildFile, "0000", teamNumber.getText());
      }

      {
        File realDirectory = new File(realFolder);
        File filesDirectory = new File(languageDirectory);

        if (realDirectory.isDirectory() && filesDirectory.isDirectory()) {
          File[] content = filesDirectory.listFiles();
          if (content != null) {
            for (File aContent : content) {
              Files.move(aContent.toPath(), Paths.get(realDirectory.toPath().toString(), aContent.getName()));
            }
          }
        }

        delete(filesDirectory);
      }

      String[] command = new String[3];
      command[0] = "cmd";
      command[1] = "/c";
      command[2] = String.format("cd %s && gradlew idea && gradlew build && gradlew shuffleboard", realFolder);
      Runtime.getRuntime().exec(command);

      Runtime.getRuntime().exec(String.format("explorer.exe /select,%s\\src", realFolder));
      ((Node) actionEvent.getTarget()).getScene().getWindow().hide();
    }
  }

  private void delete(File f) throws IOException {
    if (f.isDirectory()) {
      for (File c : Objects.requireNonNull(f.listFiles())) {
        delete(c);
      }
    }
    if (!f.delete()) {
      throw new FileNotFoundException("Failed to delete file: " + f);
    }
  }

  public final void checkText(KeyEvent keyEvent) {
//		if (((TextField) keyEvent.getTarget()).getText().length() == 0 && Character
//			.isDigit(keyEvent.getCharacter().charAt(0))) {
//			keyEvent.consume();
//			return;
//		}
    if (keyEvent.getCharacter().matches("[0-9\\s]")) {
      keyEvent.consume();
    } else if ((((TextInputControl) keyEvent.getTarget()).getText().length() <= 0) && keyEvent.getCharacter()
        .equals("\b")) {
      textCorrect = false;
      download.setDisable(true);
    } else {
      textCorrect = true;
      download.setDisable(!teamCorrect);
    }
  }

  public final void checkNumber(KeyEvent keyEvent) {
    if ((((TextInputControl) keyEvent.getTarget()).getText().length() <= 0) && keyEvent.getCharacter()
        .equals("\b")) {
      teamCorrect = false;
      download.setDisable(true);
      return;
    }

    if (Character.isDigit(keyEvent.getCharacter().charAt(0))) {
      teamCorrect = true;
      download.setDisable(!textCorrect);
    } else if (!keyEvent.getCharacter().equals("\b")) {
      keyEvent.consume();
    }
  }
}
