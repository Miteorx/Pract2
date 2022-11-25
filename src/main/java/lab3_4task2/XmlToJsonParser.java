package lab3_4task2;

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
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlToJsonParser {

  private static final String VIOLATIONS_PACKAGE_SOURCE = "src/main/java/lab3_4task2/violations";

  public static void main(String[] args) throws Exception {
    getSummaryViolatesFromXMLtoJSON(VIOLATIONS_PACKAGE_SOURCE);
  }

  private static void getSummaryViolatesFromXMLtoJSON(String violationsPackageSource)
      throws ParserConfigurationException, SAXException, IOException {
    File violationsPackage = new File(violationsPackageSource);
    File[] violations = violationsPackage.listFiles();

    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
    SAXParser parser = parserFactory.newSAXParser();
    SAXHandler handler = new SAXHandler();

    Map<String, Double> violationsMap = new HashMap<>();

    for (File file : violations) {
      parser.parse(violationsPackageSource + "/" + file.getName(), handler);

      for (Violation violate : handler.violates) {
        if (!violationsMap.containsKey(violate.getType())) {
          violationsMap.put(violate.getType(), violate.getFineAmount());
        } else {
          violationsMap.put(violate.getType(),
              violationsMap.get(violate.getType()) + violate.getFineAmount());
        }
      }
    }
    System.out.println(violationsMap);

    List<Entry<String, Double>> returned = violationsMap.entrySet().stream()
        .sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()))
        .collect(Collectors.toList());

    ObjectMapper jsonMapper = new ObjectMapper();
    JsonFactory jsonFactory = new JsonFactory();
    JsonGenerator jsonGenerator = jsonFactory.createGenerator(
        new File("src/main/java/lab3_4task2/summary.json"), JsonEncoding.UTF8);
    jsonGenerator.setCodec(jsonMapper);

    for (Entry<String, Double> para : returned) {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeObjectField("title", para.getKey());
      jsonGenerator.writeObjectField("amount", para.getValue());
      jsonGenerator.writeEndObject();
    }
    jsonGenerator.close();
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
