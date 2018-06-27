package es.ua.dlsi.im3.analysis.hierarchical.gui;

import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.io.MEIHierarchicalAnalysesModernImporter;
import es.ua.dlsi.im3.analysis.hierarchical.layout.FormTreeAnalysisLayout;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.File;
import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedTrees = new HashSet<>();
    }

    @FXML
    private void handleOpen() {
        try {
            OpenSaveFileDialog dlg = new OpenSaveFileDialog();
            File file = dlg.openFile("Open hierarchical MEI file", "MEI", ".mei");
            MEIHierarchicalAnalysesModernImporter importer = new MEIHierarchicalAnalysesModernImporter();
            importer.importSongAndAnalyses(file);
            ScoreSong scoreSong = importer.getScoreSong();

            FormAndMotivesAnalysis formAndMotivesAnalysis = (FormAndMotivesAnalysis) importer.getAnalyses().get(0);
            FormAnalysis formAnalysis = formAndMotivesAnalysis.getFormAnalysis();

            HorizontalLayout layout = new HorizontalLayout(scoreSong, LayoutFonts.bravura,
                    new CoordinateComponent(38000), new CoordinateComponent(700));

            Tree<FormAnalysisTreeNodeLabel> tree = formAnalysis.getTree();
            tree.linkSiblings(); // for being able to navigate
            LayoutFont layoutFont = new BravuraFont();
            FormTreeAnalysisLayout analysisLayout = new FormTreeAnalysisLayout(layoutFont, tree, importer.getScoreSong());

            scoreSongView = new ScoreSongView(layout);
            scrollPaneBottom.setContent(scoreSongView.getMainPanel());

            TreeFXBuilder treeFXBuilder = new TreeFXBuilder();
            TreeViewFX treeFX = treeFXBuilder.create(tree, treeHorizontalSeparationSlider.valueProperty(), treeVerticalSeparationSlider.valueProperty(), false, true);
            scrollPaneTop.setContent(treeFX.getRoot());

            initTreeInteraction(treeFX);
        } catch (Exception e) {
            e.printStackTrace();
            ShowError.show(HierarchicalAnalysisViewerApp.getMainStage(), "Cannot open file", e);
        }
    }

    private void initTreeInteraction(TreeViewFX treeFX) {
        treeFX.getLabelView().getLabel().setOnMouseEntered(event -> {
            if (!selectedTrees.contains(treeFX)) {
                treeFX.getLabelView().getLabel().setFill(Color.BLUE);
            }
        });
        treeFX.getLabelView().getLabel().setOnMouseExited(event -> {
            if (!selectedTrees.contains(treeFX)) {
                treeFX.getLabelView().getLabel().setFill(Color.BLACK);
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
                selected.getLabelView().getLabel().setFill(Color.BLACK);
            }
            selectedTrees.clear();
            selectedTrees.add(treeFX);
            treeFX.getLabelView().getLabel().setFill(Color.RED);

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
                        JavaFXUtils.ensureVisibleX(scrollPaneBottom, node);
                        break;
                    }
                }
            }

        }
    }
}
