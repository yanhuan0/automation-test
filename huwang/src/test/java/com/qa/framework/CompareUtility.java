package com.qa.framework;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.imageio.ImageIO;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

import com.qa.framework.imagetool.ImageCompareFinal;


public class CompareUtility extends ParameterConstants{
	private Test m_test;
	
	public CompareUtility(Test test){
		m_test=test;
	}
    
    private static StringReader filterNonAnsiFile(FileReader fileReader)throws IOException{ //added by xbai on 08/27/2010
        int c;
        StringBuffer buf=new StringBuffer();
        while((c=fileReader.read())>=0){
            if(c<128 && c>0)
                buf.append((char)c);
        }
        return new StringReader(buf.toString());
    }
    
    /**
     * Compare text-format file with its base file. A diff file will be generated if any differences found.
     *     
     * @param fileName     File name.   
     * @throws Exception
     * 
     */
    public void compareTextFiles (String fileName)throws Exception, IOException {    
        String baseFilePath, resultFilePath;
            
        if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
            return;
        
        baseFilePath = m_test.getBaseDir() + "/" + fileName;
        resultFilePath = m_test.getResultDir() + "/" + fileName;    
        
        compareTextFiles(baseFilePath, resultFilePath);  
    }  
    
    
    /**
     * Compare text-format file with its base file. A diff file will be generated if any differences found.
     *     
     * @param fileName     File name.   
     * @throws Exception
     * 
     */
    public void compareTextFiles (String baseFilePath, String resultFilePath)throws Exception, IOException {
    	m_test.getLogger().debugMethod(baseFilePath, resultFilePath);
        String diffName, diffFilePath;
        String baseLine, resultLine, result = null;
        boolean hasDifference;       
        File baseFile, resultFile, diffFile;
        BufferedWriter writer = null;

        hasDifference = false;      
        
        if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
            return;     
        
        baseFile = new File(baseFilePath);
        resultFile = new File(resultFilePath);          
       
        int index = baseFile.getName().lastIndexOf('.') ;
        diffName = resultFile.getName().substring(0, index);        
        diffFilePath = m_test.getResultDir() + "/" + diffName + ".diff";
        diffFile = new File(diffFilePath);
        
//        m_logger.logMethod(this.getClass().getName(), "compareTextFiles", new String[]{baseFilePath, resultFilePath});         
        
        //Validate files
        if (!baseFile.exists()) {
            m_test.getLogger().error("compareTextFile() - Can't find base file: " + baseFilePath);
            return;
        }
        
        if (!resultFile.exists()) {
        	m_test.getLogger().error("compareTextFile() - Can't find result file: " + resultFilePath);
            return;
        }            
        
        //Delete existing diff file
        if (diffFile.exists())
            diffFile.delete();
            
        //Read file     
        BufferedReader baseBuffReader = new BufferedReader(filterNonAnsiFile(new FileReader(baseFile)));   //updated by xbai on 08/27/2010     
        BufferedReader resultBuffReader = new BufferedReader(filterNonAnsiFile(new FileReader(resultFile)));   //updated by xbai on 08/27/2010
        
        //Read line    
        while (baseBuffReader.ready() && resultBuffReader.ready()) {            
            baseLine = baseBuffReader.readLine();            
            resultLine = resultBuffReader.readLine();            
            if(baseLine==null || resultLine==null)
                break; //updated by xbai on 08/27/2010
            //Write to diff file
            if (baseLine.compareTo(resultLine) != 0) {                
                if (!diffFile.exists()) {
                    hasDifference = true;
                    diffFile = new File(diffFilePath); 
                    writer = new BufferedWriter(new FileWriter(diffFile)); 
                    writer.write("Text File Comparison" + LINE_SEPARATOR); 
                    writer.write("===============================================================================================================" + LINE_SEPARATOR); 
                    result = "The result file " + resultFilePath + " and base file " + baseFilePath + " are different. The diff is generated: " + diffFilePath + LINE_SEPARATOR;
                }
                writer.write("result File : " + resultLine + LINE_SEPARATOR); 
                writer.write("base File   : " + baseLine + LINE_SEPARATOR);
                writer.write("-----------------------------------------------------------" + LINE_SEPARATOR);
            }                    
        }
        baseBuffReader.close();
        resultBuffReader.close();
        if (hasDifference){
            writer.close();
            m_test.getLogger().error(result);
        }
        else
            m_test.getLogger().info("The result file " + resultFilePath + " and base file " + baseFilePath + " are identical");           
    
    }     
    


