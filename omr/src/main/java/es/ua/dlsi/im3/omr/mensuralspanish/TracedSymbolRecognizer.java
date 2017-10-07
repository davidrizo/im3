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
package es.ua.dlsi.im3.omr.mensuralspanish;


import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Staff;
import es.ua.dlsi.im3.omr.IStringToSymbolFactory;
import es.ua.dlsi.im3.omr.PositionedSymbolType;
import es.ua.dlsi.im3.omr.model.Symbol;
import es.ua.dlsi.im3.omr.traced.ClassifierFactory;
import es.ua.dlsi.im3.omr.traced.IBimodalDatasetReader;
import es.ua.dlsi.im3.omr.traced.IClassifier;
import es.ua.dlsi.im3.omr.traced.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author drizo
 */
public class TracedSymbolRecognizer<SymbolType> extends SymbolRecognizer {
	private final static int RESIZE_W = 30;
	private final static int RESIZE_H = 30;
	IClassifier classifier;

	public TracedSymbolRecognizer(Staff staff, IBimodalDatasetReader<SymbolType> reader, IStringToSymbolFactory<SymbolType> symbolFactory) throws IM3Exception {
		super(staff);
		classifier = ClassifierFactory.getInstance().createClassifier(reader, symbolFactory);
	}

	/**
	 * 
	 * @param symbol
	 * @return Grayscale bitmap
	 * @throws IM3Exception
	 */
	static int[][] symbolImageToMat(Symbol symbol) throws IM3Exception, IOException {
		if (symbol.getSymbolImage() == null) {
			throw new IM3Exception("The symbol image is null");
		}
		BufferedImage originalImage = symbol.getSymbolImage();
		BufferedImage scaledImage = ImageUtils.rescaleToGray(originalImage, RESIZE_W, RESIZE_H);
		return ImageUtils.getGrayscalePixels(scaledImage);
		// return ImageUtils.getGrayscalePixels(originalImage);

	}

	@Override
	public ArrayList<PositionedSymbolType> recognize(Symbol symbol) throws IM3Exception {
		List<PositionedSymbolType> recognizedSymbolTypes;
		try {
			recognizedSymbolTypes = classifier.classify(symbolImageToMat(symbol), symbol.getStrokes());
		} catch (IOException ex) {
			Logger.getLogger(TracedSymbolRecognizer.class.getName()).log(Level.SEVERE, null, ex);
			throw new IM3Exception(ex);
		}

		ArrayList<PositionedSymbolType> result = new ArrayList<>();
		for (PositionedSymbolType recognizedSymbolType : recognizedSymbolTypes) {
			result.add(recognizedSymbolType);
		}

		return result;

	}

	/*
	 * private Mat image2Mat(Bitmap bitmap) { Mat tmp = new Mat (b.getWidth(),
	 * b.getHeight(), CvType.CV_8UC1); Utils.bitmapToMat(b, tmp);
	 * Imgproc.resize(sample.image, sample.image, new
	 * Size(ResizedNN.rows,ResizedNN.cols), 0, 0, Imgproc.INTER_NEAREST);
	 * Imgproc.cvtColor(sample.image, sample.image, Imgproc.COLOR_RGB2GRAY);
	 * 
	 * Resize: 30x30
	 * 
	 * }
	 * 
	 * 
	 * private ArrayList<SymbolType> recognize(????) {
	 * 
	 * 
	 * }
	 */

	@Override
	public void learn(File file) throws IOException {
		classifier.learn(file);
	}

	@Override
	public void learn(InputStream is) throws IOException {
		classifier.learn(is);
	}

    @Override
    public Long getNumberOfTrainingSymbols() {
        return classifier.getNumberOfTrainingSymbols();
    }

}
