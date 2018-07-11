package es.ua.dlsi.im3.analysis.hierarchical.gui;

import es.ua.dlsi.im3.analysis.hierarchical.Graphical;
import es.ua.dlsi.im3.analysis.hierarchical.forms.DivisionLabel;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.forms.RootLabel;
import es.ua.dlsi.im3.analysis.hierarchical.io.MEIHierarchicalAnalysesModernImporter;
import es.ua.dlsi.im3.analysis.hierarchical.layout.FormTreeAnalysisLayout;
import es.ua.dlsi.im3.analysis.hierarchical.tonal.TonalHierarchicalAnalysis;
import es.ua.dlsi.im3.core.adt.tree.StringLabel;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.LayoutCoreSymbol;
import es.ua.dlsi.im3.core.score.layout.LayoutFont;
import es.ua.dlsi.im3.core.score.layout.fonts.BravuraFont;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.gui.adt.tree.TreeFXBuilder;
import es.ua.dlsi.im3.gui.adt.tree.TreeViewFX;
import es.ua.dlsi.im3.gui.javafx.JavaFXUtils;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class HierarchicalAnalysisViewerController implements Initializable {
    @FXML
    ScrollPane scrollPaneTop;

    @FXML
    ScrollPane scrollPaneBottom;

    @FXML
    Slider treeVerticalSeparationSlider;

    @FXML
    Slider treeHorizontalSeparationSlider;

    HashSet<TreeViewFX> selectedTrees;
    private ScoreSongView scoreSongView;

    double zoomFactor = 1;
    private VBox vBoxTrees;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedTrees = new HashSet<>();
    }

    @FXML
    private void handleOpen() {
        try {
            OpenSaveFileDialog dlg = new OpenSaveFileDialog();
            File file = dlg.openFile("Open hierarchical MEI file", "MEI", "mei");
            if (file != null) {
                MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
                importer.importSongAndAnalyses(file);
                ScoreSong scoreSong = importer.getScoreSong();

                //TODO Poner de nuevo los slur y ver por qué los pone con los placeholders en lugar de con notas
                for (Staff staff : scoreSong.getStaves()) {
                    for (ITimedElementInStaff timedElementInStaff : staff.getCoreSymbols()) {
                        if (timedElementInStaff instanceof StaffTimedPlaceHolder) {
                            StaffTimedPlaceHolder staffTimedPlaceHolder = (StaffTimedPlaceHolder) timedElementInStaff;
                            if (staffTimedPlaceHolder.getConnectors() != null) {
                                staffTimedPlaceHolder.getConnectors().clear();
                                System.err.println("QUITANDO SLURS!!!!!!!!");
                            }
                        }

                    }
                }


                FormAndMotivesAnalysis formAndMotivesAnalysis = (FormAndMotivesAnalysis) importer.getAnalyses().get(0);
                FormAnalysis formAnalysis = formAndMotivesAnalysis.getFormAnalysis();

                HorizontalLayout layout = new HorizontalLayout(scoreSong, LayoutFonts.bravura,
                        new CoordinateComponent(38000), new CoordinateComponent(700));

                Tree<FormAnalysisTreeNodeLabel> tree = formAnalysis.getTree();
                tree.linkSiblings(); // for being able to navigate
                LayoutFont layoutFont = new BravuraFont();
                FormTreeAnalysisLayout analysisLayout = new FormTreeAnalysisLayout(layoutFont, tree, importer.getScoreSong());

                scoreSongView = new ScoreSongView(layout);
                scrollPaneBottom.setContent(new Group(scoreSongView.getMainPanel())); // scroll pane needs a group to be the first element to be inserted for zoom working well!!!!!!

                vBoxTrees = new VBox(10);
                scrollPaneTop.setContent(new Group(vBoxTrees)); // scroll pane needs a group to be the first element to be inserted for zoom working well!!!!!!

                // forms analysis
                TreeFXBuilder treeFXBuilderForms = new TreeFXBuilder();
                TreeViewFX treeFXForms = treeFXBuilderForms.create(tree, treeHorizontalSeparationSlider.valueProperty(), treeVerticalSeparationSlider.valueProperty(), false, true, formAndMotivesAnalysis.getGraphical(), true, 45, true);
                vBoxTrees.getChildren().add(treeFXForms.getRoot());
                initTreeInteraction(treeFXForms);

                // tonal analysis
                Graphical tonalColors = new Graphical();
                tonalColors.addColorMapping("I", "#747F00");
                tonalColors.addColorMapping("ii", "#ff5500");
                tonalColors.addColorMapping("iii", "#00ff94");
                tonalColors.addColorMapping("IV", "#2aff00");
                tonalColors.addColorMapping("iv", "#1500ff");
                tonalColors.addColorMapping("vii", "#00aaff");
                tonalColors.addColorMapping("vi", "#ff006a");
                tonalColors.addColorMapping("V", "#d400ff");
                tonalColors.addColorMapping("V/V", "#e55b16");
                tonalColors.addColorMapping("iv/ii", "#c316e5");
                TonalHierarchicalAnalysis tonalHierarchicalAnalysis = new TonalHierarchicalAnalysis(scoreSong);
                TreeFXBuilder treeFXBuilderTonal = new TreeFXBuilder();
                TreeViewFX treeFXTonal = treeFXBuilderForms.create(tonalHierarchicalAnalysis.getTree(), treeHorizontalSeparationSlider.valueProperty(), treeVerticalSeparationSlider.valueProperty(), false, true, tonalColors, true, 70, false);
                vBoxTrees.getChildren().add(treeFXTonal.getRoot());
                initTreeInteraction(treeFXTonal);

                // bars
                Tree meterTree = new Tree<>(new RootLabel("Meters"));
                for (Measure measure : scoreSong.getMeasuresSortedAsArray()) {
                    DivisionLabel divisionLabel = new DivisionLabel(Integer.toString(measure.getNumber()), scoreSong.getAnalysisStaff().findAnalysisHookWithOnset(measure.getTime()));
                    Tree<DivisionLabel> child = new Tree<>(divisionLabel);
                    meterTree.addChild(child);
                }
                TreeFXBuilder treeFXMetersBuilder = new TreeFXBuilder();
                TreeViewFX treeFXMeters = treeFXMetersBuilder.create(meterTree, treeHorizontalSeparationSlider.valueProperty(), treeVerticalSeparationSlider.valueProperty(), false, true, null, true, 70, false);
                vBoxTrees.getChildren().add(treeFXMeters.getRoot());
                initTreeInteraction(treeFXMeters);
            }

        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(HierarchicalAnalysisViewerApp.getMainStage(), "Cannot open file", e);
        }
    }

    private void initTreeInteraction(TreeViewFX treeFX) {
        treeFX.getLabelView().getLabel().setOnMouseEntered(event -> {
            if (!selectedTrees.contains(treeFX)) {
                treeFX.getLabelView().getLabel().setUserData(treeFX.getLabelView().getLabel().getFill()); //TODO Hacerlo de otra forma
                treeFX.getLabelView().getLabel().setFill(Color.ORANGE);
            }
        });
        treeFX.getLabelView().getLabel().setOnMouseExited(event -> {
            if (!selectedTrees.contains(treeFX)) {
                Paint previousColor = (Paint) treeFX.getLabelView().getLabel().getUserData(); //TODO Hacerlo de otra forma;
                treeFX.getLabelView().getLabel().setFill(previousColor);
            }
        });

        treeFX.getLabelView().getLabel().setOnMouseClicked(event -> {
            doSelect(treeFX);
        });

        for (TreeViewFX child: treeFX.getChildren()) {
            initTreeInteraction(child);
        }
    }

    private void doSelect(TreeViewFX treeFX) {
        // select music
        if (treeFX.getTree().getParent() != null) { // if not root
            // unselect others
            for (TreeViewFX selected: selectedTrees) {
                Paint previousColor = (Paint) selected.getLabelView().getLabel().getUserData(); //TODO Hacerlo de otra forma;
                selected.getLabelView().getLabel().setFill(previousColor);

            }
            selectedTrees.clear();
            selectedTrees.add(treeFX);
            //treeFX.getLabelView().getLabel().setUserData(treeFX.getLabelView().getLabel().getFill()); //TODO Hacerlo de otra forma
            treeFX.getLabelView().getLabel().setFill(Color.DARKORANGE);

            FormAnalysisTreeNodeLabel label = (FormAnalysisTreeNodeLabel) treeFX.getTree().getLabel();
            Time fromTime = label.getTime();
            Tree rightSibling = treeFX.getTree().getRightSibling();
            Time toTime;
            if (rightSibling == null) {
                toTime = Time.TIME_MAX;
            } else {
                FormAnalysisTreeNodeLabel rightLabel = (FormAnalysisTreeNodeLabel) rightSibling.getLabel();
                toTime = rightLabel.getTime();
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Selecting from {0} to {1}", new Object[]{fromTime, toTime});
            scoreSongView.select(fromTime, toTime);

            // ensure selected element is visible in the scroll
            if (!scoreSongView.getInteractionPresenter().getSelectedElements().isEmpty()) {
                for (GraphicsElement selectedElement: scoreSongView.getInteractionPresenter().getSelectedElements()) {
                    String id = selectedElement.getID();
                    Node node = scoreSongView.getNode(id);
                    if (node != null) {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Moving scroll to ID " + id + ", node: " + node);
                        JavaFXUtils.ensureVisible(scrollPaneBottom, node);
                        break;
                    }
                }
            }

        }
    }

    @FXML
    private void handleZoomIn() {
        zoomFactor += 0.1;
        setZoom(0, 0, zoomFactor);
    }

    @FXML
    private void handleZoomOut() {
        zoomFactor -= 0.1;
        setZoom(0, 0, zoomFactor);

    }

    @FXML
    private void handleZoomReset() {
        zoomFactor = 1;
        setZoom(0, 0, zoomFactor);
    }

    public void setZoom(final double x, final double y, final double factor) {
        // save the point before scaling
        final Point2D sceneToLocalPointBefore = vBoxTrees.sceneToLocal(x, y);

        // do scale
        vBoxTrees.setScaleX(factor);
        vBoxTrees.setScaleY(factor);

        // save the point after scaling
        final Point2D sceneToLocalPointAfter = vBoxTrees.sceneToLocal(x, y);

        // calculate the difference of before and after the scale
        final Point2D diffMousePoint = sceneToLocalPointBefore.subtract(sceneToLocalPointAfter);

        // translate the pane in order to point where the mouse is
        vBoxTrees.setTranslateX(vBoxTrees.getTranslateX() - diffMousePoint.getX() * vBoxTrees.getScaleX());
        vBoxTrees.setTranslateY(vBoxTrees.getTranslateY() - diffMousePoint.getY() * vBoxTrees.getScaleY());
    }

    /*public void handleSaveAnalysisTrees() {
        OpenSaveFileDialog dlg = new OpenSaveFileDialog();
        File file = dlg.saveFile("Save analysis trees", "SVG", "svg");
        if (file != null) {

        }

    }*/
}
