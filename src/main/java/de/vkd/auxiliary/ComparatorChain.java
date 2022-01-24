package de.vkd.auxiliary;
import java.util.*;

/**
 * Edited by Felix Blanke, changing all Comparators to NamedComparators.
 * Also added getComparatorChain().
 * A {@link Comparator} that puts one or more {@code Comparator}s in a sequence.
 * If a {@code Comparator} returns zero the next {@code Comparator} is taken.
 * @author Rheinwerk Computing: "Java ist auch eine Insel" by Christian Ullenboom 
 */
public class ComparatorChain<E> implements Comparator<E>
{
	private List<NamedComparator<E>> comparatorChain = new ArrayList<NamedComparator<E>>();

	/**
	 * Construct a new comparator chain from the given {@code Comparator}s.
	 * The argument is not allowed to be {@code null}.
	 * @param comparators Sequence of {@code Comparator}s
	 */
	@SafeVarargs  // ab Java 7
	public ComparatorChain( NamedComparator<E>... comparators )
	{
		if ( comparators == null )
			throw new IllegalArgumentException( "Argument is not allowed to be null" );
		Collections.addAll( comparatorChain, comparators );
	}

	/**
	 * Adds a {@link Comparator} to the end of the chain.
	 * The argument is not allowed to be {@code null}.
	 * @param comparator {@code Comparator} to add
	 */
	public void addComparator( NamedComparator<E> comparator )
	{
		if ( comparator == null )
			throw new IllegalArgumentException( "Argument is not allowed to be null" );

		comparatorChain.add( comparator );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare( E o1, E o2 )
	{
		if ( comparatorChain.isEmpty() )
		throw new UnsupportedOperationException(
				"Unable to compare without a Comparator in the chain" );
		
		for ( NamedComparator<E> comparator : comparatorChain )
		{
			int order = comparator.compare( o1, o2 );
			if ( order != 0 )
				return order;
		}
		return 0;
	}
	/**
	 * Clones the underlying {@code List<NamedComparator>} used to implement the {@code ComparatorChain} and returns the clone.
	 * @return A clone of the internal {@code ComparatorChain}
	 */
	public List<NamedComparator<E>> getComparatorChain() {
		List<NamedComparator<E>> list = new ArrayList<NamedComparator<E>>();
		list.addAll(comparatorChain);
		return list;
	}
}