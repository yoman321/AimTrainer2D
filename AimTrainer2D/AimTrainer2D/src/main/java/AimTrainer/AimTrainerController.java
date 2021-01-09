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
import static java.lang.System.out;
import java.util.ArrayList;
/**
 *
 * @author luoph
 */
public class AimTrainerController{
    
    //Create variables
    @FXML private Pane pane;
    int clickedCount = 0;
    int radiusArrayCount = 0;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private DotsAnimation dotsAmimation = new DotsAnimation();
    private ArrayList<Double> cordsArray = new ArrayList<>();
    
    //Start Trainer Button
    public void handleStartButton(){
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 2, TimeUnit.SECONDS);   
    }
    
    public void handleStopbutton(){
        executor.shutdownNow();
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
    //Create animation class
    private class DotsAnimation{
        
        //Create datafield
        private Screen screen = Screen.getPrimary();
        private Rectangle2D bounds = screen.getVisualBounds();
        private final double MAX_RADIUS = 45;
        private final double SUM_RADIUS = 90;
        private boolean avCords = false;
        double xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
        double yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);
        
        //Circle animation method
        public synchronized void animationEffect(){
            
            //Create targets cooridnates
            xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
            yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);

            
            
            //Check for overlapping circles
            while (avCords == false){
                avCords = true;
                for (int i=0, j=i+1; i<cordsArray.size(); i+=2){
                    double distance = Math.sqrt(Math.pow(xPos - cordsArray.get(i), 2) + Math.pow(yPos - cordsArray.get(j), 2));
                    
                    if (distance < SUM_RADIUS){
                        avCords = false;
                    }
                }
                xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
                yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);
            }
            //Add cords to array and create target
            cordsArray.add(xPos);
            cordsArray.add(yPos);
            final Circle circle = new Circle(xPos, yPos, 0);
            
            //Add target to pane
            pane.getChildren().add(circle);
            
            //Create animation
            new Thread(new Runnable() {
                @Override
                public void run(){
                    
                    try{
                        double radius = 0;
                        while (radius < MAX_RADIUS){
                            radius += 0.3;
                            out.println(radius); //test
                            final double finalRadius = radius;
                            Platform.runLater(() -> circle.setRadius(finalRadius));
                            Thread.sleep(50);
                        }
                        while (radius> 0){
                            radius -= 0.3;
                            final double finalRadius = radius;
                            Platform.runLater(() -> circle.setRadius(finalRadius));
                            Thread.sleep(50);
                        }
                        //Remove circle and cords if target shrink to 0
                        if (radius == 0){
                            pane.getChildren().remove(circle);
                            for (int i=0, j=i=1; i<cordsArray.size(); i+=2){
                                if (cordsArray.get(i)==xPos && cordsArray.get(j)==yPos){
                                    cordsArray.remove(i);
                                    cordsArray.remove(j);
                                }
                            }
                        }
                    }
                    catch(InterruptedException ex){
                        ex.getStackTrace();
                    }
                }
            }).start();
            
            //Make target disappear when clicked and increment clickedCount
//            circle.setOnMousePressed(e -> {
//                pane.getChildren().remove(circle);
//                clickedCount++;
//            });   
            
        }
        
    }
}
