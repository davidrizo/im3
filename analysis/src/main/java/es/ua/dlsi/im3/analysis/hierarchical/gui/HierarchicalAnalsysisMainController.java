/*
 * Copyright (C) 2017 David Rizo Valero
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ua.dlsi.im3.analysis.hierarchical.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


import es.ua.dlsi.im3.analysis.hierarchical.Analysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysis;
import es.ua.dlsi.im3.analysis.hierarchical.forms.FormAnalysisTreeNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MelodicMotiveNodeLabel;
import es.ua.dlsi.im3.analysis.hierarchical.motives.MotivesEdgeLabel;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.adt.tree.Tree;
import es.ua.dlsi.im3.core.score.layout.CoordinateComponent;
import es.ua.dlsi.im3.core.score.layout.HorizontalLayout;
import es.ua.dlsi.im3.core.score.layout.fonts.LayoutFonts;
import es.ua.dlsi.im3.gui.adt.graph.javafx.DirectedGraphViewFX;
import es.ua.dlsi.im3.gui.javafx.dialogs.OpenSaveFileDialog;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowConfirmation;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowError;
import es.ua.dlsi.im3.gui.javafx.dialogs.ShowMessage;
import es.ua.dlsi.im3.gui.score.javafx.ScoreSongView;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author drizo
 */
public class HierarchicalAnalsysisMainController implements Initializable {

	@FXML
	MenuBar menuBar;
	@FXML
	MenuItem menuClose;
	@FXML
	MenuItem menuUndo;
	@FXML
	MenuItem menuRedo;
	@FXML
	Menu menuEdit;
	@FXML
	Menu menuView;
	@FXML
	ToolBar toolBar;
	@FXML
	ToolBar playingButtonsContainer;
	@FXML
	ToggleButton btnCreateSections;
	@FXML
	ToggleGroup toggleButtonsAnalysis;
	@FXML
    ListView<FormAndMotivesAnalysis> lvAnalyses;
	@FXML
	Button btnRemoveFormAnalysis;
	@FXML
	Accordion analysisAccordion;
	@FXML
	Slider sliderVerticalTreeSeparator;
	@FXML
	MenuItem menuItemSave;
	@FXML
	VBox panelAnalysis;
	@FXML
    BorderPane mainBorderPane;
	@FXML
    ScrollPane mainScrollPane;
	
	Model model;
    //IM3 HierarchicalAnalysesDocumentView documentView;
	private ChangeListener<Analysis> lvAnalysisChangeListener;
	private DirectedGraphViewFX<MelodicMotiveNodeLabel, MotivesEdgeLabel> graphView;
	private Pane miniGraphPanel;
    private Stage mainStage;
    private ScoreSongView scoreView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: 1/5/18
        //IM3 UndoRedoMenusBehaviourAdder.addUndoRedoBehaviour(documentManager, menuUndo, menuRedo);
        //IM3 toolBar.disableProperty().bind(tabPaneDocuments.getSelectionModel().selectedItemProperty().isNull());
        menuEdit.disableProperty().bind(toolBar.disableProperty());
        menuView.disableProperty().bind(toolBar.disableProperty());
        menuItemSave.disableProperty().bind(toolBar.disableProperty());
        //IM3 initDocumentTab();

