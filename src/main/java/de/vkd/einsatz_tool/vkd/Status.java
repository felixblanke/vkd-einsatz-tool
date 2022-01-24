package de.vkd.einsatz_tool.vkd;

public enum Status {
  EINGETEILT, UE, AE, SE, ERSATZ, ZUSAETZLICH, NONE, AUSGETRETEN;

  private String shortName;
  private String listName;
  private boolean demandingAttendance;

  public static Status getStatusByShortName(String shortName) {
    Status[] v = values();
    for (Status s : v) {
      if (s.getShortName().equals(shortName)) {
        return s;
      }
    }
    return null;
  }

  public static Status getStatusByListName(String listName) {
    Status[] v = values();
    for (Status s : v) {
      if (s.getListName().equals(listName)) {
        return s;
      }
    }
    return null;
  }

  public void setName(String shortName) {
    setName(shortName, shortName);
  }

  public void setName(String shortName, String listName) {
    this.shortName = shortName;
    this.listName = listName;
  }

  public boolean isDemandingAttendance() {
    return demandingAttendance;
  }

  public void setDemandingAttendance(boolean demandingAttendance) {
    this.demandingAttendance = demandingAttendance;
  }

  public String getShortName() {
    return shortName;
  }

  public String getListName() {
    return listName;
  }

}
