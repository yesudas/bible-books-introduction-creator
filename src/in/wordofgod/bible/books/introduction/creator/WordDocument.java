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
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrGeneral;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

public class WordDocument {

	private static final int NO_OF_COLUMNS_IN_CONTENT_PAGES = 1;

	private static final int DEFAULT_FONT_SIZE = 12;

	public static final String EXTENSION = ".docx";

	private static int uniqueBookMarkCounter = 1;

	public static void build() {
		System.out.println("Word Document of the Bible Book Introduction Creation started");

		XWPFDocument document = new XWPFDocument();
		addCustomHeadingStyle(document, "Heading 1", 1);
		addCustomHeadingStyle(document, "Heading 2", 2);
		addCustomHeadingStyle(document, "Heading 3", 3);
		addCustomHeadingStyle(document, "Heading 4", 4);
		addCustomHeadingStyle(document, "Heading 5", 5);
		addCustomHeadingStyle(document, "Heading 6", 6);
		addCustomHeadingStyle(document, "Heading 7", 7);

		createPageSettings(document);
		createMetaData(document);
		createCoverPageFront(document);
		createTitlePage(document);
		createBookDetailsPage(document);
		createPDFIssuePage(document);
		createIndex(document);
		createContent(document);

		// Write to file
		File file = new File(BibleBooksIntroductionCreator.sourceDirectory + EXTENSION);
		try {
			FileOutputStream out = new FileOutputStream(file);
			document.write(out);
			System.out.println("File created here: " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Word Document of the Bible Book Introduction Creation completed");
	}

	private static void createCoverPageFront(XWPFDocument document) {
		System.out.println("Cover Page Front Page Creation started");
		XWPFParagraph paragraph = null;
		XWPFRun run = null;

		// title
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_TITLE_FONT));
		run.setFontSize(getFontSize(Constants.STR_TITLE_FONT_SIZE));
		run.setText("Space for Cover Page");
		run.addBreak();
		run.addBreak();

		addSectionBreak(document, NO_OF_COLUMNS_IN_CONTENT_PAGES);
		System.out.println("Cover Page Front Page Creation completed");
	}

	/**
	 * Adds Section Settings for the contents added so far
	 * 
	 * @param document
	 */
	private static CTSectPr addSectionBreak(XWPFDocument document, int noOfColumns) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph = document.createParagraph();

		CTSectPr ctSectPr = paragraph.getCTP().addNewPPr().addNewSectPr();
		setPageSize(ctSectPr);
		setPageMargin(ctSectPr);

