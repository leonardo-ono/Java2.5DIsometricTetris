package graphic25d;

/**
 * 
 * @author leo
 */
public class TetrisModel {
    
    private boolean gameOver = true;
    
    private int score;
    private final int[] scoreTable = { 0, 10, 30, 50, 100 };
    
    private final int gridCols = 10, gridRows = 24;
    private int[][] grid = new int[gridRows][gridCols];
    
    private long[] blockData = { 0xF00444400F02222l, 
        0x660066006600660l, 0xC6004C800C60264l, 0x6C008C4006C0462l, 
        0x8E0044C00E20644l, 0xE8044602E00C44l, 0x46404E004C400E4l };
    
    private int[][][] blocks = new int[7][4][8];
    private int blockCurrent, blockNext, blockRotation;
    private int blockRow = 0, blockCol = 3;
    
    public TetrisModel() {
        initBlocks();
        blockCurrent = (int) (7 * Math.random());
        blockNext = (int) (7 * Math.random());
    }

    private void initBlocks() {
        for (int p = 0; p < blockData.length; p++) {
            int colRow = 0;
            for (int b = 0; b < 64; b++) {
                colRow = b % 16 == 0 ? 0 : colRow;
                if (((blockData[p] >> b) & 1) == 1) {
                    blocks[p][b / 16][colRow++] = b % 4;
                    blocks[p][b / 16][colRow++] = (b % 16) / 4;
                }
            }
        }
    }
    
    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public int getGridCols() {
        return gridCols;
    }

    public int getGridRows() {
        return gridRows;
    }
    
    public int getGridValue(int col, int row) {
        int[] block = blocks[blockCurrent][blockRotation];
        for (int i = 0; i < block.length; i+=2) {
            if (col == block[i] + blockCol && row == block[i + 1] + blockRow) {
                return blockCurrent + 1;
            }
        }
        return grid[row][col];
    }
    
    public int getNextBlockValue(int col, int row) {
        int[] block = blocks[blockNext][0];
        for (int i = 0; i < block.length; i+=2) {
            if (col == block[i] && row == block[i + 1]) {
                return blockNext + 1;
            }
        }
        return 0;
    }
    
    private boolean collides(int dx, int dy, int rotation) {
        int[] block = blocks[blockCurrent][rotation];
        for (int i = 0; i < block.length; i+=2) {
            int col = block[i] + blockCol + dx;
            int row = block[i + 1] + blockRow + dy;
            if (col < 0 || col > gridCols - 1 
                    || row < 0 || row > gridRows - 1
                    || grid[row][col] > 0) {
                return true;
            }
        }
        return false;
    }
    
    public void start() {
        grid = new int[gridRows][gridCols];
        blockCol = 3;
        blockRow = 0;
        score = 0;
        gameOver = false;
    }
    
    public void move(int dx) {
        if (!collides(dx, 0, blockRotation)) {
            blockCol += dx;
        }
    }

    public void rotate() {
        int nextBlockRotation = (blockRotation + 1) % 4;
        if (!collides(0, 0, nextBlockRotation)) {
            blockRotation = nextBlockRotation;
        }
    }
    
    public void down() {
        while (!collides(0, 1, blockRotation)) {
            blockRow++;
        }
    }

    public void update() {
        int clearedLineCount = clearFilledLines();
        // update score
        score += scoreTable[clearedLineCount];
        // fall block
        if (!collides(0, 1, blockRotation)) {
            blockRow++;
            return;
        }
        // check game over
        if (blockRow < 4 || gameOver) {
            gameOver = true;
            return;
        }
        solidify();
        nextBlock();
    }
    
    private int clearFilledLines() {
        int clearedLineCount = 0;
        nextRow:
        for (int row=0; row<gridRows; row++) {
            for (int col=0; col<gridCols; col++) {
                if (grid[row][col] == 0) {
                    continue nextRow;
                }
            }
            for (int row2 = row; row2 > 0; row2--) {
                System.arraycopy(grid[row2 - 1], 0, grid[row2], 0, gridCols);
            }
            clearedLineCount++;
        }
        return clearedLineCount;
    }
    
    private void solidify() {
        int[] p = blocks[blockCurrent][blockRotation];
        for (int i=0; i<p.length; i+=2) {
            grid[p[i + 1] + blockRow][p[i] + blockCol] = blockCurrent + 1;
        }
    }

    private void nextBlock() {
        blockCol = 3;
        blockRow = 0;
        blockRotation = 0;
        blockCurrent = blockNext;
        blockNext = (int) (7 * Math.random());
    }
    
}
