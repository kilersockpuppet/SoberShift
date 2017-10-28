package org.philambdaphi;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.trolltech.qt.gui.*;

public class SoberForm extends QWidget 
{
	public static final String version = "a1.1.0";
	public static final String organization = "Truman State IFC";
	public static final String windowTitle = "Blaine Harper's SoberShift (" + version +")";	
	public static boolean firstRun = false, closeFirstRun = false;
	
	public static int screenWidth,screenHeight;
	
    private QLineEdit formLineEdit, orgOutput;
    
    private QComboBox formComboBox;
    private QRadioButton inRadioButton, outRadioButton;
    private QCheckBox soberCheckBox, minorCheckBox;
    private static QTextEdit attendanceBox;
    
    private static String userName = System.getProperty("user.name");
    private static String rootDir = "C:\\Users\\" + userName + "\\Desktop\\";
    static String fileDir = rootDir + "SoberShift\\";
    
    private static String orgIndex[] = new String[64];
    private static int orgCount = 0;
    
    public static SoberForm soberForm;
    
//    Some strings for filepaths to make this easier
    public static String namesFile = fileDir + "names.txt",
    					attendanceFile = fileDir + "\\bin\\" + Clock.getDate() + ".ssf",
    					orgsFile = fileDir + "\\bin\\organizations.txt",
    					fullLogsFile = fileDir + "fullLogs.txt";
    
    public static void main(String args[]) 
    {
    	setUpDirectory();
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	screenWidth = (int) screenSize.getWidth();
    	screenHeight = (int) screenSize.getHeight();
    	
        setupIndexArray();
        
        QApplication.initialize(args);

        if(firstRun)
        	FirstRun.run();
        
        soberForm = new SoberForm();
        soberForm.show();
        soberForm.setFixedSize((int) (screenWidth / 2.5), screenHeight / 3);
        
        if(firstRun)
        	FirstRun.firstRunWindow.raise();
        
        populateAttendance();

        QApplication.instance().exec();
    }

    public SoberForm() {
        this(null);
    }