    /**
     * Compare two PDF files    
     * @param pdfFile1   Base PDF file full path
     * @param pdfFile2   Result PDF file full path      
     */    
    public boolean comparePDFFiles(String pdfFile1, String pdfFile2) throws Exception {
        return comparePDFFiles(pdfFile1, pdfFile2, IMG_MATCH_THRESHOLD);
    }
    public boolean comparePDFFiles(String pdfFile1, String pdfFile2, double threshold) throws Exception {  
    	m_test.getLogger().debugMethod(pdfFile1, pdfFile2, threshold);
//        m_logger.logMethod(this.getClass().getName(), "comparePDFFiles", new String[]{pdfFile1, pdfFile2});          
        
        File baseFile, resultFile;        
        
        String file1 = pdfFile1;
        String file2 = pdfFile2;
        
        PDFParser parser1 = null;
        PDFParser parser2 = null;
        PDDocument pdDoc1 = null;
        PDDocument pdDoc2 = null;
        
        
        //Validate files
        baseFile = new File(file1);
        resultFile = new File(file2);      
        
        //Image file name for comparison result. Excludes file extension
        String resultImageFileName = "";     
        int index = resultFile.getName().lastIndexOf('.') ;
        resultImageFileName = resultFile.getName().substring(0, index);          
        
        if (!baseFile.exists()) {
            m_test.getLogger().error("comparePDFFiles() - Can't find base file: " + file1);
            return false;
        }         
        if (!resultFile.exists()) {
            m_test.getLogger().error("comparePDFFiles() - Can't find result file: " + file2);
            return false;
        }        
        
        try{        
        parser1 = new PDFParser(new FileInputStream(file1) );
        parser2 = new PDFParser(new FileInputStream(file2) );

        parser1.parse ();
        parser2.parse ();

        pdDoc1 = parser1.getPDDocument();
        pdDoc2 = parser2.getPDDocument();

        //Extract text 
        PDFTextStripper pdfStripper = new PDFTextStripper ();
        String doc1 = pdfStripper.getText(pdDoc1);
        String doc2 = pdfStripper.getText(pdDoc2);
        
        //Compare text    
        //System.out.println("Text Comparision       : "+ doc1.equalsIgnoreCase(doc2));   
        m_test.getLogger().sideBySide(doc1, doc2);
        if (!doc1.equalsIgnoreCase(doc2)){            
            m_test.getLogger().info("Base text:");
            m_test.getLogger().info(doc1);
            m_test.getLogger().info("Result text:");
            m_test.getLogger().info(doc2);
            m_test.getLogger().error("Text comparison: FALSE");
        }
        else
            m_test.getLogger().info("Text are indentical");            
            
        //Compare image
        BufferedImage[] pageImages1 = null;
        BufferedImage[] pageImages2 = null;
        
        pageImages1 = getPagesAsImages(file1);
        pageImages2 = getPagesAsImages(file2);        
        //Check image page length
        //System.out.println("Page Count Comparision : "+ (pageImages1.length == pageImages2.length) );
        if (pageImages1.length == pageImages2.length)
            m_test.getLogger().info("Page count are same");
        else {
            m_test.getLogger().info("Base page count:" + pageImages1.length);
            m_test.getLogger().info("Result page count:" + pageImages2.length);
            m_test.getLogger().error("Page count are not same"); 
        }
        
        //Compare image object
        for(int i=0; i< pageImages1.length; i++) {
            m_test.getLogger().sideBySide(pageImages1[i], pageImages2[i]);
            if(!checkIdentical(pageImages1[i], pageImages2[i], resultImageFileName, threshold)) {
                //System.out.println("Page Image Comparision : Failed - "+ i);
                //Close
                pageImages1 = null;
                pageImages2 = null;
                 
                parser1 = null;
                parser2 = null;
                 
                pdDoc1.close();
                pdDoc2.close();
                m_test.getLogger().error("Page images are not identical"); 
                return false;
            }
        }
        //System.out.println("Page Image Comparision : true");        
        m_test.getLogger().info("Page images are identical"); 
        
        //Close
        pageImages1 = null;
        pageImages2 = null;
        
        }
        catch(Exception e){
        	e.printStackTrace();
            m_test.getLogger().error("Exception in compare PDF files. " + "Exception: " + e.getMessage());           
            return false;
        }  
        finally{            
            pdDoc1.close();
            pdDoc2.close();
            parser1 = null;
            parser2 = null;            
        }
        return true;
    }      
   
