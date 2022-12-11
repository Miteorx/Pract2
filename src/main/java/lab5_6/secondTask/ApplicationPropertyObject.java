package lab5_6.secondTask;

import java.time.Instant;

public class ApplicationPropertyObject {
  private String stringProperty;

  @Property(name = "numberProperty")
  private int numberProperty;
  @Property(name = "timeProperty", format = "dd.MM.yyyy mm:ss")
  private Instant timeProperty;

  public String getStringProperty() {
    return stringProperty;
  }

  public int getNumbProperty() {
    return numberProperty;
  }

  public Instant getTimeProperty() {
    return timeProperty;
  }
}
