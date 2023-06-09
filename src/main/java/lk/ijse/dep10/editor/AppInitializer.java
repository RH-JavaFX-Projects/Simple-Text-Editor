package lk.ijse.dep10.editor;

import javafx.application.Application;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep10.editor.controller.EditorSceneController;

import java.io.IOException;
import java.net.URL;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL fxmlFile = getClass().getResource("/view/EditorScene.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlFile);
        AnchorPane root = fxmlLoader.load();
        EditorSceneController ctrl = fxmlLoader.getController();


        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Untitled Document 1");
        primaryStage.show();
        primaryStage.centerOnScreen();

        SimpleStringProperty simpleStringProperty = new SimpleStringProperty(primaryStage.getTitle());
        primaryStage.titleProperty().bind(simpleStringProperty);
        ctrl.initData(simpleStringProperty);


    }
}