	/**
	 * Compare two Briefing book in PDF files with system time. For example, charttest.pdf
	 * 
	 * @param pdfFile
	 *            PDF file name
	 * 
	 */
	public boolean comparePDFFilesWithTime(String pdfFile) throws Exception {
		if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
			return false;

		String file1 = m_test.getBaseDir() + "/" + pdfFile;
		String file2 = m_test.getResultDir() + "/" + pdfFile;

		return comparePDFFilesWithTime(file1, file2);
	}

	/**
	 * Compare two PDF files with system time
	 * 
	 * @param pdfFile1
	 *            Base PDF file full path
	 * @param pdfFile2
	 *            Result PDF file full path
	 */
	public boolean comparePDFFilesWithTime(String pdfFile1, String pdfFile2)
			throws Exception {
		m_test.getLogger().debugMethod(pdfFile1, pdfFile2);

		File baseFile, resultFile;

		String file1 = pdfFile1;
		String file2 = pdfFile2;

		PDFParser parser1 = null;
		PDFParser parser2 = null;
		PDDocument pdDoc1 = null;
		PDDocument pdDoc2 = null;

		// Validate files
		baseFile = new File(file1);
		resultFile = new File(file2);

		// Image file name for comparison result. Excludes file extension
		String resultImageFileName = "";
		int index = resultFile.getName().lastIndexOf('.');
		resultImageFileName = resultFile.getName().substring(0, index);

		if (!baseFile.exists()) {
			m_test.getLogger().error("comparePDFFiles() - Can't find base file: " + file1);
			return false;
		}
		if (!resultFile.exists()) {
			m_test.getLogger().error("comparePDFFiles() - Can't find result file: " + file2);
			return false;
		}

		try {
			parser1 = new PDFParser(new FileInputStream(file1));
			parser2 = new PDFParser(new FileInputStream(file2));

			parser1.parse();
			parser2.parse();

			pdDoc1 = parser1.getPDDocument();
			pdDoc2 = parser2.getPDDocument();

			// Extract text
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String doc1 = pdfStripper.getText(pdDoc1);
			String doc2 = pdfStripper.getText(pdDoc2);

			String tempDoc1 = "";
			while (doc1.indexOf("Time run:") > 0) {
				if (doc1.indexOf(" PM") > 0) {
					tempDoc1 = doc1.substring(doc1.indexOf("Time run:"),
							doc1.indexOf(" PM"))
							+ " PM";
				} else if (doc1.indexOf(" AM") > 0) {
					tempDoc1 = doc1.substring(doc1.indexOf("Time run:"),
							doc1.indexOf(" AM"))
							+ " AM";
				}
				doc1 = doc1.replaceAll(tempDoc1, "");
			}

			String tempDoc2 = "";
			while (doc2.indexOf("Time run:") > 0) {
				if (doc2.indexOf(" PM") > 0) {
					tempDoc2 = doc2.substring(doc2.indexOf("Time run:"),
							doc2.indexOf(" PM"))
							+ " PM";
				} else if (doc2.indexOf(" AM") > 0) {
					tempDoc2 = doc2.substring(doc2.indexOf("Time run:"),
							doc2.indexOf(" AM"))
							+ " AM";
				}
				doc2 = doc2.replaceAll(tempDoc2, "");
			}

			// Compare text
			// System.out.println("Text Comparision       : "+
			// doc1.equalsIgnoreCase(doc2));
			if (!doc1.equalsIgnoreCase(doc2)) {
				m_test.getLogger().info("Base text:");
				m_test.getLogger().info(doc1);
				m_test.getLogger().info("Result text:");
				m_test.getLogger().info(doc2);
				m_test.getLogger().error("Text comparison: FALSE");
			} else
				m_test.getLogger().info("Text are indentical");

			// Compare image
			BufferedImage[] pageImages1 = null;
			BufferedImage[] pageImages2 = null;

			pageImages1 = getPagesAsImages(file1);
			pageImages2 = getPagesAsImages(file2);
			// Check image page length
			// System.out.println("Page Count Comparision : "+
			// (pageImages1.length == pageImages2.length) );
			if (pageImages1.length == pageImages2.length)
				m_test.getLogger().info("Page count are same");
			else {
				m_test.getLogger().info("Base page count:" + pageImages1.length);
				m_test.getLogger().info("Result page count:" + pageImages2.length);
				m_test.getLogger().error("Page count are not same");
			}

			// Compare image object
			for (int i = 0; i < pageImages1.length; i++) {
				if (!checkIdentical(pageImages1[i], pageImages2[i],
						resultImageFileName)) {
					// System.out.println("Page Image Comparision : Failed - "+
					// i);
					// Close
					pageImages1 = null;
					pageImages2 = null;

					parser1 = null;
					parser2 = null;

					pdDoc1.close();
					pdDoc2.close();
					m_test.getLogger().error("Page images are not identical");
					return false;
				}
			}
			// System.out.println("Page Image Comparision : true");
			m_test.getLogger().info("Page images are identical");

			// Close
			pageImages1 = null;
			pageImages2 = null;

		} catch (Exception e) {
			m_test.getLogger().error("Exception in compare PDF files. " + "Exception: "
					+ e.getMessage());
			return false;
		} finally {
			pdDoc1.close();
			pdDoc2.close();
			parser1 = null;
			parser2 = null;
		}
		return true;
	}

 
    /**
     * Compare two Briefing book in PDF files. For example, charttest.pdf    
     * @param pdfFile  PDF file name   
     * 
     */    
    public boolean comparePDFFiles(String pdfFile) throws Exception {
        return comparePDFFiles(pdfFile, IMG_MATCH_THRESHOLD);
    }
    public boolean comparePDFFiles(String pdfFile, double threshold) throws Exception {   
        if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
            return false;
        
        String file1 = m_test.getBaseDir() + "/" + pdfFile;
        String file2 = m_test.getResultDir() + "/" + pdfFile;        
      
        return comparePDFFiles(file1, file2, threshold);   
    }   
    
    
    

