package week4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private int[][] tiles;
    private int blankRow;
    private int blankCol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        constructTiles(tiles);
    }

    private void constructTiles(int[][] inputTiles) {
        this.tiles = new int[inputTiles.length][];
        for (int i = 0; i < inputTiles.length; i++) {
            this.tiles[i] = new int[inputTiles.length];

            for (int j = 0; j < inputTiles.length; j++) {
                if (inputTiles[i][j] == 0) {
                    this.blankRow = i;
                    this.blankCol = j;
                }
                this.tiles[i][j] = inputTiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(this.tiles.length)
                .append("\n");

        for (int[] row : this.tiles) {
            for (int column : row) {
                builder.append(" ").append(column).append(" ");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        int counter = 0;
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles.length; col++) {
                var actualValue = this.tiles[row][col];
                if (actualValue == 0) {
                    continue;
                }
                var expectedValue = expectedValue(row, col, tiles.length);
                counter += calculateNewCounter(expectedValue, actualValue);
            }
        }
        return counter;
    }

    private int calculateNewCounter(int expected, int actual) {
        return expected != actual ? 1 : 0;
    }

    private boolean isLastElement(int rowIdx, int colIdx, int size) {
        return (rowIdx == size - 1) && (colIdx == size - 1);
    }

    private int expectedValue(int rowIdx, int columnIdx, int size) {
        if (isLastElement(rowIdx, columnIdx, size)) {
            return 0;
        } else {
            return rowIdx * size + columnIdx + 1;
        }
    }

    private int findRow(int value, int columnIdx, int size) {
        return (value - 1 - columnIdx) / size;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int counter = 0;
        for (int row = 0; row < tiles.length; row++) {
            for (int col = 0; col < tiles.length; col++) {
                var actualValue = this.tiles[row][col];
                if (actualValue == 0) {
                    continue;
                }
                var expectedY = findColumn(actualValue, tiles.length);
                var expectedX = findRow(actualValue, expectedY, tiles.length);
                counter += calculateManhattanDistance(row, col, expectedX, expectedY);
            }
        }
        return counter;
    }

    private int findColumn(int actualValue, int size) {
        int rest = actualValue % size;

        if (rest == 0) {
            return size - 1;
        } else {
            return rest - 1;
        }
    }

    private int calculateManhattanDistance(int currentRow, int currentCol, int expectedRow, int expectedCol) {
        return Math.abs(expectedRow - currentRow) + Math.abs(expectedCol - currentCol);
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }

        if (y == this) {
            return true;
        }
        if (!(y.getClass().equals(this.getClass()))) {
            return false;
        }

        final Board other = (Board) y;

        if (this.dimension() != other.dimension()) {
            return false;
        }
        return Arrays.deepEquals(this.tiles, other.tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        List<Board> boards = new ArrayList<>();
        boolean canUp = canUp();
        boolean canDown = canDown();
        boolean canLeft = canLeft();
        boolean canRight = canRight();

        if (canUp) {
            exch(blankRow - 1, blankCol, blankRow, blankCol, this.tiles);
            boards.add(new Board(this.tiles));
            exch(blankRow - 1, blankCol, blankRow, blankCol, this.tiles);
        }

        if (canDown) {
            exch(blankRow + 1, blankCol, blankRow, blankCol, this.tiles);
            boards.add(new Board(this.tiles));
            exch(blankRow + 1, blankCol, blankRow, blankCol, this.tiles);
        }

        if (canLeft) {
            exch(blankRow, blankCol - 1, blankRow, blankCol, this.tiles);
            boards.add(new Board(this.tiles));
            exch(blankRow, blankCol - 1, blankRow, blankCol, this.tiles);
        }

        if (canRight) {
            exch(blankRow, blankCol + 1, blankRow, blankCol, this.tiles);
            boards.add(new Board(this.tiles));
            exch(blankRow, blankCol + 1, blankRow, blankCol, this.tiles);
        }

        return boards;
    }

    private boolean canUp() {
        return blankRow > 0;
    }

    private boolean canDown() {
        return blankRow < (dimension() - 1);
    }

    private boolean canLeft() {
        return blankCol > 0;
    }

    private boolean canRight() {
        return blankCol < (dimension() - 1);
    }

    private static void exch(int x1, int y1, int x2, int y2, int[][] board) {
        var temp = board[x1][y1];
        board[x1][y1] = board[x2][y2];
        board[x2][y2] = temp;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board board = null;
        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j + 1 < dimension(); j++) {
                if ((i == blankRow && j == blankCol) || (i == blankRow && j + 1 == blankCol)) {
                    continue;
                }

                exch(i, j, i, j + 1, this.tiles);
                board = new Board(this.tiles);
                exch(i, j, i, j + 1, this.tiles);
            }
        }
        return board;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        testDim2();
        testDim3();
        testDim4();
        testGoalDim3();
        testGoalDim4();
    }

    private static void testDim2() {
        int[][] testTiles = new int[][] {
                new int[] {
                        0, 1
                },
                new int[] {
                        2, 3
                }
        };

        final Board board = new Board(testTiles);

        assert board.twin() != null;
    }

    private static void testGoalDim3() {
        int[][] testTiles = new int[][] {
                new int[] {
                        1, 2, 3
                },
                new int[] {
                        4, 5, 6
                },
                new int[] {
                        7, 8, 0
                }
        };

        final Board board = new Board(testTiles);
        assert board.hamming() == 0;
        assert board.isGoal();
        assert board.dimension() == 3;
        assert board.manhattan() == 0;
        assert board.manhattan() == 0;

        assert !board.equals(new Board(new int[][] {
                new int[] {
                        3, 4, 5
                },
                new int[] {
                        0, 2, 1
                },
                new int[] {
                        8, 6, 7
                }
        }));

        assert board.equals(new Board(new int[][] {
                new int[] {
                        1, 2, 3
                },
                new int[] {
                        4, 5, 6
                },
                new int[] {
                        7, 8, 0
                }
        }));

        assert board.twin() != null;
    }

    private static void testDim3() {
        int[][] testTiles = new int[][] {
                new int[] {
                        3, 4, 5
                },
                new int[] {
                        0, 2, 1
                },
                new int[] {
                        8, 6, 7
                }
        };

        final Board board = new Board(testTiles);
        assert board.hamming() == 8;
        assert !board.isGoal();
        assert board.dimension() == 3;
        assert board.manhattan() ==
                2 + 2 + 2 +
                        0 + 1 + 3 +
                        1 + 2 + 2;

        assert board.twin() != null;
    }

    private static void testDim4() {
        int[][] testTiles = new int[][] {
                new int[] {
                        3, 4, 5, 10
                },
                new int[] {
                        0, 2, 1, 12
                },
                new int[] {
                        8, 6, 7, 11
                },
                new int[] {
                        14, 15, 9, 13
                }
        };
        Board board = new Board(testTiles);

        assert board.hamming() == 15;
        assert !board.isGoal();
        assert board.dimension() == 4;
        assert board.manhattan() == 2 + 2 + 3 + 4 +
                0 + 1 + 3 + 1 +
                4 + 1 + 1 + 1 +
                1 + 1 + 3 + 3;

        assert board.twin() != null;
    }

    private static void testGoalDim4() {
        int[][] testTiles = new int[][] {
                new int[] {
                        1, 2, 3, 4
                },
                new int[] {
                        5, 6, 7, 8
                },
                new int[] {
                        9, 10, 11, 12
                },
                new int[] {
                        13, 14, 15, 0
                }
        };
        Board board = new Board(testTiles);

        assert board.hamming() == 0;
        assert board.isGoal();
        assert board.dimension() == 4;
        assert board.manhattan() == 0;

        assert !board.equals(new Board(new int[][] {
                new int[] {
                        3, 4, 5, 10
                },
                new int[] {
                        0, 2, 1, 12
                },
                new int[] {
                        8, 6, 7, 11
                },
                new int[] {
                        14, 15, 9, 13
                }
        }));

        assert board.equals(new Board(new int[][] {
                new int[] {
                        1, 2, 3, 4
                },
                new int[] {
                        5, 6, 7, 8
                },
                new int[] {
                        9, 10, 11, 12
                },
                new int[] {
                        13, 14, 15, 0
                }
        }));

        assert board.twin() != null;
    }
}
