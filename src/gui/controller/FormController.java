package gui.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class FormController implements ChoosingController, Initializable, Serializable {
    private static final String ERROR_MSG = "Error occurred! Check all form fields and try again!";
    private static final String SUCCESS_MSG = "Ferryboat has been created!";

    private boolean choosing = false;
    private static Random RAND = new Random();

    @FXML
    protected Label info;

    @FXML
    protected Button routeButton;

    @FXML
    public void addNewVehicle(ActionEvent event) {
        disableChoosing();
        if (validate()) {
            FXMLWindowController.getInstance().addVehicleButton(mapShipDetails());
            clearForm();
            info.setText(SUCCESS_MSG);
            info.setTextFill(Color.GREEN);
        } else {
            info.setText(ERROR_MSG);
            info.setTextFill(Color.RED);
        }
    }

    @FXML
    public void chooseRoute(ActionEvent event) {
        if (!choosing) {
            enableChoosing();
        } else {
            disableChoosing();
        }
    }

    public void disableChoosing() {
        choosing = false;
        FXMLWindowController.getInstance().allEnabled();
        FXMLWindowController.getInstance().setChoosingState(false);
        routeButton.setText("Choose Route");
    }

    public void enableChoosing() {
        choosing = true;
    }

    protected <T> List<T> randomPorts(T port, List<T> list) {
        List<T> ports = new ArrayList<>();

        if (list.size() < 4) {
            list.forEach(e -> {
                if (e != port)
                    ports.add(e);
            });
        } else {
            ports.addAll(prepareUniquePortList(list));
        }

        return ports;
    }

    public static <T> List<T> prepareUniquePortList(List<T> list) {
        List<T> ports = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            boolean found = false;

            while (!found) {
                int index = RAND.nextInt(list.size());
                if (!ports.contains(list.get(index))) {
                    found = true;
                    ports.add(list.get(index));
                }

            }
        }

        return ports;
    }

    protected abstract boolean validate();

    protected abstract Map<String, Object[]> mapShipDetails();

    protected abstract void clearForm();
}
