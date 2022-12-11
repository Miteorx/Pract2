import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Properties;
import lab5_6.secondTask.ApplicationPropertyObject;
import lab5_6.secondTask.ParseProperties;
import lab5_6.secondTask.Property;
import org.junit.Test;

public class ParsePropertiesTests {

  Path path = Paths.get("src/main/java/resources/application.properties");


  @Test
  public void correctlyWorkTest() {

    ApplicationPropertyObject test = new ApplicationPropertyObject();
    try {
      test = ParseProperties.loadFromProperties(test.getClass(), path);
    } catch (Exception e) {
      e.printStackTrace();
    }

    assertEquals("value1", test.getStringProperty());
    assertEquals(10, test.getNumbProperty());
    assertEquals(Instant.parse("2022-11-28T22:18:30Z"), test.getTimeProperty());
  }

  @Test
  public void checkCorrectNamespacesOrDefaultValues()
      throws ClassNotFoundException {
    Properties prop = new Properties();
    try (InputStream inputStream = new FileInputStream("src/main/java/resources/application.properties")) {
      prop.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Class test = Class.forName("lab5_6.secondTask.ApplicationPropertyObject");
    Field[] fields = test.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Annotation[] annotations = field.getAnnotations();
      for (Annotation annotation : annotations) {
        if (annotation.annotationType().equals(Property.class)) {
          Property property = (Property) annotation;
          if (prop.getProperty(field.getName()) != null || prop.getProperty(property.name()) != null) {
            assertTrue(true);
          } else {
            fail();
          }
        }
      }
    }
  }
}