        // System.setProperty("com.sun.xml.bind.v2.runtime.JAXBContextImpl.fastBoot",
        // "true");
    }


    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }



	private void initFormAnalysis() {
		panelAnalysis.disableProperty().bind(lvAnalyses.getSelectionModel().selectedItemProperty().isNull());
		
		btnRemoveFormAnalysis.disableProperty().unbind();
		btnCreateSections.disableProperty().unbind();
		lvAnalyses.itemsProperty().unbind();
		model.selectedAnalysisProperty().unbind();
		
		if (lvAnalysisChangeListener != null) {
			lvAnalyses.getSelectionModel().selectedItemProperty().removeListener(lvAnalysisChangeListener);
		}

		lvAnalysisChangeListener = new ChangeListener<Analysis>() {

			@Override
			public void changed(ObservableValue<? extends Analysis> observable, Analysis oldValue,
					Analysis newValue) {
				onAnalysisChanged(newValue);
			}
		};
		
		
		btnRemoveFormAnalysis.disableProperty().bind(lvAnalyses.getSelectionModel().selectedItemProperty().isNull());
		btnCreateSections.disableProperty().bind(lvAnalyses.getSelectionModel().selectedItemProperty().isNull());
		lvAnalyses.itemsProperty().bindBidirectional(model.possibleAnalysesProperty());
		model.selectedAnalysisProperty().bind(lvAnalyses.getSelectionModel().selectedItemProperty());
		lvAnalyses.getSelectionModel().selectedItemProperty().addListener(lvAnalysisChangeListener);
	}

	protected void onAnalysisChanged(Analysis newValue) {
        if (newValue == null) {
			//try {
                clearTree();
			/*} catch (IM3Exception e) {
				Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, "Cannot clear tree", e);
				ShowError.show(mainStage, "Cannot clear tree", e);
			}*/
		} else {
            try {
                paintFormAnalysisTree();
                paintMotiveAnalysis();
            } catch (Exception e) {
                e.printStackTrace();
                ShowError.show(mainStage, "Cannot paint analysis tree or graph", e);
            }
        }
	}

    private void clearTree() {
    }


    @FXML
	public void handleAbout(ActionEvent event) {
		ShowMessage.show(mainStage, "About...Hierarchical Analysis GUI App\n© 2018 drizo@dlsi.ua.es");
	}

	@FXML
	public void handleOpenMusicXML(ActionEvent event) {
		OpenSaveFileDialog dlg = new OpenSaveFileDialog();
		File file = dlg.openFile("Open notation file", "MusicXML files", "xml");
		if (file != null) {
			try {
				model = new Model();
				model.importMusicXML(file);
				addDocument(file, model);
			} catch (Throwable t) {
				Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, t);
				ShowError.show(mainStage,"Cannot open file " + file.getName(), t);
			}
		}
	}

	@FXML
	public void handleOpenMEI(ActionEvent event) {
		OpenSaveFileDialog dlg = new OpenSaveFileDialog();
		File file = dlg.openFile("Open hierarchical analysis MEI file", "MEI files", "mei");
		if (file != null) {
			try {
				model = new Model();
				model.importHierarchicalAnalysisMEI(file);
				addDocument(file, model);
			} catch (Throwable t) {
				Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, t);
				ShowError.show(mainStage,"Cannot open file " + file.getName(), t);
			}
		}
	}

	/*@FXML
	@FXML
	public void handleExportMusicXML(ActionEvent event) {
		OpenSaveFileDialog dlg = new OpenSaveFileDialog();
		File file = dlg.saveFile("Save notation file", "MusicXML files", "xml");
		if (file != null) {
			documentManager.getSelectedModel().getDocument().setFile(file);
			try {
				ImporterExporter exporter = new ImporterExporter();
				exporter.writeXML((ModernSong) documentManager.getSelectedModel().getDocument().getScore(),
						documentManager.getSelectedModel().getDocument().getFile());
			} catch (Exception ex) {
				Logger.getLogger(SampleScoreGUIMainController.class.getName()).log(Level.SEVERE, null, ex);
				ShowError.show(mainStage,"Cannot export document", ex);
			}
		}
	}

*/

	@FXML
	public void handleSaveAs(ActionEvent event) {
		// TODO ¿Aquí o en un modelo?
		saveDocumentAs();
	}
	
	@FXML
	public void handleExit(ActionEvent event) {
		// TODO ¿Y si pulsan en cerrar del stage? - en lugar de Platform.exit
		// hay que decirselo a la ventana principal - screens y que ésta
		// pregunte al resto si se puede salir
		boolean exit;
		if (documentsToSave()) {
			exit = ShowConfirmation.show(mainStage, "There are documents to be saved, do you really want to exit?");
		} else {
			exit = true;
		}
		if (exit) {
            Platform.exit();
		}
	}

	/**
	 *
	 * @return true if there is a document that needs saving
	 */
	private boolean documentsToSave() {
	    throw new UnsupportedOperationException();
		/*IM3 for (Tab tab : tabPaneDocuments.getTabs()) {
			HierarchicalAnalysesDocumentView documentView = (HierarchicalAnalysesDocumentView) tab.getUserData();
			if (documentView.getDocument().getFile() == null || documentView.needsSavingProperty().get()) {
				return true;
			}
		}
		return false;*/
	}

	private void addDocument(File file, Model model) throws Exception {
	    HorizontalLayout horizontalLayout = new HorizontalLayout(model.getScoreSong(), new CoordinateComponent(100000), new CoordinateComponent(500)); // TODO: 1/5/18
	    scoreView = new ScoreSongView(horizontalLayout);
        mainScrollPane.setContent(scoreView.getMainPanel());

        initFormAnalysis();

        /*IM3 try {
			boolean debug = false;
			documentView = new HierarchicalAnalysesDocumentView(this, sd.getModel(), debug);
			Tab tab = new Tab();
			tab.textProperty().bind(documentView.titleWithSavingMarkProperty());
			tab.setUserData(documentView);
			tabPaneDocuments.getTabs().add(tab);
			tabPaneDocuments.getSelectionModel().onSelect(tab);
			
			Parent svRoot = documentView.getScoreViewController().getScoreView().getRoot();
			VBox pane = new VBox(svRoot);
			VBox.setVgrow(svRoot, Priority.ALWAYS);
			tab.setContent(pane); // in order to get stretched to fill up space
			tabPaneDocuments.getSelectionModel().onSelect(tab);

			onDocumentAdded(documentView.getScoreViewController());
		} catch (Exception e) {
			throw new NotationGUIException(e); // do not add to the pane
		}*/
	}

	@FXML
	public void handleCloseDocument(ActionEvent event) {
        throw new UnsupportedOperationException();

        /*IM3 if (tabPaneDocuments.getSelectionModel().getSelectedItem() == null) {
			throw new RuntimeException("There is no tab to close"); // it should
																	// happened
																	// as the
																	// close
																	// command
																	// is
																	// disabled
																	// when no
																	// tab is
																	// selected
		}
		tabPaneDocuments.getTabs().remove(tabPaneDocuments.getSelectionModel().getSelectedItem());*/
	}

	@FXML
	public void handleUndo(ActionEvent event) {
	    throw new UnsupportedOperationException();
		/*IM3 try {
			// selectedDocumentView.get().getCommandManager().undo();
			documentManager.getActiveCommandManager().undo();
		} catch (GUIException ex) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, ex);
			ShowError.show(mainStage,"Cannot undo", ex);
		}*/
	}

	@FXML
	public void handleRedo(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 try {
			// selectedDocumentView.get().getCommandManager().redo();
			documentManager.getActiveCommandManager().redo();
		} catch (GUIException ex) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, ex);
			ShowError.show(mainStage,"Cannot redo", ex);
		}*/
	}

	private void initDocumentTab() {
        throw new UnsupportedOperationException();
		/*IM3 tabPaneDocuments.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
				playingButtonsContainer.getItems().clear();
				if (t1 == null) {
					documentManager.unselectDocument();
					documentView = null;
				} else {
					documentView = (HierarchicalAnalysesDocumentView) t1.getUserData();
					documentManager.selectDocument(documentView);
					playingButtonsContainer.getItems().add(documentView.getScoreViewController().getPlayingButtons());
					try {
						initFormAnalysis();
					} catch (Exception e) {
						Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, e);
						ShowError.show(HierarchicalAnalsysisMainController.this, "Cannot init form analysis", e);
					}
				}
			}
		});*/
	}

	/*IM3 protected void onDocumentAdded(ScoreViewController scoreView) {
		//getSelectedModel().setSong((ModernSong)scoreView.getScoreView().getScoreSong());
		registerHandlers(scoreView);
	}

	protected void registerHandlers(final ScoreViewController scoreView) {
		scoreView.getInteractionManager().registerHandler(InteractionEventGroupSelect.class, new IInteractionEventHandler<InteractionEventGroupSelect>() {
			@Override
			public void handle(InteractionEventGroupSelect event) {
				try {
					getSelectedModel().setSelectedElements(event.getSelectedElements());
				} catch (Exception e) {
					ShowError.show(HierarchicalAnalsysisMainController.this, "Cannot onSelect model", e);
				}
			}
		});
	}*/

	@FXML
	private void handleZoomIn(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 try {
			documentView.getScoreViewController().zoomIn();
		} catch (NotationGUIException ex) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, ex);
			ShowError.show(mainStage,"Cannot zoom in", ex);
		}*/
	}

	@FXML
	private void handleZoomOut(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 try {
			documentView.getScoreViewController().zoomOut();
		} catch (NotationGUIException ex) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, ex);
			ShowError.show(mainStage,"Cannot zoom out", ex);
		}*/
	}
	
	@FXML
	private void handleDummyFormAnalysis(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 if (ensureAnalysis()) {
			try {
				getSelectedModel().doFormAnalysis();
			} catch (Exception e) {
				Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.WARNING, null, e);
				ShowError.show(mainStage,"Cannot create form analysis", e);
			}
		}*/
	}
	/*private boolean ensureAnalysis() {
		try {
			getSelectedModel().ensureAnalysisStaff(true);
			documentView.getScoreViewController().updateScoreView();
			return true;
		} catch (Exception e1) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, "Cannot ensure analysis staff", e1);
			ShowError.show(HierarchicalAnalsysisMainController.this, "Cannot ensure analysis staff", e1);
			return false;
		}
		
	}
	
	private ScoreSongView getScoreView() {
		return documentView.getScoreViewController().getScoreView();
	}*/

	@FXML
	private void handleCreateMotiveWithSelected(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 try {
			//it adds the motivic analysis if not exists
			NameDescriptionColorDialog dlg = new NameDescriptionColorDialog(getContainedInWindow(), "Motive");
			if (dlg.show()) {
				String name = dlg.getName();
				String description = dlg.getDescription();
				String color = dlg.getHexaColor();
				getSelectedModel().createMotiveWithSelectedElements(name, description, color); // the selection handler has set selected elements in model (see registerHandlers)
				if (graphView == null) { // just the first time
					paintMotiveAnalysis();
				}
			}
			//repaintMotiveAnalysis(); // done using MVP pattern
		} catch (Exception e) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.WARNING, null, e);
			ShowError.show(mainStage,"Cannot create form analysis", e);
		}*/
	}
	private void paintMotiveAnalysis() {
        throw new UnsupportedOperationException();
		/*IM3 if (graphView != null) {
			getScoreView().removeExternalNotationGroupView(graphView);
		}
		if (miniGraphPanel != null) {
			getScoreView().removeOutsidePanel(miniGraphPanel);
		}
		
		MelodicMotivesAnalysis motivesAnalysis = getSelectedModel().selectedAnalysisProperty().get().getMotivesAnalysis();
		if (motivesAnalysis != null) {
			MelodicMotivesGraphRenderer renderer = new MelodicMotivesGraphRenderer();
			DirectedGraphViewModel<MelodicMotiveNodeLabel, MotivesEdgeLabel> graphVM = renderer.render(motivesAnalysis.getGraph(), false);
			DoubleBinding scale1 = new SimpleDoubleProperty(1).multiply(getScoreView().emProperty());
			graphView = new DirectedGraphViewFX<>(this, graphVM, scale1, scale1);
			getScoreView().addExternalNotationGroupView(graphView);
			
			// draw miniature graph
			miniGraphPanel = new Pane();			
			miniGraphPanel.setMinHeight(100); //TODO
			miniGraphPanel.setMinWidth(100); //TODO
			DoubleBinding scaleX = miniGraphPanel.widthProperty().multiply(getScoreView().emProperty()).divide(getScoreView().widthProperty());
			DoubleBinding scaleY = miniGraphPanel.heightProperty().multiply(getScoreView().emProperty()).divide(getScoreView().heightProperty());
			MelodicMotivesGraphRenderer miniatureRenderer = new MelodicMotivesGraphRenderer();
			DirectedGraphViewModel<MelodicMotiveNodeLabel, MotivesEdgeLabel> miniatureGraphVM = miniatureRenderer.render(motivesAnalysis.getGraph(), true);
			DirectedGraphViewFX<MelodicMotiveNodeLabel, MotivesEdgeLabel> miniatureGraphView = new DirectedGraphViewFX<>(this, miniatureGraphVM, scaleX, scaleY);
			
			miniGraphPanel.getChildren().add(miniatureGraphView.getRoot());
			
			getScoreView().addPanelOutsideScoreView(miniGraphPanel, PanelPosition.eBottom);
			//addPanel(treeFX.getRoot(), PanelPosition.eTop);
			//debugPrintSceneTree();
			
			//MotivesView motivesView;
			//if (motivesAnalysis.getView() == null) {
			//	motivesView = new MotivesView(motivesAnalysis);
			//	motivesAnalysis.setView(motivesView);
			//	getScoreView().addExternalNotationGroupView(motivesView);
			//} else {
			//	motivesView = (MotivesView) motivesAnalysis.getView();
			//}
			//motivesView.repaint(getScoreView());
			
			//documentView.getScoreViewController().updateTree(getSelectedModel().getFormAnalyses().get(0).getTree());
		}*/
	}


	@FXML
	private void handleCreateSections(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 documentView.getScoreViewController().setCursor(Cursor.CROSSHAIR);
		documentView.getScoreViewController().getScoreView().getInteractionManager().restrictInteractionTo(ScoreAnalysisHookView.class);
		//TODO Añadir alguna ayuda al usuario diciendo que tiene que seleccionar ScoreAnalysisHooks
		documentView.getScoreViewController().getScoreView().getInteractionManager().registerHandler(InteractionEventStartHover.class, new IInteractionEventHandler<InteractionEventStartHover>() {
			@Override
			public void handle(InteractionEventStartHover event) {
				//if (event.getTarget() instanceof ScoreAnalysisHookView) 
				{
					documentView.getScoreViewController().setCursor(Cursor.H_RESIZE);
				}
			}
		});
		
		documentView.getScoreViewController().getScoreView().getInteractionManager().registerHandler(InteractionEventEndHover.class, new IInteractionEventHandler<InteractionEventEndHover>() {
			@Override
			public void handle(InteractionEventEndHover event) {
				documentView.getScoreViewController().setCursor(Cursor.CROSSHAIR);
			}
		});
		documentView.getScoreViewController().getScoreView().getInteractionManager().registerHandler(InteractionEventClick.class, new IInteractionEventHandler<InteractionEventClick>() {
			@Override
			public void handle(InteractionEventClick event) {
				//if (event.getTarget() instanceof ScoreAnalysisHookView) 
				{
					try {
						NameDescriptionColorDialog dlg = new NameDescriptionColorDialog(getContainedInWindow(), "Section");
						if (dlg.show()) {
							String name = dlg.getName();
							String description = dlg.getDescription();
							String color = dlg.getHexaColor();
							getSelectedModel().addSectionAt(name, ((ScoreAnalysisHookView)event.getTarget()).getModelSymbol(), description, color);
							paintFormAnalysisTree();
						}
					} catch (Exception e) {
						Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.WARNING, "Cannot create add section", e);
						ShowError.show(HierarchicalAnalsysisMainController.this, "Cannot create add section", e);
					}
				}
			}
		});
		*/
		/*
		try {
			getSelectedModel().createMotiveWithSelectedElements();
			documentView.getScoreViewController().updateTree(getSelectedModel().getFormAnalyses().get(0).getTree()); 
		} catch (Exception e) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.WARNING, null, e);
			ShowError.show(mainStage,"Cannot create form analysis", e);
		}*/
	}
	
	private void paintFormAnalysisTree() throws Exception {
		FormAnalysis formAnalysis = model.selectedAnalysisProperty().get().getFormAnalysis();
		if (formAnalysis.getTree() == null) {
			throw new Exception("null tree in form analysis");
		}
		if (!formAnalysis.getTree().isLeaf()) {
			paintOrUpdateTree(formAnalysis.getTree(), sliderVerticalTreeSeparator.valueProperty(), false);				
		}		
	}

    private void paintOrUpdateTree(Tree<FormAnalysisTreeNodeLabel> tree, DoubleProperty doubleProperty, boolean b) {
    }

    @FXML
	private void handleAddFormAnalysis(ActionEvent event) {
        throw new UnsupportedOperationException();
		/*IM3 if (ensureAnalysis()) {
			FormAndMotivesAnalysis analysis;
			try {
				String name = ShowInput.show(this, "Forms analysis", "Give a name to the analysis");
				if (name != null) {
					analysis = getSelectedModel().createNewAnalysis(name); // it adds to the observable list
					lvAnalyses.getSelectionModel().onSelect(analysis);
					paintFormAnalysisTree();
				}
			} catch (Exception e) {
				Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.WARNING, "Cannot create form analysis", e);
				ShowError.show(mainStage,"Cannot create form analysis", e);
			}
		}*/
	}
	
	@FXML
	private void handleRemoveFormAnalysis(ActionEvent event) {
		lvAnalyses.getItems().remove(lvAnalyses.getSelectionModel().getSelectedIndex());
	}	
		
	@FXML
	private void handleSave(ActionEvent event) {
		// TODO ¿Aquí o en un modelo?
        throw new UnsupportedOperationException();
		/*IM3 if (documentManager.getSelectedDocument().getDocument().getFile() == null
				|| documentManager.getSelectedDocument().getDocument().getFile().getName().endsWith("krn")
				|| documentManager.getSelectedDocument().getDocument().getFile().getName().endsWith("xml")
				) {
			saveDocumentAs();
		} else {
			saveDocument();
		}*/
		
		
	}	
		
	// TODO ¿Aquí o en un modelo E/S?
	private void saveDocumentAs() {
        throw new UnsupportedOperationException();
		/*IM3 OpenSaveFileDialog dlg = new OpenSaveFileDialog();
		File file = dlg.saveFile("Save hierarchical MEI file", "MEI files", "mei");
		if (file != null) {
			documentManager.getSelectedDocument().getDocument().setFile(file);
			saveDocument();
		}*/
	}

	// TODO ¿Aquí o en un modelo E/S?
	private void saveDocument() {
        throw new UnsupportedOperationException();
		/*IM3 try {
			getSelectedModel().save();
			
			documentManager.getSelectedDocument().resetNeedsSave();
		} catch (Exception ex) {
			Logger.getLogger(HierarchicalAnalsysisMainController.class.getName()).log(Level.SEVERE, null, ex);
			ShowError.show(mainStage,"Cannot save document", ex);
		}*/
	}

}
