package screens;

import fccustom.*;
import main.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import java.util.ArrayList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.Collections;
import java.util.HashMap;
import javafx.event.*;

//Special

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.effect.GaussianBlur;

//KeyboardInpoot
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
/*TODO 
- FIX TRANSITION TO NEXT QUESTION ON WRONG ANSWER FOR MULTIPLE CHOICE
- For multiple choice questions: Recognize and remove duplicates of the right answer
- For multiple choice questions: Allow one or two wrongs before it is all wrong
- Slideshow mode (consider making another special pane)
- Get dsAnswerGlow to flash on all questions except flip
- Hangman mode
- Multi-line input
- Replace "â€™" with "'", "â€œ" with """, "â€�" to """
- Repeat after me mode
- Changable rules

TODO Maybe
- Allow alternate answers
- Disallow do-overs on failed questions with ESC
*/

/*
Done
February 1 2018: Fixed the True False;

*/
public class SSPFlash extends SpecialStackPane
{
	private Label lblRemaining;
	private Label lblQuestion;
	private Label lblHp;
	private Label lblHelp;
	private Label lblHint;
	private Label lblResults;
	private final String heart = "\u2665";
	
	
	private Button btnHint;
	private Button btnQuit;
	
	private StackPane stkAnswerInput;//"Socket" for the answer box
	private BorderPane bpiRoot;//Main Root
	private BorderPane bpiResults;//Results
	private TextField txtAnswer;//Where the user types in his answer
		private String strTextHelp = "Type your answer and press ENTER. Press ESC to skip.";
		private String strTFHelp = "Or type \"t\" or \"f\"";
		private String strFlipHelp = "Or type \"f\" or \"n\"";
		private String strMultiHelp = "Click the right answer or press the corresponding number";
	
	
	//Answer Inputs
	private StackPane inputType;//Type in your answer 
	private StackPane inputFlip;//Flip the things
	private StackPane inputTF;//True or False
			private Button btnTrue = new Button ("True");
			private Button btnFalse = new Button ("False");
	private StackPane inputMulti;//MultipleChoice
		private VBox inputMultiTray;
	
	private ArrayList<String> alQuestionsOriginal;//Original Questions
	private ArrayList<String> alQuestions = new ArrayList<>();//Questions used
	
	private String strAnswer; //Current Answer;
	private String strQuestion;//Current Question;
	
	private Color clrAnswerGlow = new Color (0,1,0,0);
	private DropShadow dsAnswerGlow = new DropShadow(BlurType.GAUSSIAN, clrAnswerGlow,30,0.5,0,0);
	private double dblAlpha = 0;
	private double dblAlphaStep = 0.05;
	private int score = 0;
	private int hp = 3;
	private int hpMax = 3;
	private boolean hintUnused = true;
	private boolean bolNextQ = false;//Tells cog whether or not to transition to the next question
	private int focusSpammer = 0;//Used in cog to give focus to the answer text boxes.
	private int focusSpammerMax = 100;
	
	private boolean ruleInfinity = false;
	private double rulePassMarkBase = 0.7;
	private double rulePassMark = rulePassMarkBase;
	
	public SSPFlash (Main mn, Scene daScene)
	{
		super (mn);
		daScene.setOnKeyPressed(new KeyboardHandle());
	}
	
