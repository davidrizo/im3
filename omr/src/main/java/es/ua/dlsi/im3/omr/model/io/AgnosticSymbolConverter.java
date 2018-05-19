package es.ua.dlsi.im3.omr.model.io;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.IM3RuntimeException;
import es.ua.dlsi.im3.core.score.PositionInStaff;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbol;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;

/**
 * @autor drizo
 */
public class AgnosticSymbolConverter implements Converter {
    AgnosticVersion agnosticVersion;

    public AgnosticSymbolConverter(AgnosticVersion agnosticVersion) {
        this.agnosticVersion = agnosticVersion;
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
        AgnosticSymbol agnosticSymbol = (AgnosticSymbol) o;
        hierarchicalStreamWriter.startNode("agnosticSymbol");
        hierarchicalStreamWriter.setValue(agnosticSymbol.getAgnosticString());
        hierarchicalStreamWriter.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
        hierarchicalStreamReader.moveDown();
        String value = hierarchicalStreamReader.getValue();
        hierarchicalStreamReader.moveUp();
        try {
            return AgnosticSymbol.parseString(value);
        } catch (IM3Exception e) {
            throw new IM3RuntimeException("Cannot parse agnostic symbol '" + value + "'");
        }
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass == AgnosticSymbol.class;
    }
}
