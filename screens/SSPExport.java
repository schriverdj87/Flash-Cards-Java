package screens;

import fccustom.*;
import main.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.event.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import java.util.*;

//IO
import java.io.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.*;
import java.nio.file.*;


import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.application.Application;

public class SSPExport extends SpecialStackPane
{
	
	private BorderPane MainBP = new BorderPane();
	private BorderPane ColorBP = new BorderPane();
	private Label lblInfo = new Label ("Poot help here");
	private Label lblHeader;
	private Button btnToExport;
	private Button btnFromExport;
	private ListView<String> lstBucket;
	private ObservableList<String> lstBucketCore;
	private ListView<String> lstExport;
	private ObservableList<String> lstExportCore;
	private TextField txtTitle;
	private HashMap<String,String> quizzes;
	
	/*
	private ColorPicker cpkColor = new ColorPicker(Color.WHITE);
	private ColorPicker cpkBackground = new ColorPicker(Color.BLACK);
	private ColorPicker cpkBtnDefault = new ColorPicker (Color.SILVER);
	private ColorPicker cpkBtnHover = new ColorPicker(Color.WHITE);
	private ColorPicker cpkBtnDown = new ColorPicker(Color.DARKGREY);
	*/
	
	
	private String strColor = "";
	private String strBackground = "";
	private String strBtnDefault = "";
	private String strBtnHover = "";
	private String strBtnDown = "";
	private String strTemplate ;
	
	public SSPExport (Main main)
	{
		super(main);
	}
	