	@Override
	protected void setup ()
	{
		//Setup
		lblRemaining = new Label("NN Remaining");
		lblQuestion = new Label("How many apples can you fit in your mouth.");
		lblHp = new Label (heart);
		lblHelp = new Label ("Don't joke the sick man.");
		lblHint = new Label ();
		btnHint = new Button ("Hint");
		btnQuit = new Button ("Title");
		Button btnRetry = new Button ("Retry");
		Button btnChange = new Button ("Change");
		Button btnQuit2 = new Button ("Title");

	
		txtAnswer = new TextField();
		DropShadow testGlow = new DropShadow(BlurType.GAUSSIAN,Color.RED,30,0.5,0,0);
		clrAnswerGlow = new Color(0,1,0,0);
		dsAnswerGlow = new DropShadow(BlurType.GAUSSIAN,clrAnswerGlow,30,0.5,0,0);
			txtAnswer.setEffect(dsAnswerGlow);

		//Inputs
		inputType = new StackPane();
		inputType.getChildren().add(txtAnswer);
		
		inputFlip = new StackPane();
			Button btnFlip = new Button ("Flip");
			Button btnFlipNext = new Button ("Next");
			HBox flipTray = new HBox(5);
				flipTray.setAlignment(Pos.CENTER);
				flipTray.getChildren().addAll(btnFlip,btnFlipNext);
			inputFlip.getChildren().add(flipTray);
		
		inputTF = new StackPane();
				Button btnTrue = new Button ("True");
				Button btnFalse = new Button ("False");
				HBox tfTray = new HBox(5);
					tfTray.setAlignment(Pos.CENTER);
					tfTray.getChildren().addAll(btnTrue,btnFalse);
				inputTF.getChildren().add(tfTray);
				
		inputMulti = new StackPane();
			inputMultiTray = new VBox(5);
			inputMultiTray.setAlignment(Pos.CENTER);
			inputMulti.getChildren().add(inputMultiTray);
		
		//
		
		VBox fiKebab = new VBox(5);
		VBox resultKebab = new VBox(5);
		
		stkAnswerInput = new StackPane();
		bpiRoot = new BorderPane();
		bpiResults = new BorderPane();
		lblResults = new Label();
		
		
		//Pasted
		lblQuestion.setEffect(testGlow);
		bpiRoot.getStylesheets().add("fcMakeup.css");
		bpiRoot.getStyleClass().add("root");
		bpiResults.getStylesheets().add("fcMakeup.css");
		bpiResults.getStyleClass().add("root");
		
		
		BorderPane topBox = new BorderPane();
		topBox.setRight(lblHp);
		topBox.setLeft(lblRemaining);
		lblRemaining.getStyleClass().add("whiteGlowRemaining");
		lblResults.getStyleClass().add("whiteGlowRemaining");
		txtAnswer.setMaxWidth(400);
		txtAnswer.setAlignment(Pos.CENTER);
		
		
		fiKebab.getStyleClass().add("justCenter");
		resultKebab.getStyleClass().add("justCenter");
		resultKebab.setAlignment(Pos.CENTER);
		HBox resultButtonTray = new HBox(5);
		resultButtonTray.setAlignment(Pos.CENTER);
		resultButtonTray.getChildren().addAll(btnRetry,btnChange,btnQuit2);
		resultKebab.getChildren().addAll(lblResults,resultButtonTray);
		bpiResults.setCenter(resultKebab);
		
		
		
		
		lblQuestion.setWrapText(true);
		lblHp.getStyleClass().addAll("health","redGlow");
		lblHelp.getStyleClass().add("help");
		lblHelp.setWrapText(true);
		lblHint.getStyleClass().add("help");
		lblHint.setWrapText(true);
		lblQuestion.getStyleClass().addAll("question"/*,"whiteGlow"*/);
		
		
		txtAnswer.getStyleClass().add("answer");

		BorderPane hintQuitBox = new BorderPane();
		hintQuitBox.setLeft(btnHint);
		hintQuitBox.setRight(btnQuit);
		hintQuitBox.setCenter(lblHint);
		bpiRoot.setBottom(hintQuitBox);
		
		/*
		
		HBox hintQuitBox = new HBox(500);
		hintQuitBox.setAlignment(Pos.CENTER);
		btnHint.setAlignment(Pos.CENTER_RIGHT);

		bpiRoot.setBottom(hintQuitBox);
		bpiRoot.setLeft(btnHint);
		bpiRoot.setRight(btnQuit);
		bpiRoot.setAlignment(btnHint,Pos.BOTTOM_LEFT);
		bpiRoot.setAlignment(btnQuit,Pos.BOTTOM_RIGHT);
		
		*/

		
		bpiRoot.setCenter(fiKebab);
		bpiRoot.setTop(topBox);
		
		fiKebab.getChildren().addAll(lblQuestion,stkAnswerInput,lblHelp);
		stkAnswerInput.getChildren().addAll(inputType);
		this.getChildren().add(bpiRoot);
		
		//Wiring
		btnHint.setOnAction(e -> {PootHint();});
		btnQuit.setOnAction( e -> main.switchScene("TITLE"));
		btnQuit2.setOnAction( e -> main.switchScene("TITLE"));
		
		btnChange.setOnAction( e -> main.switchScene("TOFLASH"));
		btnRetry.setOnAction( e -> reset());
		
		btnFlipNext.setOnAction(e -> 
			{
				if (hintUnused){score++;}
				nextQuestion();
			}
		);
		btnFlip.setOnAction(e -> 
		{
			if (lblQuestion.getText() == strQuestion)
			{
				setQuestionText(strAnswer);
			}
			else
			{
				setQuestionText(strQuestion);
			}
		}
		);
		
		btnTrue.setOnAction (e -> SubmitAnswer("t"));
		btnFalse.setOnAction (e -> SubmitAnswer("f"));
		
		
	}
	//----Private Methods
	
