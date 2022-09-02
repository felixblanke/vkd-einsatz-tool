package de.vkd.database;

import de.vkd.auxiliary.Auxiliary;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * It is recommended to comment the first line of each csv file that is loaded.
 *
 * @param <E>
 * @author Felix
 */
public class DatabaseCSV<E> {
  //If a line starts with this string, it will be regarded as a comment and therefore ignored
  private static final String DEFAULT_COMMENT_STR = "#";

  private final String commentString;
  private final String dbVersion;
  private final List<E> readData;

  public DatabaseCSV(String filedir, String charsetName, DatabaseEntryCreator<E> creator,
                     String versionMarker, String defaultVersion)
      throws ReadDataException, IOException {
    this(filedir, charsetName, creator, versionMarker, defaultVersion, DEFAULT_COMMENT_STR);
  }

  public DatabaseCSV(String filedir, String charsetName, DatabaseEntryCreator<E> creator,
                     String versionMarker, String defaultVersion, String commentString)
      throws ReadDataException, IOException {
    this.commentString = commentString;
    List<E> readData = new ArrayList<>();

    Scanner scanner;
    InputStream resource =
        Auxiliary.getResourceFromJAR(filedir, Logger.getLogger(DatabaseCSV.class.getName()));
    scanner = new Scanner(resource, charsetName);

    try {
      //skip first line:
      scanner.nextLine();

      String line = getNextLine(scanner);
      if (line == null) {
        throw new ReadDataException(
            filedir + " could not be loaded. File might be empty or invalid.");
      }

      if (line.startsWith(versionMarker)) {
        line = line.substring(versionMarker.length()).trim();
        if (!line.isEmpty()) {
          this.dbVersion = line;
        } else {
          throw new ReadDataException(filedir + " could not be loaded, no valid version.");
        }
        line = getNextLine(scanner);
      } else {
        this.dbVersion = defaultVersion;
      }
      String[] headings = line.split("[;]"); //column headings
      int[] indices = new int[creator.getArgNames().length];
      if (headings.length < indices.length) {
        throw new ReadDataException("Not enough columns in .csv file");
      }
      for (int i = 0; i < creator.getArgNames().length; i++) {
        boolean b = false;
        for (int k = 0; k < headings.length; k++) {
          if (headings[k].trim().equals(creator.getArgNames()[i])) {
            if (!b) {
              indices[i] = k;
            } else {
              throw new ReadDataException(
                  "Exception during the process of locating the args in the header of the csv"
                      + " file");
            }
            b = true;
          }
        }
        if (!b) {
          throw new ReadDataException(
              "There is no column called " + creator.getArgNames()[i] + " in the .csv file");
        }
      }
      while (true) {
        String csvLine;
        csvLine = getNextLine(scanner);
        if (csvLine == null) {
          break; //breaks if there is no next line
        }

        String[] arr = csvLine.split("[;]", -1);

        String[] args = new String[creator.getArgNames().length];
        for (int i = 0; i < args.length; i++) {
          args[i] = arr[indices[i]];
        }

        readData.add(creator.create(args));
      }
    } finally {
      scanner.close();
    }
    this.readData = readData;
  }

  /**
   * Uses the scanner to search for the next line that is no comment. If no valid line is found,
   * null is returned instead.
   */
  private String getNextLine(Scanner s) {
    while (s.hasNextLine()) {
      String line = s.nextLine();
      if (!line.startsWith(commentString)) {
        return line;
      }
    }
    return null;
  }

  public List<E> getReadData() {
    return readData;
  }

  public String getVersion() {
    return dbVersion;
  }

  public String getCommentString() {
    return commentString;
  }

  public String getDbVersion() {
    return dbVersion;
  }
}
