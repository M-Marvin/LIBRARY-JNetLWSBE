package jnet.util;

/**
 * Only used for compatibility between the different Vector types (Integer, Float and Double)
 * @author M_Marvin
 *
 * @param <T> Number format
 */
public interface IVector2D<N extends Number> extends IVecBase<N> {
	
	public default N getVecZ() { throw new UnsupportedOperationException(); }
	
}
