package in.wordofgod.bible.books.introduction.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyBibleZone {

	public static final String OUTPUT_FILE_NAME = "mybible-introductions.sql";
	private static final String SQL_PRE_TEXT = "\nINSERT INTO \"introductions\" (\"book_number\",\"introduction\") VALUES (";
	private static StringBuilder sb = new StringBuilder();

	public static void build() {
		System.out.println(
				"SQL version of MyBible App introductions table of the Bible Book Introduction Creation started");

		createTableCreation();
		createContent();

		// Write to file
		String filePath = BibleBooksIntroductionCreator.sourceDirectory + "/" + OUTPUT_FILE_NAME;
		try {
			Files.writeString(Path.of(filePath), sb.toString());
			System.out.println("Created the file: " + filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(
				"SQL version of MyBible App introductions table of the Bible Book Introduction Creation completed");
	}

	private static void createTableCreation() {
		sb.append("BEGIN TRANSACTION;").append("\n");
		sb.append("CREATE TABLE IF NOT EXISTS \"introductions\" (").append("\n");
		sb.append("\"book_number\"	NUMERIC NOT NULL DEFAULT 0,").append("\n");
		sb.append("\"introduction\"	TEXT NOT NULL DEFAULT '',").append("\n");
		sb.append("PRIMARY KEY(\"book_number\")").append("\n");
		sb.append(");").append("\n");
		sb.append("COMMIT;").append("\n").append("\n");
	}

	private static void createContent() {
		System.out.println("Content Creation Started...");

		File directory = new File(BibleBooksIntroductionCreator.sourceDirectory);
		BufferedReader reader = null;
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (BibleBooksIntroductionCreator.INFORMATION_FILE_NAME.equalsIgnoreCase(file.getName())
					|| OUTPUT_FILE_NAME.equalsIgnoreCase(file.getName()) || file.isDirectory()) {
				continue;
			}
			String word = file.getName().substring(0, file.getName().lastIndexOf("."));

			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				reader = new BufferedReader(isr);
				String line = reader.readLine();
				String sqlBookDescription = SQL_PRE_TEXT + word + ", '";
				String temp = "";
				while (line != null) {
					line = line.strip();
					if (!line.equals("")) {
						temp = temp + buildBookDescription(line);
					}
					line = reader.readLine();
				}
				sqlBookDescription = sqlBookDescription + temp + "');";
				sb.append(sqlBookDescription);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Content Creation Completed...");
	}

	private static String buildBookDescription(String line) {
		if (line.contains("[H1]") || line.contains("[H1[") || line.contains("[H1") || line.contains("]H1]")
				|| line.contains("]H1[") || line.contains("]H1")) {
			line = buildH1Description(line);
		} else if (line.contains("[H2]") || line.contains("[H2[") || line.contains("[H2") || line.contains("]H2]")
				|| line.contains("]H2[") || line.contains("]H2")) {
			line = buildH2Description(line);
		} else if (line.contains("[H3]") || line.contains("[H3[") || line.contains("[H3") || line.contains("]H3]")
				|| line.contains("]H3[") || line.contains("]H3")) {
			line = buildH3Description(line);
		} else {
			line = buildDescription(line);
		}
		line = line.replace("\"", "\\\"");
		line = line.replace("\'", "\\\"");
		line = line.replace("“", "\\\"");
		line = line.replace("”", "\\\"");
		return line;
	}

	private static String buildDescription(String line) {
		line = "<p>" + line + "</p>";
		return line;
	}

	private static String buildH3Description(String line) {
		// Remove the tag [H3]
		line = stripHeaderTags(line, "H3");
		line = "<p><b>" + line + "</b></p>";
		return line;
	}

	private static String buildH2Description(String line) {
		// Remove the tag [H2]
		line = stripHeaderTags(line, "H2");
		line = "<p><b>" + line + "</b></p>";
		return line;
	}

	private static String buildH1Description(String line) {
		// Remove prefix text like 0001 used for identifying unique no of words
		try {
			line = line.replace(line.substring(0, line.indexOf("[H1]")), "");
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		// Remove the tag [H1]
		line = stripHeaderTags(line, "H1");
		line = "<p><b>" + line + "</b></p>";
		return line;
	}

	private static String stripHeaderTags(String line, String headerTag) {
		line = line.replace("[" + headerTag + "]", "").strip();
		line = line.replace("[" + headerTag + "[", "").strip();
		line = line.replace("[" + headerTag + "", "").strip();

		line = line.replace("]" + headerTag + "[", "").strip();
		line = line.replace("]" + headerTag + "]", "").strip();
		line = line.replace("]" + headerTag + "[", "").strip();
		line = line.replace("]" + headerTag + "", "").strip();
		return line;
	}

}