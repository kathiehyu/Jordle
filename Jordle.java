import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.event.ActionEvent;
// import javafx.scene.input.KeyEvent;
// import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
// import javafx.event.Event;
import javafx.geometry.Insets;
// import javafx.scene.text.Font;
import javafx.scene.Node;
import java.util.ArrayList;


/**
 * class to create Wordle.
 * @author Kathie Huynh
 * @version JDK 11.0.13
 */
public class Jordle extends Application {
    //the correct word
    private static String word = Words.list.get((int) (Math.random() * 29)).toUpperCase();

    //letters in the correct word
    private static ArrayList<String> letters;

    //ArrayList of the StackPanes in the grid
    private static ArrayList<StackPane> grids = new ArrayList<>();

    //the index of the user's current grid (before input)
    private static int index = 0;

    //VBox containing all the nodes on the wordle
    private static VBox vb = new VBox(15);

    //GridPane of boxes
    private static GridPane gp;

    // private static boolean changed = false;

    //whether the last row has been submitted or not
    private static boolean enter = false;

    //the number of correct letters the user guessed on this row
    private static int correct = 0;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Jordle");

        letters = new ArrayList<>();
        for (int i = 0;  i < 5; i++) {
            letters.add(Character.toString(word.charAt(i)));
        }

        vb.setPadding(new Insets(50));
        vb.setAlignment(Pos.CENTER);

        //title
        Label tit = new Label("Jordle");
        vb.getChildren().add(tit);

        //GRIDPANE
        gp = boxes();
        displayLetter(gp);

        gp.setAlignment(Pos.CENTER);
        gp.setHgap(10);
        gp.setVgap(10);
        vb.getChildren().add(gp);

        //test gridpane

        //HBox
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        vb.getChildren().add(hb);

        // label
        Label status = new Label("Try guessing a word!");
        hb.getChildren().add(status);


        //restart button
        // Button restart = restartButton();
        // restart.setFocusTraversable(false);
        // hb.getChildren().add(restart);


        //instructions button
        hb.getChildren().add(instructionsButton());

        Scene scene = new Scene(vb, 550, 600);
        stage.setScene(scene);
        System.out.println("here");
        stage.show();

