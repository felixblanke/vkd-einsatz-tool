package de.vkd.auxiliary;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public abstract class Auxiliary {
	public static BufferedWriter getUTF8BufferedWriter(String filePath) throws FileNotFoundException{
		CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),encoder));
	}
}
