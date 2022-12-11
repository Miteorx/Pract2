package lab5_6.firstTask;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lab3_4task2.Violation;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlToJsonAsyncParser {

  private static Map<String, Double> violationsMap = new HashMap<>();
  private static File violationsPackage = new File("src/main/java/lab3_4task2/violations");
  private static File[] violations = violationsPackage.listFiles();

  public static void main(String[] args) {

    int nThreads = 8;

    long start = System.currentTimeMillis();
    startParseWithUsingThreads(nThreads);
    System.out.println(System.currentTimeMillis() - start);
  }

  public static void startParseWithUsingThreads(int nThreads) {
    ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    for (File file : violations) {
      CompletableFuture.supplyAsync(() -> file, executorService)
          .thenAccept(e -> {
            try {
              getSummaryViolatesFromXMLtoJSON(e);
              System.out.println(Thread.currentThread().getName() + " " + file.getName());
            } catch (ParserConfigurationException | SAXException | IOException ex) {
              ex.printStackTrace();
            }
          });
    }
    executorService.shutdown();
    while (!executorService.isTerminated()){
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    try {
      parseMapToJSON();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void parseMapToJSON() throws IOException {
    List<Entry<String, Double>> returned = violationsMap.entrySet().stream()
        .sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()))
        .collect(Collectors.toList());

    ObjectMapper jsonMapper = new ObjectMapper();
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jsonGenerator = jsonFactory.createGenerator(
        new File("src/main/java/lab3_4task2/summary.json"), JsonEncoding.UTF8);
    jsonGenerator.setCodec(jsonMapper);

    for (Entry<String, Double> pair : returned) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeObjectField("title", pair.getKey());
      jsonGenerator.writeObjectField("amount", pair.getValue());
      jsonGenerator.writeEndObject();
    }
    jsonGenerator.close();
  }

  private static void getSummaryViolatesFromXMLtoJSON(File file)
      throws ParserConfigurationException, SAXException, IOException {

    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    SAXParser parser = parserFactory.newSAXParser();
    SAXHandler handler = new SAXHandler();
    parser.parse(violationsPackage + "/" + file.getName(), handler);

    for (Violation violate : handler.violates) {
      putInMap(violate);
    }
  }

  private synchronized static void putInMap(Violation violate) {
    if (!violationsMap.containsKey(violate.getType())) {
      violationsMap.put(violate.getType(), violate.getFineAmount());
    } else {
      violationsMap.put(violate.getType(),
          violationsMap.get(violate.getType()) + violate.getFineAmount());
    }
  }


  static class SAXHandler extends DefaultHandler {

    List<Violation> violates = new ArrayList<>();
    Violation violate = null;
    String content = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {

      switch (qName) {
        case "violate": {
          violate = new Violation();
          break;
        }
      }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      switch (qName) {
        case "violate": {
          violates.add(violate);
          break;
        }
        case "date_time": {
          violate.setDate(content);
          break;
        }
        case "first_name": {
          violate.setFirstName(content);
          break;
        }
        case "last_name": {
          violate.setLastName(content);
          break;
        }
        case "type": {
          violate.setType(content);
          break;
        }
        case "fine_amount": {
          violate.setFineAmount(Double.valueOf(content));
          break;
        }
      }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      content = String.copyValueOf(ch, start, length).trim();
    }
  }

}
