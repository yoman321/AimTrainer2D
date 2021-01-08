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
    ExecutorService executor = Executors.newFixedThreadPool(2);
    private DotsAnimation dotsAmimation = new DotsAnimation();
    
    
    //Start Trainer Button
    public void handleStartButton(){
       
        executor.execute(new DotsAnimationTask());
        executor.shutdown();
        
    
    }
    
    //Circle animation task
    private class DotsAnimationTask implements Runnable{
        @Override
        public void run(){
            try{
                Platform.runLater(() -> dotsAmimation.animationEffect());
                Thread.sleep(100);
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
        private double radius = 0;
        
        //Circle animation method
        public synchronized void animationEffect(){
            
            //Create circle
            double xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
            double yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);
            Circle circle = new Circle(xPos, yPos, radius);
            
            
            //Create animation
            new Thread(new Runnable() {
                @Override
                public void run(){
                    try{
                        while (radius < MAX_RADIUS){
                            radius += 0.3;
                            Platform.runLater(() -> circle.setRadius(radius));
                            Thread.sleep(50);
                        }
                        while (radius > 0){
                            radius -= 0.5;
                            Platform.runLater(() -> circle.setRadius(radius));
                            Thread.sleep(50);
                        }
                    }
                    catch(InterruptedException ex){
                        ex.getStackTrace();
                    }
                }
            }).start();
            
//            new Thread(new Runnable(){
//                @Override
//                public void run(){
//                    try{
//                        while (radius > 0){
//                            radius -= 0.5;
//                            Platform.runLater(() -> circle.setRadius(radius));
//                            Thread.sleep(50);
//                        }
//                    }
//                    catch(InterruptedException ex){
//                        ex.getStackTrace();
//                    }
//                }
//            }).start();
            
            pane.getChildren().add(circle);
            
//            FadeTransition ft = new FadeTransition(Duration.millis(3000), circle);
//            ft.setFromValue(0);
//            ft.setToValue(1);
//            ft.setCycleCount(Timeline.INDEFINITE);
//            ft.setAutoReverse(true); 
//            ft.play();
//            System.out.println("something");
            
          
        
            
        }
    }
}
