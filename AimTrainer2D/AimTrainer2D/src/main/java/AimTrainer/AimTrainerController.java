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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
/**
 *
 * @author luoph
 */
public class AimTrainerController{
    
    //Create FXML variables
    @FXML private Pane gamePane;
    @FXML private Text counter;
    @FXML private Pane btnPane;
    @FXML private Rectangle healthBar1;
    @FXML private Rectangle healthBar2;
    @FXML private Rectangle healthBar3;
    
    //Create controller variables
    private int clickedCount = 0;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private DotsAnimation dotsAnimation = new DotsAnimation();
    private ArrayList<Double> cordsArray = new ArrayList<>();
    private boolean running = true;
    private int lifeCount = 3;
    
    //Initialize 
    public void initialize(){
        gamePane.setVisible(false);
    }
    
    //Trainer modes buttons
    public void handleEasyBtn(){
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);   
        startGame();
        btnPaneToGamePane();
    }
    public void handleMediumBtn(){
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);  
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);   
        startGame();
        btnPaneToGamePane();
    }
    public void handleHardBtn(){
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);   
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, 1, TimeUnit.SECONDS);
        startGame();
        btnPaneToGamePane();
    }
    //Stop trainer button
    public void handleStopBtn(){     
        running = false;
        gamePaneToBtnPane();
        endGame();
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
        private final double MAX_RADIUS = 70;
        private final double SUM_RADIUS = 90;
        private boolean ovCords = true;
        private double xPos = 0;
        private double yPos = 0;
        private long speed = 5;
        
        //aimTrainerTarget animation method
        public synchronized void animationEffect(){
             
            //Create targets cooridnates
            xPos = ThreadLocalRandom.current().nextDouble(45, gamePane.getPrefWidth()-45);
            yPos = ThreadLocalRandom.current().nextDouble(45, gamePane.getPrefHeight()-45);

            
            
            //Check for overlapping targets
            while (ovCords){
                ovCords = false;
                for (int i=0, j=i+1; i<cordsArray.size(); i+=2, j+=2){
                    double distance = Math.sqrt(Math.pow(xPos - cordsArray.get(i), 2) + Math.pow(yPos - cordsArray.get(j), 2));
                    
                    if (distance < SUM_RADIUS){
                        ovCords = true;
                        xPos = ThreadLocalRandom.current().nextDouble(45, gamePane.getPrefWidth()-45);
                        yPos = ThreadLocalRandom.current().nextDouble(45, gamePane.getPrefHeight()-45);
                    }
                    else{
                        //Add cords to array and create target
                        cordsArray.add(xPos);
                        cordsArray.add(yPos);
                    }
                }
            }
            
            Image aimTrainerImage = new Image("/images/AimTrainerTarget.png");
            final ImageView aimTrainerTarget = new ImageView(aimTrainerImage);
            aimTrainerTarget.setFitWidth(0.001);
            aimTrainerTarget.setFitHeight(0.001);
            aimTrainerTarget.setX(xPos);
            aimTrainerTarget.setY(yPos);
            
            
            //Add target to pane
            gamePane.getChildren().add(aimTrainerTarget);
            
            //Create animation
            
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run(){     
                    try{
                        Thread thread = new Thread();
                        double x = 0;
                        double y = 0;
                        while ((x < MAX_RADIUS && y <MAX_RADIUS) && running && lifeCount!=0){
                            x += 0.2121;
                            y += 0.2121;
                            final double finalX = x;
                            final double finalY = y;
                            Platform.runLater(() -> aimTrainerTarget.setFitWidth(finalX));
                            Platform.runLater(() -> aimTrainerTarget.setFitHeight(finalY));
                            Thread.sleep(speed);
                        }
                        while ((x > 0.3 && y > 0.3) && running && lifeCount!=0){
                            x -= 0.2121;
                            y -= 0.2121;
                            final double finalX = x;
                            final double finalY = y;
                            Platform.runLater(() -> aimTrainerTarget.setFitWidth(finalX));
                            Platform.runLater(() -> aimTrainerTarget.setFitHeight(finalY));
                            Thread.sleep(speed);
                            
                        }
                        //If stop btn is clicked
                        if (!running){
                            thread.interrupt();
                            Platform.runLater(() -> gamePane.getChildren().remove(aimTrainerTarget));
                            
                        }
                        if (lifeCount == 0){
                            
                            thread.interrupt();
                            Platform.runLater(() -> gamePane.getChildren().remove(aimTrainerTarget));
                            endGame();
                            gamePaneToBtnPane();
                        }
                        //Remove aimTrainerTarget and cords if target shrink to 0 and remove health bars
                        if (x <= 0.3 || y <= 0.3){
                            lifeCount--;      
                            if (lifeCount == 2){
                                Platform.runLater(() -> healthBar3.setFill(Color.BLACK));
                            }
                            if (lifeCount == 1){
                                Platform.runLater(() -> healthBar2.setFill(Color.BLACK));
                            }
                            if (lifeCount == 0){
                                Platform.runLater(() -> healthBar1.setFill(Color.BLACK));
                            }
                            
                            Platform.runLater(() -> gamePane.getChildren().remove(aimTrainerTarget));
                            for (int i=0, j=i=1; i<cordsArray.size(); i+=2){
                                if (cordsArray.get(i)==xPos && cordsArray.get(j)==yPos){
                                    cordsArray.remove(i);
                                    cordsArray.remove(j);
                                }
                            }
                        }
                        //Check if stop button has been clicked
                    }
                    catch(InterruptedException ex){
                        ex.getStackTrace();
                    }
                    
                }
            });
            thread.start();
            
            //Make target disappear when clicked and increment clickedCount
            aimTrainerTarget.setOnMousePressed(e -> {
                gamePane.getChildren().remove(aimTrainerTarget);
                clickedCount++;
                thread.interrupt();
                
                counter.setText(String.valueOf(clickedCount));
            });   
        }
    }
    //Rotate panes
    public void btnPaneToGamePane(){
        btnPane.setVisible(false);
        gamePane.setVisible(true);
    }
    public void gamePaneToBtnPane(){
         btnPane.setVisible(true);
        gamePane.setVisible(false);
    }
    //Start game tasks
    public void startGame(){
        running = true;
        counter.setText(String.valueOf(0));
        lifeCount = 3;
        clickedCount = 0;
    }
    //End threads after each game
    public void endGame(){
        executor.shutdown();
        executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        Platform.runLater(() -> healthBar1.setFill(Color.RED));
        Platform.runLater(() -> healthBar2.setFill(Color.RED));
        Platform.runLater(() -> healthBar3.setFill(Color.RED));
    }
}