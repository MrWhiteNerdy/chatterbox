package com.josephndavis.chattykathy.client.controllers;

import com.josephndavis.chattykathy.client.ClientMain;
import server.DBManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
	
	@FXML private TextArea historyTxtArea;
	@FXML private Button deleteHistoryBtn;

    public void handleDeleteHistoryBtnAction() {
        DBManager.deleteMessages();
        historyTxtArea.clear();
        historyTxtArea.appendText("All messages are cleared now\n");
    }

    public void retrieveHistory(String recipient) {
        ResultSet rs = DBManager.getMessages(ClientMain.getLoginController().getClient().getUsername(), recipient);

        try {
            while (rs.next()) {
                historyTxtArea.appendText("[ " + rs.getString(3) + " > " + rs.getString(2) + " ]: " + rs.getString(4) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ClientMain.setHistoryController(this);
    }
}
