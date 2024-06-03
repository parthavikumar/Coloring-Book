package ColoringBook.graphics;
import java.awt.Canvas;
import java.awt.Graphics;

class DrawingCanvas extends Canvas {

    private static final long serialVersionUID = 1L;
    private int _xOrig = 0;
    private int _yOrig = 0;
    private int _scale = 1;
    private Drawing _drwImage;
    
    DrawingCanvas(int xAnchor, int yAnchor, Drawing drwImage) {
        _drwImage = drwImage;
        setBounds(
            xAnchor, yAnchor,
            _drwImage.getWidth(), _drwImage.getHeight());
    }
    
    // Region: [Internal] User control methods
    private void constrainMovement() {
        _xOrig = Math.min(0, _xOrig);
        _xOrig = Math.max(_xOrig, getWidth() - _drwImage.getWidth() * _scale);
        _yOrig = Math.min(0, _yOrig);
        _yOrig = Math.max(_yOrig,  getHeight() - _drwImage.getHeight() * _scale);
    }
    
    public int xScreenToCanvas(int x) {
        return (x - _xOrig) / _scale;
    }
    
    public int yScreenToCanvas(int y) {
        return (y - _yOrig) / _scale;
    }
    
    public void zoom(int xAnchor, int yAnchor, int levels) {
        if (levels != 1 && levels != -1) {
            System.out.println("hmm");
        }
        
        int newScale = _scale + levels;
        if(newScale > 0 && newScale <= 8) {
            // find out the pixel in the unscaled image corresponding to (xAnchor, yAnchor)
            int xImg = xScreenToCanvas(xAnchor);
            int yImg = yScreenToCanvas(yAnchor);
            // recalculate the _xOrig & _yOrig
            _xOrig = (xAnchor / newScale - xImg) * newScale;
            _yOrig = (yAnchor / newScale - yImg) * newScale;
            _scale = newScale;
            constrainMovement();
            repaint();
        }
    }
    
    public void pan(int xOffset, int yOffset) {
        _xOrig += xOffset;
        _yOrig += yOffset;
        constrainMovement();
        repaint();
    }
    // EndRegion: [Internal] User control methods
    
    // Region: [Public] Canvas overrides
    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(
                _drwImage.getImage(), 
                _xOrig, _yOrig, 
                _scale * _drwImage.getWidth(),
                _scale * _drwImage.getHeight(),
                null);
    }
    // EndRegion: [Public] Canvas overrides
}
