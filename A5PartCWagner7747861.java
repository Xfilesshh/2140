import java.io.*;
import java.util.*;

/**
 /*** A5PartCWagner7747861
 * COMP 2140   SECTION A01
 * INSTRUCTOR    Helen Cameron
 * ASSIGNMENT    Assignment 5 (Part C)
 * Natasha Wagner 7747861
 * December 4, 2016
 * 
 *
 * PURPOSE: To read in lists of numbers and sort them with
 *          heap sort.
 */

public class A5PartCWagner7747861
{
  
  /*******************************************************************
    * main                                                             *
    *******************************************************************/
  public static void main( String[] args )
  {
    // Print output headers.
    System.out.println( );
    System.out.println( "Comp 2140  Assignment 5 Part C   Fall 2016" );
    System.out.println( "Sorting arrays with heap sort\n" );
    
    processAllArrays();
    
    System.out.println("\nProgram ends.");
    System.out.println( );
    
  } // end main
  
  /*******************************************************************
    * processAllArrays                                                 *
    *                                                                  *
    * Reads in the number of unsorted arrays in the input.             *
    * Then reads in that many arrays, sorting and printing each one.   *
    * (Each array is on one line, and begins with the number of items  *
    * in the array.)                                                   *
    *******************************************************************/
  private static void processAllArrays()
  {
    Scanner inFile;  // Read ints from the input file using a Scanner
    Heap myHeap;
    int numArrays;
    
    try
    {
      
      inFile = new Scanner( new File( "inputFile5C.txt" ) );
      numArrays = inFile.nextInt();
      
      for ( int i = 0; i < numArrays; i++ )
      {
        // Read in one array and make it the array in a Heap.
        
        myHeap = new Heap( readArray( inFile ) );
        
        // Print out unsorted array.
        
        System.out.println( "Unsorted array:" );
        myHeap.printHeap();
        
        // Sort array.
        
        myHeap.heapSort();
        
        // Print sorted array.
        
        System.out.println( "Sorted array:" );
        myHeap.printHeap();
        System.out.println();
      } // end for
      
    } // end try
    
    catch (IOException ex)
    {
      System.out.println( "I/O error: " + ex.getMessage() );
    }
    
  } // end processAllArrays
  
  /*******************************************************************
    * readArray                                                        *
    *                                                                  *
    * Reads in one array from file inFile and returns it.              *
    * First, read in the length of the array and construct an array of *
    * that size.                                                       *
    * Second, read in exactly that many numbers into the array.        *
    *******************************************************************/
  private static int[] readArray( Scanner inFile )
  {  
    int[] nums;
    
    // Read the data to be inserted into the heap.
    
    // Get the number of elements to read in and allocate
    // a heapArray of that size.
    
    nums = new int[ inFile.nextInt() ];
    for ( int i = 0; i < nums.length; i++ )
    {
      nums[ i ] =  inFile.nextInt();
    }
    
    return nums;
    
  } // end readArray
  
} // end class A5PartC


/***********************************************************************
  ************************************************************************
  *
  *  The Heap class
  *
  ***********************************************************************
  ************************************************************************/
class Heap
{
  
  private int[] heapArray; // The array containing the heap.
  private int heapSize; // The number of elements currently stored in the heap.
  
  /*******************************************************************
    * CONSTRUCTOR                                                      *
    *                                                                  *
    * Purpose: Accepts an array of unordered ints, which it assigns    *
    *          to heapArray. (Used only if the goal is to heap sort    *
    *          the array.)                                             *
    *******************************************************************/
  public Heap( int[] array )
  {
    heapArray = array;
    heapSize = array.length;
  } // end heap constructor
  
  
  /*******************************************************************
    * CONSTRUCTOR                                                      *
    *                                                                  *
    * Purpose: Accepts an array size.  It constructs an empty heap     *
    *          with an array of the given size (and of course          *
    *          heapSize is set to 0 to indicate that no items are      *
    *          currently stored in the heap).                          *
    *******************************************************************/
  public Heap( int size )
  {
    heapArray = new int[ size ];
    heapSize = 0;
  }
  
  /*******************************************************************
    * heapify                                                          *
    *                                                                  *
    * Purpose: Turns an unordered array into a heap ordered array:     *
    *          Starting at the parents of the leaves and working up    *
    *          to the root, sift each item down.  When the item at     *
    *          position i is sifted down, its child(ren) are already   *
    *          heap ordered.  Thus, the process of sifting down is     *
    *          exactly like sifting down in the deleteMax operation.   *
    *          After the root is sifted down, the whole array is heap  *
    *          ordered.                                                *
    *******************************************************************/
  private void heapify()
  {
    for ( int i = heapSize/2; i >= 0; i-- )
    {
      // Loop invariant: the one or two children of heapArray[i]
      // are already heaps.  Sift down hepArray[i] to make
      // it plus its children into a valid heap.
      siftDown( i );
    }
  } // end heapify
  
  
  /**************************************************************
    * deleteMax                                                   *
    *                                                             *
    * Purpose: Remove and return the maximum value from the heap. *
    *                                                             *
    * Returns: The maximum value stored in the heap.              *
    **************************************************************/
  public int deleteMax( )
  {
    int temp = heapArray[0];   //the maximum value
    int traversal = 0;        //the index of the array being handled
    
    heapArray [0] = heapArray[heapSize-1];
    heapSize--;
    
    while(leftChild(traversal) <= heapSize)
    { 
      heapify();
      traversal++;
    }
    return temp;
  } // end deleteMax
  
  
  
