
public class bptree{
	private LeafNode startNode;
	private NonleafNode root;
    private static NonleafNode nullnonleaf;
    private static LeafNode nullleaf;
	private static int MAXIMUM;
	private static int firstinsertcount;
	private static int MINIMUM= MAXIMUM/2;
	
	bptree()
	{
		startNode = nullleaf;
		root = nullnonleaf;
		
	}
	
	private static class NonleafNode
	{
		int childnum;
		boolean ispointtoleaf; //if pointtoleaf true else false;
		NonleafPair pairarray [];
		NonleafNode nonleaf_rightmost;
		LeafNode leaf_rightmost;
	    NonleafNode parent;
	    
	    NonleafNode()
	    {
	    	childnum=0;
	    	ispointtoleaf=true;
	    	pairarray = new NonleafPair[MAXIMUM]; //for split it should have 1 extra
	    	nonleaf_rightmost = nullnonleaf;
	    	leaf_rightmost = nullleaf;
	    	parent = nullnonleaf;
	    	
	    	
	    }
	    
	    void setIspointtoleaf(boolean input)
	    {
	    	ispointtoleaf =input;
	    }

	    int getParentIndex()
	    {
	    	int returnindex=0;
	    	if(this.parent==nullnonleaf)
	    	{
	    		
	    	}
	    	else
	    	{
	    		returnindex=getParentIndex(this.parent);
	    		
	    	}
	    	
	    	return returnindex;
	    	
	    }
	    
	    int getParentIndex(NonleafNode input/*parantNode*/) // when current node is nonleafnode error return -1
	    {
	    	int returnindex=-1;
	    	for(int i=0;i<(input.childnum-1);i++)
	    	{
	    		if(input.pairarray[i].nonleaf_leftchild==this)
	    		{
	    		 returnindex =i;
	    		 break;
	    		}
	    		
	    		
	    		
	    	}
	    		
	    	if(input.nonleaf_rightmost==this)
    		{
    			returnindex =input.childnum-1;
    			
    		}
	    	return returnindex;
	    	
	    }
	    
	    
	    void splitNonleafInsert(int key,int index,NonleafNode leftchild,NonleafNode rightchild) // index(child point position)0~childnum-1
	    {
	    	
	    }
	    
	    void splitLeafInsert(int key,int index,LeafNode leftchild,LeafNode rightchild)
	    {
	    	if(leaf_rightmost==nullleaf)
	    	{
	    		pairarray[0].leaf_leftchild=leftchild;
	    		pairarray[0].key=key;
	    		leaf_rightmost=rightchild;
	    		childnum=2;
	    	}
	    	else
	    	{
	    		if(index==(childnum-1))   //if rightmost there is nochange 
	    		{
	    			pairarray[childnum-1].leaf_leftchild=leftchild;
	    			pairarray[childnum-1].key=key;
	    			leaf_rightmost=rightchild;
	    			childnum++;
	    			
	    		}
	    		else  //if not rightmost , rightside should move one block
	    		{
	    			int i=1;
	    			for(int j=(childnum-i);j>=(index+1);i++)///!!! modify!!!
	    			{
	    				pairarray[j].leaf_leftchild=pairarray[j-1].leaf_leftchild;
	    				pairarray[j].key=pairarray[j-1].key;
	    			}
	    			
	    			
	    		}
	    	
	    	}
	    	
	    	
	    }
	    
		
	}
	
	private static class NonleafPair
	{
		int key;
		NonleafNode nonleaf_leftchild;
		LeafNode leaf_leftchild;
		
		NonleafPair()
		{
			nonleaf_leftchild = nullnonleaf;
			leaf_leftchild =nullleaf;
			key =0;
			
		}
		
		void nonleafNodeInsert(int inputkey,NonleafNode inputnode)
		{
		
			key = inputkey;
			nonleaf_leftchild = inputnode;
			
		}
		
		void leafNodeInsert (int inputkey,LeafNode inputnode)
		{
			key = inputkey;
			leaf_leftchild = inputnode;
			
		}
	}
	
	private static class LeafNode
	{
		int keynum;
		NonleafNode parent;
		LeafNode rightsibling;
		LeafPair pairarray[];
		
