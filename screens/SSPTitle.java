package screens;

import fccustom.*;
import main.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;

public class SSPTitle extends SpecialStackPane
{
	public SSPTitle (Main mn)
	{
		super (mn);
	}
	
	@Override
	protected void setup ()
	{
		BorderPane LeTitle = new BorderPane();
		Button titleStudyBTN = new Button("Study");
		Label titleTitle = new Label ("Flash Cards");
		Label titleCredit = new Label ("\u00A9 David Schriver (May 2018)");
		Button btnExport = new Button ("Export");
		
		
		VBox mainKebab = new VBox(5);
		mainKebab.setAlignment(Pos.CENTER);
		LeTitle.getStylesheets().add("fcMakeup.css");
		LeTitle.getStyleClass().add("root");
		mainKebab.getChildren().addAll(titleStudyBTN,btnExport);
		titleTitle.getStyleClass().addAll("question","whiteGlow","justCenter");
		LeTitle.setCenter(mainKebab);
		
		LeTitle.setTop(titleTitle);
		HBox creditHolder = new HBox();
		creditHolder.setAlignment(Pos.BOTTOM_RIGHT);
		creditHolder.getChildren().add(titleCredit);
		LeTitle.setBottom(creditHolder);
		this.getChildren().addAll(LeTitle);
		//Wiring
		
		titleStudyBTN.setOnAction(e -> {main.switchScene("TOFLASH");});
		
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
			
		});
		
	}
}