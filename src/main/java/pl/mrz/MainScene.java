package pl.mrz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainScene {

    private Pane rootLayout;
    private MainSceneController controller;
    private Scene scene;
    private DistributedMap distributedMap;

    public MainScene(DistributedMap distributedMap) {
        this.distributedMap = distributedMap;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main_scene.fxml"));
            rootLayout = loader.load();
            controller = loader.getController();
            controller.setModel(this);
            scene = new Scene(rootLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Scene getScene() {
        return scene;
    }

    public DistributedMap getDistributedMap() {
        return distributedMap;
    }
}
