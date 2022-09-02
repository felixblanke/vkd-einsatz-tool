package de.vkd.framework;

public class Variable {
  private final String name;
  private final String value;
  private final String[] args;

  public Variable(String name, String value, String... args) {
    this.name = name;
    this.value = value;
    this.args = args;
  }

  public String getName() {
    return name;
  }

  public String getValue(String localVariableMarker, String... args) {
    if (args.length != this.args.length) {
      return null;
    } else {
      String tempValue = value;
      for (int i = 0; i < this.args.length; i++) {
        if (tempValue.contains(this.args[i])) {
          tempValue = tempValue.replaceAll(
              "\\" + localVariableMarker + this.args[i] + "\\" + localVariableMarker, args[i]);
        }
      }
      return tempValue;
    }
  }
}