	@Override
	protected void setup()
	{
		
		
		//Put Style
		this.getStylesheets().add("fcMakeup.css");
		this.getStyleClass().add("root");
		
		
		//Put the exit button
		MainBP = new BorderPane();
		ColorBP = new BorderPane();
		ColorBP.getStylesheets().add("fcMakeup.css");
		ColorBP.getStyleClass().add("root");
		
		
		this.getChildren().addAll(MainBP);
		
		Stage stgColors = new Stage();
		stgColors.setScene(new Scene(ColorBP,720,200));
		
		BorderPane theBottom = new BorderPane();
			MainBP.setBottom(theBottom);
		Button btnBack = new Button ("Back");
			theBottom.setRight(btnBack);
		btnBack.setOnAction ( e -> 
		{
			main.switchScene("TITLE");
		});
		
		//Put the help
		lblInfo = new Label ("Poot help here");
		theBottom.setCenter(lblInfo);
		lblInfo.getStyleClass().add("help");
		
		//Put the header
		lblHeader = new Label ("Export HTML Standalones!");
		lblHeader.getStyleClass().addAll("smallQuestion");
		HBox hbxHeadHold = new HBox();
		hbxHeadHold.getChildren().add(lblHeader);
		hbxHeadHold.setAlignment(Pos.CENTER);
		MainBP.setTop(hbxHeadHold);
		
		
		//Title Box
		txtTitle = new TextField();
		txtTitle.setPromptText("Put Title Here");
		
		
		VBox vbxCenter = new VBox(5);
		vbxCenter.setAlignment(Pos.CENTER);
		MainBP.setCenter(vbxCenter);
		HBox hbxChangeMain = new HBox(5);
		hbxChangeMain.setAlignment(Pos.CENTER);
		vbxCenter.getChildren().addAll(txtTitle,hbxChangeMain);
		
		//Lib Box
		lstBucketCore = FXCollections.observableArrayList();
		lstBucket = new ListView<>(lstBucketCore);
		VBox vin = new VBox(5);
		Label lblLibrary = new Label ("Library");
		lblLibrary.getStyleClass().add("help");
		vin.getChildren().addAll(lblLibrary,lstBucket);
		hbxChangeMain.getChildren().add(vin);
		
		//Exchange BTNs
		
		VBox vbxExchange = new VBox(5);
		vbxExchange.setAlignment(Pos.CENTER);
		Button btnToExport = new Button(">");
		Button btnFrExport = new Button("<");
		vbxExchange.getChildren().addAll(btnToExport,btnFrExport);
		hbxChangeMain.getChildren().add(vbxExchange);
		
		//Export Libs
		lstExportCore = FXCollections.observableArrayList();
		lstExport = new ListView<>(lstExportCore);

		VBox vout = new VBox(5);
		Label lblExport = new Label ("Export");
		lblExport.getStyleClass().add("help");
		vout.getChildren().addAll(lblExport,lstExport);
		hbxChangeMain.getChildren().add(vout);
		
		//Put Color Options 
		HBox hbxColors = new HBox(5);
		ColorPicker cpkColor = new ColorPicker(Color.WHITE);
		ColorPicker cpkBackground = new ColorPicker(Color.BLACK);
		ColorPicker cpkBtnDefault = new ColorPicker (Color.SILVER);
		ColorPicker cpkBtnHover = new ColorPicker(Color.WHITE);
		ColorPicker cpkBtnDown = new ColorPicker(Color.DARKGREY);
		
		VBox ColorsSide1 = new VBox(5);
		ColorsSide1.getChildren().addAll(
		ColorWrapper(cpkColor,"Text"),
		ColorWrapper(cpkBackground,"Background")
		);
		
		VBox ColorsSide2 = new VBox(5);
		ColorsSide2.getChildren().addAll(
		ColorWrapper(cpkBtnDefault,"Button Default"),
		ColorWrapper(cpkBtnHover,"Button Hover")
		);
		
		VBox ColorsSide3 = new VBox(5);
		ColorsSide3.getChildren().addAll(
		
		ColorWrapper(cpkBtnDown,"Button Pressed")
		);
		
		hbxColors.getChildren().addAll(
		ColorsSide1,ColorsSide2,ColorsSide3
		);
		
		ColorBP.setCenter(hbxColors);
		
		//Bottom Button Tray
		
		HBox hbxOptions = new HBox(5);
		
		Button btnColor = new Button("Choose Colors");
		Button btnExport = new Button ("Export");
		Button btnReset = new Button ("Reset");
		
		hbxOptions.setAlignment(Pos.CENTER);
		hbxOptions.getChildren().addAll(btnColor, btnExport, btnReset);
		vbxCenter.getChildren().add(hbxOptions);
		
		
		// Wire the things
		
		EventHandler evt = new EventHandler()
		{
			public void handle(Event e)
			{
				ColorPicker pickme = (ColorPicker)e.getSource();
			
				System.out.println(pickme.getValue().toString());
				
				System.out.println(pickme.toString());
				
			}
		};
		
		
		cpkColor.setOnAction(e -> {strColor = cpkColor.getValue().toString();});
		cpkBackground.setOnAction(e -> {strBackground = cpkBackground.getValue().toString();});
		cpkBtnDefault.setOnAction(e -> {strBtnDefault = cpkBtnDefault.getValue().toString();});
		cpkBtnHover.setOnAction(e -> {strBtnHover = cpkBtnHover.getValue().toString();});
		cpkBtnDown.setOnAction(e -> {strBtnDown = cpkBtnDown.getValue().toString();});
		
		
		strColor = "white";
		strBackground = "black";
		strBtnDefault = "#C0C0C0";
		strBtnHover = "white";
		strBtnDown = "#A9A9A9";
		
		
		
		
		btnToExport.setOnAction(e -> 
		{
			if(lstBucket.getSelectionModel().getSelectedIndex() == -1){return;}
			String chosen = lstBucket.getSelectionModel().getSelectedItem();
			lstBucketCore.remove(chosen);
			lstExportCore.add(chosen);
			//System.out.println(lstBucket.getSelectionModel().getSelectedItem());
		}
		);
		
		btnFrExport.setOnAction(e -> 
		{
			if(lstExport.getSelectionModel().getSelectedIndex() == -1){return;}
			String chosen = lstExport.getSelectionModel().getSelectedItem();
			lstExportCore.remove(chosen);
			lstBucketCore.add(chosen);
			//System.out.println(lstBucket.getSelectionModel().getSelectedItem());
		}
		);
		
		btnColor.setOnAction(e -> 
		{
			stgColors.show();
		});
		
		btnExport.setOnAction( e-> 
			{
				
				String myTitle = txtTitle.getText().trim();
				if (myTitle.replace(" ","").trim() == "" || myTitle.length() == 0)
				{
					System.out.println("Please enter a title!");
					return;
				}
				if (lstExportCore.size() == 0)
				{
					System.out.println("Please add items");
					return;
				}
			
				String toSend = strTemplate;
				String putMe = "";
				
				for (int a = 0; a < lstExportCore.size(); a++)
				{
					String miniput = quizzes.get(lstExportCore.get(a)).replace("\"","&quot;");
					miniput = "\"" + miniput + "\"";
					putMe = putMe + miniput;
					
					if (a < lstExportCore.size() - 1)
					{
						putMe = putMe + ",\n";
					}
					
				}
				
				putMe = "[" + putMe + "]"; 
				
				
				toSend = toSend.replace("[93243234]",putMe).replace("POOTTITLEHERE","myTitle");
				toSend = toSend.replace("BKGHERE",cpkBackground.getValue().toString().replace("0x","#"));
				toSend = toSend.replace("TXTHERE",cpkColor.getValue().toString().replace("0x","#"));
				toSend = toSend.replace("BTNCOLHERE",cpkBtnDefault.getValue().toString().replace("0x",""));
				
				
				
				SSPLoader.exportDaString(myTitle,toSend);
			}
		);
		
		//Get Quizzes
		strTemplate = SSPLoader.importDaString("htmplate.html");
		quizzes = new HashMap<String,String>();
		
		try
		{
			Files.walkFileTree(Paths.get(".//bucket//"),new fileGetter());
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
	}
	
	private class fileGetter extends SimpleFileVisitor<Path>
	{
		@Override
		public FileVisitResult visitFile (Path file, BasicFileAttributes e)
		{
			String strPath = file.toString();
			
			if (strPath.indexOf(".txt") == -1)
			{
				return CONTINUE;
			}
			
			String strTitle = strPath.substring(strPath.lastIndexOf("\\") + 1,strPath.lastIndexOf("."));
			String strQuiz = strTitle +"\\n" + SSPLoader.importDaString(strPath).replace("\n","\\n");
			quizzes.put(strTitle,strQuiz);
			
			System.out.println(strTitle);
			
			lstBucketCore.add(strTitle);
			
			
			
			return CONTINUE;
		}
		
					@Override
			public FileVisitResult postVisitDirectory (Path file, IOException e)
			{
				return CONTINUE;
			}
			
			@Override
			public FileVisitResult visitFileFailed (Path file, IOException e)
			{
				return CONTINUE;
			}
	}
	
	private class Quiz 
	{
		public String title;
		public String contents;
		
		public Quiz()
		{
		}
		
		public Quiz(String leTitle, String leContents)
		{
			title = leTitle;
			contents = leContents;
		}
		
		public String getTitle()
		{
			return title;
		}
		
		public String getContents()
		{
			return contents;
		}
		
		public String getCombined()
		{
			return title + "|" + contents;
		}
	}
	
	private HBox ColorWrapper (ColorPicker wrapme, String myName)
	{
		HBox toSend = new HBox(5);
		Label leLabel = new Label (myName);
		leLabel.getStyleClass().add("help");
		toSend.getChildren().addAll(leLabel,wrapme);
		return toSend;
	}
}