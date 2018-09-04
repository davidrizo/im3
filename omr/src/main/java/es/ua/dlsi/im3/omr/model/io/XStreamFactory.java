package es.ua.dlsi.im3.omr.model.io;

import com.thoughtworks.xstream.XStream;
import es.ua.dlsi.im3.core.adt.graphics.BoundingBox;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.model.entities.*;

/**
 * @autor drizo
 */
public class XStreamFactory {
    public static XStream create(AgnosticVersion agnosticVersion) {
        //SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        //sorter.registerFieldOrder(Project.class, new String[] { "instruments", "imagesold"}); // make instruments in project appear before the ones in pages
        //XStream xStream = new XStream();
        //xStream.new Sun14ReflectionProvider(new FieldDictionary(sorter)));

        XStream xStream = new XStream();
        Package p = Project.class.getPackage();
        xStream.alias("project", Project.class);
        xStream.alias("projectversion", ProjectVersion.class);
        xStream.alias("notationType", NotationType.class);
        xStream.alias("image", Image.class);
        xStream.alias("instrument", Instrument.class);
        xStream.alias("page", Page.class);
        xStream.alias("boundingbox", BoundingBox.class);
        xStream.alias("region", Region.class);
        xStream.alias("regiontype", RegionType.class);
        xStream.alias("symbol", Symbol.class);
        xStream.alias("stroke", Stroke.class);
        //xStream.alias("rasterimage", RasterImage.class);
        //xStream.alias("rastermonochromeimage", RasterMonochromeImage.class);
        xStream.alias("point", Point.class);
        //xStream.setMode(XStream.NO_REFERENCES);
        xStream.setMode(XStream.ID_REFERENCES);
        xStream.registerConverter(new AgnosticSymbolConverter(agnosticVersion));

        return xStream;
    }

}
