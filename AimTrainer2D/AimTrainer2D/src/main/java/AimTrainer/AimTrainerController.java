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
import java.util.concurrent.atomic.AtomicBoolean;
/**
 *
 * @author luoph
 */
public class AimTrainerController{
    
    //Create variables
    @FXML private Pane pane;
    private int clickedCount = 0;
    private int radiusArrayCount = 0;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ExecutorService stopExecutor = Executors.newFixedThreadPool(1);
    private DotsAnimation dotsAnimation = new DotsAnimation();
    private ArrayList<Double> cordsArray = new ArrayList<>();
    private boolean running = true;
    
    //Start trainer Button
    public void handleStartButton(){

        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 5, TimeUnit.SECONDS);   
    }
    
    //Stop trainer button
    public void handleStopButton(){
        out.println("something");//test
        running = false;
        executor.shutdownNow();
    }
    
    //Circle animation task
    private class DotsAnimationTask implements Runnable{
        @Override
        public void run(){
            try{
                Platform.runLater(() -> dotsAnimation.animationEffect());
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
        private Thread thread = new Thread();
        
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
            thread = new Thread(new Runnable() {
                @Override
                public void run(){     
                    try{
                        double radius = 0;
                        while ((radius < MAX_RADIUS) && running){
                            radius += 0.3;
                            out.println(radius); //test
                            final double finalRadius = radius;
                            Platform.runLater(() -> circle.setRadius(finalRadius));
                            Thread.sleep(50);
                        }
                        while ((radius > 0) && running){
                            radius -= 0.3;
                            final double finalRadius = radius;
                            Platform.runLater(() -> circle.setRadius(finalRadius));
                            Thread.sleep(50);
                        }
                        //Check if stop button has been clicked
                        if (!running){
                            thread.interrupt();
                            out.println("interrupt");
                        }
                        //Remove circle and cords if target shrink to 0
                        if (radius <= 0){
                            out.println("exit");
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
            });
            thread.start();
            
            //Make target disappear when clicked and increment clickedCount
            circle.setOnMousePressed(e -> {
                pane.getChildren().remove(circle);
                clickedCount++;
            });   
            
        }
        
    }
}
