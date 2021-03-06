package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.Serializable;

/**
 * Main Class of the apps contains launcher.
 */
public class Window extends Application implements Serializable{
    public Window(){
        super();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/coffee02.jpg")));
        Parent root = FXMLLoader.load(getClass().getResource("fxml/layout.fxml"));
        System.out.println(root);
        Scene scene = new Scene(root,1137,500);
        primaryStage.setTitle("Statki i Samoloty");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}