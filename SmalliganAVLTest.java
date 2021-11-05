import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SmalliganAVLTest {

    @Rule
    public Timeout globalTimeout = Timeout.millis(400);

    private AVL<Integer> avl;

    private final Integer ten = 10;
    private final Integer zero = 0;
    private final Integer negativeTen = -10;

    @Before
    public void setUp() {
        avl = new AVL<>();
    }

    @Test
    public void collectionConstructor() {
        avl = new AVL<>(Arrays.asList(
                10, 30, 50, 0, -10, -5, -7, 5, 7, 40, 60, 35, -3, -6, -20, -4, 45, 55, 70, 33, 77, 36,
                47, 65, 58, 32, 34, 38, 42, 46, 53, 80, 31
        ));

        testStandardAvl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionConstructorNullList() {
        avl = new AVL<>(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionConstructorNullElement() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(6);
        list.add(null);
        avl = new AVL<>(list);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNull() {
        avl.add(0);
        avl.add(null);
    }

    @Test
    public void addTest() {
        // see the provision() method to see the structure
        // should cover all the main cases for adding
        provisionAvl();
        testStandardAvl();
    }

    @Test
    public void addDuplicateTest() {
        provisionAvl();

        // add a bunch of duplicates
        avl.add(40);
        avl.add(60);
        avl.add(80);
        avl.add(32);
        avl.add(-10);

        // should have no change to the structure
        testStandardAvl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNull() {
        avl.add(0);
        avl.remove(null);
    }

    @Test
    public void removeNoSuch() {
        avl.add(0);
        avl.add(2);
        avl.add(-2);
        // the reason for these nests is to ensure that the exception is thrown in
        // all termination points.
        try {
            avl.remove(-3);
            fail("NoSuchElementException should have thrown.");
        } catch (NoSuchElementException e1) {
            try {
                avl.remove(-1);
                fail("NoSuchElementException should have thrown.");
            } catch (NoSuchElementException e2) {
                try {
                    avl.remove(1);
                    fail("NoSuchElementException should have thrown.");
                } catch (NoSuchElementException e3) {
                    try {
                        avl.remove(3);
                        fail("NoSuchElementException should have thrown.");
                    } catch (NoSuchElementException e4) { }
                }
            }
        }
    }

    @Test
    // Note: test relies on add()
    public void removeNoChildrenNoRotation() {
        avl.add(zero);
        avl.add(negativeTen);
        avl.add(ten);

        assertEquals(3, avl.size());

        assertEquals(ten, avl.getRoot().getRight().getData());
        assertSame(ten, avl.remove(10));
        assertEquals(2, avl.size());
        assertNull(avl.getRoot().getRight());
        assertEquals(zero, avl.getRoot().getData());
        assertEquals(negativeTen, avl.getRoot().getLeft().getData());
        assertSame(negativeTen, avl.remove(-10));
        assertEquals(1, avl.size());
        assertNull(avl.getRoot().getLeft());
        assertEquals(zero, avl.getRoot().getData());

        assertSame(zero, avl.remove(0));
        assertNull(avl.getRoot());
        assertEquals(0, avl.size());
    }

    @Test
    // Note: relies on add()
    public void removeOneChildNoRotation() {
        avl.add(zero);
        avl.add(ten);
        avl.add(negativeTen);
        avl.add(20);

        //       0
        //   -10   10
        //           20

        assertEquals(zero, avl.getRoot().getData());
        assertEquals(-1, avl.getRoot().getBalanceFactor());
        assertEquals(2, avl.getRoot().getHeight());
        assertEquals(ten, avl.getRoot().getRight().getData());
        assertEquals(negativeTen, avl.getRoot().getLeft().getData());
        assertEquals((Integer) 20, avl.getRoot().getRight().getRight().getData());

        assertEquals(4, avl.size());
        assertSame(ten, avl.remove(10));
        assertEquals(3, avl.size());

        assertEquals(0, avl.getRoot().getBalanceFactor());
        assertEquals(1, avl.getRoot().getHeight());
        assertEquals(zero, avl.getRoot().getData());
        assertEquals((Integer) 20, avl.getRoot().getRight().getData());
        assertEquals(negativeTen, avl.getRoot().getLeft().getData());
    }

    @Test
    // Note: relies on add()
    public void removeTwoChildrenNoRotation() {
        provisionSmallAvl();

        //          0
        //     -10     10
        //  -15   -5  5   15

        assertEquals(7, avl.size());
        assertEquals(2, avl.getRoot().getHeight());
        assertEquals(0, avl.getRoot().getBalanceFactor());
        assertEquals(0, avl.getRoot().getRight().getBalanceFactor());
        assertEquals((Integer) 5, avl.getRoot().getRight().getLeft().getData());
        assertSame(zero, avl.remove(0));

        //          5
        //     -10     10
        //  -15   -5      15

        assertEquals(0, avl.getRoot().getBalanceFactor());
        assertEquals(-1, avl.getRoot().getRight().getBalanceFactor());
        assertEquals((Integer) 5, avl.getRoot().getData());
        assertEquals(ten, avl.getRoot().getRight().getData());
        assertEquals((Integer) 15, avl.getRoot().getRight().getRight().getData());
        assertNull(avl.getRoot().getRight().getLeft());
    }

    @Test
    public void removeRebalanceRotateRight() {
        provisionSmallAvl();

        //          0
        //     -10     10
        //  -15   -5  5   15

        assertEquals(0, avl.getRoot().getBalanceFactor());
        assertEquals((Integer) 10, avl.remove(10));
        assertEquals((Integer) 5, avl.remove(5));
        assertEquals((Integer) 15, avl.remove(15));


        //     -10
        // -15      0
        //       -5
        assertEquals(4, avl.size());
        assertEquals(negativeTen, avl.getRoot().getData());
        assertEquals((Integer) (-15), avl.getRoot().getLeft().getData());
        assertLeaf(avl.getRoot().getLeft());
        assertEquals(zero, avl.getRoot().getRight().getData());
        assertNull(avl.getRoot().getRight().getRight());
        assertEquals((Integer) (-5), avl.getRoot().getRight().getLeft().getData());
        assertLeaf(avl.getRoot().getRight().getLeft());
    }

    @Test
    public void removeRebalanceRotateLeft() {
        provisionSmallAvl();

        //          0
        //     -10     10
        //  -15   -5  5   15
        assertEquals((Integer) (-15), avl.remove(-15));
        assertEquals((Integer) (-5), avl.remove(-5));
        assertEquals((Integer) (-10), avl.remove(-10));

        //      10
        //  0      15
        //    5

        assertTree(Arrays.asList(10, 0, 5, 15), Arrays.asList(2, 1, 0, 0), Arrays.asList(1, -1, 0, 0));
    }

    @Test
    public void removeRebalanceRotateRightLeft() {
        provisionSmallAvl();
        avl.add(3);
        //           0
        //     -10        10
        //  -15   -5     5   15
        //             3
        assertEquals((Integer) (-15), avl.remove(-15));
        assertEquals((Integer) (-5), avl.remove(-5));


        //           5
        //        0     10
        //   -10    3     15
        //

        assertTree(Arrays.asList(5, 0, -10, 3, 10, 15), Arrays.asList(2, 1, 0, 0, 1, 0), Arrays.asList(0, 0, 0, 0, -1, 0));
    }

    @Test
    public void removeRebalanceRotateLeftRight() {
        provisionSmallAvl();
        avl.add(-3);
        //            0
        //     -10         10
        //  -15   -5      5   15
        //          -3
        assertEquals((Integer) (10), avl.remove(10));
        assertEquals((Integer) (15), avl.remove(15));


        //               -5
        //          -10      0
        //       -15      -3   5

        assertTree(Arrays.asList(-5, -10, -15, 0, -3, 5), Arrays.asList(2, 1, 0, 1, 0, 0), Arrays.asList(0, 1, 0, 0, 0, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNull() {
        avl.add(1);
        avl.get(null);
    }

    @Test
    public void getNoSuch() {
        avl.add(0);
        avl.add(2);
        avl.add(-2);
        // the reason for these nests is to ensure that the exception is thrown in
        // all termination points.
        try {
            avl.get(-3);
            fail("NoSuchElementException should have thrown.");
        } catch (NoSuchElementException e1) {
            try {
                avl.get(-1);
                fail("NoSuchElementException should have thrown.");
            } catch (NoSuchElementException e2) {
                try {
                    avl.get(1);
                    fail("NoSuchElementException should have thrown.");
                } catch (NoSuchElementException e3) {
                    try {
                        avl.get(3);
                        fail("NoSuchElementException should have thrown.");
                    } catch (NoSuchElementException e4) { }
                }
            }
        }
    }

    @Test
    public void get() {
        provisionSmallAvl();
        //            0
        //     -10         10
        //  -15   -5      5   15

        Integer myTen = 10;
        Integer myZero = 0;
        Integer myNTen = -10;
        assertSame(zero, avl.get(myZero));
        assertSame(negativeTen, avl.get(myNTen));
        assertSame(ten, avl.get(myTen));
        assertEquals((Integer) 5, avl.get(5));
        assertEquals((Integer) 15, avl.get(15));
        assertEquals((Integer) (-15), avl.get(-15));
        assertEquals((Integer) (-5), avl.get(-5));
    }

    @Test
    // requires: add(), remove()
    public void contains() {
        assertFalse(avl.contains(10));
        assertFalse(avl.contains(0));
        provisionSmallAvl();
        //            0
        //     -10         10
        //  -15   -5      5   15

        Integer myTen = 10;
        Integer myZero = 0;
        Integer myNTen = -10;
        assertTrue(avl.contains(myZero));
        assertTrue(avl.contains(myNTen));
        assertTrue(avl.contains(myTen));
        assertTrue(avl.contains(5));
        assertTrue(avl.contains(15));
        assertTrue(avl.contains(-15));
        assertTrue(avl.contains(-5));
        assertFalse(avl.contains(-20));
        assertFalse(avl.contains(20));
        avl.remove(0);
        avl.remove(15);
        assertFalse(avl.contains(0));
        assertFalse(avl.contains(15));
    }

    @Test(expected = IllegalArgumentException.class)
    public void containsNull() {
        avl.contains(null);
    }

    @Test
    public void clear() {
        assertNull(avl.getRoot());
        assertEquals(0, avl.size());
        avl.clear();
        assertNull(avl.getRoot());
        assertEquals(0, avl.size());

        avl.add(7);
        avl.add(2);
        assertTree(Arrays.asList(7, 2), Arrays.asList(1, 0), Arrays.asList(1, 0));
        avl.clear();
        assertNull(avl.getRoot());
        assertEquals(0, avl.size());
    }

    /**
     * Asserts given node is a leaf
     * @param leaf to be asserted on
     */
    private void assertLeaf(AVLNode<Integer> leaf) {
        assertNotNull(leaf);
        assertNull(leaf.getLeft());
        assertNull(leaf.getRight());
    }

    /**
     * Creates this structure:
     *            0
     *     -10       10
     *  -15   -5   5   15
     */
    private void provisionSmallAvl() {
        avl.add(zero);
        avl.add(ten);
        avl.add(negativeTen);
        avl.add(-15);
        avl.add(5);
        avl.add(-5);
        avl.add(15);
    }

    /**
     * Provisions AVL to generate this structure.
     *
     * This sequence was designed to create all 8 cases to consider:
     *
     * Left rotation,
     * Right rotation,
     * Left-Right,
     * Right-Left,
     * and all four of these where children are involved.
     *
     * (I do warn that this is a bit tedious to diagram by hand; I verified its accuracy
     * with an automated tool. Unfortunately, the 1332 AVL vistool evidently does not support
     * negative numbers, so it will not suffice. Many other online AVL visualizers also produce
     * inaccurate results with negative numbers. This site does work, but it has a limitation to
     * how many levels it will display: https://yongdanielliang.github.io/animation/web/AVLTree.html.
     * Be aware however that this tool uses the predecessor method, so you shouldn't trust its results when
     * removing.)
     */
    private void provisionAvl() {
        avl.add(10);
        avl.add(30);
        avl.add(50); // left rotation
        avl.add(0);
        avl.add(-10); // right rotation
        avl.add(-5); // right rotation w/ children
        avl.add(-7);
        avl.add(5);
        avl.add(7); // left-right rotation
        avl.add(40);
        avl.add(60);
        avl.add(35); // left rotation w/ children
        avl.add(-3);
        avl.add(-6);
        avl.add(-20);
        avl.add(-4); // left-right rotation w/ children
        avl.add(45);
        avl.add(55);
        avl.add(70);
        avl.add(33);
        avl.add(77);
        avl.add(36);
        avl.add(47);
        avl.add(65);
        avl.add(58);
        avl.add(32);
        avl.add(34);
        avl.add(38);
        avl.add(42);
        avl.add(46);
        avl.add(53);
        avl.add(80);
        avl.add(31); // right-left rotation w/ children
    }

    /**
     * Tests that the AVL contains the data as added by the
     * provisionAvl() method.
     */
    private void testStandardAvl() {
        // I will test the structure of the avl using a preorder traverse.
        // (A BST is uniquely determined by its preorder traversal, so this is a valid test of its structure.)
        // Data:           40, 30,  -5, -7, -10, -20, -6, 0, -3, -4, 7, 5, 10, 35, 33, 32, 31, 34, 36, 38, 50, 45, 42, 47, 46, 60, 55, 53, 58, 70, 65, 77, 80
        // Heights:         5,  4,   3,  2,   1,   0,  0, 2,  1,  0, 1, 0,  0,  3,  2,  1,  0,  0,  1,  0,  4,  2,  0,  1,  0,  3,  1,  0,  0,  2,  0,  1,  0
        // Balance factors: 0,  0,   0,  1,   1,   0,  0, 0,  1,  0, 0, 0,  0,  1,  1,  1,  0,  0, -1,  0, -1, -1,  0,  1,  0, -1,  0,  0,  0, -1,  0, -1,  0

        List<Integer> expectedData = Arrays.asList(
                40, 30, -5, -7, -10, -20, -6, 0, -3, -4, 7, 5, 10, 35, 33, 32, 31, 34, 36, 38, 50,
                45, 42, 47, 46, 60, 55, 53, 58, 70, 65, 77, 80
                );

        List<Integer> expectedHeights = Arrays.asList(
                5,  4,   3,  2,  1,   0,   0, 2,  1,  0, 1, 0,  0,  3,  2,  1,  0,  0,  1,  0,  4,  2,
                0,  1,  0,  3,  1,  0,  0,  2,  0,  1,  0
        );

        List<Integer> expectedBalanceFactors = Arrays.asList(
                0,  0,   0,  1,  1,   0,   0, 0,  1,  0, 0, 0,  0,  1,  1,  1,  0,  0,  -1, 0, -1, -1,
                0,  1,  0, -1,  0,  0,  0, -1,  0, -1,  0
        );

        assertTree(expectedData, expectedHeights, expectedBalanceFactors);
    }

    @Test
    // requires: add()
    public void deepestBranchesIncludesAll() {
        provisionSmallAvl();
        //           0
        //       -10       10
        //    -15   -5   5   15

        assertEquals(Arrays.asList(0, -10, -15, -5, 10, 5, 15), avl.deepestBranches());
    }

    @Test
    // requires: add(), clear()
    public void deepestBranchesOneBranchOnly() {
        provisionSmallAvl();
        avl.add(3);
        //           0
        //       -10       10
        //    -15   -5   5   15
        //              3

        assertEquals(Arrays.asList(0, 10, 5, 3), avl.deepestBranches());
        avl.clear();

        provisionSmallAvl();
        avl.add(-3);
        //             0
        //       -10         10
        //    -15   -5     5   15
        //            -3
        assertEquals(Arrays.asList(0, -10, -5, -3), avl.deepestBranches());
        avl.clear();

        provisionSmallAvl();
        avl.add(-18);
        //             0
        //       -10         10
        //    -15   -5     5   15
        //  -18
        assertEquals(Arrays.asList(0, -10, -15, -18), avl.deepestBranches());
        avl.clear();
    }

    @Test
    public void deepestBranchesTwoBranches() {
        provisionSmallAvl();
        avl.add(-3);
        avl.add(3);
        //              0
        //       -10         10
        //    -15   -5     5   15
        //            -3  3
        assertEquals(Arrays.asList(0, -10, -5, -3, 10, 5, 3), avl.deepestBranches());
    }

    @Test
    public void deepestBranchesEmpty() {
        assertEquals(Arrays.asList(), avl.deepestBranches());
    }

    @Test
    public void deepestBranchesOneElement() {
        avl.add(0);
        assertEquals(Arrays.asList(0), avl.deepestBranches());
    }

    @Test
    public void sortedInBetween() {
        provisionSmallAvl();
        //              0
        //       -10         10
        //    -15   -5     5   15

        assertEquals(Arrays.asList(0), avl.sortedInBetween(-1, 1));

        // test some boundary situations
        assertEquals(Arrays.asList(), avl.sortedInBetween(-5, 0));
        assertEquals(Arrays.asList(), avl.sortedInBetween(0, 5));
        assertEquals(Arrays.asList(), avl.sortedInBetween(0, 0));
        assertEquals(Arrays.asList(0), avl.sortedInBetween(-5, 5));

        assertEquals(Arrays.asList(-10, -5, 0, 5, 10), avl.sortedInBetween(-15, 15));
        assertEquals(Arrays.asList(-15, -10, -5, 0, 5, 10, 15), avl.sortedInBetween(-16, 16));
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortedInBetweenNullData1() {
        avl.add(0);

        avl.sortedInBetween(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortedInBetweenNullData2() {
        avl.add(0);

        avl.sortedInBetween(10, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortedInBetweenBadBounds() {
        avl.add(0);

        avl.sortedInBetween(1, 0);
    }

    /**
     * Mutates passed lists with preorder traversal
     * @param curr node to create preorder on
     * @param data data list
     * @param heights height list
     * @param balanceFactors balance factor list
     */
    private void createPreorder(AVLNode<Integer> curr, List<Integer> data, List<Integer> heights, List<Integer> balanceFactors) {
        if (curr == null) return;

        data.add(curr.getData());
        heights.add(curr.getHeight());
        balanceFactors.add(curr.getBalanceFactor());

        createPreorder(curr.getLeft(), data, heights, balanceFactors);
        createPreorder(curr.getRight(), data, heights, balanceFactors);
    }

    /**
     * Mutates passed lists with inorder traversal
     * @param curr node to create inorder on
     * @param data list to provision with inorder traversal
     */
    private void createInorder(AVLNode<Integer> curr, List<Integer> data) {
        if (curr == null) return;

        createInorder(curr.getLeft(), data);
        data.add(curr.getData());
        createInorder(curr.getRight(), data);
    }

    /**
     * Asserts that the AVL matches the passed values
     *
     * @param expDataPreorder a preorder traversal list of the expected data
     * @param expHeights a preorder traversal list of the expected node heights
     * @param expBfs a preorder traversal list of expected balance factors
     * @throws IllegalArgumentException if expData, expHeights, and expBfs don't have equal lengths
     */
    private void assertTree(List<Integer> expDataPreorder, List<Integer> expHeights, List<Integer> expBfs) {
        if (expDataPreorder.size() != expHeights.size() || expDataPreorder.size() != expBfs.size() ) {
            throw new IllegalArgumentException("Expected data, heights, and balance factors must have equal lengths.");
        }

        // make a sorted clone, which will be the inorder expectation
        List<Integer> expDataInOrder = new ArrayList<Integer>(expDataPreorder);
        Collections.sort(expDataInOrder);

        // all binary trees are uniquely determined by the combination of their inorder and
        // preorder traversals, so this should catch any out-of-order placement that occurs
        // which wouldn't be detected by only the preorder traversal (which uniquely determines
        // binary search trees, but I don't want to assume that the tree actually is a BST for
        // testing purposes

        List<Integer> data = new ArrayList<>();
        List<Integer> h = new ArrayList<>();
        List<Integer> bf = new ArrayList<>();
        List<Integer> inorder = new ArrayList<>();
        createPreorder(avl.getRoot(), data, h, bf);
        createInorder(avl.getRoot(), inorder);
        assertEquals(expDataPreorder, data);
        assertEquals(expHeights, h);
        assertEquals(expBfs, bf);
        assertEquals(expDataInOrder, inorder);
        assertEquals(expDataPreorder.size(), avl.size());
    }

}
