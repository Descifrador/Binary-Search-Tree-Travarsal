import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Binary Search Tree Class.
 */
@SuppressWarnings("DuplicatedCode")
public class BinarySearchTree {
    /**
     * Keeps track of the visited node in a set. Set ensures that no node is duplicated.
     * Similar to {@link #preVisited}, {@link #postVisited} and {@link #levelVisited}.
     */
    private static final Set<Node> inVisited = new HashSet<>();
    /**
     * Keeps track of the visited node in a set. Set ensures that no node is duplicated.
     * Similar to {@link #inVisited}, {@link #postVisited} and {@link #levelVisited}.
     */
    private static final Set<Node> preVisited = new HashSet<>();
    /**
     * Keeps track of the visited node in a set. Set ensures that no node is duplicated.
     * Similar to {@link #preVisited}, {@link #inVisited} and {@link #levelVisited}.
     */
    private static final Set<Node> postVisited = new HashSet<>();
    /**
     * Keeps track of the visited node in a set. Set ensures that no node is duplicated.
     * Similar to {@link #preVisited}, {@link #postVisited}, {@link #inVisited}.
     */
    private static final Set<Node> levelVisited = new HashSet<>();
    /**
     * Stores the last encountered node. Not needed for general drawing. Used to show path.
     * Similar to {@link #preLast}, {@link #postLast} and {@link #levelLast}.
     */
    private static Node inLast = null;
    /**
     * Stores the last encountered node. Not needed for general drawing. Used to show path.
     * Similar to {@link #inLast}, {@link #postLast} and {@link #levelLast}.
     */
    private static Node preLast = null;
    /**
     * Stores the last encountered node. Not needed for general drawing. Used to show path.
     * Similar to {@link #preLast}, {@link #inLast} and {@link #levelLast}.
     */
    private static Node postLast = null;
    /**
     * Stores the last encountered node. Not needed for general drawing. Used to show path.
     * Similar to {@link #preLast}, {@link #postLast} and {@link #inLast}.
     */
    private static Node levelLast = null;
    /**
     * The root Node
     */
    Node root;
    private int maxHeight = 0;
    private int nodeNumber = 0;
    private int SCREEN_WIDTH = 600;
    private int SCREEN_HEIGHT = 700;
    private int XSCALE;
    private int YSCALE;
    private int SPEED = 1000;
    private int preOffset = 0;
    private int postOffset = 0;

    //default constructor
    BinarySearchTree() {
        root = null;
    }

    /**
     * Initializes the remaining data of Binary Search Tree after the tree has been created.
     * Calls {@link #computeNodeCoordinates()} and {@link #treeHeight(Node)}.
     *
     * @param width   the width of the canvas, in general 600 and 1200 for compare mode
     * @param height  the height of the canvas, default 700
     * @param speed   the speed of the drawing, defaults to 1 Node/sec
     * @param compare if compare mode has been enabled or not
     */
    public void init(int width, int height, int speed, Boolean compare) {
        if (root != null) {
            computeNodeCoordinates();
            maxHeight = treeHeight(root);
            SCREEN_HEIGHT = height;
            SCREEN_WIDTH = width;
            XSCALE = (SCREEN_WIDTH) / nodeNumber;
            YSCALE = (SCREEN_HEIGHT * -1) / (maxHeight + 1);
            SPEED = speed;
            if (compare) {
                preOffset = 400;
                postOffset = 800;
            }
        }
    }

    /**
     * Calculates the height of the tree by recursion
     *
     * @param t root node of the tree
     * @return the height of the tree
     */
    private int treeHeight(Node t) {
        if (t == null) {
            return -1;
        } else {
            return 1 + Math.max(treeHeight(t.left), treeHeight(t.right));
        }
    }

    /**
     * Computes Node coordinates by inorder traversal. Inorder traversal ensures that all nodes
     * gets proper spacing noth in x and y axes. Internally calls {@link #inorder_traversal(Node, int)}.
     */
    private void computeNodeCoordinates() {
        int depth = 1;
        inorder_traversal(root, depth);
    }

    /**
     * Inorder Traversal to compute node coordinates. Could be Preorder or Postorder too but relative
     * positioning of the nodes remains same however the tree get askew.
     *
     * @param t     root node to initiate
     * @param depth parameter used for making recursion useful
     */
    private void inorder_traversal(Node t, int depth) {
        if (t != null) {
            inorder_traversal(t.left, depth + 1);
            t.xpos = nodeNumber;
            t.ypos = depth;
            inorder_traversal(t.right, depth + 1);
        } else {
            nodeNumber++;
        }
    }