		private void nextQuestion(boolean deleteCurrent)
	{
						hp = hpMax;
						if (deleteCurrent){alQuestions.remove(0);}
						else if (alQuestions.size() > 1)
						{
							String oldStr = alQuestions.get(0);
							
							while (alQuestions.get(0) == oldStr)
							{
								Collections.shuffle(alQuestions);
							}
						}
						txtAnswer.setText("");
						hintUnused = true;
						if (alQuestions.size() == 0)
						{
							if (ruleInfinity == false)
							{
								lblResults.setText(score + " out of " + alQuestionsOriginal.size() + " correct");
								this.getChildren().remove(0);
								this.getChildren().add(bpiResults);
							}
							else
							{
								cloneDeck();
								pootCurrentQuestion();
							}
						}
						else
						{
							pootCurrentQuestion();
						}
	}
	
	private void nextQuestion()
	{
		nextQuestion(true);
	}
	
	private void PootHint ()
	{
		String toPut = "";
		hintUnused = false;
		
		for (int a = 0; a < strAnswer.length(); a++)
		{
			if (Math.random() > 0.4)
			{
				toPut = toPut + strAnswer.charAt(a);
			}
			else
			{
				toPut = toPut + "#";
			}
		}
		
		lblHint.setText(toPut);
	}
	
	private void cloneDeck()
	{
		alQuestions.clear();
		
		
		for  (int a = 0; a < alQuestionsOriginal.size(); a++)
		{
			//The following line removes that stubborn newline 
			String toPoot = (Character.isWhitespace(alQuestionsOriginal.get(a).charAt(alQuestionsOriginal.get(a).length() - 1))) ? alQuestionsOriginal.get(a).substring(0,alQuestionsOriginal.get(a).length() - 1):alQuestionsOriginal.get(a);

			alQuestions.add(toPoot);
		}
		
		Collections.shuffle(alQuestions);
		
	}
	
