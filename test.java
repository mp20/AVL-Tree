public class test {
    public static void main(String[] args) {
        AVL<Integer> tree = new AVL<>();
        tree.add(0);
        tree.add(-10);
        tree.add(10);
        tree.add(-15);
        tree.add(-5);
        tree.add(5);
        tree.add(15);
        tree.remove(0);

    }
}
