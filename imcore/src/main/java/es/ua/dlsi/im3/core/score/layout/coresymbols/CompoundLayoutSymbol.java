package es.ua.dlsi.im3.core.score.layout.coresymbols;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ITimedElementInStaff;
import es.ua.dlsi.im3.core.score.layout.LayoutSymbolInStaff;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Component;
import es.ua.dlsi.im3.core.score.layout.graphics.GraphicsElement;
import es.ua.dlsi.im3.core.score.layout.graphics.Group;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CompoundLayoutSymbol<CoreSymbolType extends ITimedElementInStaff> extends LayoutSymbolInStaff<CoreSymbolType> {
    protected Collection<Component> components;
    protected Group group;

    public CompoundLayoutSymbol(LayoutStaff layoutStaff, CoreSymbolType coreSymbol) {
        super(layoutStaff, coreSymbol);
        components = new ArrayList<>();
        group = new Group();
    }

    @Override
    public GraphicsElement getGraphics() {
        return group;
    }

    /**
     * It is invoked previous to the export to the view
     * @throws IM3Exception
     */
    /*public void computeLayout() throws IM3Exception {
        computeLayoutOfComponents();
    }

    protected void computeLayoutOfComponents() throws IM3Exception {
        for (Component component : components) {
            component.computeLayout();
        }
    }*/

    protected void addComponent(Component component) {
        components.add(component);
        group.add(component.getGraphics());
    }

    /**
     * Recompute using component local information because we cannot use the layout information as the layout algorithm
     * uses this method to compute the layout
     * @return
     */
    /*@Override
    public double getWidth() {
        double fromX = Double.MAX_VALUE;
        double toX = Double.MIN_VALUE;

        for (Component component : components) {
            fromX = Math.min(fromX, component.getRelativeX());
            toX = Math.max(toX, component.getRelativeX() + component.getWidth());
        }

        return toX - fromX;
    }*/

}
