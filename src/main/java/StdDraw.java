import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("DuplicatedCode")
public final class StdDraw implements ActionListener {
    /**
     * The color black.
     */
    public static final Color BLACK = Color.BLACK;

    /**
     * The color white.
     */
    public static final Color WHITE = Color.WHITE;

    // default colors
    private static final Color DEFAULT_PEN_COLOR = BLACK;
    private static final Color DEFAULT_CLEAR_COLOR = WHITE;

    // current pen color
    private static Color penColor;

    // default canvas size is DEFAULT_SIZE-by-DEFAULT_SIZE
    private static final int DEFAULT_SIZE = 512;
    private static int width = DEFAULT_SIZE;
    private static int height = DEFAULT_SIZE;

    // default pen radius
    private static final double DEFAULT_PEN_RADIUS = 0.002;

    // current pen radius
    private static double penRadius;

    // show we draw immediately or wait until next show?
    private static boolean defer = false;

    // boundary of drawing canvas, 0% border
    // private static final double BORDER = 0.05;
    private static final double BORDER = 0.00;
    private static final double DEFAULT_XMIN = 0.0;
    private static final double DEFAULT_XMAX = 1.0;
    private static final double DEFAULT_YMIN = 0.0;
    private static final double DEFAULT_YMAX = 1.0;
    private static double xmin, ymin, xmax, ymax;

    // for synchronization
    private static final Object mouseLock = new Object();

