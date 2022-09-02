package de.vkd.auxiliary;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Auxiliary {
  public static BufferedWriter getUTF8BufferedWriter(String filePath) throws FileNotFoundException {
    CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
    encoder.onMalformedInput(CodingErrorAction.REPORT);
    encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
    return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), encoder));
  }

  public static InputStream getResourceFromJAR(String path, Logger logger)
      throws FileNotFoundException {
    InputStream resource = Auxiliary.class.getResourceAsStream(path);
    if (resource == null) {
      throw new FileNotFoundException("failed loading " + path);
    }
    logger.log(Level.FINER, "loading: " + Auxiliary.class.getResource(path).getPath());
    return resource;
  }

  public static URL getResourceURLFromJAR(String path) {
    return Auxiliary.class.getResource(path);
  }
}
