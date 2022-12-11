package lab5_6.secondTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Properties;

public class ParseProperties {

  public static void main(String[] args) {
    Path path = Paths.get("src/main/java/resources/application.properties");
    ApplicationPropertyObject test = new ApplicationPropertyObject();
    try {
      test = loadFromProperties(test.getClass(), path);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // I made String property getter in Test.class for easily checking how method works
    System.out.println("Test string : " + test.getStringProperty());
    System.out.println("Test int : " + test.getNumbProperty());
    System.out.println("Test time : " + test.getTimeProperty());

  }

  public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) throws Exception {
    Properties properties = new Properties();
    try (InputStream inputStream = new FileInputStream(String.valueOf(propertiesPath))) {
      properties.load(inputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Constructor<T> constructor = cls.getConstructor();
    T t = constructor.newInstance();

    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Annotation[] annotations = field.getDeclaredAnnotations();
      // If annotation exists
      if (annotations.length != 0) {
        for (Annotation annotation : annotations) {
          if (annotation.annotationType().equals(Property.class)) {
            Property property = (Property) annotation;
            // If default name and field exists in app.properties
            if (property.name().equals("N/A") && properties.getProperty(field.getName()) != null && property.format().equals("N/A")) {
              setParams(properties, t, field);
            } else if (property.name().equals("N/A")
                && properties.getProperty(field.getName()) != null && !property.format()
                .equals("N/A")) {
              setParamsWithPropertyFormat(properties, property, t, field);
            }
            // If property name exists in app.properties
            else if (properties.getProperty(property.name()) != null) {
              if (field.getType() == String.class) {
                field.set(t, (String) properties.getProperty(property.name()));
              } else if (field.getType() == int.class) {
                field.set(t, (int) Integer.parseInt(properties.getProperty(property.name())));
              } else if (field.getType() == Instant.class && !property.format().equals("N/A")) {
                try {
                  SimpleDateFormat format = new SimpleDateFormat(property.format());
                  Instant instant = format.parse(properties.getProperty(property.name()))
                      .toInstant();
                  field.set(t, instant);
                } catch (Exception e) {
                  System.err.println("Wrong format");
                  e.printStackTrace();
                }
              } else if (field.getType() == Instant.class) {
                SimpleDateFormat format = new SimpleDateFormat(property.format());
                Instant instant = format.parse(properties.getProperty(property.name())).toInstant();
                field.set(t, instant);
              }
            }
            // Doesn't exist any matches
            else {
              System.err.println("Any matches found");
              throw new Exception();
            }

          }
        }
      }
      // If annotation doesn't exist but field exists in app.properties
      else if (properties.getProperty(field.getName()) != null) {
        setParams(properties, t, field);
      }
      // Doesn't exist any matches
      else {
        throw new Exception();
      }

    }

    return t;
  }

  private static <T> void setParams(Properties properties, T t, Field field)
      throws IllegalAccessException, ParseException {

    if (field.getType() == String.class) {
      field.set(t, (String) properties.getProperty(field.getName()));
    } else if (field.getType() == int.class) {
      field.set(t, (int) Integer.parseInt(properties.getProperty(field.getName())));
    } else if (field.getType() == Instant.class) {
      SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy mm:ss");
      Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
      field.set(t, instant);
    }
  }
  private static <T> void setParamsWithPropertyFormat(Properties properties, Property property, T t, Field field)
      throws IllegalAccessException {

    if (field.getType() == String.class) {
      field.set(t, (String) properties.getProperty(field.getName()));
    } else if (field.getType() == int.class) {
      field.set(t, (int) Integer.parseInt(properties.getProperty(field.getName())));
    } else if (field.getType() == Instant.class) {
      try {
        SimpleDateFormat format = new SimpleDateFormat(property.format());
        Instant instant = format.parse(properties.getProperty(field.getName())).toInstant();
        field.set(t, instant);
      } catch (Exception e) {
        System.err.println("Wrong format");
        e.printStackTrace();
      }
    }
  }

}
