package ColoringBook.graphics;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

class DbgButton extends Canvas {
    
    enum BtnState {
        ENABLED,
        DISABLED
    };
    
    private static final long serialVersionUID = 1L;
    private char _btnKey;
    private BufferedImage[] _btnFaces;
    private int _crtFace;
    
    public DbgButton(char btnKey, int xAnchor, int yAnchor, String... btnFaceFiles) throws IOException {
        _btnKey = btnKey;
        _btnFaces = new BufferedImage[btnFaceFiles.length];
        for(int i = 0; i < _btnFaces.length; i++) {
            _btnFaces[i] = ImageIO.read(new File(btnFaceFiles[i]));
        }
        _crtFace = 0;
        this.setBounds(
                xAnchor, yAnchor,
                _btnFaces[_crtFace].getWidth(), _btnFaces[_crtFace].getHeight());
    }
    
    // Region: [Public] Canvas overrides
    @Override
    public void paint(Graphics g) {
        g.drawImage(_btnFaces[_crtFace], 0, 0, null);
    }
    // EndRegion: [Public] Canvas overrides
    
    // Region: [Internal] Accessors and mutators
    char getKey() {
        return _btnKey;
    }
    
    void setState(BtnState state) {
        _crtFace = (state == BtnState.ENABLED) ? 0 : 1;
        repaint();
    }
    
    BtnState getState() {
        return (_crtFace == 0) ? BtnState.ENABLED : BtnState.DISABLED;
    }
    // EndRegion: [Internal] Accessors and mutators
}
