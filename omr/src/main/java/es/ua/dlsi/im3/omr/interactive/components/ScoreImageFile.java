/*
 * Copyright (C) 2016 David Rizo Valero
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
package es.ua.dlsi.im3.omr.interactive.components;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author drizo
 */
public class ScoreImageFile {
    private final File file;
    /**
     * Used for the GUI
     */
    private final ObservableObjectValue<Image> image;
    private final ObservableObjectValue<String> name;
    private final ObjectProperty<ScoreImageTags> tagsFile;
    //TODO podríamos no tener que mantener dos objetos referentes a la imagen
    /**
     * Used to extract pixels from it
     */
    BufferedImage bufferedImage;

    public ScoreImageFile(File file) throws MalformedURLException, IOException {
        this.file = file;
        this.name = new SimpleObjectProperty<>(file.getName());
        Image img = new Image(file.toURI().toURL().toString());
        bufferedImage = ImageIO.read(file);
        Logger.getLogger(ScoreImageFile.class.getName()).log(Level.INFO, "Loading image {0}, width={1}, height={2}", new Object[]{file.getAbsolutePath(), img.getWidth(), img.getHeight()});
        this.image = new SimpleObjectProperty<>(img);
        tagsFile = new SimpleObjectProperty<>();
    }

    public ObservableObjectValue<Image> imageProperty() {
	return image;
    }

    public BufferedImage getBufferedImage() {
	return bufferedImage;
    }
    
    public ObservableObjectValue<String> nameProperty() {
	return name;
    }

    public String getName() {
	return name.get();
    }
    
    public void addTagsFile(ScoreImageTags sit) {
	    tagsFile.setValue(sit);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Added " + sit.getNumTags() + " tags");
    }

    public ObjectProperty<ScoreImageTags> tagsFileProperty() {
        return tagsFile;
    }

    public ScoreImageTags addNewScoreImageTags() {
	    File newFile = new File(file.getParent(), file.getName() + ".txt");
	    ScoreImageTags sit = new ScoreImageTags(newFile);
	    tagsFile.set(sit);
	    return sit;
    }
    
}
