/**
 * 
 */
package in.wordofgod.bible.books.introduction.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * 
 */
public class BibleBooksIntroductionCreator {

	public static final String INFORMATION_FILE_NAME = "INFORMATION.txt";
	public static boolean formatXML = true;
	public static String sourceDirectory;
	public static String outputFile;
	public static Properties BOOK_DETAILS = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException {

		if (!validateInput(args)) {
			return;
		}

		loadBookDetails();
		if ("yes".equalsIgnoreCase(BOOK_DETAILS.getProperty(Constants.STR_CREATE_WORD_DOCUMENT))) {
			WordDocument.build();
		}
		if ("yes".equalsIgnoreCase(BOOK_DETAILS.getProperty(Constants.STR_CREATE_FOR_MYBIBLE_APP))) {
			MyBibleZone.build();
		}
	}

	private static boolean validateInput(String[] args) {
		if (args.length == 0) {
			System.out.println("Please input source folder name/path..");
			printHelpMessage();
			return false;
		} else {
			sourceDirectory = args[0];

			File folder = new File(sourceDirectory);
			if (!folder.exists() || !folder.isDirectory()) {
				System.out.println("Directory " + sourceDirectory + " Does not exists");
				return false;
			}

			if (folder.listFiles().length == 0) {
				System.out.println("Directory " + sourceDirectory + " does not have any files.\n");
				printHelpMessage();
				return false;
			}

			if (sourceDirectory.contains("\\")) {
				outputFile = sourceDirectory.substring(sourceDirectory.lastIndexOf("\\"), sourceDirectory.length())
						.replace("\\", "");
			} else {
				outputFile = sourceDirectory.substring(0, sourceDirectory.length());
			}
		}
		return true;
	}

	private static void loadBookDetails() throws IOException {
		BOOK_DETAILS = new Properties();
		BufferedReader propertyReader;
		try {
			File infoFile = new File(sourceDirectory + "//" + INFORMATION_FILE_NAME);
			propertyReader = new BufferedReader(new InputStreamReader(new FileInputStream(infoFile), "UTF8"));
			BOOK_DETAILS.load(propertyReader);
			propertyReader.close();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			System.out.println("INFORMATION.txt file is missing, it is mandatory to continue..");
			printHelpMessage();
			throw e1;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.out.println("INFORMATION.txt file is missing, it is mandatory to continue..");
			printHelpMessage();
			throw e1;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("INFORMATION.txt file is missing, it is mandatory to continue..");
			printHelpMessage();
			throw e;
		}
	}

	public static void printHelpMessage() {
		System.out.println("\nHelp on Usage of this prorgam:");
		System.out.println("You can check the sample input files in the directory \"sample-directory\"");
		System.out.println("\nInclude INFORMATION.txt inside the folder....");
		System.out.println(
				"INFORMATION.txt can contain non mandatory values this way: regEx=<your regular expression>;replaceWith=<matching data to be replaced with>");
		System.out.println("Example: \nsubject=Bible Books Introduction\n" + "publisher=Publisher Name\n"
				+ "title=Bible Books Introduction\n" + "subTitle=Introduction to all 66 books in the bible\n"
				+ "author=Author Name\n" + "creator=Your Naame\n"
				+ "descriptionTitle=Additional Details of this book:\n" + "description=Some description goes here\n"
				+ "identifier=Some Unique Name without space\n" + "language=en\n"
				+ "createZefaniaXML=no\t(it can take yes or no)\n" + "createWordDocument=yes\t(it can take yes or no)\n"
				+ "titleFont=Uni Ila.Sundaram-08\r\n" + "titleFontSize=36\r\n" + "subTitleFont=Uni Ila.Sundaram-04\r\n"
				+ "subTitleFontSize=16\r\n" + "authorFont=Uni Ila.Sundaram-08\r\n" + "authorFontSize=22\r\n"
				+ "headerFont=Uni Ila.Sundaram-08\r\n" + "headerFontSize=16\r\n" + "contentFont=Uni Ila.Sundaram-04\r\n"
				+ "contentFontSize=12\r\n" + "indexPageTitle=Index");
		System.out
				.println("\nPlease use one file per book introduction. Directory should have the numbering prefixed.");
		System.out.println("First line of the file will be used to create the indexes in the Index Page");
		System.out.println(
				"Include [H1] or [H2] or [H3] in the beginning of the line to highlight heading, sub headings");
		System.out.println("Exmple 1: 01-Genesis.txt, 02-Exodus.txt, etc");
		System.out.println("Exmple 2: 1-Genesis.txt, 2-Exodus.txt, etc");
		System.out.println(
				"\nSyntax to run this progam:\njava -jar bible-books-introduction-creator.jar <Source Folder Name or Path>");
		System.out.println("\nExample 1: java -jar bible-books-introduction-creator.jar directory1");
		System.out.println("Example 2: java -jar bible-books-introduction-creator.jar \"C:/somedirectory/directory1\"");
	}

}
