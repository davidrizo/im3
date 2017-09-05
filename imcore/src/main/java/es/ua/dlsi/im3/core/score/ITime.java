package es.ua.dlsi.im3.core.score;

public interface ITime extends Comparable<ITime> {
	ITime add(ITime a);
	ITime substract(ITime a);
}
