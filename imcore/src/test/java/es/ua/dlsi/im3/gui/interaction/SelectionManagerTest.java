package es.ua.dlsi.im3.gui.interaction;

import es.ua.dlsi.im3.core.IM3Exception;
import org.junit.Test;

import static org.junit.Assert.*;

public class SelectionManagerTest {
    class A implements ISelectable {
        String id;

        public A(String id) {
            this.id = id;
        }

        @Override
        public String getUniqueID() {
            return id;
        }

        @Override
        public ISelectableTraversable getSelectionParent() {
            return null;
        }

        @Override
        public void onSelect() {

        }

        @Override
        public void onUnselect() {

        }


        @Override
        public void onStartHover() {

        }

        @Override
        public void onEndHover() {

        }

    };

    class B implements ISelectable {
        String id;

        public B(String id) {
            this.id = id;
        }

        @Override
        public String getUniqueID() {
            return id;
        }

        @Override
        public ISelectableTraversable getSelectionParent() {
            return null;
        }

        @Override
        public void onSelect() {

        }

        @Override
        public void onUnselect() {

        }


        @Override
        public void onStartHover() {

        }

        @Override
        public void onEndHover() {

        }
    }

    class AA extends A {
        public AA(String id) {
            super(id);
        }
    }

    class AAA extends AA {
        public AAA(String id) {
            super(id);
        }
    }

    @Test
    public void getCommonBaseClass() {
        SelectionManager selectionManager = new SelectionManager();

        A a1 = new A("a1");
        A a2 = new A("a2");
        AA aa1 = new AA("aa1");
        AA aa2 = new AA("aa2");
        AAA aaa1 = new AAA("aaa1");
        B b1 = new B("b1");
        B b2 = new B("b2");

        selectionManager.select(a1, a2, aa1, aa2, aaa1);
        assertEquals("Base A", A.class, selectionManager.getCommonBaseClass());
        assertTrue(selectionManager.isCommonBaseClass(A.class));

        selectionManager.select(b1, b2);
        assertEquals("Base B", B.class, selectionManager.getCommonBaseClass());
        assertTrue(selectionManager.isCommonBaseClass(B.class));

        selectionManager.select(a1, b1);
        assertEquals("Base Object", Object.class, selectionManager.getCommonBaseClass());

        selectionManager.clearSelection();
        assertNull("No selection", selectionManager.getCommonBaseClass());

        selectionManager.select(aa1, aa2, aaa1);
        assertEquals("Base AA", AA.class, selectionManager.getCommonBaseClass());
        assertTrue(selectionManager.isCommonBaseClass(AA.class));

    }
}