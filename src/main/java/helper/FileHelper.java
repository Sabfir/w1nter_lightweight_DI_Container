package helper;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
	private static Logger logger = Logger.getRootLogger();
	public static boolean createFile(final String filepath, final String fileName) {
		String lastSymbol = filepath.substring(filepath.length() - 1);
		String splitter = "/";
		String fullpath = filepath;
		if (!lastSymbol.equals("/")) {
			fullpath += splitter;
		}
		fullpath += fileName;

		return createFileByFullpath(fullpath);
	}

	public static boolean createFileByFullpath(final String fullpath) {
		Path path = Paths.get(fullpath);

		try {
			if (Files.exists(path)) {
				Files.delete(path);
			} else {
				Files.createFile(path);
				logger.info("File " + fullpath + " created successfully.");
			}
		} catch (IOException e) {
			logger.error("Error while creating a File " + fullpath + ".");
		}

		return true;
	}

	public static void deleteFile(final String fullpath) {
		Path path = Paths.get(fullpath);

		try {
			Files.deleteIfExists(path);
			logger.info("File " + fullpath + " deleted successfully.");
		} catch (IOException e) {
			logger.error("Error while deleting a File " + fullpath + ".");
		}
	}

	public static void addLineToFile(final String fullpath, final String content) {
		try {
			FileWriter fw = new FileWriter(fullpath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			logger.error("Error while writing line to the fiel " + fullpath);
		}
	}
}