    public SoberForm(QWidget parent) {
        super(parent);
        
        QGroupBox orgGroup = new QGroupBox(tr(organization + " Sign-in"));
        QLabel formLabel = new QLabel(tr("Organization: "));
        formComboBox = new QComboBox();
        
        for(int i=0; i < orgCount; i++)
        {
        	formComboBox.addItem(tr(orgIndex[i]));
        }
        soberCheckBox = new QCheckBox(tr("Sober"));
        minorCheckBox = new QCheckBox(tr("Minor"));
        inRadioButton = new QRadioButton(tr("In"));
        outRadioButton = new QRadioButton(tr("Out"));
        QLabel orgLabelName = new QLabel(tr("Name: "));
        QPushButton submit = new QPushButton(tr("Submit")); 
        formLineEdit = new QLineEdit();
        
        QGroupBox outGroup = new QGroupBox(tr("Output:"));
        orgOutput = new QLineEdit();

        QGroupBox resetGroup = new QGroupBox(tr("File:"));
        QPushButton resetButton = new QPushButton(tr("Reset"));
        QPushButton quitButton = new QPushButton(tr("Quit"));

        QGroupBox attendeeGroup = new QGroupBox(tr("Attendance:"));
        attendanceBox = new QTextEdit();
        
        QGridLayout formLayout = new QGridLayout();
        formLayout.addWidget(formLabel, 0, 0);
        formLayout.addWidget(formComboBox, 0, 1);
        formLayout.addWidget(orgLabelName, 2, 0);
        formLayout.addWidget(formLineEdit, 3, 0, 1, 3);
        formLayout.addWidget(soberCheckBox, 4, 1);
        formLayout.addWidget(minorCheckBox, 5, 1);
        formLayout.addWidget(inRadioButton, 4, 0);
        formLayout.addWidget(outRadioButton, 5, 0);
        formLayout.addWidget(submit, 6, 2);
        orgGroup.setLayout(formLayout);
        
        QGridLayout outLayout = new QGridLayout();
        outLayout.addWidget(orgOutput, 0, 0, 1, 2);
        outGroup.setLayout(outLayout);
        orgOutput.setReadOnly(true);
        
        QGridLayout resetLayout = new QGridLayout();
        resetButton.setMaximumWidth(150);
        quitButton.setMaximumWidth(150);
        resetLayout.addWidget(resetButton, 0, 0, 1, 2);
        resetLayout.addWidget(quitButton, 0, 1, 1, 2);
        resetGroup.setLayout(resetLayout);
        
        QGridLayout attendeeLayout = new QGridLayout();
        attendeeGroup.setLayout(attendeeLayout);
        attendeeLayout.addWidget(attendanceBox, 0, 0);
        attendanceBox.setReadOnly(true);

        QGridLayout layout = new QGridLayout();
        layout.addWidget(orgGroup, 0, 0);
        layout.addWidget(outGroup, 1, 0);
        layout.addWidget(attendeeGroup, 0, 1);
        layout.addWidget(resetGroup, 1, 1);
        
        setLayout(layout);

        inRadioButton.setChecked(true);
        
        submit.clicked.connect(this, "submit()");
        resetButton.clicked.connect(this, "reset()");
        quitButton.clicked.connect(this, "quit()");
        
        setWindowTitle(tr(windowTitle));
        setWindowIcon(new QIcon(fileDir+"images/soberShift.png"));
        
        
//        Below this will setup the first run window
//        This should only happen if firstRun variable is true
        if(firstRun)
        {
	        QTextEdit introText;
	        
	        QGroupBox firstRun = new QGroupBox(tr("Your first time with SoberShift"));
	        introText = new QTextEdit();
	        introText.setReadOnly(true);
	        introText.setText("This appears to be your first time running my program! "
	        		+ "I'm glad you've decided to test out SoberShift " + SoberForm.version
	        		+ " and I hope you'll enjoy it very much!");
	        introText.append("");
	        introText.append("The goal of this program is to make taking attendance at "
	        		+ "any IFC party a little easier and give fraternities a better way "
	        		+ "to collect data about who's going to their parties and when.");
	        introText.append("");
	        introText.append("To see the files that are created with SoberShift you can "
	        		+ "check the file that should be created on your desktop folder after "
	        		+ "you close this prompt. ");
	        introText.append("");
	        introText.append("To populate the list of organizations that you want to add "
	        		+ "to the combo wheel all you have to do is open SoberShift -> bin -> "
	        		+ "organizations.txt and fill that with whichever orgs you would like to "
	        		+ "allow at your party. By default the file should contain both N/A (Non-Affiliated) "
	        		+ "and UnK (membership unknown), but you can add more if you wish!");
	        introText.append("");
	        introText.append("I hope you enjoy SoberShift " + SoberForm.version + "!");
	        introText.append("");
	        introText.append("   - Blaine Harper");
	        QPushButton okay = new QPushButton(tr("Okay"));
	        okay.clicked.connect(this, "closeFirstRun()");
	        
	        QGridLayout runLayout = new QGridLayout();
	        runLayout.addWidget(introText, 0, 0);
	        firstRun.setLayout(runLayout);
	
	        QGridLayout firstRunLayout = new QGridLayout();
	        firstRunLayout.addWidget(firstRun, 0, 0);
	        firstRunLayout.addWidget(okay, 1, 0);
	
	        FirstRun.firstRunWindow.setFixedWidth(640);
	        FirstRun.firstRunWindow.setFixedHeight(320);
	        
	        FirstRun.firstRunWindow.setLayout(firstRunLayout);
	        
	        FirstRun.firstRunWindow.setWindowTitle(tr("Your first time using SoberShift?"));
	        FirstRun.firstRunWindow.setWindowIcon(new QIcon(fileDir+"images/soberShift.png"));
	        
	        if(soberCheckBox.isChecked())
	        	orgOutput.setText("Checked!");
	        else
	        	orgOutput.setText("Not checked!");
        }
    }
    
    public void closeFirstRun()
    {
        FirstRun.firstRunWindow.close();
    }
    
    public void submit()
    {	
    	if(nameOnList(formLineEdit.text()))
    		System.out.println("Already in");
    	if(!formLineEdit.text().isEmpty())
    	{
    		writeFile();
    		formLineEdit.setText("");
    		soberCheckBox.setChecked(false);
    		minorCheckBox.setChecked(false);
    	} else {
        	orgOutput.setText("Name line should not be blank!");
    	}
    }
    
