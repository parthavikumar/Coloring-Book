package ColoringBook.main;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import ColoringBook.graphics.DrawingFrame;
import ColoringBook.graphics.Drawing;

public class Program {
    
    /**
     * Global static fields for the Drawing object being worked on
     * and the DrawingFrame containing and displaying it.
     */
    private static Drawing _drawing;
    private static DrawingFrame _frame;
    
    /**
     * Demonstrates a simple alteration to the drawing:
     * On a square section of the image, from top-left: (40,30) to bottom-right (140, 130)
     * replace the dark pixels with yellow and the bright pixels with yellow.
     */
    public static void paint() throws InterruptedException {
        for(int x = 40; x < 140; x++) {
            for (int y = 30; y < 130; y++) {
                _frame.step(1);
                if (_drawing.isDarkPixel(x, y)) {
                    _drawing.setPixel(x, y, Color.yellow);
                } else {
                    _drawing.setPixel(x, y, Color.red);
                }
            }
        }
    }
    
    /**
     * Main entry point in the program:
     * Initializes the static Drawing (_drawing) with an image of your choice,
     * then initializes the static DrawingFrame (_frame) loading into it the new drawing.
     * Subsequently the frame is opened on the screen then the drawing is painted upon
     * and displayed as it is being modified before the program terminates. 
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Welcome to the Coloring Festival!");
        
        // pick a drawing
        _drawing = new Drawing("ColoringBook/drawings/bird.jpg");
        
        // put it in a frame
        _frame = new DrawingFrame(_drawing);

        // put the frame on display and stop to admire it.
        _frame.open();
        _frame.step();
        
        // make some change to the drawing, then stop for applause.
        stack(130, 140, Color.pink);
        queue(113, 211, Color.red);

        try {
            recursive(95,100, Color.green);
        } catch (Exception e) {
            System.out.println("Error");
        }
        _frame.stop();
        
        // the show is over.
        //_frame.close();
        
        System.out.println("Well done, goodbye!");
    }

    public static void recursive(int xSeed, int ySeed, Color color) throws InterruptedException{
        for (int x = xSeed-1; x<xSeed+2; x++){
            for(int y = ySeed-1; y<ySeed+2; y++){
                if(_drawing.isValidPixel(x,y)&&_drawing.isBrightPixel(x,y)){
                    _drawing.setPixel(xSeed, ySeed,color);
                    _frame.step(1);
                    
                    recursive(x,y,color);
                }
            }
        }
    }

    public static void stack(int xSeed, int ySeed, Color color)throws InterruptedException{

        Stack<WorkItem> s = new Stack<WorkItem>();
        WorkItem w = new WorkItem(xSeed, ySeed, _drawing);
        w.Paint(color);
        s.push(w);
        while(!s.isEmpty()){
            WorkItem pix = s.pop();
            for(WorkItem n: getNeighbors(pix)){
                if(n.isBright() && n.isValid()){
                    _frame.step(1);
                    n.Paint(color);
                    s.push(n);
                }
            }
        }
    }

    public static void queue(int xSeed, int ySeed, Color color) throws InterruptedException{

        Queue<WorkItem> q = new LinkedList<WorkItem>();
        WorkItem w = new WorkItem(xSeed, ySeed, _drawing);
        w.Paint(color);
        q.add(w);
        while(!q.isEmpty()){
            WorkItem pix = q.remove();
            for(WorkItem n: getNeighbors(pix)){
                if(n.isBright() && n.isValid()){
                    _frame.step(1);
                    n.Paint(color);
                    q.add(n);
                }
            }
        }
    }
    

    private static ArrayList<WorkItem> getNeighbors(WorkItem pix){
        ArrayList<WorkItem> neighbors = new ArrayList<WorkItem>();
        for(int x = pix.getX()-1; x<pix.getX()+2; x++){
            for(int y = pix.getY()-1; y<pix.getY()+2; y++){
                //exlcuding the current pixel
                if(pix.getX() == x && pix.getY() == y){
                    continue;
                }
                neighbors.add(new WorkItem(x, y, pix.getDrawing()));
                    
            }
        }
        return neighbors;
    }

}


