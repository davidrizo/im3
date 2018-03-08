package es.ua.dlsi.im3.visual;

import es.ua.dlsi.im3.visual.nodes.MusicXMLImportNode;
import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainController implements Initializable{
    @FXML
    Pane mainPane;

    private Model model;
    private VFlow flow;
    private HashMap<String, Node> nodeHashMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new Model();
        nodeHashMap = new HashMap<>();
        flow = FlowFactory.newFlow();
        flow.setVisible(true);
        FXSkinFactory fXSkinFactory = new FXSkinFactory(mainPane);
        flow.setSkinFactories(fXSkinFactory);
    }

    @FXML
    private void handleAddMusicXML() {
        VNode vnode = flow.newNode();
        vnode.setTitle("Import MusicXML");
        vnode.addInput("filename");
        vnode.addOutput("scoreSong");

        Node node = new MusicXMLImportNode(vnode);
        model.addNode(node);
        nodeHashMap.put(vnode.getId(), node);
    }
}
