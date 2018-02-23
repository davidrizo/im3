package demo;/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import eu.mihosoft.vrl.workflow.fx.ScalableContentPane;
import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.mihosoft.vrl.workflow.FlowFactory;
import eu.mihosoft.vrl.workflow.VFlow;
import eu.mihosoft.vrl.workflow.VNode;
import eu.mihosoft.vrl.workflow.fx.FXSkinFactory;
import javafx.scene.Scene;
//TODO import jfxtras.labs.scene.layout.ScalableContentPane;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class Main extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // create scalable root pane
        //TODO Dejar conforme estaba ScalableContentPane canvas = new ScalableContentPane();
        Pane canvas = new Pane();
        
        // define background style
        canvas.setStyle("-fx-background-color: linear-gradient(to bottom, rgb(10,32,60), rgb(42,52,120));");

        // create a new flow object
        VFlow flow = FlowFactory.newFlow();

        // make it visible
        flow.setVisible(true);

        // add two nodes to the flow
        VNode n1 = flow.newNode();
        VNode n2 = flow.newNode();

        // specify input & output capabilities...
        
        // ... for node 1
        n1.addInput("data");
        n1.addOutput("data");
        
        // ... for node 2
        n2.addInput("data");
        n2.addOutput("data");

        // create skin factory for flow visualization
        //TODO FXSkinFactory fXSkinFactory = new FXSkinFactory(canvas.getContentPane());
        FXSkinFactory fXSkinFactory = new FXSkinFactory(canvas);
        
        // generate the ui for the flow
        flow.setSkinFactories(fXSkinFactory);

        // the usual application setup
        Scene scene = new Scene(canvas, 800, 800);
        primaryStage.setTitle("VWorkflows Tutorial 01");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
