import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Your implementation of an AVL Tree.
 *
 * @author Ariya Nazari Foroshani
 * @userid aforoshani3
 * @GTID 903627990
 * @version 1.0
 */
public class AVL<T extends Comparable<? super T>> {
    // DO NOT ADD OR MODIFY INSTANCE VARIABLES.
    private AVLNode<T> root;
    private int size;

    /**
     * A no-argument constructor that should initialize an empty AVL.
     *
     * Since instance variables are initialized to their default values, there
     * is no need to do anything for this constructor.
     */
    public AVL() {
        // DO NOT IMPLEMENT THIS CONSTRUCTOR!
    }

    /**
     * Initializes the AVL tree with the data in the Collection. The data
     * should be added in the same order it appears in the Collection.
     *
     * @throws IllegalArgumentException if data or any element in data is null
     * @param data the data to add to the tree
     */
    public AVL(Collection<T> data) {
        if (data == null) {
            throw new IllegalArgumentException("collection is null");
        }
        for (T element : data) {
            if (element == null) {
                throw new IllegalArgumentException("element is null");
            }
            add(element);
        }
    }

    /**
     * Adds the data to the AVL. Start by adding it as a leaf like in a regular
     * BST and then rotate the tree as needed.
     *
     * If the data is already in the tree, then nothing should be done (the
     * duplicate shouldn't get added, and size should not be incremented).
     *
     * Remember to recalculate heights and balance factors going up the tree,
     * rebalancing if necessary.
     *
     * @throws java.lang.IllegalArgumentException if the data is null
     * @param data the data to be added
     */
    public void add(T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        root = rAdd(root, data);
    }

    /**
     *
     * @param curr the current node
     * @param data the data being added
     * @return the node with pointer reinforcement (balaned)
     */
    private AVLNode<T> rAdd(AVLNode<T> curr, T data) {
        if (curr == null) {
            size++;
            return new AVLNode<>(data);
        } else if (data.compareTo(curr.getData()) < 0) {
            curr.setLeft(rAdd(curr.getLeft(), data));
        } else if (data.compareTo(curr.getData()) > 0) {
            curr.setRight(rAdd(curr.getRight(), data));
        }
        update(curr);
        return balance(curr);
    }

    /**
     * Removes the data from the tree. There are 3 cases to consider:
     *
     * 1: the data is a leaf. In this case, simply remove it.
     * 2: the data has one child. In this case, simply replace it with its
     * child.
     * 3: the data has 2 children. Use the successor to replace the data,
     * not the predecessor. As a reminder, rotations can occur after removing
     * the successor node.
     *
     * Remember to recalculate heights going up the tree, rebalancing if
     * necessary.
     *
     * @throws IllegalArgumentException if the data is null
     * @throws java.util.NoSuchElementException if the data is not found
     * @param data the data to remove from the tree.
     * @return the data removed from the tree. Do not return the same data
     * that was passed in.  Return the data that was stored in the tree.
     */
    public T remove(T data) {
        AVLNode<T> dummy = new AVLNode<T>(null);
        root = rRemove(root, data, dummy);
        return dummy.getData();
    }

    /**
     * the recursive helper method to remove.
     * @param root the current node
     * @param data the data to be removed
     * @param dummy Node used to get the data in removed node
     * @return returns the node being removed
     */
    private AVLNode<T> rRemove(AVLNode<T> root, T data, AVLNode<T> dummy) {
        if (data == null) {
            throw new IllegalArgumentException("Data is null");
        }
        if (!contains(data)) {
            throw new NoSuchElementException("the data is not in the tree");
        }
        if (root == null) {
            return root;
        } else if (data.compareTo(root.getData()) < 0) {
            root.setLeft(rRemove(root.getLeft(), data, dummy));
        } else if (data.compareTo(root.getData()) > 0) {
            root.setRight(rRemove(root.getRight(), data, dummy));
        } else {
            dummy.setData(root.getData());
            size--;
            if (root.getLeft() == null && root.getRight() == null) {
                return null;
            } else if (root.getLeft() != null && root.getRight() == null) {
                return root.getLeft();
            } else if (root.getRight() != null && root.getLeft() == null) {
                return root.getRight();
            } else {
                AVLNode<T> dummy2 = new AVLNode<>(null);
                root.setRight(removeSuccessor(root.getRight(), dummy2));
                root.setData(dummy2.getData());
            }
        }
        update(root);
        return balance(root);
    }
    /**
     * Helps to do the actual removing and setting the dummy nodes data
     *
     * @param root the node currently in recursion
     * @param dummy what contains the actual data from the removed node
     * @return returns the node to remove
     */
    private AVLNode<T> removeSuccessor(AVLNode<T> root, AVLNode<T> dummy) {
        if (root.getLeft() == null) {
            dummy.setData(root.getData());
            return root.getRight();
        } else {
            root.setLeft(removeSuccessor(root.getLeft(), dummy));
        }
        update(root);
        return balance(root);
    }