	private void pootCurrentQuestion()
	{//Displays current question and puts current question
		String[] rawQuestion = SplitQuestion(alQuestions.get(0));
		rawQuestion = ScanForParms(rawQuestion);
		strQuestion = rawQuestion[0];
		strAnswer = rawQuestion[1];
		
			if (strAnswer.toLowerCase().indexOf("true") == 0)
			{
				strAnswer = "t";
			}
			if (strAnswer.toLowerCase().indexOf("false") == 0)
			{
				strAnswer = "f";
			}
			
		lblHint.setText("");
		
		
		//Decide Which Input here
		while (stkAnswerInput.getChildren().size() > 0)
		{
			stkAnswerInput.getChildren().remove(0);
		}
		
		
		if (rawQuestion.length > 2)
		{//Multiple choice 
			//Remember: inputMultiTray is inside inputMulti
			PurgeContainer(inputMultiTray);
			ArrayList<Button> btnRelay = new ArrayList<>();//Because shuffling the inputMultiTray.getChildren doesn't work
			
			double tempPass = rulePassMarkBase;
			for (int a = 1; a < rawQuestion.length; a++)
			{
				String pootAnswer = rawQuestion[a];
				
				
				if (a > 1 && CheckThisCore(rawQuestion[a],rawQuestion[1]) == 1.0){continue;}//If it's the question don't do anything.
				
				Button btnChoice = new Button(rawQuestion[a]);
				btnChoice.setOnAction(e -> SubmitAnswer(pootAnswer));
				btnChoice.getStyleClass().add("buttonSmaller");
				btnRelay.add(btnChoice);
			}
			

			
			Collections.shuffle(btnRelay);
			
			for (int b = 0; b < btnRelay.size(); b++)//Number and add the buttons to the inputMultiTray
			{
				btnRelay.get(b).setText(String.valueOf(b + 1)+": " +btnRelay.get(b).getText());
				inputMultiTray.getChildren().add(btnRelay.get(b));
			}
			
			stkAnswerInput.getChildren().add(inputMulti);
			lblHelp.setText(strMultiHelp);
			
		}
		
		else if ((strAnswer.indexOf("t") == 0 && strAnswer.length() == 1)|| (strAnswer.indexOf("f") == 0 && strAnswer.length() == 1))
		{
			System.out.println("DING");
			stkAnswerInput.getChildren().add(inputTF);
			lblHelp.setText(strTFHelp);
		}
		else if (strQuestion.indexOf("[FLIP]") != -1)
		{
			stkAnswerInput.getChildren().add(inputFlip);
			strQuestion = strQuestion.replace("[FLIP]", "");
			lblHelp.setText(strFlipHelp);
		}
		else//Is Type in
		{
			stkAnswerInput.getChildren().addAll(inputType); 
			//txtAnswer.setEffect(dsAnswerGlow);
			lblHelp.setText(strTextHelp);
		}
		
		setQuestionText (strQuestion);
		ReStapleEffect();
		
		
		//
		lblHint.setText("");
		focusSpammer = focusSpammerMax;
		
	}
	
	private void setQuestionText (String to)
	{//Use lblQuestion.text() through this to ensure text is sized properly
		lblQuestion.getStyleClass().remove("question");
		lblQuestion.getStyleClass().remove("smallQuestion");
		lblQuestion.getStyleClass().remove("extraSmallQuestion");
		lblQuestion.setText(to);
		if (to.length() >= 95){lblQuestion.getStyleClass().add("extraSmallQuestion");return;}
		if (to.length() >= 35){lblQuestion.getStyleClass().add("smallQuestion");return;}
		
		lblQuestion.getStyleClass().add("question");
		
	}
	//-------Classes
	private class KeyboardHandle implements EventHandler<KeyEvent>
	{
		@Override
		public void handle (KeyEvent e)
		{
			if (IsOnStage() == false || dblAlpha != 0){return;}//Is not on stage or flash is not done flashing yet.
			String testWord = e.getEventType().toString();
			String daKey = e.getCode().toString();
			
			if (daKey == "ESCAPE")
			{
				nextQuestion(false);
				return;
			}
			
			if (stkAnswerInput.getChildren().contains(inputType))
			{
				if (daKey == "ENTER" && txtAnswer.getText() != "")
				{
					SubmitAnswer(txtAnswer.getText());
				}
				return;
			}
			
			if (stkAnswerInput.getChildren().contains(inputFlip))
			{
				System.out.println("DING");
				if (daKey == "F")
				{

					
					if (lblQuestion.getText() == strQuestion)
					{
						setQuestionText(strAnswer);
					}
					else
					{
						setQuestionText(strQuestion);
					}
				}
				else if (daKey == "N")
				{
					if (hintUnused){score++;}
					nextQuestion();
				}
				return;
			}
			
			if (stkAnswerInput.getChildren().contains(inputTF))
			{
				if (daKey == "T")
				{
					SubmitAnswer("t");
				}
				if (daKey == "F")
				{
					SubmitAnswer("f");
				}
				
				return;
			}
			
			if (stkAnswerInput.getChildren().contains(inputMulti))
			{
				if (isNumber(daKey.replace("DIGIT",""))== false){return;} //Is not a number
				int daIndex = Integer.valueOf(daKey.replace("DIGIT","")) - 1;
				if (daIndex == -1 || daIndex >= inputMultiTray.getChildren().size())//Invalid index
				{
						return;
				}
				
				Button daButton = (Button)inputMultiTray.getChildren().get(daIndex);
				
				SubmitAnswer(daButton.getText().substring(daButton.getText().indexOf(":") + 1,daButton.getText().length()-1));
				
			}
			
			if (stkAnswerInput.getChildren().size() == 0)
			{
				if (daKey == "ENTER")
				{
					dblAlpha = 0.01;
					clrAnswerGlow = new Color(1,0,0,dblAlpha);
					dsAnswerGlow.setColor(clrAnswerGlow);
					bolNextQ = true;
				}
			}
			
		}
	}
	
