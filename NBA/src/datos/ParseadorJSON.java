package datos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.*;

public class ParseadorJSON {
	
	public static JSONObject getObjetoPrimario(String dir) {
		JSONObject o = null;
		try {
			o = new JSONObject(archivoAString(dir));
		} catch (Exception e) {}
		return o;
	}
	
	public static String archivoAString(String dir) throws IOException {
		return new String(Files.readAllBytes(Paths.get(dir)));
	}
	
}
