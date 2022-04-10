package jnet.util;

/**
 * Only used for compatibility between the different Vector types (Integer, Float and Double)
 * @author M_Marvin
 *
 * @param <T>
 */
public interface IVector3<T> {
	public T getVecX();
	public T getVecY();
	public T getVecZ();
}
