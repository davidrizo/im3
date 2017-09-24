package es.ua.dlsi.im3.core.score.layout;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutStaff;

import java.lang.reflect.Constructor;

/**
 * It creates a core symbol object based on the name. E.g. given an object of class XYZ it creates a core symbol object
 * of class LayoutXYZ
 */
public class LayoutSymbolFactory {
    private static final String PREFIX = "es.ua.dlsi.im3.core.score.layout.coresymbols.LayoutCore";
    public LayoutCoreSymbol createCoreSymbol(LayoutFont layoutFont, ITimedElementInStaff coreSymbol) throws IM3Exception {

        Class coreSymbolClass = coreSymbol.getClass();
        Class clazz = null;
        do {
            String className = PREFIX + coreSymbolClass.getSimpleName();
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                coreSymbolClass = coreSymbolClass.getSuperclass();

            }
        } while (clazz == null && coreSymbolClass != null);

        if (clazz == null) {
            throw new IM3Exception("There is not a layout class for " + coreSymbol.getClass().getName());
        }

        Constructor<LayoutCoreSymbol> constructor;
        try {
            constructor = clazz.getConstructor(LayoutFont.class, coreSymbolClass);
        } catch (NoSuchMethodException e) {
            throw new IM3Exception("There layout class " + clazz.getName() + " has not the required constructor (LayoutFont, " + coreSymbolClass.getName() + ")");
        }

        try {
            LayoutCoreSymbol result = constructor.newInstance(layoutFont, coreSymbol);
            return  result;
        } catch (Exception e) {
            throw new IM3Exception("Cannot instantiate the layout object", e);
        }
    }
}