	//-------Public Methods
	
	public boolean IsOnStage()
	{//Checks if this is on stage because I don't know if using "this" in a custom class will refer to the custom class or actually "this"
		return main.OnStage(this);
	}
	
	public void InputQuestions (ArrayList<String> toPut)
	{
		alQuestionsOriginal = toPut;
		
		cloneDeck();

		
		
	}
	
	@Override
	public void reset()
	{
		
		score = 0;
		hintUnused = true;
		hp = hpMax;
		bolNextQ = false;
		cloneDeck();
		//System.out.println(String.valueOf(alQuestionsOriginal) + "," + String.valueOf(alQuestions));
		
		if (this.getChildren().contains(bpiRoot) == false)
		{
			this.getChildren().remove(0);
			this.getChildren().add(bpiRoot);
		}
		pootCurrentQuestion();
		ReStapleEffect();//It seems the effect is removed every time the node is removed, putting it back here seems to work;
	
		
	}
	
	public void ReStapleEffect()
	{//Reapplies the effect to everything that could flash
		lblQuestion.setEffect(dsAnswerGlow);
		//txtAnswer.setEffect(dsAnswerGlow);
		btnFalse.setEffect(dsAnswerGlow);
		btnTrue.setEffect(dsAnswerGlow);
	}
	
	@Override
	public void cog()
	{
		
		
		if (focusSpammer > 0)//This makes sure that the right thing is focused on because requestingFocus elsewhere doesn't seem to work.
		{
			focusSpammer --;
			
			if (stkAnswerInput.getChildren().contains(inputType)) {txtAnswer.requestFocus();}
		}
		
		lblRemaining.setText(alQuestions.size() + " Remaining");
		lblHp.setText(heart+hp);
		
		
		if (dblAlpha == 0){return;}
		
		dblAlpha = Math.max(dblAlpha - dblAlphaStep, 0);
		
		if (dblAlpha != 0)
		{
			clrAnswerGlow = new Color(clrAnswerGlow.getRed(),clrAnswerGlow.getGreen(),clrAnswerGlow.getBlue(),dblAlpha);
			dsAnswerGlow.setColor(clrAnswerGlow);
		}
		
		if (bolNextQ && dblAlpha <= 0)
		{
			
			dsAnswerGlow.setColor(clrAnswerGlow);
			
				if (1 == 1)//this used to be hp == 3 for some reason
					{
						hp = hpMax;
						alQuestions.remove(0);
						txtAnswer.setText("");
						hintUnused = true;
						if (alQuestions.size() == 0)
						{
							if (ruleInfinity == false)
							{
								lblResults.setText(score + " out of " + alQuestionsOriginal.size() + " correct");
								this.getChildren().remove(0);
								this.getChildren().add(bpiResults);
							}
							else
							{
								cloneDeck();
								pootCurrentQuestion();
							}
						}
						else
						{
							pootCurrentQuestion();
						}
						
					}
					bolNextQ = false;
					
		}
		
		
	}
	

	
	public static String[] SplitQuestion (String raw)
	{
		String[] toSend = raw.replace("~","slkfjsdklfjslfjlf").replace("|","~").split("~");
		
		for (int a = 0; a < toSend.length; a++)
		{
			toSend[a] = toSend[a].replace("slkfjsdklfjslfjlf","~");
		}
		
		return toSend;
	}
	
