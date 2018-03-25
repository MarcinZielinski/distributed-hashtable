package pl.mrz;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainSceneController {

    @FXML
    public ListView<String> mapListView;
    public TextField keyTextField;
    public TextField valueTextField;
    public Button putButton;
    public Button removeButton;
    public Button getButton;
    public Label getLabel;


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
        //FXCollections.observableMap(model.getDistributedMap().getHashMap()).addListener((SetChangeListener.Change<? extends String> c) -> {
        map = model.getDistributedMap().getHashMap();
        map.addListener((MapChangeListener.Change<? extends String, ? extends String> c) -> {
            if (c.wasAdded()) {
                mapListView.getItems().add(c.getKey());
            }
            if (c.wasRemoved()) {
                mapListView.getItems().remove(c.getKey());
            }
        });
    }

    public void putClicked(ActionEvent actionEvent) {
        model.getDistributedMap().put(keyTextField.getText(), valueTextField.getText());
        keyTextField.clear();
        valueTextField.clear();
    }

    public void removeClicked(ActionEvent actionEvent) {
        model.getDistributedMap().remove(mapListView.getSelectionModel().getSelectedItem());
    }

    public void getClicked(ActionEvent actionEvent) {
        getLabel.setText(model.getDistributedMap().get(mapListView.getSelectionModel().getSelectedItem()));
    }
}
