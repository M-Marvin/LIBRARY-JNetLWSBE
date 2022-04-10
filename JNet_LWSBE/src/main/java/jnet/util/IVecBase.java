package jnet.util;

/**
 * Only used for compatibility between 2d and 2d Vector types
 * @author M_Marvin
 *
 * @param <T> Number format
 */
public interface IVecBase<N extends Number> {
	public N getVecX();
	public N getVecY();
	public N getVecZ();
}