    /**
     * Returns the data in the tree matching the parameter passed in (think
     * carefully: should you use value equality or reference equality?).
     *
     * @throws IllegalArgumentException if the data is null
     * @throws java.util.NoSuchElementException if the data is not found
     * @param data the data to search for in the tree.
     * @return the data in the tree equal to the parameter. Do not return the
     * same data that was passed in.  Return the data that was stored in the
     * tree.
     */
    public T get(T data) {
        return rGet(root, data);
    }

    /**
     * recursive helper method of get.
     * @param root the current node
     * @param data the data we are searching for
     * @return returns the data
     */
    private T rGet(AVLNode<T> root, T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        if (root == null) {
            throw new NoSuchElementException("data is not in the tree");
        } else if (root.getData() == data) {
            return root.getData();
        } else if (root.getData().compareTo(data) > 0) {
            return rGet(root.getLeft(), data);
        } else if (root.getData().compareTo(data) < 0) {
            return rGet(root.getRight(), data);
        }
        return root.getData();
    }

    /**
     * balances the tree if needed.
     * @param curr the current node
     * @return returns the node with pointer reinforcement
     */
    public AVLNode<T> balance(AVLNode curr) {
        //left heavy
        if (curr.getBalanceFactor() > 1) {
            //left-right
            if (curr.getLeft().getBalanceFactor() < 0) {
                return leftRightCase(curr);
            }
            //single right
            return leftLeftCase(curr);
            //right heavy
        } else if (curr.getBalanceFactor() < -1) {
            //right-left
            if (curr.getRight().getBalanceFactor() > 0) { //left heavy
                return rightLeftCase(curr);
            }
            return rightRightCase(curr);
        }
        return curr;
    }

    /**
     * single-right rotate.
     * @param node current node
     * @return the child node
     */
    private AVLNode<T> leftLeftCase(AVLNode node) {
        return rightR(node);
    }

    /**
     * left-right rotate
     * @param node current node
     * @return the child node
     */
    private AVLNode<T> leftRightCase(AVLNode node) {
        node.setLeft(leftR(node.getLeft()));
        return leftLeftCase(node);
    }

    /**
     * single-left rotate
     * @param node current node
     * @return the child node
     */
    private AVLNode<T> rightRightCase(AVLNode node) {
        return leftR(node);
    }

    /**
     * right-left rotate
     * @param node current node
     * @return the child node
     */
    private AVLNode<T> rightLeftCase(AVLNode node) {
        node.setRight(rightR(node.getRight()));
        return rightRightCase(node);
    }

    /**
     * performs the actual rotation(left).
     * @param parent the parent node
     * @return the child node
     */
    public AVLNode leftR(AVLNode parent) {
        AVLNode child = parent.getRight();
        parent.setRight(child.getLeft());
        child.setLeft(parent);
        update(parent);
        update(child);
        return child;
    }

    /**
     * performs the actual rotation(right).
     * @param parent the parent node
     * @return the child node
     */
    public AVLNode rightR(AVLNode parent) {
        AVLNode child = parent.getLeft();
        parent.setLeft(child.getRight());
        child.setRight(parent);
        update(parent);
        update(child);
        return child;
    }

    /**
     * updates the balance factor and height of the node.
     * @param curr current node
     */
    private void update(AVLNode curr) {
        curr.setBalanceFactor(easyHeight(curr.getLeft()) - easyHeight(curr.getRight()));
        curr.setHeight(1 + Math.max(easyHeight(curr.getLeft()), easyHeight(curr.getRight())));
    }

    /**
     * helper method for update.
     * @param curr current node
     * @return the height
     */
    private int easyHeight(AVLNode curr) {
        if (curr == null) {
            return -1;
        } else {
            return curr.getHeight();
        }
    }

    /**
     * Returns whether or not data equivalent to the given parameter is
     * contained within the tree. The same type of equality should be used as
     * in the get method.
     *
     * @throws IllegalArgumentException if the data is null
     * @param data the data to search for in the tree.
     * @return whether or not the parameter is contained within the tree.
     */
    public boolean contains(T data) {
        return rContains(root, data);
    }


    /**
     * helper method to contains.
     * @param root the current node
     * @param data the data we are searching for
     * @return returns wether or not the data was found/ is in the tree
     */
    private boolean rContains(AVLNode<T> root, T data) {
        if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        if (root == null) {
            return false;
        } else if (root.getData() == data) {
            return true;
        } else if (root.getData().compareTo(data) > 0) {
            return rContains(root.getLeft(), data);
        } else if (root.getData().compareTo(data) < 0) {
            return rContains(root.getRight(), data);
        }
        return true;
    }