	/**
	* Returns looks for parameters in a question, applies them and removes them.
	**/
	private String[] ScanForParms (String[] checkme)
	{
		
		ArrayList<String> fineToSend =  new ArrayList<String>();
		double newPass = rulePassMarkBase;
		
		//Loop through checkme
		for (int a = 0; a < checkme.length; a++)
		{
			if (a==0){continue};
			if (checkme[a].trim().replace(" ","").indexOf("PASS") == 0)//Look for a pass parameter
			{
				String rawNumber = checkme[a].trim().replace(" ","").replace("PASS","");
				
				try
				{
					newPass = Double.parseDouble(rawNumber);
					if (newPass != 0)
					{
						newPass = newPass/100;
					}
					
				}
				catch (NumberFormatException ex)
				{
					continue;
				}
				
				continue;
			}
			
			fineToSend.add(checkme[a]);
		}
		
		//Create and put doppleganger
		String[] toSend = new String[fineToSend.size()];
		
		for (int b = 0; b < fineToSend.size(); b++)
		{
			toSend[b] = fineToSend.get(b);
		}
		
		rulePassMark = newPass;
		return toSend;
	}
	
	public static void ReadArray (String[] toGrind)
	{
		for (int a = 0; a < toGrind.length; a++)
		{
			System.out.println(String.valueOf(a) + " : " + toGrind[a]);
		}
	}
	
	public void SetInfinite(boolean to)
	{
		ruleInfinity = to;
	}
	
	public void PurgeContainer (Pane grindme)
	{
		while (grindme.getChildren().size() > 0)
		{
			grindme.getChildren().remove(0);
		}
	}
	
	//-----------------Checkers---------------------
	public void SubmitAnswer(String Checkme)
	{
		if (CheckThis(strAnswer,Checkme,rulePassMark))//Is a pass
		{
			clrAnswerGlow = new Color(0,1,0,1);
			if (hintUnused){score++;}
			if (stkAnswerInput.getChildren().contains(inputType))
			{
				txtAnswer.setText(strAnswer);
			}
			bolNextQ = true;
			
						
			dsAnswerGlow.setColor(clrAnswerGlow);
		}
		else//Is not a pass
		{
			clrAnswerGlow = new Color(1,0,0,1);
			hp--;
			dsAnswerGlow.setColor(clrAnswerGlow);
			System.out.println("DING");
			
			if (hp == 0 || stkAnswerInput.getChildren().contains(inputTF))//Hp is 0 or they guessed wrong on a true/false question
			{
				hp = 0;
				lblQuestion.setText(strAnswer);
				
				//if (stkAnswerInput.getChildren().contains(inputType))
				stkAnswerInput.getChildren().remove(0);
			}
		}
		
		
		dblAlpha = 1.0;
		
	}
	
	public static boolean CheckThis (String a, String b, double pass)
	{
		String Araw = a.replace(", and ",",").replace(" and ",",").replace("\n","").replace(" ","").toLowerCase();
		String Braw = b.replace(", and ",",").replace(" and ",",").replace("\n","").replace(" ","").toLowerCase();;
		
		String[] Ahuh = Araw.split(",");
		String[] Bhuh = Braw.split(",");
		
		if (Ahuh.length != Bhuh.length){return false;}
		
		String[] A = Ahuh;
		String[] B = Bhuh;
		
		ArrayList<Double> scores = new ArrayList<>();
		
		
		if (B.length > A.length)
		{
			B = Ahuh;
			A = Bhuh;
		}
		
		for (int x = 0; x < B.length; x++)
		{
			ArrayList<Double> tempScores = new ArrayList<>();
			for (int y = 0; y < A.length; y++)
			{
				tempScores.add(CheckThisCore(A[y],B[x]));
				
			}
			scores.add((Double) returnHighest(tempScores));
		}
		
		Double score = 0.0;
	
		//best score
		score = (Double) returnLowest(scores);
		if (pass <= 0.0)
		{
			//System.out.println("Score: " + score);
			return true;
		}
		
		return score * (new Double(Math.min(Bhuh.length,Ahuh.length))/new Double(Math.max(Bhuh.length,Ahuh.length))) >= pass; 
	}
	
