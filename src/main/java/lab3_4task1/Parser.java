package lab3_4task1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

  static final String FROM_FILE = "src/main/java/lab3_4task1/input.xml";
  static final String TO_FILE = "src/main/java/lab3_4task1/output.xml";

  public static void main(String[] args) {
    getParsedXmlFromAnotherXml(FROM_FILE, TO_FILE);
  }


  private static void getParsedXmlFromAnotherXml(String fromFile, String toFile) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fromFile));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toFile));) {
      Pattern namePattern = Pattern.compile("\\sname\\s*=\\s*\"(\\W+)\"");
      Pattern surnamePattern = Pattern.compile("surname\\s*=\\s*\"(\\W+)\"");
      Pattern endPersonPattern = Pattern.compile(">");
      Pattern replaced = Pattern.compile(
          "\\w*name\\s*=\\s*\"(\\W+)\"\\s*\\w*name\\s*=\\s*\"(\\W+)\"");

      String name = null;
      String surname = null;
      String temp = "";
      do {
        temp += bufferedReader.readLine() + "\r\n";
        Matcher nameMatch = namePattern.matcher(temp);
        Matcher surnameMatch = surnamePattern.matcher(temp);
        Matcher endMatch = endPersonPattern.matcher(temp);
        if (nameMatch.find() && name == null) {
          name = nameMatch.group(0).substring(1, nameMatch.group(0).length() - 1);
        }
        if (surnameMatch.find() && surname == null) {
          surname = surnameMatch.group(1);
        }
        if (endMatch.find()) {
          Matcher replacedMatcher = replaced.matcher(temp);
          String replacement;
          if (replacedMatcher.find()) {
            replacement = replacedMatcher.group(0);
            temp = temp.replaceAll(replacement, name + " " + surname + "\"");
            bufferedWriter.write(temp);
            System.out.println(temp);
            name = null;
            surname = null;
            temp = "";
          } else {
            bufferedWriter.write(temp);
            System.out.println(temp);
            temp = "";
          }
        }
      } while (bufferedReader.ready());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