    /**
     * Returns the data on branches of the tree with the maximum depth. If you
     * encounter multiple branches of maximum depth while traversing, then you
     * should list the remaining data from the left branch first, then the
     * remaining data in the right branch. This is essentially a preorder
     * traversal of the tree, but only of the branches of maximum depth.
     *
     * Your list should not duplicate data, and the data of a branch should be
     * listed in order going from the root to the leaf of that branch.
     *
     * Should run in worst case O(n), but you should not explore branches that
     * do not have maximum depth. You should also not need to traverse branches
     * more than once.
     *
     * Hint: How can you take advantage of the balancing information stored in
     * AVL nodes to discern deep branches?
     *
     * Example Tree:
     *                           10
     *                       /        \
     *                      5          15
     *                    /   \      /    \
     *                   2     7    13    20
     *                  / \   / \     \  / \
     *                 1   4 6   8   14 17  25
     *                /           \          \
     *               0             9         30
     *
     * Returns: [10, 5, 2, 1, 0, 7, 8, 9, 15, 20, 25, 30]
     *
     * @return the list of data in branches of maximum depth in preorder
     * traversal order
     */
    public List<T> deepestBranches() {
        List<T> list = new ArrayList<>();
        deepestR(root, list);
        return list;

    }

    /**
     * helper method to deepestBranches method.
     * @param root the current node.
     * @param list the list that contains all the values.
     */
    private void deepestR(AVLNode<T> root, List<T> list) {
        if (root == null) {
            return;
        }
        if (root.getBalanceFactor() == 0) {
            list.add(root.getData());
            deepestR(root.getLeft(), list);
            deepestR(root.getRight(), list);
        } else if (root.getBalanceFactor() > 0) {
            list.add(root.getData());
            deepestR(root.getLeft(), list);
        } else if (root.getBalanceFactor() < 0) {
            list.add(root.getData());
            deepestR(root.getRight(), list);
        }
        return;
    }

    /**
     * Returns a sorted list of data that are within the threshold bounds of
     * data1 and data2. That is, the data should be > data1 and < data2.
     *
     * Should run in worst case O(n), but this is heavily dependent on the
     * threshold data. You should not explore branches of the tree that do not
     * satisfy the threshold.
     *
     * Example Tree:
     *                           10
     *                       /        \
     *                      5          15
     *                    /   \      /    \
     *                   2     7    13    20
     *                  / \   / \     \  / \
     *                 1   4 6   8   14 17  25
     *                /           \          \
     *               0             9         30
     *
     * sortedInBetween(7, 14) returns [8, 9, 10, 13]
     * sortedInBetween(3, 8) returns [4, 5, 6, 7]
     * sortedInBetween(8, 8) returns []
     *
     * @throws java.lang.IllegalArgumentException if data1 or data2 are null
     * @param data1 the smaller data in the threshold
     * @param data2 the larger data in the threshold
     * or if data1 > data2
     * @return a sorted list of data that is > data1 and < data2
     */
    public List<T> sortedInBetween(T data1, T data2) {
        if (data1 == null || data2 == null) {
            throw new IllegalArgumentException("null data(s)");
        }
        if (data1.compareTo(data2) > 0) {
            throw new IllegalArgumentException("data 1 cannot be greater");
        }
        List<T> list = new LinkedList<>();
        sortR(root, data1, data2, list);
        return list;
    }

    /**
     * recursive helper method to sortedInBetween.
     * @param root the current node
     * @param data1 the first data (smaller value)
     * @param data2 the second data (bigger value)
     * @param list the list with all the values
     */
    private void sortR(AVLNode<T> root, T data1, T data2, List<T> list) {
        if (root == null) {
            return;
        }
        if (data1.compareTo(root.getData()) < 0) {
            sortR(root.getLeft(), data1, data2, list);
        }
        if (data1.compareTo(root.getData()) < 0 && data2.compareTo(root.getData()) > 0) {
            list.add(root.getData());
        }
        if (data2.compareTo(root.getData()) > 0) {
            sortR(root.getRight(), data1, data2, list);
        }
    }

    /**
     * Clears the tree.
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Returns the height of the root of the tree.
     *
     * Since this is an AVL, this method does not need to traverse the tree
     * and should be O(1)
     *
     * @return the height of the root of the tree, -1 if the tree is empty
     */
    public int height() {
        if (size == 0) {
            return -1;
        }
        return root.getHeight();
    }

    /**
     * Returns the size of the AVL tree.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return number of items in the AVL tree
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD
        return size;
    }

    /**
     * Returns the root of the AVL tree.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the root of the AVL tree
     */
    public AVLNode<T> getRoot() {
        // DO NOT MODIFY THIS METHOD!
        return root;
    }
}