    /**
     * Compare two Briefing book in PDF files. For Briefing book, the first page will be ignore since it has time stemp
     *
     * @param pdfFile1   Base PDF file full path
     * @param pdfFile2   Result PDF file full path     
     * 
     */    
    public boolean compareBriefingBookInPDFFiles(String pdfFile1, String pdfFile2) throws Exception {    
        m_test.getLogger().debugMethod(pdfFile1, pdfFile2);
        File baseFile, resultFile;        
        
        String file1 = pdfFile1;
        String file2 = pdfFile2;
        
        PDFParser parser1 = null;
        PDFParser parser2 = null;     
        PDDocument pdDoc1 = null;
        PDDocument pdDoc2 = null;
        
        //Validate files
        baseFile = new File(file1);
        resultFile = new File(file2);      
        
        //Image file name for comparison result. Excludes file extension
        String resultImageFileName = "";     
        int index = resultFile.getName().lastIndexOf('.') ;
        resultImageFileName = resultFile.getName().substring(0, index);        
        
        if (!baseFile.exists()) {
            m_test.getLogger().error("compareBriefingBookPDFFiles() - Can't find base file: " + file1);
            return false;
        }         
        if (!resultFile.exists()) {
            m_test.getLogger().error("compareBriefingBookPDFFiles() - Can't find result file: " + file2);
            return false;
        }        
        
        try{
            parser1 = new PDFParser(new FileInputStream(file1) );
            parser2 = new PDFParser(new FileInputStream(file2) );
            parser1.parse ();
            parser2.parse ();    

            pdDoc1 = parser1.getPDDocument();
            pdDoc2 = parser2.getPDDocument();
            
            //Compare image
            BufferedImage[] pageImages1 = null;
            BufferedImage[] pageImages2 = null;
            pageImages1 = getPagesAsImages(file1);
            pageImages2 = getPagesAsImages(file2);        
            //Check image page length
            //System.out.println("Page Count Comparision : "+ (pageImages1.length == pageImages2.length) );
            if (pageImages1.length == pageImages2.length)
                m_test.getLogger().info("Page count are same");
            else {
                m_test.getLogger().info("Base page count:" + pageImages1.length);
                m_test.getLogger().info("Result page count:" + pageImages2.length);
                m_test.getLogger().error("Page count are not same"); 
            }
            //Compare image object
            for(int i=1; i< pageImages1.length; i++) {
                if(!checkIdentical(pageImages1[i], pageImages2[i], resultImageFileName)) {
                    //System.out.println("Page Image Comparision : Failed - "+ i);
                    //Close
                    pageImages1 = null;
                    pageImages2 = null;
                    parser1 = null;
                    parser2 = null;                 
                    m_test.getLogger().error("Page images are not identical"); 
                    return false;
                }
            }
            //System.out.println("Page Image Comparision : true");        
            m_test.getLogger().info("Page images are identical"); 
        }
        catch(Exception e){
            m_test.getLogger().error("Exception in compareBriefingBookPDFFiles. " + "Exception: " + e.getMessage());                  
            return false;
        }   
        finally{                
            parser1 = null;
            parser2 = null;            
            pdDoc1.close();
            pdDoc2.close();
        }
        return true;
    }      
    
    
    