    public boolean messageBox(QMessageBox.Icon icon, String title, String text)
    {
    	boolean bool = true;
    	QMessageBox.StandardButtons buttons = new QMessageBox.StandardButtons();
    	buttons.set(QMessageBox.StandardButton.Yes);
    	buttons.set(QMessageBox.StandardButton.No);

		QMessageBox msgBox = new QMessageBox();
		msgBox.setStandardButtons(buttons);
		msgBox.setWindowTitle(title);
		msgBox.setText(text);
		msgBox.setWindowIcon(new QIcon(fileDir+"images/soberShift.png"));
		msgBox.setIcon(icon);
		
		int ret = msgBox.exec();
		
		if(ret==16384)
			bool=true;
		else
			bool=false;
    	return bool;
    }
    
	@SuppressWarnings("static-access")
	public void quit()
    {
    	if(messageBox(QMessageBox.Icon.Critical, "Quit", "Are you sure you would like to quit?"))
    		QApplication.instance().quit();
    }
    
    public void reset()
    {
    	if(messageBox(QMessageBox.Icon.Warning, "Reset Files", "You are about to "
    			+ "reset all your attendance files for today.\n\n "
    			+ "Are you sure you would like to continue?"))
    	{
            try {
    			PrintWriter writer = new PrintWriter(fullLogsFile, "UTF-8");
    			writer.println("");
    			writer.close();
    			
    			writer = new PrintWriter(namesFile, "UTF-8");
    			writer.println("");
    			writer.close();
    			
    			writer = new PrintWriter(attendanceFile, "UTF-8");
    			writer.println("");
    			writer.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		attendanceBox.setText("");
    		formLineEdit.setText("");
    		orgOutput.setText("All files reset to default!");
    	}
    }
    
    public static boolean nameOnList(String name)
    {
    	boolean inList = false;
    	
    	BufferedReader br = null;
		FileReader fr = null;
		
		try {
			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(namesFile);
			br = new BufferedReader(fr);
			String sCurrentLine;
			
			while ((sCurrentLine = br.readLine()) != null) 
			{
				if(StringFunctions.similarity(name, sCurrentLine) >= .8)
					inList = true;
			}
			br.close();

		} catch (IOException e) {

			e.printStackTrace();

		}
		
    	return inList;
    }
    
    public static void setupIndexArray()
    {
    	BufferedReader br = null;
		FileReader fr = null;
		
		try {
			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(orgsFile);
			br = new BufferedReader(fr);
			String sCurrentLine;
			int i=0;
			
			while ((sCurrentLine = br.readLine()) != null) 
			{
				orgIndex[i]=sCurrentLine;	
				i++;
			}
			orgCount = i;
			br.close();

		} catch (IOException e) {

			e.printStackTrace();

		}
    }
    
    public void writeFile()
    {		
    	if(minorCheckBox.isChecked())
    		soberCheckBox.setChecked(true);
    	
    	String in = "IN | ";
    	String minor = "";
    	
    	if(!inRadioButton.isChecked())
    		in = "OUT | ";
    	
    	if(minorCheckBox.isChecked())
    		minor = " X";
    	
    	try
    	{
            //Specify the file name and path here
        	File file = new File(fullLogsFile);

        	/* This logic is to create the file if the
        	 * file is not already present
        	 */
        	if(!file.exists())
        	{
				file.createNewFile();
        	}

        	//Here true is to append the content to file
        	FileWriter fw = new FileWriter(file,true);
        	//BufferedWriter writer give better performance
        	BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter pw = new PrintWriter(bw);
        	//This will add a new line to the file content
        	pw.println("");
        	/* Below three statements would add three 
        	 * mentioned Strings to the file in new lines.
        	 */
        	
        	String fullLogsText = "[" + in + Clock.getTime() + "] | " + orgIndex[formComboBox.currentIndex()] + " | " + formLineEdit.text();
        	
        	if(soberCheckBox.isChecked())
    		{
        		pw.print(fullLogsText + " (Sober)");
    		} else {
    			pw.print(fullLogsText);
    		}
        	
    		if(soberCheckBox.isChecked())
    		{
        		orgOutput.setText(orgIndex[formComboBox.currentIndex()] + " | " + formLineEdit.text() + " (Sober)");
    		} else {
        		orgOutput.setText(orgIndex[formComboBox.currentIndex()] + " | " + formLineEdit.text());
    		}
    		
        	pw.close();
        	
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	try
    	{
            //Specify the file name and path here
        	File file = new File(namesFile);

        	/* This logic is to create the file if the
        	 * file is not already present
        	 */
        	if(!file.exists())
        	{
				file.createNewFile();
        	}

        	//Here true is to append the content to file
        	FileWriter fw = new FileWriter(file,true);
        	//BufferedWriter writer give better performance
        	BufferedWriter bw = new BufferedWriter(fw);
        	PrintWriter pw = new PrintWriter(bw);
        	//This will add a new line to the file content
        	pw.println("");
        	/* Below three statements would add three 
        	 * mentioned Strings to the file in new lines.
        	 */
    		pw.print(formLineEdit.text());
        	
        	pw.close();

//        	This will write everything for the attendance box 
//        	and work on refreshing the box every time content
//        	is submitted
        	
        	file = new File(attendanceFile);
        	if(!file.exists())
        	{
				file.createNewFile();
        	}
        	fw = new FileWriter(file,true);
        	bw = new BufferedWriter(fw);
        	pw = new PrintWriter(bw);
        	pw.println("");
        	
        	if(soberCheckBox.isChecked())
    		{
        		attendanceBox.append("[" + in + Clock.getTime() + "] " + formLineEdit.text() + " (Sober)" + minor);
        		pw.print("[" + in + Clock.getTime() + "] " + formLineEdit.text() + " (Sober)" + minor);
    		} else {
    			attendanceBox.append("[" + in + Clock.getTime() + "] " + formLineEdit.text() + minor);
    			pw.print("[" + in + Clock.getTime() + "] " + formLineEdit.text() + minor);
    		}
        	
        	pw.close();
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void makeNewDir(String dir)
    {
    	File theDir = new File(dir);

//    	If the directory does not exist, create it
    	if (!theDir.exists()) {
    	    System.out.println("creating directory: " + theDir.getName());
    	    boolean result = false;

    	    try{
    	        theDir.mkdir();
    	        result = true;
    	    } 
    	    catch(SecurityException se){
    	        //handle it
    	    }        
    	    if(result) {    
    	        System.out.println("DIR created");  
    	    }
    	}
    }
    
    public static void makeNewFile(String filePath, String defaultText)
    {
    	File file = new File(filePath);
		
		if(!file.exists())
    	{
			try {
				file.createNewFile();
//				Here true is to append the content to file
	        	FileWriter fw = new FileWriter(file,true);
//	        	BufferedWriter writer give better performance
	        	BufferedWriter bw = new BufferedWriter(fw);
	        	PrintWriter pw = new PrintWriter(bw);
//	        	This will add a new line to the file content
	        	pw.println(defaultText);
//	        	Below three statements would add three 
//	        	mentioned Strings to the file in new lines.

				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public static void setUpDirectory()
    {
    	File file = new File(rootDir + "SoberShift");
    	if(!file.exists())
    		firstRun = true;
    	
    	if(firstRun)
    		System.out.println("First RUN!");
    	
    	makeNewDir(rootDir + "SoberShift");
    	makeNewDir(fileDir + "images");
    	makeNewDir(fileDir + "bin");
    	makeNewFile(orgsFile,"N/A\n"
    			+ "UnK");
    	makeNewFile(attendanceFile, "");
    }
    
    public static void populateAttendance()
    {
    	BufferedReader br = null;
		FileReader fr = null;
		
		try {
			fr = new FileReader(attendanceFile);
			br = new BufferedReader(fr);
			
			String sCurrentLine;
			
			while ((sCurrentLine = br.readLine()) != null) 
			{
				attendanceBox.append(sCurrentLine);	
			}
			br.close();

		} catch (IOException e) {

			e.printStackTrace();

		}
    }
}