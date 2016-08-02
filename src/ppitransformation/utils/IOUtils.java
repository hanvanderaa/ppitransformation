package ppitransformation.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class IOUtils {

	public static List<File> getFilesWithExtension(File directory, String ext) {
		List<File> filtered = new ArrayList<File>();
		List<String> test = new ArrayList<String>();
		for (File file : directory.listFiles()) {
			if (file.getName().endsWith(ext)) {
				filtered.add(file);
				test.add(IOUtils.getFileName(file));
			}
		}
		
		return filtered;
	}
	
	
	public static String readFile(File file) throws IOException {
		Charset encoding = Charset.defaultCharset();
		byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		return new String(encoded, encoding);
	}
	
	
	public static String getFileName(File file) {
		return FilenameUtils.removeExtension(file.getName()).toLowerCase();
	}
	

}