    /**
     * Compare a page from result PDF file with its base file. For Briefing book, the first page will be ignore since it has time stemp
     *
     * @param pdfFile   PDF file name. For example, charttest.pdf
     * 
     */   
    public boolean compareBriefingBookInPDFFiles(String pdfFile) throws Exception {            
        if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
            return false;
        
        String file1 = m_test.getBaseDir() + "/" + pdfFile;
        String file2 = m_test.getResultDir() + "/" + pdfFile;
        
        return compareBriefingBookInPDFFiles(file1, file2);
    }      
    
    
    /**
     * Compare text only from two PDF files 
     * @param pdfFile1   Base PDF file full path
     * @param pdfFile2   Result PDF file full path   
     * 
     */    
    public boolean compareTextInPDFFiles(String pdfFile1, String pdfFile2) throws Exception {    
        m_test.getLogger().debugMethod(pdfFile1, pdfFile2);
        File baseFile, resultFile;        
        
        PDFParser parser1 = null;
        PDFParser parser2 = null;
        PDDocument pdDoc1 = null;
        PDDocument pdDoc2 = null;
        
        String file1 = pdfFile1;
        String file2 = pdfFile2;
        
        //Validate files
        baseFile = new File(file1);
        resultFile = new File(file2);                
        
        if (!baseFile.exists()) {
            m_test.getLogger().error("comparePDFFiles() - Can't find base file: " + file1);
            return false;
        }         
        if (!resultFile.exists()) {
            m_test.getLogger().error("comparePDFFiles() - Can't find result file: " + file2);
            return false;
        }        
        
        try{
        
        parser1 = new PDFParser(new FileInputStream(file1) );
        parser2 = new PDFParser(new FileInputStream(file2) );

        parser1.parse ();
        parser2.parse ();

        pdDoc1 = parser1.getPDDocument();
        pdDoc2 = parser2.getPDDocument();

        //Extract text 
        PDFTextStripper pdfStripper = new PDFTextStripper ();
        String doc1 = pdfStripper.getText(pdDoc1);
        String doc2 = pdfStripper.getText(pdDoc2);
        
        //Compare text    
        //System.out.println("Text Comparision       : "+ doc1.equalsIgnoreCase(doc2));                
        if (!doc1.equalsIgnoreCase(doc2)){            
            m_test.getLogger().info("Base text:");
            m_test.getLogger().info(doc1);
            m_test.getLogger().info("Result text:");
            m_test.getLogger().info(doc2);
            m_test.getLogger().error("Text comparison: FALSE");
        }
        else
            m_test.getLogger().info("Text are indentical");    
        }
        catch(Exception e){
            m_test.getLogger().error("Exception in compare text in PDF files. " + "Exception: " + e.getMessage());                         
            return false;
        }      
        finally{            
            pdDoc1.close();
            pdDoc2.close();
            parser1 = null;
            parser2 = null;            
        }
        return true;
    }      
    
    
    
    
    
