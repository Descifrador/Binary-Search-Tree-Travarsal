import java.awt.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class BinarySearchTree {
    Node root;
    int maxHeight = 0;
    int nodeNumber = 0;
    int SCREEN_WIDTH = 600;
    int SCREEN_HEIGHT = 700;
    int XSCALE;
    int YSCALE;
    int SPEED = 1000;
    int preOffset = 0;
    int postOffset = 0;

    BinarySearchTree() {
        root = null;
    }

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

    private int treeHeight(Node t) {
        if (t == null) {
            return -1;
        } else {
            return 1 + Math.max(treeHeight(t.left), treeHeight(t.right));
        }
    }

    private void computeNodeCoordinates() {
        int depth = 1;
        inorder_traversal(root, depth);
    }

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

    public Node insert(Node root, String s) {
        if (root == null) {
            root = new Node(s, null,null);
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


    private static final Set<Node> inVisited = new HashSet<>();
    private static Node inLast = null;

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

    private static final Set<Node> preVisited = new HashSet<>();
    private static Node preLast = null;

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

    private static final Set<Node> postVisited = new HashSet<>();
    private static Node postLast = null;

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

    private static final Set<Node> levelVisited = new HashSet<>();
    private static Node levelLast = null;
    private static final Queue<Node> levelNode = new LinkedList<>();

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

class Node {
    String data;
    int xpos;
    int ypos;
    Node left;
    Node right;
    Set<Node> neighbour = new HashSet<>();

    Node(String data, Node left, Node right) {
        this.left = left;
        this.right = right;
        this.data = data;
    }
}