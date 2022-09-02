package de.vkd.database;

import java.util.List;

public class DatabaseReturnType<E> {
  final List<E> readData;
  final String version;

  public DatabaseReturnType(List<E> readData, String version) {
    this.readData = readData;
    this.version = version;
  }

  public List<E> getReadData() {
    return readData;
  }

  public String getVersion() {
    return version;
  }
}
