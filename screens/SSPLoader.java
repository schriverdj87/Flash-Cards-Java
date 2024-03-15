package screens;

import fccustom.*;
import main.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.geometry.Pos;
import java.util.*;

//

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import javafx.scene.control.SelectionModel;

//IO
import java.io.*;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
/*
TODO
- Detect and weed-out questions with empty answers OR have flash handle it
- Ensure empty files don't get through
- Help box that displays the button's function on mouse-over
- Display preview of questions to be loaded on mouse over
- Make numbering of preview buttons start at 1
- Get quotes to show on questions
*/
public class SSPLoader extends SpecialStackPane
{
	private ObservableList<String> olMain;
	private ListView<String> lvMain;
	private HBox hbxBtnBar;
	private BorderPane Choosey;
	private BorderPane ChooseSplit;//Where the user is taken when a split is detected
	private VBox ChooseSplitKebab;//Where the split kebab is kept
	
	private Button btnInfinite = new Button ("Infinite");
	
	private boolean includeInfinite = true;
	
	private ArrayList<ArrayList<String>> alLoadedQuiz = new ArrayList<>();
	
	
	
	public SSPLoader (Main mn)
	{
		super (mn);
	}	
	
	public SSPLoader (Main mn, boolean allowInfinite)
	{
		super (mn);
		if (allowInfinite == false)
		{
			hbxBtnBar.getChildren().remove(1);
		}
	}
	
	@Override
	protected void setup ()
	{
		
		olMain = FXCollections.observableArrayList("I","Eat","Your","Face");
		Choosey = new BorderPane();
		lvMain = new ListView<String>(olMain);
		hbxBtnBar = new HBox(5);
		
			Choosey.getStylesheets().add("fcMakeup.css");
			Choosey.getStyleClass().add("root");
		Button btnLoad = new Button ("Load");
		btnInfinite = new Button ("Infinite");
		Button btnDelete = new Button ("Delete");
		Button btnRefresh = new Button ("Refresh");
		Button btnExport = new Button ("Export");
		Button btnBack = new Button ("Back");
		fileGetterSparker();
		
		
		// Actual setup
		
		VBox mainKebab = new VBox(5);

		
		
		
		lvMain.getStyleClass().add("answer");
		
		hbxBtnBar.setAlignment(Pos.CENTER);
		

			hbxBtnBar.getChildren().addAll(btnLoad,btnInfinite,btnDelete,btnRefresh,btnExport,btnBack);


		
		mainKebab.getChildren().addAll(lvMain,hbxBtnBar);
		Choosey.setCenter(mainKebab);
		this.getChildren().add(Choosey);
		
		//Setting up the splitty
		
		ChooseSplit = new BorderPane();
		ChooseSplit.getStylesheets().add("fcMakeup.css");
			ChooseSplit.getStyleClass().add("root");
		Label mainChooseLabel = new Label("Choose Section");
			mainChooseLabel.getStyleClass().addAll("question","whiteGlow","justCenter");
		ChooseSplitKebab = new VBox(5);
		VBox daKebab = new VBox(5);
		daKebab.getChildren().addAll(mainChooseLabel,ChooseSplitKebab);
		daKebab.setAlignment(Pos.CENTER);
		ChooseSplitKebab.setAlignment(Pos.CENTER);
		ChooseSplit.setCenter(daKebab);
		//this.getChildren().add(ChooseSplit);
		
		
		
		//Wiring main.switchScene("FLASH");
		lvMain.setOnMouseClicked(e->{btnDelete.setText("Delete");});
		btnLoad.setOnAction (e->{main.SetInfinite(false);loadinate();});
		btnInfinite.setOnAction(e->{main.SetInfinite(true);loadinate();});
		btnDelete.setOnAction(e->
		{
			if (btnDelete.getText() == "Delete")
			{
				btnDelete.setText("Really?");
				return;
			}
			
			if (lvMain.getSelectionModel().getSelectedItem() != null)
			{
				String daName = lvMain.getSelectionModel().getSelectedItem();
				
				try
				{
					Files.delete(Paths.get("bucket\\"+daName+".txt"));
					olMain.remove(olMain.indexOf(lvMain.getSelectionModel().getSelectedItem()));
					
				}
				catch(IOException f)
				{
					System.out.println(f);
				}
			}
			
			btnDelete.setText("Delete");
		}
		);
		btnRefresh.setOnAction(e -> fileGetterSparker());
		btnExport.setOnAction(e -> 
		{
			/*
			Alert testMessage = new Alert(AlertType.INFORMATION);
			testMessage.setTitle("Heyo!");
			testMessage.setHeaderText(null);
			testMessage.setContentText("Ebola!");
			testMessage.showAndWait();
			*/
			main.switchScene("EXPORT");
			
		}
		
		);
		
		btnBack.setOnAction( e -> main.switchScene("TITLE"));
		
		
		
		
	}
	
