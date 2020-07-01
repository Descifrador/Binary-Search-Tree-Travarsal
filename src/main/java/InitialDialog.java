import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class InitialDialog extends JDialog implements ActionListener {
    JPanel contentPanel = new JPanel(new BorderLayout());

    JPanel filePane = new JPanel();
    JLabel fileName = new JLabel("Open a File");
    JButton openButton = new JButton("Open");

    JPanel traversePane = new JPanel(new GridLayout(3, 1));
    JRadioButton inRadio = new JRadioButton("InOrder");
    JRadioButton preRadio = new JRadioButton("PreOrder");
    JRadioButton postRadio = new JRadioButton("PostOrder");
    JRadioButton levelRadio = new JRadioButton("LevelOrder");
    ButtonGroup traverseRadio = new ButtonGroup();

    JPanel experimentPane = new JPanel(new GridLayout(3, 1));
    JCheckBox showPath = new JCheckBox("Show Path");
    JCheckBox compareMode = new JCheckBox("Compare Mode");
    JSlider speed = new JSlider(500, 1500, 1000);

    JPanel confirmPane = new JPanel();
    JButton confirmButton = new JButton("Confirm");
    JButton cancelButton = new JButton("Cancel");

    private final DataStructure dataMap = new DataStructure();

    public InitialDialog(Frame parent) {
        super(parent, "Choose Options", true);
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

        {
            confirmButton.addActionListener(this);
            cancelButton.addActionListener(this);

            confirmPane.add(confirmButton);
            confirmPane.add(cancelButton);
            confirmPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            contentPanel.add(confirmPane, BorderLayout.SOUTH);
        }
        this.getContentPane().add(contentPanel);
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == confirmButton) {
            if (dataMap.width == null || dataMap.traversal == null || dataMap.file == null) {
                JOptionPane.showMessageDialog(contentPanel, "Please select one or more options");
            }
        }
        dispose();
    }

    public DataStructure run() {
        this.setVisible(true);
        return dataMap;
    }
}

class DataStructure {
    Integer width = 600;
    String traversal;
    Boolean showPath = false;
    Boolean compareMode = false;
    Integer speed = 1000;
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



