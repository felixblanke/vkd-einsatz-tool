package de.vkd.einsatz_tool.vkd;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VK {
  private static int currID = 0;
  private final String name;
  private final String surname;
  private final Rank rank;
  private final int group;
  private final int position;
  private final int id;
  private final List<Kuerzung> kuerzungsListe = new ArrayList<>();
  private final boolean driver;
  private boolean selected = false;
  private Status status = Status.NONE;
  private String remark = "";
  private VK ersatz;
  private LocalDateTime beginDateTime = null;
  private LocalDateTime endDateTime = null;

  //constructors
  public VK(int group, Rank rank, String name, String surname, int position, boolean driver) {
    currID++;
    this.name = name;
    this.surname = surname;
    this.group = group;
    this.rank = rank;
    this.position = position;
    this.driver = driver;
    this.id = currID;
  }

  //copies the parameter, but creates a 'clean' copy
  public VK(VK vk) {
    this.name = vk.getName();
    this.surname = vk.getSurname();
    this.group = vk.getGroup();
    this.rank = vk.getRank();
    this.position = vk.getPosition();
    this.driver = vk.isDriver();
    this.id = vk.getId();
    //TODO: Check if this is the intendet behaviour
    this.remark = vk.getRemark();
    this.selected = vk.isSelected();
    this.status = vk.getStatus();
    this.ersatz = vk.getErsatz();
  }

  //methods
  @Override
  public String toString() {
    String s = " || ";
    StringBuilder kuerzungBuilder = new StringBuilder();
    for (Kuerzung k : getKuerzungsListe()) {
      kuerzungBuilder.append("(").append(k.getId()).append(", ").append(k.getPercentage())
          .append("%, ").append(k.getReason()).append("), ");
    }
    String kuerzung = kuerzungBuilder.toString();
    if (kuerzung.length() > 2) {
      kuerzung = kuerzung.substring(0, kuerzung.length() - 2);
    }

    if (status.equals(Status.ERSATZ) && ersatz != null) {
      return (Main.outputGroup(group) + s + Main.getRankString(rank) + s + Main.outputPosition(
          position) + s + this.name + s + this.surname + s + status.getListName() + s
          + ersatz.getStringRepresentation() + s + kuerzung + s + getRemark());
    }
    return (Main.outputGroup(group) + s + Main.getRankString(rank) + s + Main.outputPosition(
        position) + s + this.name + s + this.surname + s + status.getListName() + s + kuerzung + s
        + getRemark());
  }

  //getter and setter
  public int getGroup() {
    return group;
  }

  public Rank getRank() {
    return rank;
  }

  public int getPosition() {
    return position;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public int getId() {
    return id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public List<Kuerzung> getKuerzungsListe() {
    return kuerzungsListe;
  }

  public VK getErsatz() {
    return ersatz;
  }

  public void setErsatz(VK ersatz) {
    this.ersatz = ersatz;
  }

  public boolean isDriver() {
    return driver;
  }

  public boolean hasAttendedEinsatz() {
    return getStatus().equals(Status.EINGETEILT) || getStatus().equals(Status.ERSATZ)
        || getStatus().equals(Status.ZUSAETZLICH);
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getStringRepresentation() {
    return Main.getRankString(getRank()).concat(" ").concat(getName()).concat(" ")
        .concat(getSurname());
  }

  public LocalDateTime getBeginDateTime() {
    return this.beginDateTime;
  }

  public LocalDateTime getEndDateTime() {
    return this.endDateTime;
  }

  public boolean hasIndividualTimes() {
    return this.beginDateTime != null && this.endDateTime != null;
  }

  public void updateIndividualTimes(LocalDateTime beginDateTime, LocalDateTime endDateTime) {
    assert ! ((beginDateTime == null) ^ (endDateTime == null));
    this.beginDateTime = beginDateTime;
    this.endDateTime = endDateTime;
  }
}
