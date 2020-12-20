package client.gui;

import client.controller.ConcentrationController;
import client.model.ConcentrationModel;
import client.model.Observer;
import common.ConcentrationException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.*;

/**
 * A JavaFX GUI client for the network concentration game.  It represent the "View"
 * component of the MVC architecture in use here.
 *
 * @author Huong Vu htv5447
 */
public class ConcentrationGUI extends Application implements Observer<ConcentrationModel, ConcentrationModel.CardUpdate> {
    private ConcentrationModel model;
    private ConcentrationController controller;
    private myButton[][] buttons;
    private TextField[] texts;
    private final static int TEXT_FIELD_SIZE = 80;

    /**
     * Constructor of ConcentrationGUI
     */
    public ConcentrationGUI(){}

    /**
     * Represents each button in view
     */
    private static class myButton extends Button{
        private HashMap<String, Image> map = new HashMap<>();
        private int row;
        private int col;
        private ConcentrationGUI gui;

        /**
         * Constructor of the button
         * @param row of button
         * @param col of button
         * @param gui
         */
        public myButton(int row, int col, ConcentrationGUI gui){
            this.row = row;
            this.col = col;
            this.gui = gui;
        }

        /**
         * map the letter of card and pokemon images
         */
        public void create_map(){
            this.map.put("pokeball",new Image(getClass().getResourceAsStream("images/pokeball.png")));
            this.map.put("A",new Image(getClass().getResourceAsStream("images/abra.png")));
            this.map.put("B", new Image(getClass().getResourceAsStream("images/bulbasaur.png")));
            this.map.put("C", new Image(getClass().getResourceAsStream("images/charizard.png")));
            this.map.put("D",new Image(getClass().getResourceAsStream("images/diglett.png")));
            this.map.put("E",new Image(getClass().getResourceAsStream("images/golbat.png")));
            this.map.put("F",new Image(getClass().getResourceAsStream("images/golem.png")));
            this.map.put("G",new Image(getClass().getResourceAsStream("images/snorlak.png")));
            this.map.put("H",new Image(getClass().getResourceAsStream("images/jigglypuff.png")));
            this.map.put("I",new Image(getClass().getResourceAsStream("images/magikarp.png")));
            this.map.put("J",new Image(getClass().getResourceAsStream("images/meowth.png")));
            this.map.put("K", new Image(getClass().getResourceAsStream("images/mewtwo.png")));
            this.map.put("L",new Image(getClass().getResourceAsStream("images/natu.png")));
            this.map.put("M",new Image(getClass().getResourceAsStream("images/pidgey.png")));
            this.map.put("N",new Image(getClass().getResourceAsStream("images/pikachu.png")));
            this.map.put("O",new Image(getClass().getResourceAsStream("images/poliwag.png")));
            this.map.put("P",new Image(getClass().getResourceAsStream("images/psyduck.png")));
            this.map.put("Q",new Image(getClass().getResourceAsStream("images/rattata.png")));
            this.map.put("R",new Image(getClass().getResourceAsStream("images/slowpoke.png")));
        }

        /**
         * check if the card is hidden or not
         * display and update the button if it is clicked
         * @param model
         */
        public void updateButton(ConcentrationModel model){
            if(model.isHidden(this.row,this.col)){
                this.setGraphic(new ImageView(this.map.get("pokeball")));
                this.setOnAction(event-> this.gui.controller.revealCard(this.row,this.col));
            }
            else{
                this.setGraphic(new ImageView(this.map.get(model.getCard(this.row,this.col))));
                this.setOnAction(null);
            }
        }
    }

    /**
     * Non-GUI initializations occurs here such as model, controller, array of buttons and textfields
     */
    @Override
    public void init() throws ConcentrationException {
        List<String> args = getParameters().getRaw();
        // get host and port from command line
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        // create the model, and add ourselves as an observer
        this.model = new ConcentrationModel();
        this.model.addObserver(this);
        // initiate the controller
        this.controller = new ConcentrationController(host,port,this.model);
    }

    /**
     * GUI-related initializations and setup. Create a borderpane with 3 parts: title, gridpane and borderpane
     * @param stage
     */
    @Override
    public void start(Stage stage){
        // initiate array of buttons and texts
        this.buttons = new myButton[this.model.getDIM()][this.model.getDIM()];
        this.texts = new TextField[3];
        BorderPane border = new BorderPane();
        // bottom
        border.setBottom(this.makeBorderPane());
        //center
        border.setCenter(this.makeGridPane(this.model));
        stage.setTitle("Concentration GUI");
        stage.setScene(new Scene(border));
        stage.show();

    }

    /**
     * update the moves, matches and status
     */
    private void updateTexts() {
        if (this.texts != null) {
            this.texts[0].setText("Moves" + this.model.getNumMoves());
            this.texts[1].setText("Matches" + this.model.getNumMatches());
            this.texts[2].setText(this.model.getStatus() + "");
        }
    }

    /**
     * Create bottom part of scene
     * @return BorderPane at bottom of scene
     */
    private BorderPane makeBorderPane(){
        BorderPane bottom = new BorderPane();
        TextField moves= new TextField("Moves: " + this.model.getNumMoves());
        this.texts[0]= moves;
        this.texts[0].setPrefWidth(TEXT_FIELD_SIZE);
        this.texts[0].setAlignment(Pos.CENTER_LEFT);
        this.texts[0].setEditable(false);
        bottom.setLeft(moves);
        TextField matches = new TextField("Matches: " + this.model.getNumMatches());
        this.texts[1]= matches;
        this.texts[1].setPrefWidth(TEXT_FIELD_SIZE);
        this.texts[1].setAlignment(Pos.CENTER);
        this.texts[1].setEditable(false);
        bottom.setCenter(matches);
        TextField status = new TextField(this.model.getStatus()+"");
        this.texts[2]=status;
        this.texts[2].setPrefWidth(TEXT_FIELD_SIZE);
        this.texts[2].setAlignment(Pos.CENTER_LEFT);
        this.texts[2].setEditable(false);
        bottom.setRight(status);
        return bottom;
    }

    /**
     * Create a gridpane with buttons
     * @param model
     * @return GridPane at center of scene
     */
    private GridPane makeGridPane(ConcentrationModel model) {
        GridPane gridPane = new GridPane();
        for (int row=0; row< this.model.getDIM(); ++row) {
            for (int col=0; col<this.model.getDIM(); ++col) {
                    myButton ball_button = new myButton(row,col,this);
                    ball_button.create_map();
                    ball_button.updateButton(model);
                    gridPane.add(ball_button, row,col);
                    this.buttons[row][col] = ball_button;
            }
        }
        return gridPane;
    }

    /**
     * The observed subject calls this method on each observer that has previously registered with it.
     * @param model
     * @param card
     */
    @Override
    public void update(ConcentrationModel model, ConcentrationModel.CardUpdate card) {
        //Check the status of the model
        // if good and ready than update
        if (model.getStatus() == ConcentrationModel.Status.OK) {
            if (card != null) {
                Platform.runLater(() -> {this.buttons[card.getRow()][card.getCol()].updateButton(model); this.updateTexts();});
            }
            else{
                Platform.runLater(this::updateTexts);
            }
        }
    }

    /**
     * Ask controller to close
     */
    @Override
    public void stop() {
        this.controller.close();
    }

    /**
     * Main method to launch the application
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ConcentrationGUI host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
