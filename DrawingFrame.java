package ColoringBook.graphics;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;

import ColoringBook.graphics.DbgButton.BtnState;

/**
 * Encapsulates a representation of a generic drawing image frame as a window that 
 * can be interacted with. A DrawingFrame object can be created only by providing a valid
 * Drawing object as argument to its constructor. In return, the object can be used for
 * displaying the drawing image on the screen and reflecting its changes as coded
 * in the program, in an interactive manner.
 */
public class DrawingFrame implements 
    Closeable, WindowListener, 
    MouseListener, MouseMotionListener, MouseWheelListener {
    
    private static final String TITLE = "Coloring Book";
    private static final int PADDING = 4;
    private static final int STATUS_XY_WIDTH = 32;
    private static final int STATUS_TEXT_WIDTH = 200;
    private static final int STATUS_HEIGHT = 20;

    private MouseEvent _lastMouseEvent = null;
    private Drawing _drawing = null;
    private Frame _frame = null;
    private DbgButton[] _dbgButtons = null;
    private DrawingCanvas _canvas = null;
    private TextField _statusX = null;
    private TextField _statusY = null;
    private TextField _statusText = null;
    
    // Region: [Public] Execution control methods
    private KeyInterceptor _keyInterceptor = new KeyInterceptor();
    
    /**
     * In "step-by-step" mode (default) it causes the execution to stop until user resumes.<br>
     * In "continue" mode it does nothing.<p>
     * The “step-by-step” mode is defined as the segments of execution controlled by the user
     * either pressing the key “1” or clicking the first (left-most) interactive button.<br>
     * The “continue” mode is defined as the segments of execution controlled by the user
     * either pressing the key “2” or clicking the second (from the left) interactive button.
     * @throws InterruptedException
     */
    public void step() throws InterruptedException {
        step(1, 0);
    }
    
    /**
     * In "step-by-step" mode (default) it causes the execution to stop until user resumes.<br>
     * In “continue” mode it causes the execution to be paused for a given milliseconds duration.<p>
     * @throws InterruptedException
     * @see DrawingFrame#step()
     */
    public void step(long delay) throws InterruptedException {
        step(1, delay);
    }
    
    /**
     * In “step-by-step” or “continue” modes it causes the program execution 
     * to stop until the user is explicitly resuming it.
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        StackTraceElement stackFrame = new Throwable().getStackTrace()[1]; 
        String dbgLine = String.format("%s @ %d",stackFrame.getFileName(), stackFrame.getLineNumber());
        _statusText.setText(dbgLine);
        step(2, 0);
        _statusText.setText("");
    }
    
    private void step(int level, long delay) throws InterruptedException {
        if (_keyInterceptor.blocksOnLevel(level)) {
            _dbgButtons[0].setState(BtnState.ENABLED);
            _dbgButtons[1].setState(BtnState.ENABLED);
            _dbgButtons[2].setState(BtnState.ENABLED);
        }
        _canvas.repaint();
        _keyInterceptor.step(level, delay);
    }
    // EndRegion: [Public] Execution control methods
    
    // Region: [Private] KeyInterceptor hooks
    private KeyInterceptor.KeyHook _onKeyInteceptorCtrl = (keyEvent) -> {
        char ch = keyEvent.getKeyChar();
        switch (Character.toUpperCase(ch)) {
        case '1':
            _dbgButtons[0].setState(BtnState.ENABLED);
            _dbgButtons[1].setState(BtnState.ENABLED);
            _dbgButtons[2].setState(BtnState.ENABLED);
            break;
        case '2':
            _dbgButtons[0].setState(BtnState.ENABLED);
            _dbgButtons[1].setState(BtnState.DISABLED);
            _dbgButtons[2].setState(BtnState.ENABLED);
            break;
        case ' ':
            _dbgButtons[0].setState(BtnState.ENABLED);
            _dbgButtons[1].setState(BtnState.ENABLED);
            _dbgButtons[2].setState(BtnState.DISABLED);
            break;
        }
    };
    // EndRegion: [Private] KeyInterceptor hooks

    // Region: [Private] DbgButtons management
    private void dbgButtonsSetup(int xAnchor, int yAnchor) throws IOException {
        char[] dbgKeys = {'1', '2', ' '};
        _dbgButtons = new DbgButton[3];
        for (int i = 0; i < _dbgButtons.length; i++) {
            if (i < _dbgButtons.length - 1) {
                _dbgButtons[i] = new DbgButton(
                        dbgKeys[i],
                        xAnchor,
                        yAnchor,
                        String.format("ColoringBook/graphics/res/%d_up.png", i+1),
                        String.format("ColoringBook/graphics/res/%d_down.png", i+1));
                xAnchor += _dbgButtons[i].getWidth();
            } else {
                xAnchor += 4 * PADDING;
                _dbgButtons[i] = new DbgButton(
                        dbgKeys[i],
                        xAnchor,
                        yAnchor,
                        "ColoringBook/graphics/res/ff_up.png",
                        "ColoringBook/graphics/res/ff_down.png");
            }
            _dbgButtons[i].setState(BtnState.DISABLED);
        }
    }
    // EndRegion: [Private] DbgButtons management
    
    // Region: [Private] StatusBar management
    private void statusBarSetup(int xAnchor, int yAnchor, int width) {
        int x = xAnchor;
        _statusX = new TextField();
        _statusX.setEditable(false);
        _statusX.setBackground(Color.LIGHT_GRAY);
        _statusX.setBounds(x, yAnchor, STATUS_XY_WIDTH, STATUS_HEIGHT);
        x += STATUS_XY_WIDTH;
        _statusY = new TextField();
        _statusY.setEditable(false);
        _statusY.setBackground(Color.LIGHT_GRAY);
        _statusY.setBounds(x, yAnchor, STATUS_XY_WIDTH, STATUS_HEIGHT);
        x += STATUS_XY_WIDTH;
        _statusText = new TextField();
        _statusText.setEditable(false);
        _statusText.setBackground(Color.LIGHT_GRAY);
        int w = Math.min(STATUS_TEXT_WIDTH, width - x);
        _statusText.setBounds(
                Math.max(x, xAnchor + width - STATUS_TEXT_WIDTH),
                yAnchor, 
                w,
                STATUS_HEIGHT);
    }
    // EndRegion: [Private] StatusBar management
    
    /**
     * Creates an instance of a DrawingFrame object encapsulating the representation of a window displaying the pixels of the given drawing object.
     * @param drawing - the drawing to be displayed by this frame.
     * @throws IOException
     */
    public DrawingFrame(Drawing drawing) throws IOException {
        // setup callback methods for keyInterceptor control keys
        _keyInterceptor.setKeyTypedHook('1', _onKeyInteceptorCtrl);
        _keyInterceptor.setKeyTypedHook('2', _onKeyInteceptorCtrl);
        _keyInterceptor.setKeyTypedHook(' ', _onKeyInteceptorCtrl);
        
        _drawing = drawing;
        
        // create the frame and get the insets
        _frame = new Frame(TITLE);
        _frame.setBackground(Color.LIGHT_GRAY);
        _frame.pack();
        Insets insets = _frame.getInsets();
        
        // setup the xAnchor and yAnchor to anchor controls
        int xAnchor = insets.left + PADDING;
        int yAnchor = insets.top + PADDING;
        
        // create the debug buttons
        dbgButtonsSetup(xAnchor, yAnchor);
        yAnchor += _dbgButtons[0].getHeight() + PADDING;
        
        // create the map canvas
        _canvas = new DrawingCanvas(xAnchor, yAnchor, _drawing);
        _canvas.addKeyListener(_keyInterceptor);
        _canvas.addMouseMotionListener(this);
        _canvas.addMouseListener(this);
        _canvas.addMouseWheelListener(this);
        yAnchor += _canvas.getHeight() + PADDING;
        
        // create the status bar indicators
        statusBarSetup(xAnchor, yAnchor, _drawing.getWidth());
        yAnchor += STATUS_HEIGHT + PADDING;
        
        // layout the frame size and attributes
        _frame.setSize(
                xAnchor + _drawing.getWidth() + PADDING + insets.right,
                yAnchor + insets.bottom);
        _frame.setLayout(null);
        _frame.setLocationRelativeTo(null);
        _frame.setResizable(false);
        
        // add the controls
        for(DbgButton dbgButton : _dbgButtons) {
            dbgButton.addMouseListener(this);
            dbgButton.addKeyListener(_keyInterceptor);
            _frame.add(dbgButton);
        }
        
        _frame.add(_canvas);
        _frame.add(_statusX);
        _frame.add(_statusY);
        _frame.add(_statusText);
        
        // add the listeners
        _frame.addKeyListener(_keyInterceptor);
        _frame.addWindowListener(this);
    }
    
    // Region: [Public] Frame display methods
    /**
     * Opens a window on the screen, displaying the associated Drawing
     * and the controls for interacting with it. 
     */
    public void open() {
        _frame.setVisible(true);
    }
    
    /**
     * Forces a refresh of the window content such that any changes that may have been
     * operated on the associated Drawing are reflected on the screen.
     */
    public void repaint() {
        _canvas.repaint();
    }
    
    /**
     * Prints out the given message in the status bar area, the lower right corner of
     * the drawing window.
     * @param message - message to be printed in the status bar area.
     */
    public void setStatusMessage(String message) {
        _statusText.setText(message);
    }
    
    /**
     * Closes the window.
     */
    @Override
    public void close() throws IOException {
        if (_frame != null) {
            _frame.setVisible(false);
            _frame.dispose();
            _frame = null;
        }
    }
    // EndRegion: [Public] Frame display methods

    // Region: [Public] WindowListener overrides
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            this.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    // EndRegion: [Public] WindowListener overrides

    // Region: [Public] MouseListener overrides
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof DbgButton) {
            DbgButton dbgButton = (DbgButton)e.getSource();
            if (dbgButton.getState() == BtnState.ENABLED) {
                _keyInterceptor.simulateKeyTyped(dbgButton, dbgButton.getKey());
            }
        } else {
            BufferedImage bi = _drawing.getImage();
            Color c = new Color(bi.getRGB(e.getX(), e.getY()));
            _statusText.setText(String.format("R:%d, G:%d, B:%d", c.getRed(), c.getGreen(), c.getBlue()));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    // EndRegion: [Public] MouseListener overrides

    // Region: [Public] MouseMotionListener overrides
    @Override
    public void mouseDragged(MouseEvent e) {
        _canvas.pan(e.getX()-_lastMouseEvent.getX(), e.getY()-_lastMouseEvent.getY());
        _lastMouseEvent = e;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        _lastMouseEvent = e;
        _statusX.setText(""+_canvas.xScreenToCanvas(e.getX()));
        _statusY.setText(""+_canvas.yScreenToCanvas(e.getY()));
        _statusText.setText("");
    }
    // EndRegion: [Public] MouseMotionListener overrides

    // Region: [Public] MouseWheelListener overrides
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // wheel upwards (negative rotation) => zoom in => positive level value
        int levels = -e.getWheelRotation();
        _canvas.zoom(e.getX(), e.getY(), levels);
    }
    // EndRegion: [Public] MouseWheelListener overrides
}