    /**
     * Compare text only from two PDF files 
     * @param pdfFile   Base PDF file name. For example, charttest.pdf
    * 
     */
    public boolean compareTextInPDFFiles(String pdfFile) throws Exception {    
        if (System.getProperty("qa.result.mode").equalsIgnoreCase("false"))
            return false;
        
        String baseFile = m_test.getBaseDir() + "/" + pdfFile;
        String resultFile = m_test.getResultDir() + "/" + pdfFile;
        
        return compareTextInPDFFiles(baseFile, resultFile);      
    }              


    
    private BufferedImage[] getPagesAsImages(String pdfFile) throws Exception {
        PDDocument document = null;
        BufferedImage pageImages[] = null;        
        
        document = PDDocument.load(pdfFile);
        
        java.util.List pages = document.getDocumentCatalog().getAllPages();
        pageImages = new BufferedImage[pages.size ()];
        
        for( int i=0; i<pages.size(); i++ ) {
            org.pdfbox.pdmodel.PDPage page = (org.pdfbox.pdmodel.PDPage)pages.get( i );
            pageImages[i] = page.convertToImage();           
        }
        document.close ();
	return pageImages;
    }    
    
    
    private BufferedImage[] getPagesAsImages(String pdfFile, int pageIndex) throws Exception {
        PDDocument document = null;
        BufferedImage pageImages[] = null;              
        
        document = PDDocument.load(pdfFile);
        
        java.util.List pages = document.getDocumentCatalog().getAllPages();
        pageImages = new BufferedImage[pages.size ()];
        
        for( int i=0; i<pages.size(); i++ ) {
            if (i ==  pageIndex){
                org.pdfbox.pdmodel.PDPage page = (org.pdfbox.pdmodel.PDPage)pages.get( i );
                pageImages[i] = page.convertToImage();           
            }
        }
        document.close ();
        return pageImages;
    }      
    
    
    //Comment by vzhou
    private boolean checkIdentical(BufferedImage imgA, BufferedImage imgB)throws Exception  {
    	m_test.getLogger().debugMethod("<imgA>", "<imgB>");
        boolean identical = true;
        
//        m_logger.logMethod(this.getClass().getName(), "checkIdentical"); 

        if(imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()){
            int w = imgA.getWidth();
            int h = imgA.getHeight();

            for(int i=0; i<h; i++){
                for(int j=0; j<w; j++){
                    if(imgA.getRGB(j,i) != imgB.getRGB(j,i)){
                        return false;
                    }
                }
                if( !identical ){
                    break;
                }
            }
        }
        else {
            return false;
        }
        return identical;
    }
    
    
    private boolean checkIdentical(BufferedImage imgA, BufferedImage imgB, String imageFile) throws Exception {
	return checkIdentical(imgA, imgB, imageFile, IMG_MATCH_THRESHOLD);
    }
    //VZHOU: Calling imagetool to do comparison with option
    private boolean checkIdentical(BufferedImage imgA, BufferedImage imgB, String imageFile, double threshold) throws Exception {
//		return false;    
    	m_test.getLogger().debugMethod("<imgA>", "<imgB>", imageFile, threshold);
        String currentTestID = m_test.getName();//BaseBISeleniumTest.currentTestId; 
        if(currentTestID==null)
            currentTestID="currentTestID";
        String pngDir = "";   
        
        double matchThreshold = 0;       
        
        //Check flag for using old or new image tool
        //if (getSystemProperty("ENABLE_IMG_TOOL").equalsIgnoreCase("false"))
//        if (ENABLE_IMG_TOOL == false)
//            return checkIdentical (imgA, imgB);       
            
//        m_logger.logMethod(this.getClass().getName(), "checkIdentical - Image tool"); 
                                   
        try {            
            pngDir = m_test.getResultDir() + "/" + currentTestID;
            String bigDir = m_test.getResultDir() + "/" + currentTestID + "/bigcomponents";
            String smallDir = m_test.getResultDir() + "/" + currentTestID + "/smallcomponents";         
            
            File dir=new File(pngDir);
            if(!dir.exists())
                dir.mkdir();
                
            File dir1=new File(bigDir);
            if(!dir1.exists())
                dir1.mkdir();
                
            File dir2=new File(smallDir);
            if(!dir2.exists())
                dir2.mkdir();
            
            ImageCompareFinal imgcmp = new ImageCompareFinal(imgA, imgB, pngDir, imageFile, false, false, true);
            //Get percent from setup.properties file
            matchThreshold = threshold * 100;                       
            m_test.getLogger().info("Expected image match threshold value is: " + matchThreshold + "%");    
        
            double percent = imgcmp.compareImages();
            m_test.getLogger().info("Actual image match threshold value is: " + percent + "%"); 
            
            //if (percent > 95){
            if (percent > matchThreshold){
                return true;
            } else {     
                m_test.getLogger().error("Actual image match threshold value " + percent + "%" + " which is less than expected value " + matchThreshold + "%"); 
                return false;
            }
        } 
        catch(Exception e){
            //e.printStackTrace();
            m_test.getLogger().error("Exception in checkIdentical - Image tool. " + "Exception: " + e.getMessage());
            return false;
        }   
        
    }
    
    
    /**
     * Convert PDF file to PNG file
     *
     * @param pdfFile   PDF file full path   
     * 
     */        
    public void convertPDFToImages(String pdfFile) throws Exception {
        PDDocument document = null;
        BufferedImage pageImages[] = null;              
        
        document = PDDocument.load(pdfFile);
        
        String pdfFilePath_NoExt = pdfFile.substring(0, (pdfFile.lastIndexOf(".")));   
        
        java.util.List pages = document.getDocumentCatalog().getAllPages();
        pageImages = new BufferedImage[pages.size ()];
        
        for( int i=0; i<pages.size(); i++ ) {
            org.pdfbox.pdmodel.PDPage page = (org.pdfbox.pdmodel.PDPage)pages.get( i );
            pageImages[i] = page.convertToImage();               
            write(pageImages[i], pdfFilePath_NoExt + "_Page_" + i);            
        }
        document.close ();        
    }
    
