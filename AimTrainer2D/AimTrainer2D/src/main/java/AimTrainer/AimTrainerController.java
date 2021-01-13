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
/**
 *
 * @author luoph
 */
public class AimTrainerController{
    
    //Create FXML variables
    @FXML private Pane pane;
    @FXML private TextField counter;
    @FXML private Pane btnPane;
    @FXML private Button stopBtn;
    
    //Create controller variables
    private int clickedCount = 0;
    private int radiusArrayCount = 0;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ExecutorService stopExecutor = Executors.newFixedThreadPool(1);
    private DotsAnimation dotsAnimation = new DotsAnimation();
    private ArrayList<Double> cordsArray = new ArrayList<>();
    private boolean running = true;
    private int threadDelayTime = 0;
    
    //Initialize 
    public void initialize(){
        counter.setEditable(false);
        stopBtn.setVisible(false);
    }
    
    //Trainer modes buttons
    public void handleEasyBtn(){
        threadDelayTime = 3;
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, threadDelayTime, TimeUnit.SECONDS);   
        btnPane.setVisible(false);
        stopBtn.setVisible(true);
    }
    public void handleMediumBtn(){
        threadDelayTime = 2;
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, threadDelayTime, TimeUnit.SECONDS);   
        btnPane.setVisible(false);
        stopBtn.setVisible(true);
    }
    public void handleHardBtn(){
        threadDelayTime = 1;
        executor.scheduleWithFixedDelay(new DotsAnimationTask(), 0, threadDelayTime, TimeUnit.SECONDS);   
        btnPane.setVisible(false);
        stopBtn.setVisible(true);
    }
    
    //Stop trainer button
    public void handleStopBtn(){
        out.println("something");//test
        running = false;
        btnPane.setVisible(true);
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
        
        //aimTrainerTarget animation method
        public synchronized void animationEffect(){
            
            //Create targets cooridnates
            xPos = ThreadLocalRandom.current().nextDouble(45, bounds.getWidth()-45);
            yPos = ThreadLocalRandom.current().nextDouble(45, bounds.getHeight()-45);

            
            
            //Check for overlapping aimTrainerTarget
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
            
            Image aimTrainerImage = new Image("/images/AimTrainerTarget.png");
            final ImageView aimTrainerTarget = new ImageView(aimTrainerImage);
            aimTrainerTarget.setFitWidth(0.001);
            aimTrainerTarget.setFitHeight(0.001);
            aimTrainerTarget.setX(xPos);
            aimTrainerTarget.setY(yPos);
            
            
            //Add target to pane
            pane.getChildren().add(aimTrainerTarget);
            
            //Create animation
            thread = new Thread(new Runnable() {
                @Override
                public void run(){     
                    try{
                        double x = 0;
                        double y = 0;
                        while ((x < MAX_RADIUS && y <MAX_RADIUS) && running){
                            x += 0.2121;
                            y += 0.2121;
                            final double finalX = x;
                            final double finalY = y;
                            Platform.runLater(() -> aimTrainerTarget.setFitWidth(finalX));
                            Platform.runLater(() -> aimTrainerTarget.setFitHeight(finalY));
                            Thread.sleep(35);
                        }
                        while ((x > 0.3 && y > 0.3) && running){
                            x -= 0.2121;
                            y -= 0.2121;
                            final double finalX = x;
                            final double finalY = y;
                            Platform.runLater(() -> aimTrainerTarget.setFitWidth(finalX));
                            Platform.runLater(() -> aimTrainerTarget.setFitHeight(finalY));
                            Thread.sleep(35);
                            
                            //Remove target if x = y = 0
//                            if (x==1 || y==1){
//                                pane.getChildren().remove(aimTrainerTarget);
//                                for (int i=0, j=i=1; i<cordsArray.size(); i+=2){
//                                if (cordsArray.get(i)==xPos && cordsArray.get(j)==yPos){
//                                    cordsArray.remove(i);
//                                    cordsArray.remove(j);
//                                }
//                            }
//                            }
                        }
                        //Check if stop button has been clicked
                        if (!running){
                            thread.interrupt();
                            out.println("interrupt");
                        }
                        //Remove aimTrainerTarget and cords if target shrink to 0
                        if (x <= 0.3 || y <= 0.3){
                            out.println("exit");
                            Platform.runLater(() -> pane.getChildren().remove(aimTrainerTarget));
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
            aimTrainerTarget.setOnMousePressed(e -> {
                pane.getChildren().remove(aimTrainerTarget);
                clickedCount++;
                counter.setText(String.valueOf(clickedCount));
            });   
            
        }
        
    }
}
