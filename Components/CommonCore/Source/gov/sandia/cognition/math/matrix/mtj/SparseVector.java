/*
 * File:                SparseVector.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A vector that only stores the nonzero elements, relies on MTJ's
 * SparseVector. It has fast traversal by skipping nonzero elements but has slow 
 * indexing.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-27",
    changesNeeded=false,
    comments="Looks good."
)
public class SparseVector 
    extends AbstractMTJVector
{
    /**
     * Creates a new instance of SparseVector with no initial size.
     *
     * @param numDimensions Maximum number of entries in the SparseVector.
     */
    protected SparseVector(
        int numDimensions)
    {
        this(numDimensions, 0);
    }
    
    /**
     * Creates a new instance of SparseVector with a specified initial size.
     * 
     * @param numDimensions Maximum number of entries in the SparseVector.
     * @param initialNonZeros Initial size of the SparseVector.
     */
    protected SparseVector(
        int numDimensions,
        int initialNonZeros)
    {
        this(new no.uib.cipr.matrix.sparse.SparseVector( 
            numDimensions, initialNonZeros));
    }
    
    /**
     * Creates a new copy of SparseVector.
     *
     * @param internalVector Internal MTJ-based vector to set into this.
     */
    protected SparseVector(
        no.uib.cipr.matrix.sparse.SparseVector internalVector)
    {
        super(internalVector);
    }

    /**
     * Creates a new copy of SparseVector.
     *
     * @param vector Vector to copy into this, will not be modified.
     */
    protected SparseVector(
        Vector vector)
    {
        this(vector.getDimensionality());
        
        for ( VectorEntry e : vector )
        {
            double value = e.getValue();
            if ( value != 0.0 )
            {
                this.setElement(e.getIndex(), value);
            }
        }
    }
    
    /**
     * Gets the number of elements used inside the sparse vector, equals the
     * allocation size of the vector.
     *
     * @return The number of nonzero elements in the SparseVector.
     */
    public int getNumElementsUsed()
    {
        return this.getInternalVector().getUsed();
    }

    @Override
    public void setElement(
        int index,
        double value)
    {
        // Only set a value in a SparseVector if it's different than what's
        // in there already!
        // (This is to prevent us from adding zeros into the SparseVector
        // unnecessarily)
        double existing = this.getElement( index );
        if( existing != value )
        {
            super.setElement( index, value );
        }
        
    }
    
    @Override
    protected no.uib.cipr.matrix.sparse.SparseVector getInternalVector()
    {
        return (no.uib.cipr.matrix.sparse.SparseVector) 
            super.getInternalVector();
    }

    /**
     * Setter for the internal MTJ vector.
     *
     * @param internalVector Internal MTJ-based vector.
     */
    protected void setInternalVector(
        no.uib.cipr.matrix.sparse.SparseVector internalVector)
    {
        super.setInternalVector(internalVector);
    }

    public SparseRowMatrix outerProduct(
        final AbstractMTJVector other)
    {  
        int numRows = this.getDimensionality();
        int numColumns = other.getDimensionality();
        SparseRowMatrix result = new SparseRowMatrix(numRows, numColumns);
        
        for ( VectorEntry e : this )
        {
            int i = e.getIndex();
            
            for ( VectorEntry o : other )
            {
                int j = o.getIndex();
                
                result.setElement(i, j, e.getValue() * o.getValue());
            }
        }
        
        return result;
    }

    /**
     * Prints the SparseVector in "Index: Value" format.
     *
     * @return String containing the representation of the SparseVector
     */
    @Override
    public String toString()
    {
        final StringBuffer result = new StringBuffer();
        
        for ( VectorEntry e : this )
        {
            result.append("(" + e.getIndex() + "): " + e.getValue() + "\n");
        }
        
        return result.toString();
    }


    /**
     * Compacts the SparseVector, getting rid of any zero'ed elements.
     */
    public void compact()
    {
        this.getInternalVector().compact();
    }

    public SparseVector stack(
        Vector other)
    {
        int d1 = this.getDimensionality();
        int d2 = other.getDimensionality();
        
        SparseVector stacked = new SparseVector(d1 + d2);
        for ( VectorEntry e : this )
        {
            stacked.setElement(e.getIndex(), e.getValue());
        }
        
        for ( VectorEntry e : other )
        {
            stacked.setElement(d1 + e.getIndex(), e.getValue());
        }
        
        return stacked;
    }
    
    /**
     * This method provides custom serialization for the class since the MTJ
     * class does not implement Serializable.
     *
     * @param out The ObjectOutputStream to write this object to.
     * @throws java.io.IOException If there is an error with the stream.
     */
    private void writeObject(
        ObjectOutputStream out) 
        throws IOException 
    {

        // First compact the SparseVector
        this.compact();

        // Do the default stuff.
        out.defaultWriteObject();
        
        // Get all the data to write.
        int dimensionality = this.getDimensionality();
        int[] index = this.getInternalVector().getIndex();
        double[] data = this.getInternalVector().getData();
        
        // Manually serialize the super class.
        out.writeObject( dimensionality );
        out.writeObject( index );
        out.writeObject( data );
    }    
    
    /**
     * This method provides custom deserialization for the class since the MTJ
     * class does not implement Serializable.
     *
     * @param in The ObjectInputStream to read the object from.
     * @throws java.io.IOException If there is an error reading the object.
     * @throws java.lang.ClassNotFoundException If a class cannot be found.
     */
    private void readObject(
        ObjectInputStream in) 
        throws IOException, ClassNotFoundException 
    {
        // First do the default stuff.
        in.defaultReadObject();
        
        // Next read in the data.
        int dimensionality = (Integer) in.readObject();
        int[] index = (int[]) in.readObject();
        double[] data = (double[]) in.readObject();
        
        // Set the internal vector.
        boolean deepCopy = false;
        this.setInternalVector( 
            new no.uib.cipr.matrix.sparse.SparseVector(
                dimensionality, index, data, deepCopy));

        this.compact();
    }

    public SparseVector subVector(
        int minIndex,
        int maxIndex)
    {
        int M = maxIndex - minIndex + 1;
        SparseVector subvector = new SparseVector( M );
        for( int i = 0; i < M; i++ )
        {
            double value = this.getElement(i + minIndex);
            if( value != 0.0 )
            {
                subvector.setElement( i, value );
            }
        }
        
        return subvector;
    }

}
