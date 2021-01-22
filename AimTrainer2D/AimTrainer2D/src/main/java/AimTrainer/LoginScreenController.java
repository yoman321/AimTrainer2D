/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package AimTrainer;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import static java.lang.System.out;
/**
 *
 * @author luoph
 */
public class LoginScreenController {
    
    //Create fxml variables
    @FXML private Text proceedText;
    @FXML private Pane loginScreenPane;
    
    //Create methods
    public void initialize() throws Exception{
        
        //Create animation 
        FadeTransition ft = new FadeTransition(Duration.millis(1000), proceedText);
        ft.setFromValue(0);
        ft.setToValue(1.0);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        
        //Enable pane change
        Parent nextPane = FXMLLoader.load(getClass().getResource(("AimTrainerFXML.fxml")));
        loginScreenPane.setOnMousePressed(e -> {
            Scene trainerScene = new Scene (nextPane);
        
            Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            stage.setScene(trainerScene);
            stage.show();
        });
    }
}