    /**
     * Convert a specified page in PDF file to PNG file
     *
     * @param pdfFile     PDF file full path   
     * @param pageIndex   A specified page index in PDF file
     */    
    public void convertPDFToImages(String pdfFile, int pageIndex) throws Exception {
        PDDocument document = null;
        BufferedImage pageImages[] = null;              
        
        document = PDDocument.load(pdfFile);
        
        String pdfFilePath_NoExt = pdfFile.substring(0, (pdfFile.lastIndexOf(".")));   
        
        java.util.List pages = document.getDocumentCatalog().getAllPages();
        pageImages = new BufferedImage[pages.size ()];
        
        org.pdfbox.pdmodel.PDPage page = (org.pdfbox.pdmodel.PDPage)pages.get(pageIndex);
        pageImages[pageIndex] = page.convertToImage();    
        write(pageImages[pageIndex], pdfFilePath_NoExt + "_Page_" + pageIndex);         
        document.close ();        
    }
    
    //VZHOU
    private BufferedImage read(String filename) throws IOException {
        InputStream istream = new FileInputStream(filename);
        BufferedImage img = ImageIO.read(istream);
        return img;
    }

    //VZHOU
    private void write(BufferedImage img, String filePath_NoExt) throws IOException {            
        ImageIO.write(img, "png", new File(filePath_NoExt + ".png"));        
    }   