    // default font
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 16);

    // current font
    private static Font font;

    // double buffered graphics
    private static BufferedImage offscreenImage, onscreenImage;
    private static Graphics2D offscreen, onscreen;

    // singleton for callbacks: avoids generation of extra .class files
    private static final StdDraw std = new StdDraw();

    // the frame for drawing to the screen
    private static JFrame frame;

    //data carry structure
    public static DataStructure dataStructure;

    static InitialDialog dialog;
    private static int initialize = 0;

    // static initializer
    static {
        init();
        initialize++;
    }

    /**
     * Sets the canvas (drawing area) to be <em>width</em>-by-<em>height</em>
     * pixels. This also erases the current drawing and resets the coordinate
     * system, pen radius, pen color, and font back to their default values.
     * Ordinarily, this method is called once, at the very beginning of a program.
     *
     * @param canvasWidth  the width as a number of pixels
     * @param canvasHeight the height as a number of pixels
     * @throws IllegalArgumentException unless both {@code canvasWidth} and
     *                                  {@code canvasHeight} are positive
     */
    public static void setCanvasSize(int canvasWidth, int canvasHeight) {
        if (canvasWidth <= 0)
            throw new IllegalArgumentException("width must be positive");
        if (canvasHeight <= 0)
            throw new IllegalArgumentException("height must be positive");
        width = canvasWidth;
        height = canvasHeight;
        init();
    }

    // init
    private static void init() {
        if (frame != null)
            frame.setVisible(false);
        frame = new JFrame();

        if (initialize == 0) {
            dialog = new InitialDialog(frame);
            dataStructure = dialog.run();
        }

        offscreenImage = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_ARGB);
        onscreenImage = new BufferedImage(2 * width, 2 * height, BufferedImage.TYPE_INT_ARGB);
        offscreen = offscreenImage.createGraphics();
        onscreen = onscreenImage.createGraphics();
        offscreen.scale(2.0, 2.0); // since we made it 2x as big
        setXscale();
        setYscale();
        offscreen.setColor(DEFAULT_CLEAR_COLOR);
        offscreen.fillRect(0, 0, width, height);
        setPenColor();
        setPenRadius();
        setFont();
        clear();

        // add antialiasing
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        offscreen.addRenderingHints(hints);

        // frame stuff
        RetinaImageIcon icon = new RetinaImageIcon(onscreenImage);
        JLabel draw = new JLabel(icon);
        frame.setContentPane(draw);
        frame.setFocusTraversalKeysEnabled(false); // allow VK_TAB with isKeyPressed()
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes all windows

        // current window
        frame.setTitle("Tree Draw");
        frame.setJMenuBar(createMenuBar());
        frame.pack();
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    // create the menu bar (changed to private)
    private static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("File");
        menuBar.add(menu1);
        JMenuItem menuItem1 = new JMenuItem(" Save ");
        menuItem1.addActionListener(std);
        menuItem1.setActionCommand("save");
        menu1.add(menuItem1);

        JMenu menu3 = new JMenu("Help");
        menuBar.add(menu3);

        JMenuItem help = new JMenuItem("Help");
        help.setActionCommand("help");
        menu3.add(help);
        help.addActionListener(std);

        JMenuItem github = new JMenuItem("GitHub");
        github.setActionCommand("github");
        menu3.add(github);
        github.addActionListener(std);

        JMenuItem about = new JMenuItem("About");
        about.setActionCommand("about");
        menu3.add(about);
        about.addActionListener(std);
        return menuBar;
    }

    // throw an IllegalArgumentException if x is NaN or infinite
    private static void validate(double x, String name) {
        if (Double.isNaN(x))
            throw new IllegalArgumentException(name + " is NaN");
        if (Double.isInfinite(x))
            throw new IllegalArgumentException(name + " is infinite");
    }

    // throw an IllegalArgumentException if s is null
    private static void validateNonnegative(double x, String name) {
        if (x < 0)
            throw new IllegalArgumentException(name + " negative");
    }

    // throw an IllegalArgumentException if s is null
    private static void validateNotNull(Object x, String name) {
        if (x == null)
            throw new IllegalArgumentException(name + " is null");
    }

    /**
     * Sets the <em>x</em>-scale to be the default (between 0.0 and 1.0).
     */
    public static void setXscale() {
        setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
    }

    /**
     * Sets the <em>y</em>-scale to be the default (between 0.0 and 1.0).
     */
    public static void setYscale() {
        setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
    }

    /**
     * Sets the <em>x</em>-scale to the specified range.
     *
     * @param min the minimum value of the <em>x</em>-scale
     * @param max the maximum value of the <em>x</em>-scale
     * @throws IllegalArgumentException if {@code (max == min)}
     * @throws IllegalArgumentException if either {@code min} or {@code max} is
     *                                  either NaN or infinite
     */
    public static void setXscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;
        if (size == 0.0)
            throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            xmin = min - BORDER * size;
            xmax = max + BORDER * size;
        }
    }

    /**
     * Sets the <em>y</em>-scale to the specified range.
     *
     * @param min the minimum value of the <em>y</em>-scale
     * @param max the maximum value of the <em>y</em>-scale
     * @throws IllegalArgumentException if {@code (max == min)}
     * @throws IllegalArgumentException if either {@code min} or {@code max} is
     *                                  either NaN or infinite
     */
    public static void setYscale(double min, double max) {
        validate(min, "min");
        validate(max, "max");
        double size = max - min;
        if (size == 0.0)
            throw new IllegalArgumentException("the min and max are the same");
        synchronized (mouseLock) {
            ymin = min - BORDER * size;
            ymax = max + BORDER * size;
        }
    }

    // helper functions that scale from user coordinates to screen coordinates and
    // back
    private static double scaleX(double x) {
        return width * (x - xmin) / (xmax - xmin);
    }

    private static double scaleY(double y) {
        return height * (ymax - y) / (ymax - ymin);
    }

    /**
     * Clears the screen to the default color (white).
     */
    public static void clear() {
        clear(DEFAULT_CLEAR_COLOR);
    }

    /**
     * Clears the screen to the specified color.
     *
     * @param color the color to make the background
     * @throws IllegalArgumentException if {@code color} is {@code null}
     */
    public static void clear(Color color) {
        validateNotNull(color, "color");
        offscreen.setColor(color);
        offscreen.fillRect(0, 0, width, height);
        offscreen.setColor(penColor);
        draw();
    }

    /**
     * Sets the pen size to the default size (0.002). The pen is circular, so that
     * lines have rounded ends, and when you set the pen radius and draw a point,
     * you get a circle of the specified radius. The pen radius is not affected by
     * coordinate scaling.
     */
    public static void setPenRadius() {
        setPenRadius(DEFAULT_PEN_RADIUS);
    }

    /**
     * Sets the radius of the pen to the specified size. The pen is circular, so
     * that lines have rounded ends, and when you set the pen radius and draw a
     * point, you get a circle of the specified radius. The pen radius is not
     * affected by coordinate scaling.
     *
     * @param radius the radius of the pen
     * @throws IllegalArgumentException if {@code radius} is negative, NaN, or
     *                                  infinite
     */
    public static void setPenRadius(double radius) {
        validate(radius, "pen radius");
        validateNonnegative(radius, "pen radius");

        penRadius = radius;
        float scaledPenRadius = (float) (radius * DEFAULT_SIZE);
        BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        // BasicStroke stroke = new BasicStroke(scaledPenRadius);
        offscreen.setStroke(stroke);
    }

    /**
     * Sets the pen color to the default color (black).
     */
    public static void setPenColor() {
        setPenColor(DEFAULT_PEN_COLOR);
    }

    /**
     * Sets the pen color to the specified color.
     * <p>
     * The predefined pen colors are {@code StdDraw.BLACK}, {@code StdDraw.BLUE},
     * {@code StdDraw.CYAN}, {@code StdDraw.DARK_GRAY}, {@code StdDraw.GRAY},
     * {@code StdDraw.GREEN}, {@code StdDraw.LIGHT_GRAY}, {@code StdDraw.MAGENTA},
     * {@code StdDraw.ORANGE}, {@code StdDraw.PINK}, {@code StdDraw.RED},
     * {@code StdDraw.WHITE}, and {@code StdDraw.YELLOW}.
     *
     * @param color the color to make the pen
     * @throws IllegalArgumentException if {@code color} is {@code null}
     */
    public static void setPenColor(Color color) {
        validateNotNull(color, "color");
        penColor = color;
        offscreen.setColor(penColor);
    }

    /**
     * Sets the font to the default font (sans serif, 16 point).
     */
    public static void setFont() {
        setFont(DEFAULT_FONT);
    }

    /**
     * Sets the font to the specified value.
     *
     * @param font the font
     * @throws IllegalArgumentException if {@code font} is {@code null}
     */
    public static void setFont(Font font) {
        validateNotNull(font, "font");
        StdDraw.font = font;
    }

    /**
     * Draws a line segment between (<em>x</em><sub>0</sub>, <em>y</em><sub>0</sub>)
     * and (<em>x</em><sub>1</sub>, <em>y</em><sub>1</sub>).
     *
     * @param x0 the <em>x</em>-coordinate of one endpoint
     * @param y0 the <em>y</em>-coordinate of one endpoint
     * @param x1 the <em>x</em>-coordinate of the other endpoint
     * @param y1 the <em>y</em>-coordinate of the other endpoint
     * @throws IllegalArgumentException if any coordinate is either NaN or infinite
     */
    public static void line(double x0, double y0, double x1, double y1) {
        validate(x0, "x0");
        validate(y0, "y0");
        validate(x1, "x1");
        validate(y1, "y1");
        offscreen.draw(new Line2D.Double(scaleX(x0), scaleY(y0), scaleX(x1), scaleY(y1)));
        draw();
    }

    /**
     * Writes the given text string in the current font, centered at (<em>x</em>,
     * <em>y</em>).
     *
     * @param x    the center <em>x</em>-coordinate of the text
     * @param y    the center <em>y</em>-coordinate of the text
     * @param text the text to write
     * @throws IllegalArgumentException if {@code text} is {@code null}
     * @throws IllegalArgumentException if {@code x} or {@code y} is either NaN or
     *                                  infinite
     */
    public static void text(double x, double y, String text) {
        validate(x, "x");
        validate(y, "y");
        validateNotNull(text, "text");

        offscreen.setFont(font);
        FontMetrics metrics = offscreen.getFontMetrics();
        double xs = scaleX(x);
        double ys = scaleY(y);
        int ws = metrics.stringWidth(text);
        int hs = metrics.getDescent();
        offscreen.drawString(text, (float) (xs - ws / 2.0), (float) (ys + hs));
        draw();
    }

    /**
     * Pauses for t milliseconds. This method is intended to support computer
     * animations.
     *
     * @param t number of milliseconds
     */
    public static void pause(int t) {
        validateNonnegative(t, "t");
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            System.out.println("Error sleeping");
        }
    }

    /**
     * Copies offscreen buffer to onscreen buffer. There is no reason to call this
     * method unless double buffering is enabled.
     */
    public static void show() {
        onscreen.drawImage(offscreenImage, 0, 0, null);
        frame.repaint();
    }

    // draw onscreen if defer is false
    private static void draw() {
        if (!defer)
            show();
    }

    /**
     * Enables double buffering. All subsequent calls to drawing methods such as
     * {@code line()}, {@code circle()}, and {@code square()} will be deferred until
     * the next call to show(). Useful for animations.
     */
    public static void enableDoubleBuffering() {
        defer = true;
    }

    /**
     * Saves the drawing to using the specified filename. The supported image
     * formats are JPEG and PNG; the filename suffix must be {@code .jpg} or
     * {@code .png}.
     *
     * @param filename the name of the file with one of the required suffixes
     * @throws IllegalArgumentException if {@code filename} is {@code null}
     */
    public static void save(String filename) {
        validateNotNull(filename, "filename");
        File file = new File(filename);
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);

        // png files
        if ("png".equalsIgnoreCase(suffix)) {
            try {
                ImageIO.write(onscreenImage, suffix, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // need to change from ARGB to RGB for JPEG
        // reference:
        // http://archives.java.sun.com/cgi-bin/wa?A2=ind0404&L=java2d-interest&D=0&P=2727
        else if ("jpg".equalsIgnoreCase(suffix)) {
            WritableRaster raster = onscreenImage.getRaster();
            WritableRaster newRaster;
            newRaster = raster.createWritableChild(0, 0, width, height, 0, 0, new int[]{0, 1, 2});
            DirectColorModel cm = (DirectColorModel) onscreenImage.getColorModel();
            DirectColorModel newCM = new DirectColorModel(cm.getPixelSize(), cm.getRedMask(), cm.getGreenMask(),
                    cm.getBlueMask());
            BufferedImage rgbBuffer = new BufferedImage(newCM, newRaster, false, null);
            try {
                ImageIO.write(rgbBuffer, suffix, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid image file type: " + suffix);
        }
    }

    /**
     * This method cannot be called directly.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String option = e.getActionCommand();
        switch (option) {
            case "save":
                FileDialog chooser = new FileDialog(StdDraw.frame, "Use a .png or .jpg extension", FileDialog.SAVE);
                chooser.setVisible(true);
                String filename = chooser.getFile();
                if (filename != null) {
                    StdDraw.save(chooser.getDirectory() + File.separator + chooser.getFile());
                }
                break;
            case "about":
                JOptionPane.showMessageDialog(StdDraw.frame, "Created By:");
                break;
            case "help":
                JOptionPane.showMessageDialog(StdDraw.frame, "Get help here");
                break;
        }
    }

    /***************************************************************************
     * For improved resolution on Mac Retina displays.
     ***************************************************************************/

    private static class RetinaImageIcon extends ImageIcon {

        public RetinaImageIcon(Image image) {
            super(image);
        }

        public int getIconWidth() {
            return super.getIconWidth() / 2;
        }

        /**
         * Gets the height of the icon.
         *
         * @return the height in pixels of this icon
         */
        public int getIconHeight() {
            return super.getIconHeight() / 2;
        }

        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.scale(0.5, 0.5);
            super.paintIcon(c, g2, x * 2, y * 2);
            g2.dispose();
        }
    }

}