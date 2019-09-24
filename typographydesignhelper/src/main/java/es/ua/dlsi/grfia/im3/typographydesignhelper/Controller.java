package es.ua.dlsi.grfia.im3.typographydesignhelper;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.JSONGlyphNamesReader;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.gui.javafx.JavaFXUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Label labelCurrentFontFileName;
    @FXML
    Label labelJSONFilename;

    @FXML
    Spinner<Integer> spinnerKerning;
    @FXML
    Spinner<Integer> spinnerFontSize;
    @FXML
    Spinner<Integer> spinnerStaffLineThickness;
    @FXML
    Spinner<Double> spinnerVerticalPosition;
    @FXML
    TableColumn<Symbol, String> colSymbol;
    @FXML
    TableColumn<Symbol, String> colUnicode;
    @FXML
    TableView<Symbol> tbSymbols;
    @FXML
    ScrollPane scrollPane;
    @FXML
    TextField textInputFilter;

    HBox hBoxSymbols;

    IntegerProperty kerning;
    IntegerProperty fontSize;
    IntegerProperty verticalPosition;

    ObservableList<Symbol> symbols;
    SymbolView selectedValue;

    HashMap<Symbol, SymbolView> symbolSymbolViewHashMap;
    ObjectProperty<Font> font;
    private String fontFileName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        symbolSymbolViewHashMap = new HashMap<>();
        labelCurrentFontFileName.setVisible(false);
        labelJSONFilename.visibleProperty().bind(labelCurrentFontFileName.visibleProperty());
        spinnerKerning.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 200));
        spinnerKerning.getValueFactory().setValue(0);

        spinnerFontSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 200));
        spinnerFontSize.getValueFactory().setValue(48);

        spinnerStaffLineThickness.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20));
        spinnerStaffLineThickness.getValueFactory().setValue(1);

        spinnerVerticalPosition.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(-20.0, 20.0));
        spinnerVerticalPosition.getValueFactory().setValue(0.0);

        kerning  = new SimpleIntegerProperty();
        fontSize  = new SimpleIntegerProperty();
        verticalPosition = new SimpleIntegerProperty();
        kerning.bind(spinnerKerning.valueProperty());
        fontSize.bind(spinnerFontSize.valueProperty());
        verticalPosition.bind(spinnerVerticalPosition.valueProperty());

        symbols = FXCollections.observableArrayList();
        tbSymbols.itemsProperty().bindBidirectional(new SimpleListProperty<>(symbols));
        font = new SimpleObjectProperty<>();

        hBoxSymbols = new HBox();
        hBoxSymbols.spacingProperty().bind(kerning);
        scrollPane.setContent(hBoxSymbols);

        initFontSize();
        initTable();
        initDrawnSymbols();
    }

    private void initTable() {
        colSymbol.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnicode.setCellValueFactory(new PropertyValueFactory<>("unicode"));
        colUnicode.setCellFactory(TextFieldTableCell.<Symbol>forTableColumn());

        tbSymbols.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Symbol>() {
            @Override
            public void changed(ObservableValue<? extends Symbol> observable, Symbol oldValue, Symbol newValue) {
                if (selectedValue != null) {
                    unselect(selectedValue);
                }
                selectedValue = symbolSymbolViewHashMap.get(newValue);
                if (selectedValue != null) {
                    select(selectedValue);
                }
            }
        });
    }

    private void unselect(SymbolView item) {
        item.setFill(Color.BLACK);
    }

    private void select(SymbolView item) {
        item.setFill(Color.RED);
        JavaFXUtils.ensureVisible(scrollPane, item);
    }

    /*private void drawStaff() {
        int WIDTH = 200000/5; //TODO Automático
        scorePane.setMinWidth(WIDTH);
        lines = new Line[5];
        for (int i=0; i<lines.length; i++) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.strokeWidthProperty().bind(spinnerStaffLineThickness.valueProperty());
            line.setStartX(0);
            line.setEndX(WIDTH);

            line.startYProperty().bind(fontSize.multiply(i).divide(5).add(20)); // avoid problems on line width and margins
            line.endYProperty().bind(line.startYProperty());
            scorePane.getChildren().add(line);
            lines[4-i] = line;
        }
    }*/

    @FXML
    private void handleOpenFont() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File file = dlg.openFile("Open a OTF font", "OTF", "otf");
        if (file != null) {
            doOpenFont(file);
        }
    }

    private void initFontSize() {
        fontSize.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue != null && fontFileName != null) {
                    try {
                        loadFont();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ShowError.show(null, "Cannot load font", e);
                    }
                }
            }
        });

    }

    private void doOpenFont(File file) {
        symbols.clear();
        symbolSymbolViewHashMap.clear();
        hBoxSymbols.getChildren().clear();
        OTFParser otfParser = new OTFParser(true);
        try {
            OpenTypeFont otfMusicFont = otfParser.parse(file); // because we use it in im3, to check it does not crash
            labelCurrentFontFileName.setVisible(true);
            labelCurrentFontFileName.setText(file.getName());
            fontFileName = file.getAbsolutePath();
            loadFont();
            String basename = FileUtils.getFileNameWithoutExtension(file.getName());
            String jsonName = basename + ".json";
            File jsonFile = new File(file.getParent(), jsonName);
            loadJSon(jsonFile);
            labelJSONFilename.setText(jsonName);
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(null, "Cannot open font", e);
        }
    }

    private void loadFont() throws IM3Exception, FileNotFoundException {
        InputStream inputStream = new FileInputStream(fontFileName);
        Font fontValue = Font.loadFont(inputStream, (double) fontSize.get());
        if (fontValue == null) {
            throw new IM3Exception("Cannot load font file " + fontFileName + ", size " + fontSize.get());
        }
        font.setValue(fontValue);
    }

    private void loadJSon(File jsonFile) throws IM3Exception {
        //createControlSymbols();

        JSONGlyphNamesReader mapping = new JSONGlyphNamesReader(jsonFile);
        HashMap<String, String> map = mapping.readCodepointToOrderedGlyphMap();
        for (Map.Entry<String, String> entry: map.entrySet()) {
            Symbol symbol = new Symbol(entry.getValue(), entry.getKey());
            symbols.add(symbol);
        }
    }

    /**
     * To check sizes
     */
   /* private void createControlSymbols() {
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.YELLOW);
        rectangle.setOpacity(0.5);
        rectangle.yProperty().bind(lines[4].startYProperty());
        rectangle.heightProperty().bind(fontSize);
        rectangle.widthProperty().bind(fontSize);
        hBoxSymbols.getChildren().add(rectangle);

    }*/

    private void initDrawnSymbols() {
        symbols.addListener(new ListChangeListener<Symbol>() {
            @Override
            public void onChanged(Change<? extends Symbol> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // no-op
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (Symbol remitem : c.getRemoved()) {
                            symbolRemoved(remitem);
                        }
                        for (Symbol additem : c.getAddedSubList()) {
                            symbolAdded(additem);
                        }
                    }
                }                
            }
        });
    }

    private void symbolAdded(Symbol additem) {
        String unicode = JSONGlyphNamesReader.getJavaUnicodeString(additem.getUnicode());
        SymbolView symbolView = new SymbolView(spinnerStaffLineThickness.valueProperty(), fontSize, verticalPosition.divide(2.0), font, unicode);
        symbolSymbolViewHashMap.put(additem, symbolView);
        hBoxSymbols.getChildren().add(symbolView);
    }

    private void symbolRemoved(Symbol remitem) {
        SymbolView symbolView = symbolSymbolViewHashMap.get(remitem);
        if (symbolView != null) {
            hBoxSymbols.getChildren().remove(symbolView);
        }
    }


}