    /**
     * Compare text file with static lines information, all exists in a compare to file 
     *     
     * @param staticFilePath:    a text file contains lines of static information    
     * @param compareToFilePath: a file may contains dynamic content, that user want to search for
     *                           whether alls lines of information in the static file exist in 
     *                           this compare file in the order listed in static file. 
     * @throws Exception
     * 
     */
    public boolean compareStaticContentInTextFiles (String compareToFilePath, String staticFilePath)throws Exception, IOException {
//        m_logger.logMethod(this.getClass().getName(), "compareStaticContentInTextFiles", new String[]{compareToFilePath, staticFilePath});           
        m_test.getLogger().debugMethod(compareToFilePath, staticFilePath);
        String diffName, diffFilePath;
        String result;
        boolean hasDifference = true;       
        File compareToFile, staticFile, diffFile;
        BufferedWriter writer = null;  
        
        compareToFile = new File(compareToFilePath);          
        staticFile = new File(staticFilePath);        
        
        //Read files             
        FileReader fileReader_c = new FileReader(compareToFile); //compare to file
        FileReader fileReader_s = new FileReader(staticFile); //static file
        
        BufferedReader bufRead_c = new BufferedReader(filterNonAnsiFile(fileReader_c));     
        BufferedReader bufRead_s = new BufferedReader(filterNonAnsiFile(fileReader_s));          
        
        String line_c, line_s;       
                   
        compareToFile = new File(compareToFilePath);          
        
        int index = compareToFile.getName().lastIndexOf('.') ;
        diffName = compareToFile.getName().substring(0, index);        
        diffFilePath = m_test.getResultDir() + "/" + diffName + ".diff";
        diffFile = new File(diffFilePath);
        
        line_s=bufRead_s.readLine(); 
        line_c=bufRead_c.readLine();
        
        //Read first line
        while(bufRead_s.ready() && line_s !=null ){       
            hasDifference = true;
            
            while(bufRead_c.ready() && line_c !=null){                
                if(line_c.contains(line_s)){
                   hasDifference = false; 
                   break; 
                }
                
                line_c=bufRead_c.readLine();
            }
            
            line_s=bufRead_s.readLine();
        }       
        
        bufRead_c.close();
        bufRead_s.close();    
        
        if(hasDifference){
            diffFile = new File(diffFilePath); 
            writer = new BufferedWriter(new FileWriter(diffFile)); 
            writer.write("Export File Comparison" + LINE_SEPARATOR); 
            writer.write("===============================================================================================================" + LINE_SEPARATOR); 
            result = "export file is different from baseline.  The diff is generated: " + diffFilePath + LINE_SEPARATOR;            
            
            writer.close();
            m_test.getLogger().error(result);
        }    
        
        return hasDifference;
    }      
   public static void main(String[] args)throws Exception{
   }
}
