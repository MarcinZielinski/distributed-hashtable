package pl.mrz;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage stage;
    private String channelName = "distribute";

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle("FLSocial");
        primaryStage.show();
        loadMainScene();
    }

    private void loadMainScene() {
        stage.setScene(new MainScene(new DistributedMap(channelName)).getScene());
    }
}
