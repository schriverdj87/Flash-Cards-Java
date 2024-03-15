package main;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.application.Application;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.animation.AnimationTimer;

import java.util.HashMap;
import java.util.ArrayList;
import javafx.geometry.Pos;

import fccustom.*;
import screens.*;
import java.util.ArrayList;


public class Main extends Application
{//Also handles the exchange of data between modules
	private Stage theStage;
	private StackPane spnMain = new StackPane();//Main "socket" where everything is placed
	private Scene theScene = new Scene (spnMain,720,540);
	private Engine engine = new Engine();
	
	private SpecialStackPane spnTitle = new SpecialStackPane(this);
	
	
	//Screen Holder
	private String strScreen = "";//The current screen that is up;
	private HashMap<String, SpecialStackPane> hshScreens = new HashMap<>();
		{
			hshScreens.put("TITLE",new SSPTitle(this));
			hshScreens.put("TOFLASH",new SSPLoader(this,true));
			hshScreens.put("FLASH",new SSPFlash(this,theScene));
			hshScreens.put("TEST",new SpecialStackPane(this));
			hshScreens.put("EXPORT",new SSPExport(this));
		}
	
	
	@Override
	public void start (Stage stage)
	{
		theStage = stage;
		theStage.setScene(theScene);
		theStage.setTitle("Flash Cards");
		theStage.show();
		engine.start();
		theStage.setOnCloseRequest(e -> {engine.stop();});

		switchScene("TITLE");
		

	}
	
	
	
	
	//-------Public Methods-------//
	public void switchScene (String to)
	{
		//If it is not found return
		
		if (hshScreens.get(to) == null)
		{
			System.out.println(to +" not found!");
			return;
		}
		
		
		strScreen = to;
		while (spnMain.getChildren().size() > 0)//Clear the main stackpane;
		{
			spnMain.getChildren().remove(0);
		}
		
		if (hshScreens.get(to) instanceof SpecialStackPane)//Hit the reset function if it's a special stackpane
		{
			SpecialStackPane toHit = (SpecialStackPane)hshScreens.get(to);
			toHit.reset();
		}
		
		spnMain.getChildren().add(hshScreens.get(to));
		
	}
	
	public void switchScene (ArrayList<String> questions)
	{
		SSPFlash daFlash = (SSPFlash) hshScreens.get("FLASH");
		daFlash.InputQuestions(questions);
		switchScene("FLASH");
	}
	
	public void SetInfinite (boolean to)
	{
		SSPFlash daFlash = (SSPFlash) hshScreens.get("FLASH");
		daFlash.SetInfinite(to);
	}
	
	public boolean OnStage (SpecialStackPane checkit)
	{
		return spnMain.getChildren().indexOf(checkit) != -1;
	}
	
	//Classes
	private class Engine extends AnimationTimer
	{
		@Override
		public void handle (long now)
		{
			Platform.runLater(() -> runCore());
		}
		
		public void runCore()
		{
			hshScreens.get(strScreen).cog();
		}
		
	}
	
	//-------End Junk-------//
	public static void Main (String[] args)
	{
		launch(args);
	}
}