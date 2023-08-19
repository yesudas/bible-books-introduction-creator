package in.wordofgod.bible.books.introduction.creator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.apache.poi.ooxml.POIXMLProperties.CoreProperties;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public class WordDocument {

	private static final int NO_OF_COLUMNS_IN_CONTENT_PAGES = 1;

	private static final int DEFAULT_FONT_SIZE = 12;

	public static final String EXTENSION = ".docx";

	private static boolean CONTENT_IN_TWO_COLUMNS = false;

	private static int uniqueBookMarkCounter = 1;

	public static void build() {
		System.out.println("Word Document of the Bible Book Introduction Creation started");

		XWPFDocument document = new XWPFDocument();

		createPageSettings(document);
		createMetaData(document);
		createTitlePage(document);
		createBookDetailsPage(document);
		createPDFIssuePage(document);
		createIndex(document);
		createContent(document);

		// Write to file
		File file = new File(BibleBooksIntroductionCreator.outputFile + EXTENSION);
		try {
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			System.out.println("File created here: " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Word Document of the Bible Book Introduction Creation completed");

	}

	private static void createContent(XWPFDocument document) {
		System.out.println("Content Creation Started...");

		File directory = new File(BibleBooksIntroductionCreator.sourceDirectory);
		XWPFParagraph paragraph = null;
		CTBookmark bookmark = null;
		BufferedReader reader = null;
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (BibleBooksIntroductionCreator.INFORMATION_FILE_NAME.equalsIgnoreCase(file.getName())
					|| file.isDirectory()) {
				continue;
			}
			String word = file.getName().substring(0, file.getName().lastIndexOf("."));

			// Display the word as header
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			// run = paragraph.createRun();
			// run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_HEADER_FONT));
			// run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE) + 8);
			// run.setBold(true);
			// run.setText(word);

			// Set background color
			// CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
			// cTShd.setVal(STShd.CLEAR);
			// cTShd.setFill("ABABAB");

			// Create bookmark for the word
			bookmark = paragraph.getCTP().addNewBookmarkStart();
			bookmark.setName(word.replaceAll(" ", "_"));
			bookmark.setId(BigInteger.valueOf(uniqueBookMarkCounter));
			paragraph.getCTP().addNewBookmarkEnd().setId(BigInteger.valueOf(uniqueBookMarkCounter));
			uniqueBookMarkCounter++;

			try {
				FileInputStream fis = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
				reader = new BufferedReader(isr);
				String line = reader.readLine();

				while (line != null) {

					line = line.strip();
					if (!line.equals("")) {

						if (line.contains("[H1]")) {
							buildH1Description(document, line, paragraph);
						} else if (line.contains("[H2]")) {
							buildH2Description(document, line);
						} else if (line.contains("[H3]")) {
							buildH3Description(document, line);
						} else {
							buildDescription(document, line, null, false);
						}
					}
					line = reader.readLine();
				}

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (i < files.length) {
				addSectionBreak(document, NO_OF_COLUMNS_IN_CONTENT_PAGES, true);
			}
		}

		System.out.println("Content Creation Completed...");
	}

	private static void buildDescription(XWPFDocument document, String line, XWPFParagraph paragraph, boolean isBold) {
		if (paragraph == null) {
			paragraph = document.createParagraph();
		}

		if (CONTENT_IN_TWO_COLUMNS) {
			paragraph.setAlignment(ParagraphAlignment.BOTH);
		}
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE));
		run.setBold(isBold);
		run.setText(line);
	}

	private static int getFontSize(String key) {
		if (BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(key) == null) {
			return DEFAULT_FONT_SIZE;
		} else {
			try {
				return (Integer.parseInt(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(key)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return DEFAULT_FONT_SIZE;
			}
		}
	}

	private static void buildH3Description(XWPFDocument document, String line) {
		// Remove the tag [H3]
		line = line.replaceAll("\\[H3\\]", "").strip();
		XWPFParagraph paragraph = document.createParagraph();
		// paragraph.setStyle("Heading 3");
		// paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 2);
		run.setBold(true);
		run.setText(line);
	}

	private static void buildH2Description(XWPFDocument document, String line) {
		// Remove the tag [H2]
		line = line.replaceAll("\\[H2\\]", "").strip();
		XWPFParagraph paragraph = document.createParagraph();
		// paragraph.setStyle("Heading 2");
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 4);
		run.setBold(true);
		run.setText(line);
	}

	private static void buildH1Description(XWPFDocument document, String line, XWPFParagraph paragraph) {
		// Remove prefix text like 0001 used for identifying unique no of words
		try {
			line = line.replace(line.substring(0, line.indexOf("[H1]")), "");
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		// Remove the tag [H1]
		line = line.replaceAll("\\[H1\\]", "").strip();
		// XWPFParagraph paragraph = document.createParagraph();
		// Keep the title always in the middle
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		// paragraph.setStyle("Heading 1");
		// if (CONTENT_IN_TWO_COLUMNS) {
		// paragraph.setAlignment(ParagraphAlignment.BOTH);
		// }
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 6);
		run.setBold(true);
		run.setText(line);
	}

	private static void createBookDetailsPage(XWPFDocument document) {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;

		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);

		// Dictionary Details - Label
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_HEADER_FONT));
		run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_DESCRIPTION_TITLE));

		// Dictionary Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_HEADER_FONT));
		run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_DESCRIPTION));

		// run.addBreak(BreakType.PAGE);
		addSectionBreak(document, 1, false);
	}

	private static void createPageSettings(XWPFDocument document) {
		CTDocument1 doc = document.getDocument();
		CTBody body = doc.getBody();

		if (!body.isSetSectPr()) {
			body.addNewSectPr();
		}

		CTSectPr section = body.getSectPr();

		if (!section.isSetPgSz()) {
			section.addNewPgSz();
		}
		CTPageSz pageSize = section.getPgSz();
		pageSize.setOrient(STPageOrientation.PORTRAIT);
		pageSize.setW(BigInteger.valueOf(595 * 20));
		pageSize.setH(BigInteger.valueOf(842 * 20));
		System.out.println("Page Setting completed");
	}

	private static void createMetaData(XWPFDocument document) {
		CoreProperties props = document.getProperties().getCoreProperties();
		// props.setCreated("2019-08-14T21:00:00z");
		props.setLastModifiedByUser(
				BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CREATOR));
		props.setCreator(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CREATOR));
		// props.setLastPrinted("2019-08-14T21:00:00z");
		// props.setModified("2019-08-14T21:00:00z");
		try {
			document.getProperties().commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Meta Data Creation completed");
	}

	private static void createTitlePage(XWPFDocument document) {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;

		// title
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_TITLE_FONT));
		run.setFontSize(getFontSize(Constants.STR_TITLE_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_TITLE));
		run.addBreak();
		run.addBreak();

		// sub title
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_SUB_TITLE_FONT));
		run.setFontSize(getFontSize(Constants.STR_SUB_TITLE_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_SUB_TITLE));
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();

		// author
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_AUTHOR_FONT));
		run.setFontSize(getFontSize(Constants.STR_AUTHOR_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_AUTHOR));

		run.addBreak(BreakType.PAGE);
		System.out.println("Title Page Creation completed");
	}

	private static void createPDFIssuePage(XWPFDocument document) {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 2);
		run.setText(
				"If you are using this PDF in mobile, Navigation by Index may not work with Google Drive's PDF viewer. I would recommend ReadEra App for better performance and navigation experience.");
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();
		run.addBreak();

		// run.addBreak(BreakType.PAGE);
		addSectionBreak(document, 1, false);
	}

	private static void createIndex(XWPFDocument document) {
		System.out.println("Index Creation Started...");

		File directory = new File(BibleBooksIntroductionCreator.sourceDirectory);
		XWPFParagraph paragraph;
		XWPFRun run = null;
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setStyle("Heading 1");

		// Index Page Heading
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_HEADER_FONT));
		run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE));
		String temp = BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_INDEX_TITLE);
		if (temp == null || temp.isBlank()) {
			run.setText("Index");
		} else {
			run.setText(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_INDEX_TITLE));
		}

		// Set background color
		// CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
		// cTShd.setVal(STShd.CLEAR);
		// cTShd.setFill("ABABAB");

		CTBookmark bookmark = paragraph.getCTP().addNewBookmarkStart();
		bookmark.setName(BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_INDEX_TITLE));
		bookmark.setId(BigInteger.valueOf(uniqueBookMarkCounter));
		paragraph.getCTP().addNewBookmarkEnd().setId(BigInteger.valueOf(uniqueBookMarkCounter));
		uniqueBookMarkCounter++;

		// Words Index
		paragraph = document.createParagraph();
		paragraph.setSpacingAfter(0);
		for (File file : directory.listFiles()) {
			if (BibleBooksIntroductionCreator.INFORMATION_FILE_NAME.equalsIgnoreCase(file.getName())) {
				continue;
			}
			// String word = file.getName().substring(0, file.getName().lastIndexOf("."));
			String word = getIndexWord(file);
			if(word==null || word.isBlank()) {
				System.out.println("First line of the file cannot be blank, it is being used as index titles in the Index Page.");
				BibleBooksIntroductionCreator.printHelpMessage();
				return;
			}
			createAnchorLink(paragraph, word, word.replaceAll(" ", "_"), true, "",
					BibleBooksIntroductionCreator.DICTIONARY_DETAILS.getProperty(Constants.STR_CONTENT_FONT),
					getFontSize(Constants.STR_CONTENT_FONT_SIZE));
		}

		paragraph = document.createParagraph();
		run = paragraph.createRun();
		// run.addBreak(BreakType.PAGE);
		addSectionBreak(document, NO_OF_COLUMNS_IN_CONTENT_PAGES, true);

		System.out.println("Index Creation Completed...");
	}

	private static String getIndexWord(File file) {
		String line = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
			BufferedReader reader = new BufferedReader(isr);
			line = reader.readLine();
			line = line.strip();

			// Remove prefix text like 0001 used for identifying unique no of words
			try {
				line = line.replace(line.substring(0, line.indexOf("[H1]")), "");
			} catch (StringIndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			// Remove the tag [H1]
			line = line.replaceAll("\\[H1\\]", "").strip();

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}

	private static void createAnchorLink(XWPFParagraph paragraph, String linkText, String bookMarkName,
			boolean carriageReturn, String space, String fontFamily, int fontSize) {
		CTHyperlink cthyperLink = paragraph.getCTP().addNewHyperlink();
		cthyperLink.setAnchor(bookMarkName);
		cthyperLink.addNewR();
		XWPFHyperlinkRun hyperlinkrun = new XWPFHyperlinkRun(cthyperLink, cthyperLink.getRArray(0), paragraph);
		hyperlinkrun.setFontFamily(fontFamily);
		hyperlinkrun.setFontSize(fontSize);
		hyperlinkrun.setText(linkText);
		hyperlinkrun.setColor("0000FF");
		hyperlinkrun.setUnderline(UnderlinePatterns.SINGLE);
		if (!space.isEmpty()) {
			XWPFRun run = paragraph.createRun();
			run.setText(space);
		}
		if (carriageReturn) {
			XWPFRun run = paragraph.createRun();
			run.addCarriageReturn();
		}
	}

	/**
	 * Adds Section Settings for the contents added so far
	 * 
	 * @param document
	 */
	private static CTSectPr addSectionBreak(XWPFDocument document, int noOfColumns, boolean setMargin) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph = document.createParagraph();
		CTSectPr ctSectPr = paragraph.getCTP().addNewPPr().addNewSectPr();
		CTColumns ctColumns = ctSectPr.addNewCols();
		ctColumns.setNum(BigInteger.valueOf(noOfColumns));

		if (setMargin) {
			CTPageMar pageMar = ctSectPr.getPgMar();
			if (pageMar == null) {
				pageMar = ctSectPr.addNewPgMar();
			}
			pageMar.setLeft(BigInteger.valueOf(648));// 0.45"*72*20
			// 720 TWentieths of an Inch Point (Twips) = 720/20 = 36 pt; 36/72 = 0.5"
			pageMar.setRight(BigInteger.valueOf(648));
			pageMar.setTop(BigInteger.valueOf(648));
			pageMar.setBottom(BigInteger.valueOf(648));
			// pageMar.setFooter(BigInteger.valueOf(720));
			// pageMar.setHeader(BigInteger.valueOf(720));
			// pageMar.setGutter(BigInteger.valueOf(0));
		}
		return ctSectPr;
	}
}