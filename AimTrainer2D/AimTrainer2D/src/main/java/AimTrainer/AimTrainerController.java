/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AimTrainer;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import java.util.concurrent.*;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.application.Platform;
/**
 *
 * @author luoph
 */
public class AimTrainerController{
    
    //Create variables
    @FXML private Pane pane;
    @FXML private Button button;
    ExecutorService executor = Executors.newFixedThreadPool(5);
    private DotsAnimation dotsAmimation = new DotsAnimation();

    
    //Start Trainer Button
    public void handleStartButton(){
        executor.execute(new DotsAnimationTask());
        
        executor.shutdown();
        
    
    }
    
    private class DotsAnimationTask implements Runnable{
        @Override
        public void run(){
            try{
                Platform.runLater(() -> dotsAmimation.animationEffect());
            }
            catch(Exception ex){
                ex.getStackTrace();
            }
        }
           
    }

    private class DotsAnimation{
        
        //Create datafield
        private Screen screen = Screen.getPrimary();
        private Rectangle2D bounds = screen.getVisualBounds();
        private final double MAX_RADIUS = 45;
        
        public synchronized void animationEffect(){
            double xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
            double yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);
            Circle circle = new Circle(xPos, yPos, MAX_RADIUS);
            
            
            FadeTransition ft = new FadeTransition(Duration.millis(3000), circle);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setCycleCount(Timeline.INDEFINITE);
            ft.setAutoReverse(true); 
            ft.play();
            System.out.println("something");
            
          
        
            pane.getChildren().add(circle);
        }
    }
}