		LeafNode()
		{
			keynum=0;
			parent = nullnonleaf;
			rightsibling = nullleaf;
			pairarray = new LeafPair[MAXIMUM+1]; //for split it should have 1 extra	
		}
		
		int getParentIndex()
	    {
	    	int returnindex=0;
	    	if(this.parent==nullnonleaf)
	    	{
	    		
	    	}
	    	else
	    	{
	    		returnindex=getParentIndex(this.parent);
	    		
	    	}
	    	
	    	return returnindex;
	    	
	    }
	    
		
		int getParentIndex(NonleafNode input/*parantNode*/) // when current node is nonleafnode error return -1
	    {
	    	int returnindex=-1;
	    	for(int i=0;i<(input.childnum-1);i++)
	    	{
	    		if(input.pairarray[i].leaf_leftchild==this)
	    		{
	    		 returnindex =i;
	    		 break;
	    		}
	    		
	    	}
	    				
	    	if(input.leaf_rightmost==this)
    		{
    			returnindex = input.childnum-1;
    			
    		}
	    	return returnindex;
	    	
	    }
		
		void insert(int inputkey,int inputvalue)
		{
		 pairarray[keynum].insert(inputkey, inputvalue);
		 keynum++;
		 sort();
		}
		
		void setRightsibling (LeafNode inputNode)
		{
			rightsibling = inputNode;
		}
		
		void setParent (NonleafNode inputNode)
		{
			parent = inputNode;
			
		}
	 
		boolean isfull()
	    {
	    	
	    	if(keynum<MAXIMUM)
	    	{
	    		return false;
	    		
	    	}
	    	else
	    	{
	    		return true;
	    	}
	    	
	    }	

		boolean isunderflow()
		{
			if(keynum<MINIMUM)
			{
			  return true;	
			}
			else
			{
				return false;
				
			}
			
		}
	
	    void delete(int inputkey)
	    {
	    	int index =findindex(inputkey);
	    	if(index==-1)  //notkey in the bptree
	    	{
	    		System.out.println(inputkey+"not in the bptree");
	    	}
	    	else
	    	{
	    		pairarray[index].key = pairarray[keynum].key;
	    		pairarray[index].value = pairarray[keynum].value;
	    		keynum--;
	    		sort();
	    	}
	    }
	    
	    int findindex(int inputkey)
	    {
	    	int index=-1;
	    	for(int i=0;i<keynum;i++)
	    	{
	    		if(pairarray[i].key==inputkey)
	    		{
	    			index=i;
	    			break;
	    			
	    		}
	    			
	    	}
	    	
	    	return index;
	    	
	    	
	    }
	    void sort()
	    {
	    	int i, j, indexMin, tempkey,tempvalue;
	    	 
	        for (i = 0; i < keynum - 1; i++)
	        {
	            indexMin = i;
	            for (j = i + 1; j < keynum; j++)
	            {
	                if (pairarray[j].key < pairarray[indexMin].key)
	                {
	                    indexMin = j;
	                }
	            }
	            tempkey = pairarray[indexMin].key;
	            tempvalue = pairarray[indexMin].value;
	            pairarray[indexMin].key = pairarray[i].key;
	            pairarray[indexMin].value = pairarray[i].value;
	            pairarray[i].key = tempkey;
	            pairarray[i].value = tempvalue;
	        }

	    	
	    	    	
	    }
	}

    private static class LeafPair
    {
    	int key;
    	int value;
    	LeafPair()
    	{
    		key =0;
    		value=0;
    		
    	}
    	
    	void insert(int inputkey,int inputvalue)
    	{
    	
    		key = inputkey;
    		value =inputvalue;
    	}
 
    	
    	
    }
	
   
    
    

    void insert(int inputkey,int inputvalue)
    {
    	if(root ==nullleaf)
    	{
    		firstinsertcount =1;
    		root = new LeafNode();
    		
    		
    		
    	}
    	else
    	{
    		
    		
    		
    	}
    	
    	
    }
   
	 public static void main( String [ ] args ) {
		    bptree jktree = new bptree();
		    

	 }
}