		CTColumns ctColumns = ctSectPr.addNewCols();
		ctColumns.setNum(BigInteger.valueOf(noOfColumns));
		return ctSectPr;
	}

	private static void setPageSize(CTSectPr ctSectPr) {
		CTPageSz pageSize;
		if (!ctSectPr.isSetPgSz()) {
			pageSize = ctSectPr.addNewPgSz();
		} else {
			pageSize = ctSectPr.getPgSz();
		}

		pageSize.setOrient(STPageOrientation.PORTRAIT);

		// double width_cm =
		// Math.round(pageSize.getW().doubleValue()/20d/72d*2.54d*100d)/100d;
		// double height_cm =
		// Math.round(pageSize.getH().doubleValue()/20d/72d*2.54d*100d)/100d;

		String strPageSize = BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_PAGE_SIZE);
		if ("B5".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_B5_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_B5_H * 20));
		} else if ("B4".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_B4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_B4_H * 20));
		} else if ("A5".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A5_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A5_H * 20));
		} else if ("A4".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A4_H * 20));
		} else if ("A3".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A3_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A3_H * 20));
		} else if ("A2".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A2_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A2_H * 20));
		} else if ("A1".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A1_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A1_H * 20));
		} else if ("A0".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A0_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A0_H * 20));
		} else if ("Executive".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Executive_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Executive_H * 20));
		} else if ("Statement".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Statement_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Statement_H * 20));
		} else if ("Legal".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Legal_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Legal_H * 20));
		} else if ("Ledger".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Ledger_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Ledger_H * 20));
		} else if ("Tabloid".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Tabloid_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Tabloid_H * 20));
		} else if ("Letter".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Letter_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Letter_H * 20));
		} else if ("Folio".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Folio_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Folio_H * 20));
		} else if ("Quarto".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Quarto_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Quarto_H * 20));
		} else if ("10x14".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_10x14_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_10x14_H * 20));
		} else {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A4_H * 20));
		}
	}

	private static void addCustomHeadingStyle(XWPFDocument docxDocument, String strStyleId, int headingLevel) {

		CTStyle ctStyle = CTStyle.Factory.newInstance();
		ctStyle.setStyleId(strStyleId);

		CTString styleName = CTString.Factory.newInstance();
		styleName.setVal(strStyleId);
		ctStyle.setName(styleName);

		CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
		indentNumber.setVal(BigInteger.valueOf(headingLevel));

		// lower number > style is more prominent in the formats bar
		ctStyle.setUiPriority(indentNumber);

		CTOnOff onoffnull = CTOnOff.Factory.newInstance();
		ctStyle.setUnhideWhenUsed(onoffnull);

		// style shows up in the formats bar
		ctStyle.setQFormat(onoffnull);

		// style defines a heading of the given level
		CTPPrGeneral ppr = CTPPrGeneral.Factory.newInstance();
		ppr.setOutlineLvl(indentNumber);
		ctStyle.setPPr(ppr);

		XWPFStyle style = new XWPFStyle(ctStyle);

		// is a null op if already defined
		XWPFStyles styles = docxDocument.createStyles();

		style.setType(STStyleType.PARAGRAPH);
		styles.addStyle(style);

	}

	private static void createContent(XWPFDocument document) {
		System.out.println("Content Creation Started...");

		File directory = new File(BibleBooksIntroductionCreator.sourceDirectory);
		XWPFParagraph paragraph = null;
		CTBookmark bookmark = null;
		BufferedReader reader = null;
		File[] files = directory.listFiles();
		int chapterNo = 1;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (BibleBooksIntroductionCreator.INFORMATION_FILE_NAME.equalsIgnoreCase(file.getName())
					|| file.isDirectory()) {
				continue;
			}
			String word = file.getName().substring(0, file.getName().lastIndexOf("."));
			if ("yes".equalsIgnoreCase(
					BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_GENERATE_CHAPTER_NO))) {
				word = chapterNo + ". " + word;
			}

			// Display the word as header
			paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			// run = paragraph.createRun();
			// run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_HEADER_FONT));
			// run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE) + 8);
			// run.setBold(true);
			// run.setText(word);

			// Set background color
			// CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
			// cTShd.setVal(STShd.CLEAR);
			// cTShd.setFill("ABABAB");

			// Create bookmark for the word
			bookmark = paragraph.getCTP().addNewBookmarkStart();
			bookmark.setName(word.replace(" ", "_").replace(".", "_"));
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
							buildH1Description(document, line, paragraph, chapterNo++);
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

		paragraph.setAlignment(ParagraphAlignment.BOTH);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE));
		run.setBold(isBold);
		run.setText(line);
	}

	private static int getFontSize(String key) {
		if (BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(key) == null) {
			return DEFAULT_FONT_SIZE;
		} else {
			try {
				return (Integer.parseInt(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(key)));
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
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE));
		run.setBold(true);
		run.setText(line);
	}

	private static void buildH2Description(XWPFDocument document, String line) {
		// Remove the tag [H2]
		line = line.replaceAll("\\[H2\\]", "").strip();
		XWPFParagraph paragraph = document.createParagraph();
		// paragraph.setStyle("Heading 2");
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 2);
		run.setBold(true);
		run.setText(line);
	}

	private static void buildH1Description(XWPFDocument document, String line, XWPFParagraph paragraph, int chapterNo) {
		// Remove prefix text like 0001 used for identifying unique no of words
		try {
			line = line.replace(line.substring(0, line.indexOf("[H1]")), "");
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		if ("yes".equalsIgnoreCase(BibleBooksIntroductionCreator.BOOK_DETAILS
				.getProperty(Constants.STR_GENERATE_CHAPTER_NO))) {
			line = chapterNo + ". " + line;
		}
		// Remove the tag [H1]
		line = line.replaceAll("\\[H1\\]", "").strip();
		// XWPFParagraph paragraph = document.createParagraph();
		// Keep the title always in the middle
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		paragraph.setStyle("Heading 1");
		// paragraph.setStyle("Heading 1");
		// if (CONTENT_IN_TWO_COLUMNS) {
		// paragraph.setAlignment(ParagraphAlignment.BOTH);
		// }
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 6);
		run.setBold(true);
		run.setText(line);
	}

	private static void createBookDetailsPage(XWPFDocument document) {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;

		// Book Edition - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOK_EDITION));

		// Book Description - Label
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_DESCRIPTION_TITLE));

		// Book Description - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_DESCRIPTION));

		// Author Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_AUTHOR_TITLE));

		// Author Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_AUTHOR));

		// Creator Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CREATOR_TITLE));

		// Creator Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CREATOR));

		// Publisher Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_PUBLISHER_TITLE));

		// Publisher Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_PUBLISHER));

		// Copyright Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_COPYRIGHT_TITLE));

		// Copyright Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_COPYRIGHT));

		// Download Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_DOWNLOAD_TITLE));

		// Download Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_DOWNLOAD));

		// Contact US Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTACTUS_TITLE));

		// Contact US Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTACTUS));

		// Follow US Details - Label
		paragraph = document.createParagraph();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setBold(true);
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_FOLLOWUS_TITLE));

		// Follow US Details - Content
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		run = paragraph.createRun();
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_BOOKDETAILS_FONT));
		run.setFontSize(getFontSize(Constants.STR_BOOKDETAILS_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_FOLLOWUS));

		// run.addBreak(BreakType.PAGE);
		addSectionBreak(document, 1, false);
	}

	private static void createPageSettings(XWPFDocument document) {

		CTDocument1 doc = document.getDocument();
		CTBody body = doc.getBody();

		if (!body.isSetSectPr()) {
			body.addNewSectPr();
		}

		CTSectPr ctSectPr = body.getSectPr();

		CTPageSz pageSize;
		if (!ctSectPr.isSetPgSz()) {
			pageSize = ctSectPr.addNewPgSz();
		} else {
			pageSize = ctSectPr.getPgSz();
		}

		pageSize.setOrient(STPageOrientation.PORTRAIT);

		// double width_cm =
		// Math.round(pageSize.getW().doubleValue()/20d/72d*2.54d*100d)/100d;
		// double height_cm =
		// Math.round(pageSize.getH().doubleValue()/20d/72d*2.54d*100d)/100d;

		String strPageSize = BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_PAGE_SIZE);
		if ("B5".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_B5_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_B5_H * 20));
		} else if ("B4".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_B4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_B4_H * 20));
		} else if ("A5".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A5_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A5_H * 20));
		} else if ("A4".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A4_H * 20));
		} else if ("A3".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A3_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A3_H * 20));
		} else if ("A2".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A2_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A2_H * 20));
		} else if ("A1".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A1_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A1_H * 20));
		} else if ("A0".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A0_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A0_H * 20));
		} else if ("Executive".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Executive_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Executive_H * 20));
		} else if ("Statement".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Statement_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Statement_H * 20));
		} else if ("Legal".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Legal_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Legal_H * 20));
		} else if ("Ledger".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Ledger_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Ledger_H * 20));
		} else if ("Tabloid".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Tabloid_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Tabloid_H * 20));
		} else if ("Letter".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Letter_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Letter_H * 20));
		} else if ("Folio".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Folio_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Folio_H * 20));
		} else if ("Quarto".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_Quarto_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_Quarto_H * 20));
		} else if ("10x14".equalsIgnoreCase(strPageSize)) {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_10x14_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_10x14_H * 20));
		} else {
			pageSize.setW(BigInteger.valueOf(Constants.PAGE_A4_W * 20));
			pageSize.setH(BigInteger.valueOf(Constants.PAGE_A4_H * 20));
		}

		setPageMargin(ctSectPr);
		System.out.println("Page Setting completed");
	}

	private static void createMetaData(XWPFDocument document) {
		CoreProperties props = document.getProperties().getCoreProperties();
		// props.setCreated("2019-08-14T21:00:00z");
		props.setLastModifiedByUser(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CREATOR));
		props.setCreator(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CREATOR));
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
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_TITLE_FONT));
		run.setFontSize(getFontSize(Constants.STR_TITLE_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_TITLE));
		run.addBreak();
		run.addBreak();
		run.addBreak();

		// sub title
		paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run = paragraph.createRun();
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_SUB_TITLE_FONT));
		run.setFontSize(getFontSize(Constants.STR_SUB_TITLE_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_SUB_TITLE));
		run.addBreak();
		run.addBreak();
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
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_AUTHOR_FONT));
		run.setFontSize(getFontSize(Constants.STR_AUTHOR_FONT_SIZE));
		run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_AUTHOR));

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
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT));
		run.setFontSize(getFontSize(Constants.STR_CONTENT_FONT_SIZE) + 2);
		run.setText(
				"If you are using this PDF in mobile, Navigation by Index may not work with Google Drive's PDF viewer. We would recommend ReadEra App (https://play.google.com/store/apps/details?id=org.readera) in Android or other similar Apps in iPhone for better performance and navigation experience.");
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
		run.setFontFamily(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_HEADER_FONT));
		run.setFontSize(getFontSize(Constants.STR_HEADER_FONT_SIZE));
		String temp = BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_INDEX_TITLE);
		if (temp == null || temp.isBlank()) {
			run.setText("Index");
		} else {
			run.setText(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_INDEX_TITLE));
		}

		// Set background color
		// CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
		// cTShd.setVal(STShd.CLEAR);
		// cTShd.setFill("ABABAB");

		CTBookmark bookmark = paragraph.getCTP().addNewBookmarkStart();
		bookmark.setName(BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_INDEX_TITLE));
		bookmark.setId(BigInteger.valueOf(uniqueBookMarkCounter));
		paragraph.getCTP().addNewBookmarkEnd().setId(BigInteger.valueOf(uniqueBookMarkCounter));
		uniqueBookMarkCounter++;

		// Words Index
		paragraph = document.createParagraph();
		paragraph.setSpacingAfter(0);
		int chapterNo = 1;
		for (File file : directory.listFiles()) {
			if (BibleBooksIntroductionCreator.INFORMATION_FILE_NAME.equalsIgnoreCase(file.getName())) {
				continue;
			}
			// String word = file.getName().substring(0, file.getName().lastIndexOf("."));
			String word = getIndexWord(file);
			if (word == null || word.isBlank()) {
				System.out.println(
						"First line of the file cannot be blank, it is being used as index titles in the Index Page.");
				BibleBooksIntroductionCreator.printHelpMessage();
				return;
			}
			if ("yes".equalsIgnoreCase(
					BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_GENERATE_CHAPTER_NO))) {
				word = chapterNo++ + ". " + word;
			}
			createAnchorLink(paragraph, word, word.replace(" ", "_").replace(".", "_"), true, "",
					BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_CONTENT_FONT),
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
				System.out.println("Error processing the line: " + line);
				System.out.println("This line found in the file: " + file.getName());
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
			setPageMargin(ctSectPr);
		}
		return ctSectPr;
	}

	private static void setPageMargin(CTSectPr ctSectPr) {
		CTPageMar pageMar = ctSectPr.getPgMar();
		if (pageMar == null) {
			pageMar = ctSectPr.addNewPgMar();
		}

		pageMar.setLeft(getMargin(Constants.STR_MARGIN_LEFT));
		pageMar.setRight(getMargin(Constants.STR_MARGIN_RIGHT));
		pageMar.setTop(getMargin(Constants.STR_MARGIN_TOP));
		pageMar.setBottom(getMargin(Constants.STR_MARGIN_BOTTOM));
		// pageMar.setFooter(BigInteger.valueOf(720));
		// pageMar.setHeader(BigInteger.valueOf(720));
		// pageMar.setGutter(BigInteger.valueOf(0));
	}

	private static BigInteger getMargin(String key) {
		String temp = BibleBooksIntroductionCreator.BOOK_DETAILS.getProperty(Constants.STR_MARGIN_TOP);
		if (temp != null && !temp.isBlank()) {
			try {
				Double margin = Double.parseDouble(temp);
				// 720 TWentieths of an Inch Point (Twips) = 720/20 = 36 pt; 36/72 = 0.5"
				margin = margin * 72 * 20;
				return BigInteger.valueOf(margin.intValue());
			} catch (NumberFormatException e) {
				System.out.println("Using default Margin since it is NOT set for " + key + " in the "
						+ BibleBooksIntroductionCreator.INFORMATION_FILE_NAME + " file");
			}
		}
		return BigInteger.valueOf(648);// 0.45"*72*20
	}
}