	private void loadinate()
				{
				if (lvMain.getSelectionModel().getSelectedItem() == null){return;}//Ensures something is slected
				String daName = lvMain.getSelectionModel().getSelectedItem();
				String[] daContents = importDaString("bucket\\"+daName+".txt").split("\n");//This is the flash card file
				ArrayList <ArrayList<String>> toPoot = new ArrayList<>();//TODO make \n denote a separation in the file
				ArrayList<String> toPootMaster = new ArrayList<>();
				toPoot.add(new ArrayList<String>());
				for (int a = 0; a < daContents.length; a++)
				{
					
					
					if (daContents[a].length() <= 1)//Blank space means a split
					{
						toPoot.add(new ArrayList<String>());
						continue;
					}
					
					if (daContents[a].indexOf("|") == -1)
					{
						continue;
					}
					
					toPoot.get(toPoot.size() - 1).add(daContents[a]);
					toPootMaster.add(daContents[a]);
				}
				
				
				for (int b = toPoot.size()-1; -1 < b; b--)//Removes quiz frags
				{
					if (toPoot.get(b).size() == 0){toPoot.remove(b);}
				}
				
				if(toPoot.size() > 1)
				{
					toPoot.add(toPootMaster);
				}
				else
				{
					//Is only one, load the Flash
					
					main.switchScene(toPoot.get(0));
					return;
				}
				
				alLoadedQuiz = toPoot;
				
				while (ChooseSplitKebab.getChildren().size() > 0){ChooseSplitKebab.getChildren().remove(0);}
				
				
				HBox newhbox = new HBox(5);
				newhbox.setAlignment(Pos.CENTER);
				ChooseSplitKebab.getChildren().add(newhbox);
				
				for (int a = 0; a < alLoadedQuiz.size()-1; a++)
				{
					Button btnToPut = new Button (String.valueOf(a));
					int scabA = a;
					btnToPut.setOnAction(f -> {main.switchScene(alLoadedQuiz.get(scabA));});//Button gives Flash the quiz at the index
					HBox lastHBox = (HBox)ChooseSplitKebab.getChildren().get(ChooseSplitKebab.getChildren().size()-1);
					lastHBox.getChildren().add(btnToPut);//Gets the last HBox in the Choose Split kebab
					if (a % 5 != 0 || a == 0){continue;}//Ensures that a new row is added every 5 buttons
					HBox newhbox2 = new HBox(5);
					newhbox2.setAlignment(Pos.CENTER);
					ChooseSplitKebab.getChildren().add(newhbox2);
				}
				
				while (this.getChildren().size() > 0)
				{
					this.getChildren().remove(0);
				}
				
				this.getChildren().add(ChooseSplit);
				
				Button btnAll = new Button ("All");
				
				btnAll.setOnAction(e -> main.switchScene(toPootMaster));
				
				ChooseSplitKebab.getChildren().add(btnAll);
			}
	
		private class fileGetter extends SimpleFileVisitor<Path>
	{
		@Override
		public FileVisitResult visitFile (Path file, BasicFileAttributes e)
		{
			
			//System.out.println(file.toString().replace(".\\bucket\\","").replace(".txt",""));
			//System.out.println(importDaString(file.toString()));
			if (validate(file.toString()) == false)//TODO maybe: Just make sure there's a half-pipe in there somewhere and have a method to filter out unusable lines
			{
				return CONTINUE;
			}
			
			
			olMain.add(file.toString().replace(".\\bucket\\","").replace(".txt",""));
			//TODOmaybe: Create a name - path hashmap to allow it to display files in directories properly or maybe even make a smaller list for sub-directories
			
			return CONTINUE;
		}
		
		
		
		private boolean validate (String checkthis)
		{
			String[] toCheck = importDaString(checkthis).split("\n");
			for (int a = 0; a < toCheck.length; a++)
			{
				
				if (toCheck[a].indexOf("|") == -1)//Has at least one |
				{
					//return false;
				}

				if (toCheck[a] == "|")// Is more than a |
				{
					return false;
				}
				
				
				//System.out.println(toCheck[a]);
			}
			
			return true;
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
	
	private void fileGetterSparker()
	{
		while (olMain.size() > 0){olMain.remove(0);}
		try
		{
		Files.walkFileTree(Paths.get(".//bucket//"),new fileGetter());
		
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
	//----Public Meth heads
	public void reset()
	{
				while (this.getChildren().size() > 0)
				{
					this.getChildren().remove(0);
				}
				
				this.getChildren().add(Choosey);
	}
	
	public static String importDaString (String from)
	{//Imports string data
		String toSend = "";
		
		Path daPath = Paths.get(from);
		
		if (Files.exists(daPath))
		{
			try
			{
				byte[] raw = Files.readAllBytes(daPath);
				toSend = new String(raw);
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
		}
		else
		{
			System.out.println("DOES NOT EXIST!");
		}
		
		return toSend;
	}
	
	public static void exportDaString (String to, String holding)
	{

		try
		{
			Files.createDirectories(Paths.get(".\\standalones"));
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		
		Path exportTo = Paths.get("standalones\\" + to + ".html");
		
		try 
		{
			Files.write(exportTo,holding.getBytes());
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}
	
	
	
}