	public static double CheckThisCore(String a, String b)
	{//Assume they are already processed with no spaces
		if (a.equals(b)){return 1.0;}
		//if (a.charAt(0) != b.charAt(0)){return 0;}
		
		String A = a;
		String B = b;
		
		if (b.length() > a.length())
		{
			A = b;
			B = a;
		}
		
		double total = A.length();
		int score = A.length() - (A.length() - B.length());
		double scoreBack = 0.0;
		double lenDiff = A.length() - B.length();
		
		HashMap<Character,Integer> Achars = new HashMap<>();//Character along with how many instances are in it
		HashMap<Character,Integer> Bchars = new HashMap<>();
		ArrayList<Character> charlist = new ArrayList<Character>();//List of all characters in B
		
		for (int c = 0; c < A.length(); c++)
		{
			char daChar = A.charAt(c);
			if (Achars.get(daChar) == null)
			{
				Achars.put(daChar,1);
			}
			else
			{
				Achars.put(daChar,Achars.get(daChar) + 1);
			}
			
			if (c < B.length())
			{
					daChar = B.charAt(c);
					if (Bchars.get(daChar) == null)
					{
						
						charlist.add(daChar);
						
						Bchars.put(daChar,1);
					}
					else
					{
						Bchars.put(daChar,Bchars.get(daChar) + 1);
					}
			}
		}
		
		//System.out.println(charlist);
		
		for (int d = 0; d < charlist.size(); d++)
		{
			if (Achars.get(charlist.get(d)) != null && Bchars.get(charlist.get(d)) != null )
			{
				
				int toSubtract = Math.abs(Achars.get(charlist.get(d)) - Bchars.get(charlist.get(d)));
				score = score - toSubtract;
				
				
			}
			else if (Achars.get(charlist.get(d)) != null)
			{
				
				score = score - Achars.get(charlist.get(d));
			}
			else if (Bchars.get(charlist.get(d)) != null)
			{
				
				score = score - Bchars.get(charlist.get(d));
			}
			
		}
		
		return (score/total);
		
	}
	
	public static void swapIndex (int swappy1,int swappy2, ArrayList<String> in)
	{
		if (swappy1 < 0 || swappy2 < 0 || swappy1 > in.size() - 1 || swappy2 > in.size() - 1)
		{
			System.out.println("WON'T WORK");
		}
		
		String swap1 = in.get(swappy1);
		String swap2 = in.get(swappy2);
		
		in.set(swappy1, swap2);
		in.set(swappy2, swap1);
		
	}
	
	public static int indexOfPegleg(String pattern, String[] from)
	{//an indexOf for String[]

		for (int a = 0; a < from.length; a++)
		{
			//System.out.println(pattern + "," + from[a]);
			if (pattern == from[a])
			{
				return a;
			}
		}
		return -1;
	}
	

	public static Double returnHighest (ArrayList<Double> toGrind)
	{
		Double toSend = toGrind.get(0);
		
		if (toGrind.size() <= 1){return toSend;} 
		
		for (int a = 1; a < toGrind.size(); a++)
		{
			toSend = Math.max((Double) toSend,(Double) toGrind.get(a));
		}
		
		return toSend;
	}
	
	public static Double returnLowest (ArrayList<Double> toGrind)
	{
		Double toSend = toGrind.get(0);
		
		if (toGrind.size() <= 1){return toSend;} 
		
		for (int a = 1; a < toGrind.size(); a++)
		{
			toSend = Math.min((Double) toSend,(Double) toGrind.get(a));
		}
		
		return toSend;
	}
	
	public static boolean isNumber(String toCheck)
	{
		String numString = "0123456789-.";
		if (toCheck == "-" || toCheck == "."){return false;}
		if (toCheck.charAt(0) == '.' || (toCheck.indexOf("-") != -1 && toCheck.indexOf("-") != 0)){return false;}
		
		for (int a = 0; a < toCheck.length(); a++)
		{
			if (numString.indexOf(toCheck.charAt(a)) == -1){return false;}
		}
		
		return true;
	}
	
	
	
}