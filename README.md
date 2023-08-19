# bible-books-introduction-creator
Creates MS Word document with title pages, index pages and content pages from text files from a directory

## Help on Usage of this prorgam:
- You can check the sample input files in the directory "sample-directory"

- Include INFORMATION.txt inside the folder....
- INFORMATION.txt can contain values this way:

    subject=Bible Books Introduction
    publisher=Publisher Name
    title=Bible Books Introduction
    subTitle=Introduction to all 66 books in the bible
    author=Author Name
    creator=Your Naame
    descriptionTitle=Additional Details of this book:
    description=Some description goes here
    identifier=Some Unique Name without space
    language=en\n"
    createZefaniaXML=no\t(it can take yes or no)
    createWordDocument=yes\t(it can take yes or no)
    titleFont=Uni Ila.Sundaram-08
    titleFontSize=36
    subTitleFont=Uni Ila.Sundaram-04
    subTitleFontSize=16
    authorFont=Uni Ila.Sundaram-08
    authorFontSize=22
    headerFont=Uni Ila.Sundaram-08
    headerFontSize=16
    contentFont=Uni Ila.Sundaram-04
    contentFontSize=12
    indexPageTitle=Index

- Please use one file per book introduction. Directory should have the numbering prefixed.
- First line of the file will be used to create the indexes in the Index Page
- Include [H1] or [H2] or [H3] in the beginning of the line to highlight heading, sub headings
  Exmple 1: 01-Genesis.txt, 02-Exodus.txt, etc
  Exmple 2: 1-Genesis.txt, 2-Exodus.txt, etc

### Syntax to run this progam:
    java -jar apply-regex-to-files.jar sourceDir=<Source Folder Name or Path> regexFile=<RegEx file name or file path>

Example 1: 

    java -jar apply-regex-to-files.jar sourceDir=directory1 regexFile=regex.txt

Example 2:

    java -jar apply-regex-to-files.jar sourceDir="C:/somedirectory/directory1" regexFile="C:/somedirectory/regex-config.ini"