    /**
     * Recursive routine to insert a new node into the tree. Binary Search Tree posses special properties.
     * Data is feed as a string {@code s} later parsed to integer and inserted by comparision.
     *
     * @param root root node of the tree
     * @param s    integers as string
     * @return root node of the tree
     */
    public Node insert(Node root, String s) {
        if (root == null) {
            root = new Node(s, null, null);
        } else {
            if (Integer.parseInt(s) == Integer.parseInt(root.data)) {
                return root;
            } else if (Integer.parseInt(s) < Integer.parseInt(root.data)) {
                root.left = insert(root.left, s);
                root.neighbour.add(root.left);
                root.left.neighbour.add(root);
            } else if (Integer.parseInt(s) > Integer.parseInt(root.data)) {
                root.right = insert(root.right, s);
                root.right.neighbour.add(root);
                root.neighbour.add(root.right);
            }
        }
        return root;
    }

    /**
     * Recursive drawing routine to visualize Inorder Traversal of the tree. Animates one node at a time.
     *
     * @param node initially root node to be passed
     * @param path the path should be shown or not
     */
    public void drawInOrder(Node node, Boolean path) {
        int x1, y1, x2, y2, x3, y3;
        if (node != null) {
            x1 = node.xpos * XSCALE;
            y1 = node.ypos * YSCALE;
            drawInOrder(node.left, path);
            StdDraw.pause(SPEED);
            StdDraw.text(x1, y1, node.data);
            inVisited.add(node);
            for (Node n : node.neighbour) {
                if (inVisited.contains(n)) {
                    x2 = n.xpos * XSCALE;
                    y2 = n.ypos * YSCALE;
                    if (y1 > y2) {
                        StdDraw.line(x1, y1 - 10, x2, y2 + 15);
                    } else {
                        StdDraw.line(x1, y1 + 15, x2, y2 - 10);
                    }
                }
            }
            if (inLast != null && path) {
                StdDraw.setPenColor(Color.RED);
                x3 = inLast.xpos * XSCALE;
                y3 = inLast.ypos * YSCALE;
                if (y1 > y3) {
                    StdDraw.line(x1, y1 - 10, x3, y3 + 15);
                } else if (y1 == y3) {
                    StdDraw.line(x1, y1 + 15, x3, y3 + 15);
                } else {
                    StdDraw.line(x1, y1 + 15, x3, y3 - 10);
                }
                StdDraw.setPenColor();
            }
            inLast = node;
            StdDraw.show();
            drawInOrder(node.right, path);
        }
    }

    /**
     * Recursive drawing routine to visualize Preorder Traversal of the tree. Animates one node at a time.
     *
     * @param node initially root node to be passed
     * @param path the path should be shown or not
     */
    public void drawPreOrder(Node node, Boolean path) {
        int x1, y1, x2, y2, x3, y3;
        if (node != null) {
            x1 = node.xpos * XSCALE;
            y1 = node.ypos * YSCALE;
            StdDraw.pause(SPEED);
            StdDraw.text(x1 + preOffset, y1, node.data);
            preVisited.add(node);
            for (Node n : node.neighbour) {
                if (preVisited.contains(n)) {
                    x2 = n.xpos * XSCALE;
                    y2 = n.ypos * YSCALE;
                    if (y1 > y2) {
                        StdDraw.line(x1 + preOffset, y1 - 10, x2 + preOffset, y2 + 15);
                    } else {
                        StdDraw.line(x1 + preOffset, y1 + 15, x2 + preOffset, y2 - 10);
                    }
                }
            }
            if (preLast != null && path) {
                StdDraw.setPenColor(Color.RED);
                x3 = preLast.xpos * XSCALE;
                y3 = preLast.ypos * YSCALE;
                if (y1 > y3) {
                    StdDraw.line(x1, y1 - 10, x3, y3 + 15);
                } else if (y1 == y3) {
                    StdDraw.line(x1, y1 + 15, x3, y3 + 15);
                } else {
                    StdDraw.line(x1, y1 + 15, x3, y3 - 10);
                }
                StdDraw.setPenColor();
            }
            preLast = node;
            StdDraw.show();
            drawPreOrder(node.left, path);
            drawPreOrder(node.right, path);
        }
    }

