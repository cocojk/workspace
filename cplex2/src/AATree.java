public class AATree {
    /**
     *d Construct the tree.
     */
    public AATree( ) {
        root = nullNode;
    }
    
    /**
     * Insert into the tree.
     * @param x the item to insert.
     * @throws DuplicateItemException if x is already present.
     */
    public void insert( Comparable x ) {
        root = insert( x, root );
    }
    
    public void shownodeinfo()
    {
    	
    	shownodeinfo(root);
    }
    
    public void shownodeinfo(AANode input)
    {
    	if(input!=nullNode)
    	{
    		System.out.format("%d node level %d\n",input.element,input.level);
    		if(input.left!=nullNode)
    		{
    			System.out.format("%d node left %d\n",input.element,input.left.element);
        		
    		}
    		if(input.right!=nullNode)
    		{
    			System.out.format("%d node right %d\n",input.element,input.right.element);
        		
    		}
    		shownodeinfo(input.left);
    		shownodeinfo(input.right);
    	}
    	
    }
    /**
     * Remove from the tree.
     * @param x the item to remove.
     * @throws ItemNotFoundException if x is not found.
     */
    public void remove( Comparable x ) {
        deletedNode = nullNode;
        root = remove( x, root );
    }
    
    /**
     * Find the smallest item in the tree.
     * @return the smallest item or null if empty.
     */
    public Comparable findMin( ) {
        if( isEmpty( ) )
            return null;
        
        AANode ptr = root;
        
        while( ptr.left != nullNode )
            ptr = ptr.left;
        
        return ptr.element;
    }
    
    /**
     * Find the largest item in the tree.
     * @return the largest item or null if empty.
     */
    public Comparable findMax( ) {
        if( isEmpty( ) )
            return null;
        
        AANode ptr = root;
        
        while( ptr.right != nullNode )
            ptr = ptr.right;
        
        return ptr.element;
    }
    
    /**
     * Find an item in the tree.
     * @param x the item to search for.
     * @return the matching item of null if not found.
     */
    public Comparable find( Comparable x ) {
        AANode current = root;
        nullNode.element = x;
        
        for( ; ; ) {
            if( x.compareTo( current.element ) < 0 )
                current = current.left;
            else if( x.compareTo( current.element ) > 0 )
                current = current.right;
            else if( current != nullNode )
                return current.element;
            else
                return null;
        }
    }
    
    /**
     * Make the tree logically empty.
     */
    public void makeEmpty( ) {
        root = nullNode;
    }
    
    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( ) {
        return root == nullNode;
    }
    
    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     * @throws DuplicateItemException if x is already present.
     */
    private AANode insert( Comparable x, AANode t ) {
        if( t == nullNode )
            t = new AANode( x );
        else if( x.compareTo( t.element ) < 0 )
            t.left = insert( x, t.left );
        else if( x.compareTo( t.element ) > 0 )
            t.right = insert( x, t.right );
        else
            System.out.println("alread in tree");
        
        t = skew( t );
        t = split( t );
        return t;
    }
    
    /**
     * Internal method to remove from a subtree.
     * @param x the item to remove.
     * @param t the node that roots the tree.
     * @return the new root.
     * @throws ItemNotFoundException if x is not found.
     */
    private AANode remove( Comparable x, AANode t ) {
        if( t != nullNode ) {
            // Step 1: Search down the tree and set lastNode and deletedNode
            lastNode = t;
            if( x.compareTo( t.element ) < 0 )
                t.left = remove( x, t.left );
            else {
                deletedNode = t;
                t.right = remove( x, t.right );
            }
            
            // Step 2: If at the bottom of the tree and
            //         x is present, we remove it
            if( t == lastNode ) {
                if( deletedNode == nullNode || x.compareTo( deletedNode.element ) != 0 )
                    System.out.println("not item in tree");
                deletedNode.element = t.element;
                t = t.right;
            }
            
            // Step 3: Otherwise, we are not at the bottom; rebalance
            else
            	
            	if( t.left.level < t.level - 1 || t.right.level < t.level - 1 ) {
                if( t.right.level > --t.level )
                    t.right.level = t.level;
                t = skew( t );
                t.right = skew( t.right );
                t.right.right = skew( t.right.right );
                t = split( t );
                t.right = split( t.right );
                }
        }
        return t;
    }
    
    /**
     * Skew primitive for AA-trees.
     * @param t the node that roots the tree.
     * @return the new root after the rotation.
     */
    private static AANode skew( AANode t ) {
        if( t.left.level == t.level )
            t = rotateWithLeftChild( t );
        return t;
    }
    
    /**
     * Split primitive for AA-trees.
     * @param t the node that roots the tree.
     * @return the new root after the rotation.
     */
    private static AANode split( AANode t ) {
        if( t.right.right.level == t.level ) {
            t = rotateWithRightChild( t );
            t.level++;
        }
        return t;
    }
    
    /**
     * Rotate binary tree node with left child.
     */
    private static AANode rotateWithLeftChild( AANode k2 ) {
        AANode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        return k1;
    }
    
    /**
     * Rotate binary tree node with right child.
     */
    private static AANode rotateWithRightChild( AANode k1 ) {
        AANode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        return k2;
    }
    
    private static class AANode {
        // Constructors
        AANode( Comparable theElement ) {
            element = theElement;
            left    = right = nullNode;
            level   = 1;
        }
        
        Comparable element;      // The data in the node
        AANode     left;         // Left child
        AANode     right;        // Right child
        int        level;        // Level
    }
    
    private AANode root;
    private static AANode nullNode;
    
    static         // static initializer for nullNode
    {
        nullNode = new AANode( null );
        nullNode.left = nullNode.right = nullNode;
        nullNode.level = 0;
    }
    
    private static AANode deletedNode;
    private static AANode lastNode;
    
    // Test program; should print min and max and nothing else
    public static void main( String [ ] args ) {
        AATree t = new AATree( );
        //final int NUMS = 40000;
        //final int GAP  =   307;
        
        System.out.println( "Checking... (no bad output means success)" );
        t.insert( new Integer( 41 ) );
        t.insert( new Integer( 18467) );
        t.insert( new Integer( 6334) );
        t.insert( new Integer( 26500 ) );
        t.insert( new Integer( 19169) );
        t.insert( new Integer( 15724 ) );
        t.insert( new Integer( 11478) );
        t.insert( new Integer( 29358 ) );
        t.insert( new Integer( 26962) );
        t.insert( new Integer( 24464) );
        t.insert( new Integer( 5705 ) );
        t.insert( new Integer( 28145) );
        t.insert( new Integer( 23281 ) );
        t.insert( new Integer( 16827) );
        t.insert( new Integer( 9961) );
        t.insert( new Integer( 491) );
        t.insert( new Integer( 2995 ) );
        t.insert( new Integer( 11942) );
        t.insert( new Integer( 4827 ) );
        t.insert( new Integer( 5346) );
        
        t.remove( new Integer( 41 ) );
        t.remove( new Integer( 18467) );
        t.remove( new Integer( 6334) );
        t.remove( new Integer( 26500 ) );
        t.remove( new Integer( 19169) );
     /*   t.remove( new Integer( 15724 ) );
        t.remove( new Integer( 11478) );
        t.remove( new Integer( 29358 ) );
        t.remove( new Integer( 26962) );
        t.remove( new Integer( 24464) );
        t.remove( new Integer( 5705 ) );*/
        System.out.println( "Inserts complete" );
        t.shownodeinfo();
        
       /* t.remove( t.findMax( ) );
        for( int i = 1; i < NUMS; i+= 2 )
            t.remove( new Integer( i ) );
        t.remove( t.findMax( ) );
        System.out.println( "Removes complete" );
        
        
        if( ((Integer)(t.findMin( ))).intValue( ) != 2 ||
                ((Integer)(t.findMax( ))).intValue( ) != NUMS - 2 )
            System.out.println( "FindMin or FindMax error!" );
        
        for( int i = 2; i < NUMS; i+=2 )
            if( ((Integer)t.find( new Integer( i ) )).intValue( ) != i )
                System.out.println( "Error: find fails for " + i );
        
        for( int i = 1; i < NUMS; i+=2 )
            if( t.find( new Integer( i ) )  != null )
                System.out.println( "Error: Found deleted item " + i );*/
    }
}


/**
 * Exception class for duplicate item errors
 * in search tree insertions.
 * @author Mark Allen Weiss
 */
