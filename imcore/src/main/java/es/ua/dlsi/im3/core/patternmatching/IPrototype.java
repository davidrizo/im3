package es.ua.dlsi.im3.core.patternmatching;

/**
 * @autor drizo
 */
public interface IPrototype<ClassType> {
    /**
     * May be null for not classified instances
     * @return
     */
    ClassType getPrototypeClass();
}
