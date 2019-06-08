package setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Tester {

  private static BufferedReader stdInput;
  private static BufferedReader stdError;


  public static void main(String[] args) throws IOException {
//    System.out.println(getGradleVersion());

//    String[] command = new String[2];
//    command[0] = "powershell.exe";
//    command[1] = "gradle -version";
//    command[1] = "/c";
//    command[1] = String
//        .format("Get-Location; Set-Location -Path %s; Get-Location; .\\gradlew build; .\\gradlew shuffleboard", "C:\\Users\\mariu\\Downloads\\RobotTest");
//    Process process = Runtime.getRuntime().exec(command);

//    stdInput = new BufferedReader(new
//        InputStreamReader(process.getInputStream()));

//    String collect = stdInput.lines().collect(Collectors.joining());
//    stdError.lines().forEach(System.out::println);


    Controller.download("https://github.com/wpilibsuite/GradleRIO/tree/master/examples/java/", ".\\Hello");

  }
}
