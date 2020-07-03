import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * InitialDialog class
 */
public class InitialDialog extends JDialog implements ActionListener {
    JPanel contentPanel = new JPanel(new BorderLayout());

    JPanel filePane = new JPanel();
    JLabel fileName = new JLabel("Open a File");
    JButton openButton = new JButton("Open");

    /**
     * This is used to store initialization values after action performed by user.
     */
    private final DataStructure dataMap = new DataStructure();
    JRadioButton inRadio = new JRadioButton("InOrder");
    JRadioButton preRadio = new JRadioButton("PreOrder");
    JRadioButton postRadio = new JRadioButton("PostOrder");
    JRadioButton levelRadio = new JRadioButton("LevelOrder");
    ButtonGroup traverseRadio = new ButtonGroup();
    JPanel traversePane = new JPanel(new GridLayout(4, 1));
    JCheckBox showPath = new JCheckBox("Show Path");
    JCheckBox compareMode = new JCheckBox("Compare Mode");
    JSlider speed = new JSlider(500, 1500, 1000);

    JPanel confirmPane = new JPanel();
    JButton confirmButton = new JButton("Confirm");
    JButton cancelButton = new JButton("Cancel");
    JPanel experimentPane = new JPanel(new GridLayout(4, 1));

    /**
     * Default Constructor
     *
     * @param parent JFrame used as parent Window
     */
    public InitialDialog(Frame parent) {
        super(parent, "Choose Options", true);

        // File Pane Initialization Block
        {
            openButton.addActionListener(e -> {
                FileDialog opener = new FileDialog(this, "Use a text file containing numbers only", FileDialog.LOAD);
                opener.setVisible(true);
                if (opener.getFile() != null) {
                    dataMap.setFile(new File(opener.getDirectory() + File.separator + opener.getFile()));
                    fileName.setText(opener.getFile());
                }
            });

            filePane.add(fileName);
            filePane.add(openButton);
            filePane.setBorder(BorderFactory.createTitledBorder("Choose File"));
            contentPanel.add(filePane, BorderLayout.NORTH);
        }

        // Traversal Radiobutton Pane Initialization Block
        {
            traverseRadio.add(inRadio);
            traverseRadio.add(preRadio);
            traverseRadio.add(postRadio);
            traverseRadio.add(levelRadio);

            inRadio.addActionListener(e -> {
                dataMap.setTraversal("inorder");
                dataMap.setCompareMode(false);
                dataMap.setWidth(600);
            });

            preRadio.addActionListener(e -> {
                dataMap.setTraversal("preorder");
                dataMap.setCompareMode(false);
                dataMap.setWidth(600);
            });

            postRadio.addActionListener(e -> {
                dataMap.setTraversal("postorder");
                dataMap.setCompareMode(false);
                dataMap.setWidth(600);
            });

            levelRadio.addActionListener(e -> {
                dataMap.setTraversal("levelorder");
                dataMap.setCompareMode(false);
                dataMap.setWidth(600);
            });

            traversePane.add(inRadio);
            traversePane.add(preRadio);
            traversePane.add(postRadio);
            traversePane.add(levelRadio);
            traversePane.setBorder(BorderFactory.createTitledBorder("Traversal"));
            contentPanel.add(traversePane, BorderLayout.WEST);
        }

        // Experiment Pane Initialization Block
        {
            showPath.addActionListener(e -> {
                if (showPath.isSelected()) {
                    compareMode.setSelected(false);
                    dataMap.setShowPath(true);
                    dataMap.setCompareMode(false);
                }
            });

            compareMode.addActionListener(e -> {
                if (compareMode.isSelected()) {
                    traverseRadio.clearSelection();
                    dataMap.setTraversal("compare");
                    dataMap.setCompareMode(true);
                    showPath.setSelected(false);
                    dataMap.setShowPath(false);
                    dataMap.setWidth(1200);
                }
            });

            speed.addChangeListener(e -> dataMap.setSpeed((speed.getValue() - 2000) * -1));

            experimentPane.add(compareMode);
            experimentPane.add(showPath);
            experimentPane.add(speed);
            experimentPane.setBorder(BorderFactory.createTitledBorder("Experimental"));
            contentPanel.add(experimentPane, BorderLayout.EAST);
        }

        // Confirm Pane Initialization Block
        {
            confirmButton.addActionListener(this);
            cancelButton.addActionListener(this);

            confirmPane.add(confirmButton);
            confirmPane.add(cancelButton);
            confirmPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            contentPanel.add(confirmPane, BorderLayout.SOUTH);
        }
        this.getContentPane().add(contentPanel);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.pack();
    }

    /**
     * Handles the action performed on {@link #confirmButton} and {@link #cancelButton}.
     *
     * @param actionEvent Event performed
     */
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == confirmButton) {
            if (dataMap.width == null || dataMap.traversal == null || dataMap.file == null) {
                JOptionPane.showMessageDialog(contentPanel, "Please select one or more options");
            }
        } else if (source == cancelButton) {
            System.exit(0);
        }
        dispose();
    }

    /**
     * This method invokes the Initial Dialog Class.
     *
     * @return {@link #dataMap} containing user interactions.
     */
    public DataStructure run() {
        this.setVisible(true);
        return dataMap;
    }
}

/**
 * Container Class to hold data selected/enter by the used when application starts.
 */
class DataStructure {
    /**
     * Width of the Drawing Area.
     */
    Integer width = 600;

    /**
     * Traversal types: inorder, preorder, postorder, levelorder
     */
    String traversal;

    /**
     * Enable/Disables the show path feature in drawing routines.
     */
    Boolean showPath = false;

    /**
     * Enable/Disables the comparision mode, currently only supports comparision between
     * inorder, preorder and postorder.
     */
    Boolean compareMode = false;

    /**
     * Drawing speed.
     */
    Integer speed = 1000;

    /**
     * File chosen for tree creation. Must contain numbers only.
     */
    File file;

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setTraversal(String traversal) {
        this.traversal = traversal;
    }

    public void setShowPath(Boolean showPath) {
        this.showPath = showPath;
    }

    public void setCompareMode(Boolean compareMode) {
        this.compareMode = compareMode;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setFile(File file) {
        this.file = file;
    }
}



