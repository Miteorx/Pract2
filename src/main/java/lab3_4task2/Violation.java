package lab3_4task2;

public class Violation {

  private String date;
  private String firstName;
  private String lastName;
  private String type;
  private Double fineAmount;

  public Violation(String date, String firstName, String lastName, String type,
      Double fineAmount) {
    this.date = date;
    this.firstName = firstName;
    this.lastName = lastName;
    this.type = type;
    this.fineAmount = fineAmount;
  }

  public Violation() {
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getFineAmount() {
    return fineAmount;
  }

  public void setFineAmount(Double fineAmount) {
    this.fineAmount = fineAmount;
  }

  @Override
  public String toString() {
    return "Violate{" +
        "date='" + date + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", type='" + type + '\'' +
        ", fineAmount='" + fineAmount + '\'' +
        '}';
  }
}
