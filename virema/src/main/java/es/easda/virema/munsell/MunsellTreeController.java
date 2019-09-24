package es.easda.virema.munsell;

import es.ua.dlsi.im3.core.adt.Pair;
import es.ua.dlsi.im3.gui.javafx.dialogs.CustomDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class MunsellTreeController implements Initializable {
    private static final double DEFAULT_RECTANGLE_SIZE = 100;

    @FXML
    BorderPane borderPane;
    @FXML
    Pane mainPane;
    @FXML
    ToolBar toolbar;
    @FXML
    FlowPane floxpane;
    @FXML
    HBox hboxDissonances;
    @FXML
    ScrollPane scrollPaneDissonances;
    @FXML
    BorderPane borderPaneDissonances;
    @FXML
    Button btnAddSizedArea;

    List<CheckBox> checkBoxes;
    HashMap<String, Node> hpanes;

    MunsellTreeModel munsellTreeModel;

    List<MunsellColor> selectedColors;
    List<Rectangle> selectedRectangles;

    public MunsellTreeController() {
        selectedColors = new ArrayList<>();
        selectedRectangles = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 217217217
            hboxDissonances.setMinHeight(500);
            hboxDissonances.setMinWidth(2000);
            hboxDissonances.setFillHeight(true);
            hboxDissonances.setStyle("-fx-background-color: #7F7F7F;");
            scrollPaneDissonances.setStyle("-fx-background-color: #7F7F7F;");
            borderPaneDissonances.setStyle("-fx-background-color: #7F7F7F;");

            floxpane.prefHeightProperty().bind(borderPane.widthProperty());
            floxpane.prefHeightProperty().bind(borderPane.heightProperty());
            hpanes = new HashMap<>();
            munsellTreeModel = new MunsellTreeModel();
            createToolbar();

            btnAddSizedArea.setDisable(true);
            hboxDissonances.getChildren().addListener(new ListChangeListener<Node>() {
                @Override
                public void onChanged(Change<? extends Node> c) {
                    btnAddSizedArea.setDisable(hboxDissonances.getChildren().isEmpty());
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot load Munsell tree data", e);
        }
    }

    private void createToolbar() {
        checkBoxes = new ArrayList<>();
        for (String hname: munsellTreeModel.getHues()) {
            CheckBox cb = new CheckBox(hname);
            checkBoxes.add(cb);
            toolbar.getItems().add(cb);

            cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    onHueSelected(hname, newValue);
                }
            });
        }
    }

    @FXML
    private void handleClear() {
        checkBoxes.stream().forEach(checkBox -> {
            checkBox.setSelected(false);
        });

    }

    private void onHueSelected(String name, boolean newValue) {
        if (!newValue) {
            Node pane = hpanes.get(name);
            if (pane != null) {
                floxpane.getChildren().remove(pane);
                hpanes.remove(name);
            }
        } else {
            Node pane = createPane(name);
            floxpane.getChildren().add(pane);
            hpanes.put(name, pane);
        }
    }

    private Node createPane(String name) {
        VBox vBox = new VBox(5);
        vBox.getChildren().add(new Text("HUE=" + name));


        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        List<MunsellColor> colors = munsellTreeModel.getColors(name);

        for (MunsellColor color: colors) {
            Rectangle rectangle = new Rectangle(30, 30);
            rectangle.setFill(color.toColor());
            gridPane.add(rectangle, color.getC(), color.getV());

            rectangle.setOnMouseClicked(event -> addColorToDissonances(color, DEFAULT_RECTANGLE_SIZE));
        }

        vBox.getChildren().add(gridPane);
        return vBox;
    }

    private void addColorToDissonances(MunsellColor color, double width) {
        MunsellColor lastColor = null;
        if (this.selectedColors.size() > 0) {
            lastColor = this.selectedColors.get(selectedColors.size()-1);
        }
        this.selectedColors.add(color);
        this.hboxDissonances.getChildren().add(createDissonanceRectangle(color, lastColor, width));
    }

    private Node createDissonanceRectangle(MunsellColor color, MunsellColor lastColor, double width) {
        Rectangle rectangle = new Rectangle(width, width);
        this.selectedRectangles.add(rectangle);
        rectangle.setFill(color.toColor());

        if (lastColor != null) {
            DecimalFormat formatter = new DecimalFormat("#0.000");
            String balance = formatter.format(lastColor.computeBalanceWith(color));
            Tooltip tooltip = new Tooltip("Balance with previous color:\n" + balance);
            Tooltip.install(rectangle, tooltip);
            //Text label = new Text(balance);
            //this.hboxDissonances.getChildren().add(label);
        }
        return rectangle;
    }

    @FXML
    private void handleReset() {
        this.selectedColors.clear();
        this.hboxDissonances.getChildren().clear();
        this.selectedRectangles.clear();
    }

    @FXML
    private void handleResizeDissonance() {
        // it adjusts the sizes of the rectanges according to the dissonance values
        double size = 100;
        for (int i=0; i<selectedColors.size(); i++) {
            Rectangle rectangle = selectedRectangles.get(i);
            MunsellColor color = selectedColors.get(i);
            if (i==0) {
                rectangle.setWidth(size);
                rectangle.setHeight(size);
            } else {
                MunsellColor prevColor = selectedColors.get(i-1);
                double balance = prevColor.computeBalanceWith(color);
                size = size * balance;
                rectangle.setWidth(size);
                rectangle.setHeight(size);
            }
        }
    }

    @FXML
    private void handleResetDissonancesSizes() {
        double size = DEFAULT_RECTANGLE_SIZE;
        for (Rectangle rectangle: selectedRectangles) {
            rectangle.setWidth(size);
            rectangle.setHeight(size);
        }
    }

    @FXML
    private void handleAddArea() {
        TextInputDialog textInputDialog = new TextInputDialog("100");
        textInputDialog.setTitle("Input the area rectangle width");
        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent()) {
            double width = Double.parseDouble(result.get());

            double prevWidth = selectedRectangles.get(selectedRectangles.size()-1).getWidth();

            MunsellColor munsellColor = showAllPossibleColors(width / prevWidth);
            if (munsellColor != null) {
                addColorToDissonances(munsellColor, width);
            }
        }
    }

    private MunsellColor showAllPossibleColors(double relation) {
        TreeSet<Pair<MunsellColor, Double>> colors = new TreeSet<>((o1, o2) -> {
            double diff = o1.getY()-o2.getY();
            if (diff == 0) {
                return o1.getX().hashCode() - o2.getX().hashCode();
            } else if (diff < 0) {
                return -1;
            } else {
                return 1;
            }

        });
        MunsellColor lastColor = selectedColors.get(selectedColors.size() - 1);
        // now find all colors whose size is approximately the one specified in the width, order them in decreasing order of difference from 1
        for (List<MunsellColor> munsellColorList: munsellTreeModel.colors.values()) {
            for (MunsellColor munsellColor: munsellColorList) {
                double balance = lastColor.computeBalanceWith(munsellColor);
                Pair<MunsellColor, Double> pair = new Pair<>(munsellColor, Math.abs(relation-balance));
                colors.add(pair);
            }
        }

        VBox rectangleVBox = new VBox(5);
        int n = 0;
        ArrayList<Pair<MunsellColor, Double>> selectedColor = new ArrayList<>(); // because must be final
        ArrayList<Pair<MunsellColor, Double>> topNColors = new ArrayList<>();
        Iterator<Pair<MunsellColor, Double>> iterator = colors.iterator();
        while (n<100 && iterator.hasNext()) {
            Pair<MunsellColor, Double> pair = iterator.next();
            topNColors.add(pair);
            HBox hBox = new HBox(3);
            rectangleVBox.getChildren().add(hBox);

            Rectangle rectangle = new Rectangle(20, 20, pair.getX().toColor());
            hBox.getChildren().add(rectangle);
            hBox.getChildren().add(new Label(pair.getY().toString()));

            rectangle.setOnMouseClicked(event -> {
                selectedColor.clear();
                selectedColor.add(pair);
            });
            n++;
        }
        ScrollPane scrollPane = new ScrollPane(rectangleVBox);
        scrollPane.setPrefHeight(500);

        CustomDialog customDialog = new CustomDialog(null, "Select a color", scrollPane);
        boolean result = customDialog.show();
        if (result && !selectedColor.isEmpty()) {
            return selectedColor.get(0).getX();
        }
        return null;
    }
}
