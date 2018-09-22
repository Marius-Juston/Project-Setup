package setup;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class ZipSaver {

  public static void main(String[] args) throws IOException {
    byte[] zipFile = java.nio.file.Files.readAllBytes(Paths.get("Quickstart.zip"));

    System.out.println("byte[] zipFile = " + Arrays.toString(zipFile).replace("[", "{").replace("]", "}") + ";");
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(zipFile.length)) {
      byteArrayOutputStream.write(zipFile);
      byteArrayOutputStream.writeTo(new FileOutputStream("Quickstart"));

      ResourceBundle myResources = ResourceBundle
          .getBundle("MyResources", Locale.getDefault(), ClassLoader.getSystemClassLoader());

      String[] split = ((String) myResources.getObject("zipFile")).split(", ");
      byte[] data = new byte[split.length];
      for (int i = 0; i < split.length; i++) {
        data[i] = Byte.parseByte(split[i]);
      }

      try (FileOutputStream fileOutputStream = new FileOutputStream("Test.zip")) {
        fileOutputStream.write(data);
      } catch (IOException e) {
        e.printStackTrace();
      }
//      System.out.println(zipFile1);
    }

//    try (FileOutputStream fileOutputStream = new FileOutputStream(new File("Test.zip"))) {
//      fileOutputStream.write(zipFile);
//    }

//    InputStreamReader isReader = new InputStreamReader(ZipSaver.class.getResourceAsStream("Quickstart"));
//    BufferedReader br = new BufferedReader(isReader);
  }

}
