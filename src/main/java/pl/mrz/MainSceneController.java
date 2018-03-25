package pl.mrz;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class MainSceneController {

    @FXML
    public ListView<String> mapListView;
    public TextField keyTextField;
    public TextField valueTextField;
    public Button putButton;
    public Button removeButton;
    public Button getButton;
    public Label getLabel;
    public TextField containsKeyTextField;
    public Label containsKeyLabel;


    private MainScene model;
    private ObservableMap<String, String> map;

    @FXML
    public void initialize() {
        putButton.disableProperty().bind(keyTextField.textProperty().isEmpty().or(valueTextField.textProperty().isEmpty()));
        removeButton.disableProperty().bind(mapListView.getSelectionModel().selectedItemProperty().isNull());
        getButton.disableProperty().bind(mapListView.getSelectionModel().selectedItemProperty().isNull());
    }

    public void setModel(MainScene model) {
        this.model = model;
        map = model.getDistributedMap().getHashMap();
        mapListView.getItems().addAll(map.keySet());
        map.addListener((MapChangeListener.Change<? extends String, ? extends String> c) -> {
            if (c.wasAdded()) {
                Platform.runLater(() -> mapListView.getItems().add(c.getKey()));
            }
            if (c.wasRemoved()) {
                Platform.runLater(() -> mapListView.getItems().remove(c.getKey()));
            }
        });
    }

    public void putClicked(ActionEvent actionEvent) {
        putItem();
    }

    public void removeClicked(ActionEvent actionEvent) {
        model.getDistributedMap().remove(mapListView.getSelectionModel().getSelectedItem());
    }

    public void getClicked(ActionEvent actionEvent) {
        getLabel.setText(model.getDistributedMap().get(mapListView.getSelectionModel().getSelectedItem()));
    }

    public void enterHandle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER && !putButton.isDisabled()) {
            putItem();
        }
    }

    private void putItem() {
        model.getDistributedMap().put(keyTextField.getText(), valueTextField.getText());
        keyTextField.clear();
        valueTextField.clear();
    }

    public void containsKeyTyped(KeyEvent keyEvent) {
        containsKeyLabel.setText(map.containsKey(containsKeyTextField.getText()) ? "Yes" : "No");
    }
}