        gp.getChildren().get(0).requestFocus();
    }

    /**
     * creates the grids in the wordle.
     * @return the GridPane that holds the grid
     */
    private static GridPane boxes() {
        GridPane gridp = new GridPane();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane sp = new StackPane();
                Rectangle r = grid();
                sp.getChildren().add(r);

                sp.setFocusTraversable(false);

                grids.add(sp);
                gridp.add(sp, j, i);
            }
        }
        for (StackPane s: grids) {
            addKeyEvents(s);
        }
        return gridp;
    }

    /**
     * adds the KeyEvents to a StackPane (grid) in the GridPane.
     * @param sp the StackPane to add the KeyEvent to
     */
    private static void addKeyEvents(StackPane sp) {
        int ind = grids.indexOf(sp);
        if (ind % 5 == 0) { //why does it do every stackpane?
            firstColumn(sp);
        }
    }

    /**
     * sets the KeyEvent of the StackPane.
     * @param sp the StackPane's KeyEvent to set.
     */
    private static void firstColumn(StackPane sp) {
        StackPane thisLetter = sp;
        int ind = grids.indexOf(thisLetter);
        StackPane nextLetter = grids.get(ind + 1);

        //ENTER: ALERT, LETTER: NEXT BOX
        thisLetter.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && index % 5 == 0 && index != 0) {
                StackPane s = grids.get(0);

                for (int i = 0; i < 5; i++) {
                    String correctLetter = Jordle.letters.get(i);
                    StackPane stack = grids.get(index - 5 + i);
                    String userLetter = (((Text) (stack.getChildren().get(1))).getText());
                    if (correctLetter.equals(userLetter)) {
                        ((Rectangle) (stack.getChildren().get(0))).setFill(Color.GREEN);
                        correct++;
                    } else if (letters.contains(userLetter)) {
                        ((Rectangle) (stack.getChildren().get(0))).setFill(Color.GOLD);
                    } else {
                        ((Rectangle) (stack.getChildren().get(0))).setFill(Color.GRAY);
                    }
                    enter = true;
               
                    if (correct == 5) {
                        HBox h = (HBox) (vb.getChildren().get(2));
                        Label status = (Label) (h.getChildren().get(0));
                        status.setText("Congratulations! You've guessed the word!");
                    } else if (index >= 29 && correct < 5) {
                        HBox h = (HBox) (vb.getChildren().get(2));
                        Label status = (Label) (h.getChildren().get(0));
                        status.setText("Game Over. The word was " + word.toLowerCase() + ".");
                    }
                }
                if (correct != 5) {
                    correct = 0;
                }
            } else if (e.getCode() == KeyCode.ENTER) {
                displayAlert();
            }
        });
    }

    /**
     * creates base KeyEvent to display letters when the user inputs them.
     * @param gp the GridPane to take in the user's letters
     */
    private static void displayLetter(GridPane gridp) {
        StackPane thisLetter = grids.get(index);
        gridp.setOnKeyPressed(e -> {
            if (index <= 29 && e.getCode().isLetterKey() && e.getCode() != KeyCode.TAB && correct != 5) {
                StackPane s = (StackPane) (gridp.getChildren().get(index));

                if (index % 5 == 0 && index != 0
                    && (((StackPane) (gridp.getChildren().get(index - 1))).getChildren()).size()
                    == 2 && !enter) {
                    Stage stage = new Stage();
                    stage.setTitle("Invalid Entry!");

                    Label alert = new Label("You can only enter 5-letter words.\nHit Enter key to submit.");
                    BorderPane b = new BorderPane(alert);
                    Scene sc = new Scene(b, 300, 200);
                    stage.setScene(sc);
                    stage.show();

                } else {
                    Text letter = new Text(e.getText().toUpperCase());
                    s.getChildren().add(letter);
                    index++;
                    if (thisLetter.getChildren().size() > 1) {
                        enter = false;
                    }
                }
            } else if (index != 0 && e.getCode() == KeyCode.BACK_SPACE) {
                if (enter && index % 5 == 0) {
                    //do nothing
                } else {
                    StackPane previous = grids.get(index - 1);
                    if (previous.getChildren().size() >= 2) {
                        Node n = previous.getChildren().remove(1);
                        if (index % 5 == 0) {
                            enter = true;
                        }
                        if (n != null) {
                            index--;
                        }
                    } else {
                        if (previous.getChildren().size() >= 2) {
                            Node n = previous.getChildren().remove(1);
                            if (n != null) {
                                index--;
                            }
                        }
                    }
                    enter = false;
                }
                if (index % 5 == 0) {
                    enter = true;
                }
            }
        });
    }

    /**
     * displays an alert telling the user that they must enter a 5-letter word.
     */
    private static void displayAlert() {
        Stage alert = new Stage();
        alert.setTitle("Invalid Guess!");

        Label alertMessage = new Label("You must enter a 5-letter word!");
        BorderPane bp = new BorderPane(alertMessage);
        Scene s = new Scene(bp, 200, 150);

        alert.setScene(s);
        alert.show();
    }

    /**
     * creates the rectangles that serve as the background of the grids.
     * @return a Rectangle to use as the background of the grid
     */
    private static Rectangle grid() {
        Rectangle r = new Rectangle();
        r.setWidth(60);
        r.setHeight(60);
        r.setFill(Color.WHITE);
        r.setStroke(Color.GRAY);
        return r;
    }

    /**
     * creates a button to restart the game.
     * @return a Button that will restart the game
     */
    // private static Button restartButton() {
    //     Button restart = new Button("Restart");
    //     // restart.setOnAction(new EventHandler<ActionEvent>() {
    //     //     public void handle(ActionEvent e) {
    //     //         //for each StackPane in the grid
    //     //         for (int i = 0; i < grids.size(); i++) {
    //     //             StackPane sp = grids.get(i);
    //     //             ((Rectangle) (sp.getChildren().get(0))).setFill(Color.WHITE);
    //     //             //remove the text
    //     //             if (sp.getChildren().size() > 1) {
    //     //                 sp.getChildren().remove(1);
    //     //             } //unnessecary actions ?
    //     //         }
    //     //         ((Label) ((HBox) (vb.getChildren().get(2))).getChildren().get(0)).setText("Try guessing a word!");
    //     //         word = Words.list.get((int) (Math.random() * 29)).toUpperCase();
    //     //         letters = new ArrayList<>();
    //     //         for (int i = 0;  i < 5; i++) {
    //     //             letters.add(Character.toString(word.charAt(i)));
    //     //         }
    //     //         System.out.println(letters);
    //     //         index = 0;
    //     //         enter = false;
    //     //         gp.getChildren().clear();
    //     //         gp = boxes();
    //     //         gp.setAlignment(Pos.CENTER);
    //     //         gp.setHgap(10);
    //     //         gp.setVgap(10);
    //     //         displayLetter(gp);
    //     //         for (Node s: gp.getChildren()) {
    //     //             addKeyEvents((StackPane) s);
    //     //         }
    //     //         vb.getChildren().add(1, gp);
    //     //         correct = 0;
    //     //         gp.requestFocus();

    //     //         // GridPane gp = ((GridPane) (vb.getChildren().get(1)));
    //     //         //??
    //     //     }
    //     // });
    //     return restart;
    // }

    /**
     * creates a button to show the instructions.
     * @return a Button that displays the instructions
     */
    private static Button instructionsButton() {
        Button instructions = new Button("Instructions");
        instructions.setFocusTraversable(false);
        instructions.setOnAction(e -> {
            Stage inst = new Stage();
            inst.setTitle("How to Play Jordle");

            String i = "Use your keyboard to guess a 5-letter word, then press Enter.\nYou have 6 guesses.\n"
                + "Each guess corresponds to one row on the grid.\nIn your guess, if there is a letter\n"
                + "that is in the correct word, but in the wrong column,\nthat block will turn gold. If the letter\n"
                + "you guessed is correct and in the right place, it will turn green."
                + "\nLetters you guessed that are not"
                + "in the correct word will turn grey.\nHave fun!";
            Text in = new Text(i);
            BorderPane bp = new BorderPane(in);
            bp.setPadding(new Insets(50));

            Scene s = new Scene(bp, 500, 400);
            inst.setScene(s);
            inst.show();
        });
        return instructions;
    }
}