  /***************************************************************
    * siftDown                                                     *
    *                                                              *
    * Purpose: The value in heapArray[i] may not be heap-ordered,  *
    *          --- it may be smaller than one or both of its       *
    *          children.  But its children (if any) and their      *
    *          descendants are in heap order.  To restore heap     *
    *          order, sift the value down (repeatedly exchange it  *
    *          with its largest child) until it has no children    *
    *          (is in a leaf) or it is larger than its largest     *
    *          child.                                              *
    * Param i: Input parameter                                     *
    *          The index of the element that may have messed up    *
    *          the heap ordering.                                  *
    ***************************************************************/
  private void siftDown( int i )
  {
    int j = i;
    int rcIndex; // index of the right child of heapArray[j]
    int lcIndex; // index of the left child of heapArray[j]
    int largerChildIndex;
    boolean mightNeedToSwap = true; // if we had to swap the parent
    // with its larger child at the previous level, then
    // we might need to swap again at this level.
    // If we did NOT swap at the previous level, then
    // we're done sifting and should stop.
    int temp;
    
    rcIndex = rightChild( j );
    lcIndex = leftChild( j );
    while ( mightNeedToSwap && lcIndex < heapSize )
    {
      largerChildIndex = rcIndex; // assume right child is larger
      if ( rcIndex >= heapSize || heapArray[lcIndex] > heapArray[rcIndex] )
      {
        // left child is the larger (or only) child
        largerChildIndex = lcIndex;
      }
      
      if ( heapArray[j] < heapArray[largerChildIndex] )
      {
        // heapArray[j] is NOT larger than its largest child.
        // Exchange heapArray[j] with its larger child.
        temp = heapArray[j];
        heapArray[j] = heapArray[largerChildIndex];
        heapArray[largerChildIndex] = temp;
        j = largerChildIndex;
        rcIndex = rightChild( j );
        lcIndex = leftChild( j );
      }
      else
      {
        mightNeedToSwap = false;
      }
    } // end while
    
  } // end siftDown
  
  /**************************************************************
    * rightChild                                                  *
    *                                                             *
    * Purpose: Compute the index of the right child of            *
    *          heapArray[i].                                      *
    * Param i: Input parameter.                                   *
    *          The index in heapArray of a node --- we want to    *
    *          find the right child of heapArray[i].              *
    * Returns: The index of the right child of heapArray[i].      *
    *          (Does not check if the right child exists.)        *
    **************************************************************/
  private int rightChild( int i )
  {
    
    return 2*i +2;
    
  }
  
  /**************************************************************
    * leftChild                                                   *
    *                                                             *
    * Purpose: Compute the index of the left child of             *
    *          heapArray[i].                                      *
    * Param i: Input parameter.                                   *
    *          The index in heapArray of the node --- we want to  *
    *          find the left child of heapArrary[i].              *
    * Returns: The index of the left child of heapArray[i].       *
    *          (Does not check if the left child exists.)         *
    **************************************************************/
  private int leftChild( int i )
  {
    
    return 2*i +1;
  }
  
  /***************************************************************
    * printHeap                                                    *
    *                                                              *
    * Purpose: Print out the values stored in heapArray.           *
    ***************************************************************/
  public void printHeap( )
  {
    int i;
    int numPrintedOnLine = 0;
    
    for (i = 0; i < heapSize; i++)
    {
      System.out.print( heapArray[i] + " " );
      numPrintedOnLine++;
      if (numPrintedOnLine == 20)
      {
        System.out.println();
        numPrintedOnLine = 0;
      }
    }
    if ( numPrintedOnLine != 0 )
      System.out.println();
  } // end printHeap
  
  /*****************************************************************
    * heapSort                                                       *
    *                                                                *
    * Purpose: To sort the array heapArray using a heap sort.        *
    *                                                                *
    * How heap sort works:  It depends on deleteMax, which returns   *
    * the largest remaining value in the array, and also makes the   *
    * heap one item smaller --- the last leaf's position becomes     *
    * empty.                                                         *
    * So heap sort first turns the unsorted array into a proper      *
    * heap by putting it into heap order.  Then it sorts the         *
    * heap-ordered array by repeatedly doing a deleteMax.            *
    * After each deleteMax, it stores the result returned by         *
    * the deleteMax in the just-emptied last leaf's position.        *
    * For example, heap sort's first deleteMax makes the last        *
    * position in the array empty and returns the maximum value in   *
    * the array. So it stores the maximum value in the last position *
    * in the array, and that is the correct sorted position for the  *
    * maximum value.                                                 *
    * Similarly, the second deleteMax will make the second-last      *
    * position in the array empty and will return the second-largest *
    * value.  So heap sort stores the second-largest value in the    *
    * second-last position in the array, which is the correct sorted *
    * position for the second-largest value.                         *
    * An so on.                                                      *
    *****************************************************************/
  public void heapSort()
  {
    int i;
    int tempSize = heapSize;
    
    // First, turn the unsorted array into a proper heap by
    // putting it into heap order.
    heapify();
    
    // Now, sort the array into ordinary sorted order
    // using deleteMax's.
    for (i = heapSize - 1; i > 0; i--)
    {
      heapArray[i] = deleteMax();
    }
    heapSize = tempSize; // deleteMax decreases heapSize.
    // Restore it to the original value so that
    // printHeap prints out the whole array.
  } // end heapSort
  
  
} // end class Heap
