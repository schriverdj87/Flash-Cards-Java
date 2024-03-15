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



public class MainTest extends Application
{//Also handles the exchange of data between modules
	private Stage theStage;
	private StackPane spnMain = new StackPane();//Main "socket" where everything is placed
	private Scene theScene = new Scene (spnMain,720,540);
	private Engine engine = new Engine();
	
	private StackPane spnTitle = new StackPane();
	
	
	//Screen Holder

	
	@Override
	public void start (Stage stage)
	{
		theStage = stage;
		theStage.setScene(theScene);
		theStage.setTitle("Flash Cards");
		theStage.show();
		engine.start();
		theStage.setOnCloseRequest(e -> {engine.stop();});
		

	}
	
	
	
	
	//-------Public Methods-------//

	
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
		
		}
		
	}
	
	//-------End Junk-------//
	public static void Main (String[] args)
	{
		launch(args);
	}
}