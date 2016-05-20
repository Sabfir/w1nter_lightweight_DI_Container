package helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
	public static boolean createFile(String filepath, String fileName) {
		String lastSymbol = filepath.substring(filepath.length() - 1);
		String splitter = "/";
		String fullpath = filepath;
		if (!lastSymbol.equals("/")) {
			fullpath += splitter;
		}
		fullpath += fileName;

		return createFileByFullpath(fullpath);
	}

	public static boolean createFileByFullpath(String fullpath) {
		Path path = Paths.get(fullpath);

		try {
			if (Files.exists(path)) {
				Files.delete(path);
			} else {
				Files.createFile(path);
				System.out.println("File " + fullpath
						+ " created successfully.");
			}
		} catch (IOException e) {
			System.out.println("Error while creating a File " + fullpath + ".");
		}

		return true;
	}

	public static void deleteFile(String fullpath) {
		Path path = Paths.get(fullpath);

		try {
			Files.deleteIfExists(path);
			System.out.println("File " + fullpath + " deleted successfully.");
		} catch (IOException e) {
			System.out.println("Error while deleting a File " + fullpath + ".");
		}
	}

	public static void addLineToFile(String fullpath, String content) {
		try {
			FileWriter fw = new FileWriter(fullpath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			System.out.println("Error while writing line to the fiel "
					+ fullpath);
		}
	}
}
