package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main Class of the apps contains launcher.
 */
public class Window extends Application{
    public Window(){
        super();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resources/coffee02.jpg")));
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/layout.fxml"));
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