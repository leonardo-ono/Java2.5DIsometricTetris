package graphic25d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class TetrisView extends JFrame {
    
    private BufferedImage offscreen;
    private TetrisModel model = new TetrisModel();

    private Color[] colors = { 
        Color.BLACK, 
        new Color(255, 0, 0), 
        new Color(0, 255, 0), 
        new Color(0, 0, 255), 
        new Color(255, 255, 0), 
        new Color(0, 255, 255), 
        new Color(255, 0, 255), 
        new Color(55, 155, 255), 
    };

    private Color[] colorsDarker = { 
        Color.BLACK, 
        new Color(110, 0, 0), 
        new Color(0, 110, 0), 
        new Color(0, 0, 110), 
        new Color(110, 110, 0), 
        new Color(0, 110, 110), 
        new Color(110, 0, 110), 
        new Color(10, 50, 110), 
    };
    
    private Font font = new Font("Arial", Font.PLAIN, 20);
    
    public TetrisView() throws HeadlessException {
        setSize(450, 600);
        setTitle("2.5D Isometric Tetris test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        offscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!model.isGameOver()) {
                        model.update();
                    }
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) { }
                }
            }
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        draw((Graphics2D) offscreen.getGraphics());
        g.drawImage(offscreen, 0, 0, null);
    }
    
    
    public void draw(Graphics2D g) {
        g.setBackground(getBackground());
        g.clearRect(0, 0, getWidth(), getHeight());
        
        drawScore(g, 50, 100);
        drawNextPiece(g, 50, 150);
        
        AffineTransform at = g.getTransform();
        drawGrid(g);
        g.setTransform(at);
        
        if (model.isGameOver()) {
            drawGameOver(g);
        }
        
        drawCredit(g, 370, 590);
    }

    private int blockSize = 50;
    private Polygon block = new Polygon();
    
    private void drawBlock(Graphics2D g, int col, int row, Color color, Color darker) {
        int ry = row * blockSize;
        
        block.reset();
        block.addPoint(row * blockSize, ry + col * blockSize);
        block.addPoint(row * blockSize, ry + col * blockSize + blockSize);
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize + 2 * blockSize);
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize + blockSize);
        g.setColor(color);
        g.fill(block);
        g.setColor(Color.BLACK);
        g.draw(block);
        
        block.reset();
        block.addPoint(row * blockSize, ry + col * blockSize);
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize + blockSize);
        block.addPoint(row * blockSize + 2 * blockSize, ry + col * blockSize + blockSize);
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize);
        g.setColor(darker);
        g.fill(block);
        g.setColor(Color.BLACK);
        g.draw(block);

        block.reset();
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize + blockSize);
        block.addPoint(row * blockSize + blockSize, ry + col * blockSize + 2 * blockSize);
        block.addPoint(row * blockSize + 2 * blockSize, ry + col * blockSize + 2 * blockSize);
        block.addPoint(row * blockSize + 2 * blockSize, ry + col * blockSize + blockSize);
        g.setColor(darker);
        g.fill(block);
        g.setColor(Color.BLACK);
        g.draw(block);
    }
    
    private void drawCredit(Graphics g, int x, int y) {
        g.setColor(Color.GRAY);
        g.setFont(font);
        g.drawString("by O.L.", x, y);
    }

    private void drawScore(Graphics g, int x, int y) {
        g.setColor(getForeground());
        g.setFont(font);
        g.drawString("SCORE: " + model.getScore(), x, y);
    }
    
    private void drawGrid(Graphics2D g) {
        g.scale(0.5, 0.5);
        g.translate(700, 1100);
        g.scale(1, -0.5);
        g.rotate(Math.toRadians(45));
        // gray background
        for (int y=1; y<21; y++) {
            for (int x=8; x>=-1; x--) {
                drawBlock(g, x, y, Color.GRAY, Color.DARK_GRAY);
            }
        }
        // draw grid
        for (int y=0; y<20; y++) {
            for (int x=9; x>=0; x--) {
                int c = model.getGridValue(9 - x, 23 - y);
                if (c > 0) {
                    drawBlock(g, x, y, colors[c], colorsDarker[c]);
                }
            }
        }
    }
    
    private void drawNextPiece(Graphics g, int dx, int dy) {
        g.setColor(Color.BLACK);
        g.setFont(font);
        g.drawString("NEXT: ", dx, dy);
        int cellSize = 10;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int x = col * cellSize + dx;
                int y = row * cellSize + dy + 5;
                int c = model.getNextBlockValue(col, row);
                g.setColor(Color.GRAY);
                g.fillRect(x, y, cellSize, cellSize);
                if (c > 0) {
                    g.setColor(colors[c]);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(75, 245, 300, 100);
        g.setColor(getForeground());
        g.drawRect(75, 245, 300, 100);
        g.drawString("GAME OVER", 160, 285);
        g.drawString("PRESS SPACE TO PLAY", 105, 320);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (model.isGameOver()) {
                if (e.getKeyCode() == 32) {
                    model.start();
                }
            }
            else {
                switch (e.getKeyCode()) {
                    case 37: model.move(-1); break;
                    case 39: model.move(1); break;
                    case 38: model.rotate(); break;
                    case 40: model.down(); break;
                    case 65: model.update(); break;
                }
            }
        }
        repaint();
    }
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TetrisView view = new TetrisView();
                view.setVisible(true);
            }
        });
    }
    
}
