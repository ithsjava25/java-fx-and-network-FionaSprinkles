package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();
    public ListView<NtfyMessageDto> messageView;

    @FXML
    private Label versionLabel;

    @FXML
    private void initialize() {
        if (versionLabel != null) {
            versionLabel.setText(model.getGreeting());
        }
        messageView.setItems(model.getMessages());
        //todo fixa snygga meddelanden
        //messageView.setCellFactory();
    }

    public void sendMessage(ActionEvent actionEvent) {
        model.sendMessage();
    }
}
