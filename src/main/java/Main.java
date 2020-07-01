import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        StdDraw.enableDoubleBuffering();
        drawRoutine(StdDraw.dataStructure);
    }

    public static void drawRoutine(DataStructure dataMap) {
        BinarySearchTree tree = new BinarySearchTree();
        BufferedReader diskInput;
        String word;
        try {
            diskInput = new BufferedReader(new InputStreamReader(new FileInputStream(dataMap.file)));
            Scanner input = new Scanner(diskInput);
            while (input.hasNext()) {
                word = input.next();
                tree.root = tree.insert(tree.root, word);
            }
            input.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Maybe you didn't choose correct file !!");
            System.exit(1);
        }
        tree.init(dataMap.width == 1200 ? 400 : 600, 700, dataMap.speed, dataMap.compareMode);
        StdDraw.setCanvasSize(dataMap.width, 600);
        StdDraw.setXscale(0, dataMap.width);
        StdDraw.setYscale(-790, 10);
        if (dataMap.compareMode && "compare".equals(dataMap.traversal)) {
            StdDraw.line(400, 10, 400, -790);
            StdDraw.line(800, 10, 800, -790);

            Thread inThread = new Thread("InThread") {
                @Override
                public void run() {
                    StdDraw.text(200, -5, "InOrder");
                    tree.drawInOrder(tree.root, dataMap.showPath);
                }
            };

            Thread preThread = new Thread("PreThread") {
                @Override
                public void run() {
                    StdDraw.text(600, -5, "PreOrder");
                    tree.drawPreOrder(tree.root, dataMap.showPath);
                }
            };

            Thread postThread = new Thread("PostThread") {
                @Override
                public void run() {
                    StdDraw.text(1000, -5, "PostOrder");
                    tree.drawPostOrder(tree.root, dataMap.showPath);
                }
            };

            inThread.start();
            preThread.start();
            postThread.start();

            try {
                inThread.join();
                preThread.join();
                postThread.join();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Thread Terminated Unexpectedly");
                System.exit(1);
            }
        } else if (!dataMap.compareMode && "inorder".equals(dataMap.traversal)) {
            StdDraw.text(300, -5, "InOrder");
            tree.drawInOrder(tree.root, dataMap.showPath);
        } else if (!dataMap.compareMode && "preorder".equals(dataMap.traversal)) {
            StdDraw.text(300, -5, "PreOrder");
            tree.drawPreOrder(tree.root, dataMap.showPath);
        } else if (!dataMap.compareMode && "postorder".equals(dataMap.traversal)) {
            StdDraw.text(300, -5, "PostOrder");
            tree.drawPostOrder(tree.root, dataMap.showPath);
        } else if (!dataMap.compareMode && "levelorder".equals(dataMap.traversal)) {
            StdDraw.text(300, -5, "LevelOrder");
            tree.drawLevelOrder(tree.root, dataMap.showPath);
        }
    }
}

