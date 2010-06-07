package uk.ac.ed.ph.ballviewer;

/** 
 * Tool for quickly sorting and retrieving objects. <p>
 * To use, add all objects with {@link #addNode(Object,double)}, call {@link #resetTraverser()}
 * and then repeatedly call {@link #findNextSmallest()} (or <code>findNextLargest()</code>, 
 * but DO NOT mix them; it won't work). Retrieve the object from the current node with 
 * {@link #object()} and the value with {@link #value()}. <p>
 * Note: the user may need to use casting as the objects are retrieved.
 */
public class SortTree {
	/** Number of nodes added. */
	public int total = 0;
	TreeNode root;
	
	public SortTree() {
	}
	
	/** 
	 * Adds <code>obj</code> to the appropriate position in the SortTree, 
	 * keeping <code>val</code> associated with it. 
	 */ 
	//this is a really fast method because it involves no other method calls.
	public void addNode (Object obj, double val) {
		if (root==null) { root = new TreeNode(TreeNode.NULL,obj, val); total++; return; } //catches 1st
		
		TreeNode cur = root;											//start with the root
		while (true) {													//then recursively:
				if (val<=cur.value) {   							// if smaller or equal
				if (cur.left!=null) cur=cur.left;				//  if possible, go left
				else {
					cur.left = new TreeNode(cur, obj, val);	//  otherwise place on left and stop
					total++;
					return;
				}
			}
			else {						 								// but if greater
				if (cur.right!=null) cur=cur.right ;			//  if possible, go right
				else { 
					cur.right = new TreeNode(cur, obj, val);  //  otherwise place on right and stop
					total++;
					return;
				} 
			}
		}
	}
	/** 
	 * Prepares to start retrieving (again). 
	 * Leaves user free to choose either findNextSmallest() or findNextLargest() (again).
	 */
	public void resetTraverser() {  
		if (total>0) {
			prev = root.parent;
			curr = root;
		}
	}
	
	TreeNode curr,prev;
	boolean found=false;
	/** 
	 * Focusses on the node having the next largest value. <p>
	 * Incompatible with <code>getNextSmallest()</code>! 
	 */
	public void findNextLargest() {
		while (true) {
			if (found==true) { // if we are still on the old (found) node, can't go right
				if (curr.left!=null) {  prev = curr; curr = curr.left; }  // if possible, go left 
				else {prev = curr; curr = curr.parent; }                  // otherwise, go up
				found=false;
			}               
			else if (prev==curr.right || (prev==curr.parent && curr.right==null) ) {
				found=true; // otherwise, if we just came up from the right, or we just came down
				return;     // and can't go right we have found the next largest value
			}
			else if (prev==curr.left)  
				{ prev = curr; curr = curr.parent; }//if came from left, go up
			else if (curr.right!=null) 
				{ prev = curr; curr = curr.right; } //or if possible, go right
			else if (curr.left!=null)  
				{ prev = curr; curr = curr.left; }	//or if possible, go left
			else 
				{prev = curr; curr = curr.parent; } //otherwise go up
		}
	}
	/** 
	 * Focusses on the node having the next smallest value. <p>
	 * Incompatible with <code>getNextLargest()</code>! 
	 */
	public void findNextSmallest() {  // just swap right for left 
		while (true) {
			if (found==true) { // if we are still on the old (found) node, can't go left
				if (curr.right!=null) {  prev = curr; curr = curr.right; } // if possible, go right
				else {prev = curr; curr = curr.parent; }                   // otherwise, go up
				found=false;
			}             
			else if (prev==curr.left || (prev==curr.parent && curr.left==null) ) {
				found=true; // otherwise, if we just came up from the left, or we just came down
				return;		// and can't go left we have found the next smallest value
			}
			else if (prev==curr.right) 
				{ prev = curr; curr = curr.parent; } 	//if came from right, go up
			else if (curr.left!=null)  
				{ prev = curr; curr = curr.left; }   	//or if possible, go left
			else if (curr.right!=null) 
				{ prev = curr; curr = curr.right; }		//or if possible, go right
			else 
				{prev = curr; curr = curr.parent; }    //otherwise, go up
		}
	}
	/** Returns the object referenced by the focussed node. */
	public Object object() { return curr.object; }
	/** Returns the value (eg depth) associated with the focussed node. */
	public double value() { return curr.value; }
}				
class TreeNode {
	public TreeNode parent;  // up //
	public TreeNode left;    // down&left //
	public TreeNode right;   // down&right //
	public Object object;
	public double value;
	
	public TreeNode (TreeNode par, Object obj, double val) {
		parent = par;
		object = obj;
		value = val;
	}
	public TreeNode () {}
	
	static final TreeNode NULL = new TreeNode();	

}
		