    /**
     * Recursive drawing routine to visualize Postorder Traversal of the tree. Animates one node at a time.
     *
     * @param node initially root node to be passed
     * @param path the path should be shown or not
     */
    public void drawPostOrder(Node node, Boolean path) {
        int x1, y1, x2, y2, x3, y3;
        if (node != null) {
            x1 = node.xpos * XSCALE;
            y1 = node.ypos * YSCALE;
            drawPostOrder(node.left, path);
            drawPostOrder(node.right, path);
            StdDraw.pause(SPEED);
            StdDraw.text(x1 + postOffset, y1, node.data);
            postVisited.add(node);
            for (Node n : node.neighbour) {
                if (postVisited.contains(n)) {
                    x2 = n.xpos * XSCALE;
                    y2 = n.ypos * YSCALE;
                    if (y1 > y2) {
                        StdDraw.line(x1 + postOffset, y1 - 10, x2 + postOffset, y2 + 15);
                    } else {
                        StdDraw.line(x1 + postOffset, y1 + 15, x2 + postOffset, y2 - 10);
                    }
                }
            }
            if (postLast != null && path) {
                StdDraw.setPenColor(Color.RED);
                x3 = postLast.xpos * XSCALE;
                y3 = postLast.ypos * YSCALE;
                if (y1 > y3) {
                    StdDraw.line(x1, y1 - 10, x3, y3 + 15);

                } else if (y1 == y3) {
                    StdDraw.line(x1, y1 + 15, x3, y3 + 15);

                } else {
                    StdDraw.line(x1, y1 + 15, x3, y3 - 10);

                }
                StdDraw.setPenColor();
            }
            postLast = node;
            StdDraw.show();
        }
    }

    private static final Queue<Node> levelNode = new LinkedList<>();

    /**
     * Recursive drawing routine to visualize Level Traversal of the tree. Animates one node at a time.
     * Also Knows as Breadth First Search
     *
     * @param node initially root node to be passed
     * @param path the path should be shown or not
     */
    public void drawLevelOrder(Node node, Boolean path) {
        int x1, y1, x2, y2, x3, y3;
        levelNode.add(node);
        while (!levelNode.isEmpty()) {
            Node curr = levelNode.remove();
            x1 = curr.xpos * XSCALE;
            y1 = curr.ypos * YSCALE;
            StdDraw.pause(SPEED);
            StdDraw.text(x1, y1, curr.data);
            if (curr.left != null) {
                levelNode.add(curr.left);
            }
            if (curr.right != null) {
                levelNode.add(curr.right);
            }
            levelVisited.add(curr);
            for (Node n : curr.neighbour) {
                if (levelVisited.contains(n)) {
                    x2 = n.xpos * XSCALE;
                    y2 = n.ypos * YSCALE;
                    if (y1 > y2) {
                        StdDraw.line(x1, y1 - 10, x2, y2 + 15);
                    } else {
                        StdDraw.line(x1, y1 + 15, x2, y2 - 10);
                    }
                }
            }
            if (levelLast != null && path) {
                StdDraw.setPenColor(Color.RED);
                x3 = levelLast.xpos * XSCALE;
                y3 = levelLast.ypos * YSCALE;
                if (y1 == y3) {
                    StdDraw.line(x1, y1 + 15, x3, y3 + 15);
                } else {
                    StdDraw.line(x1, y1 + 15, x3, y3 - 10);
                }
                StdDraw.setPenColor();
            }
            levelLast = curr;
            StdDraw.show();
        }
    }
}

/**
 * Node class to form Binary Search Tree.
 */
class Node {
    /**
     * Data of the Node
     */
    String data;

    /**
     * abscissa of the Node
     */
    int xpos;

    /**
     * Ordinate of the Node
     */
    int ypos;

    /**
     * Left child of the Node
     */
    Node left;

    /**
     * Right child of the node
     */
    Node right;

    /**
     * Contains closest neighbours of a node. Maximum 3 and at least 1.
     */
    Set<Node> neighbour = new HashSet<>();

    /**
     * Default constructor of the Node class.
     *
     * @param data  data of the node
     * @param left  left child
     * @param right right child
     */
    Node(String data, Node left, Node right) {
        this.left = left;
        this.right = right;
        this.data = data;
    }
}