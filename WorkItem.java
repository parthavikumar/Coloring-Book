package ColoringBook.main;


import ColoringBook.graphics.Drawing;
import java.awt.Color;

public class WorkItem {
    private int y;
    private int x;
    private Drawing d;
    

    public WorkItem(int newX, int newY, Drawing newD){
        y= newY;
        x = newX;
        d = newD;

    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Drawing getDrawing(){
        return d;
    }

    public boolean isBright(){
        return d.isBrightPixel(x, y);
    }

    public boolean isValid(){
        return d.isValidPixel(x,y);
    }

    public void Paint(Color c){
        d.setPixel(x, y, c);
    }
}
