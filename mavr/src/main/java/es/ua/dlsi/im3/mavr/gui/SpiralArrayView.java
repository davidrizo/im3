package es.ua.dlsi.im3.mavr.gui;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.PitchClass;
import es.ua.dlsi.im3.core.score.PitchClasses;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @autor drizo
 */
public class SpiralArrayView {
    private final VBox layout;
    private final Node meshView;
    private Sphere [] pitchClassesSpheres;

    public SpiralArrayView(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) throws IM3Exception {
        meshView = loadMeshView();
        Group group = buildScene();

        RotateTransition rotate = rotate3dGroup(group);

        layout = new VBox(
                createControls(rotate),
                createScene3D(group, width, height)
        );
        }

    private MeshView drawChord(int ... pitchClasses) throws IM3Exception {
        if (pitchClasses.length != 3) {
            throw new IM3Exception("Cannot build non triangular meshes yet");
        }
        TriangleMesh triangleMesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        MeshView meshView = new MeshView(triangleMesh);

        for (int pitchClass: pitchClasses) {
            float x = (float) pitchClassesSpheres[pitchClass].getTranslateX();
            float y = (float) pitchClassesSpheres[pitchClass].getTranslateY();
            float z = (float) pitchClassesSpheres[pitchClass].getTranslateZ();
            triangleMesh.getPoints().addAll(x, y, z);
        }
        return meshView;
    }

    public Node getRoot() {
        return layout;
    }

    private Group buildScene() throws IM3Exception {
        /*meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        meshView.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
        meshView.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
        meshView.setScaleX(MODEL_SCALE_FACTOR);
        meshView.setScaleY(MODEL_SCALE_FACTOR);
        meshView.setScaleZ(MODEL_SCALE_FACTOR);*/
        MeshView chordView = drawChord(0, 4, 7); // Tonic

        return new Group(meshView, chordView);
    }


    private Node loadMeshView() {
        double r = 50; // radius of the spiral, and h is the "rise" of the spiral.
        double h = 50; // h is the "rise" of the spiral

        Group spiralArrayGroup = new Group();

        PitchClass [] pitchClasses = new PitchClass[] {
                PitchClasses.C.getPitchClass(),
                PitchClasses.C_SHARP.getPitchClass(),
                PitchClasses.D.getPitchClass(),
                PitchClasses.D_SHARP.getPitchClass(),
                PitchClasses.E.getPitchClass(),
                PitchClasses.F.getPitchClass(),
                PitchClasses.F_SHARP.getPitchClass(),
                PitchClasses.G.getPitchClass(),
                PitchClasses.G_SHARP.getPitchClass(),
                PitchClasses.A.getPitchClass(),
                PitchClasses.A_SHARP.getPitchClass(),
                PitchClasses.B.getPitchClass()
        };

        pitchClassesSpheres = new Sphere[12];
        for (int pc=0; pc<12; pc++) {
            Sphere sphere1 = new Sphere();
            pitchClassesSpheres[pc] = sphere1;
            sphere1.setRadius(20.0);
            sphere1.setMaterial(new PhongMaterial(Color.LIGHTBLUE));

            //sphere1.setCullFace(CullFace.FRONT);

            // see https://en.wikipedia.org/wiki/Spiral_array_model
            double x = r * Math.sin(pc * Math.PI/2);
            double y = r * Math.cos(pc * Math.PI/2);
            double z = pc * h;

            sphere1.setTranslateX(x);
            sphere1.setTranslateY(y);
            sphere1.setTranslateZ(z);

            spiralArrayGroup.getChildren().add(sphere1);

            Tooltip t = new Tooltip("Pitch class " + pitchClasses[pc]);
            Tooltip.install(sphere1, t);
        }
        spiralArrayGroup.setTranslateX(400);
        spiralArrayGroup.setTranslateY(900);
        Rotate rotate = new Rotate();
        rotate.setAxis(Rotate.X_AXIS);
        rotate.setAngle(90);
        spiralArrayGroup.getTransforms().add(rotate);
        return spiralArrayGroup;
    }

    /*private MeshView loadMeshView() {
        float[] points = {
                -5, 5, 0,
                -5, -5, 0,
                5, 5, 0,
                5, -5, 0
        };
        float[] texCoords = {
                1, 1,
                1, 0,
                0, 1,
                0, 0
        };
        int[] faces = {
                2, 2, 1, 1, 0, 0,
                2, 2, 3, 3, 1, 1
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        return new MeshView(mesh);
    }*/

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);

        return rotate;
    }
    private SubScene createScene3D(Group group, ObservableValue<? extends Number> width, ObservableValue<? extends Number> height) {
        SubScene scene3d = new SubScene(group, 100, 100);
        scene3d.widthProperty().bind(width);
        scene3d.heightProperty().bind(height);

        scene3d.setFill(Color.rgb(10, 10, 40));
        scene3d.setCamera(new PerspectiveCamera());
        return scene3d;
    }
    private VBox createControls(RotateTransition rotateTransition) {
        CheckBox rotate = new CheckBox("Rotate");
        rotate.selectedProperty().addListener(observable -> {
            if (rotate.isSelected()) {
                rotateTransition.play();
            } else {
                rotateTransition.pause();
            }
        });

        VBox controls = new VBox(10, rotate);
        controls.setPadding(new Insets(10));
        return controls;
    }

}
