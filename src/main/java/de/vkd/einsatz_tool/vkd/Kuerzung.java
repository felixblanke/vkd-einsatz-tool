package de.vkd.einsatz_tool.vkd;

public class Kuerzung {
  private static int currId = 0;

  private final int id;
  private int percentage;
  private String reason;

  public Kuerzung(int percentage, String reason) {
    this.id = currId;
    this.percentage = percentage;
    this.reason = reason;
    currId++;
  }

  public Kuerzung(int id, int percentage, String reason) {
    this.id = id;
    this.percentage = percentage;
    this.reason = reason;
  }

  public static int getCurrId() {
    return currId;
  }

  public int getId() {
    return id;
  }

  public int getPercentage() {
    return percentage;
  }

  public void setPercentage(int percentage) {
    this.percentage = percentage;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Kuerzung clone() {
    return new Kuerzung(getId(), getPercentage(), getReason());
  }
}
