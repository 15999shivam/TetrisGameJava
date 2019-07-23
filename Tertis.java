import java.util.*;
import java.io.IOException;

class Tertis {

    ShapeTemplate m[];
    ShapeTemplate k;
    Board b;
    int random;
    Stack<String> stack;
    Stack<String> redoStack;
    Stack<BoardState> boardStateUndo;
    Stack<BoardState> boardStateRedo;
    boolean status;

    Tertis() {
        m = new ShapeTemplate[] { new Line(), new T(), new Sq(), new Z(), new L() };
        random = (int) (Math.random() * 5);
        k = m[random];
        b = new Board();

        boardStateUndo = new Stack<BoardState>();
        boardStateRedo = new Stack<BoardState>();
        stack = new Stack<String>();
        redoStack = new Stack<String>();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Tertis t = new Tertis();
        t.boardFunctions();
        t.functions(t);
    }

    void functions(Tertis t) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        char choice = sc.next().charAt(0);

        while (choice != 'e') {

            switch (choice) {
            case 's':
                boardStateRedo = new Stack<BoardState>();
                t.redoStack = new Stack<String>();
                t.k.rotateLeft();
                t.stack.push("s");
                t.boardFunctions();
                break;

            case 'w':
                boardStateRedo = new Stack<BoardState>();
                t.redoStack = new Stack<String>();
                t.k.rotateRight();
                t.stack.push("w");
                t.boardFunctions();
                break;

            case 'd':
                boardStateRedo = new Stack<BoardState>();
                t.redoStack = new Stack<String>();
                t.status = t.k.moveRight(t.b);
                if (t.status) {
                    t.stack.push("d");
                }
                t.boardFunctions();
                break;

            case 'a':
                boardStateRedo = new Stack<BoardState>();
                t.redoStack = new Stack<String>();
                t.status = t.k.moveLeft(t.b);
                if (t.status) {
                    t.stack.push("a");
                }
                t.boardFunctions();
                break;

            case 'x':
                boardStateRedo = new Stack<BoardState>();
                t.redoStack = new Stack<String>();
                t.k.moveDown();
                t.stack.push("x");
                t.boardFunctions();
                break;

            case 'u':
                handleU(t);
                break;

            case 'r':
                handleR(t);
                break;
            }
            choice = sc.next().charAt(0);
        }
        sc.close();
    }

    void handleR(Tertis t) throws IOException, InterruptedException {
        if (t.redoStack.empty()) {
            System.out.println("hii first level");
            if (t.boardStateRedo.empty()) {
                System.out.println("hii second level");
                t.boardFunctions();
            } else {
                System.out.println("Redo state !!!!!!!!!");
                t.b = t.boardStateRedo.peek().b;
                t.k = t.boardStateRedo.peek().k;
                t.stack = t.boardStateRedo.peek().undo;
                t.redoStack = t.boardStateRedo.peek().redo;
                // t.handleR(t);
                t.boardStateUndo.push(t.boardStateRedo.pop());
                t.boardFunctions();
            }

            // System.out.println("hi");
        } else {
            // System.out.println("hi Redo Working");
            if (t.redoStack.peek() == "s") {
                t.stack.push(t.redoStack.pop());
                t.k.rotateLeft();
            } else if (t.redoStack.peek() == "w") {
                t.stack.push(t.redoStack.pop());
                t.k.rotateLeft();
            } else if (t.redoStack.peek() == "d") {
                t.stack.push(t.redoStack.pop());
                t.k.moveRight(t.b);
            } else if (t.redoStack.peek() == "a") {
                t.stack.push(t.redoStack.pop());
                t.k.moveLeft(t.b);
            } else {
                t.stack.push(t.redoStack.pop());
                t.k.moveDown();
            }
            t.boardFunctions();
        }
    }

    void handleU(Tertis t) throws IOException, InterruptedException {
        if (t.stack.empty()) {

            if (t.boardStateUndo.empty()) {
                t.boardFunctions();
            } else {
                System.out.println("Undo state !!!!!!!!!");
                for (int i = 0; i < 30; i++) {
                    for (int j = 0; j < 31; j++) {
                        System.out.print(t.boardStateUndo.peek().b.finalBoard[i][j] + " ");
                    }
                    System.out.println("");
                }
                boardStateRedo.push(new BoardState(t.b, t.stack, t.k, t.redoStack));
                t.b = new Board(t.boardStateUndo.peek().b);
                m = new ShapeTemplate[] { new Line(t.boardStateUndo.peek().k), new T(t.boardStateUndo.peek().k),
                        new Sq(t.boardStateUndo.peek().k), new Z(t.boardStateUndo.peek().k),
                        new L(t.boardStateUndo.peek().k) };
                t.k = m[random];
                t.stack = new Stack<String>();
                t.stack.addAll(t.boardStateUndo.peek().undo);
                t.redoStack = new Stack<String>();
                t.redoStack.addAll(t.boardStateUndo.peek().redo);
                t.handleU(t);
                t.boardFunctions();
            }

        } else {
            if (t.stack.peek() == "s") {
                t.redoStack.push(t.stack.pop());
                t.k.rotateRight();
            } else if (t.stack.peek() == "w") {
                t.redoStack.push(t.stack.pop());
                t.k.rotateLeft();
            } else if (t.stack.peek() == "d") {
                t.redoStack.push(t.stack.pop());
                t.k.moveLeft(t.b);
            } else if (t.stack.peek() == "a") {
                t.redoStack.push(t.stack.pop());
                t.k.moveRight(t.b);
            } else {
                t.redoStack.push(t.stack.pop());
                t.k.moveUp();
            }
            t.boardFunctions();
        }
    }

    void boardFunctions() throws IOException, InterruptedException {
        b.makeBoard();
        boolean flag = b.insertShape(k.getCurrentShape());
        // new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        b.printBoard();
        if (flag) {
            boardStateUndo.push(new BoardState(b, stack, k, redoStack));

            b = new Board(b);
            b.save(k.getCurrentShape().getCordinates());
            System.out.println("After saving!!!!!!!");
            stack = new Stack<String>();
            redoStack = new Stack<String>();
            m = new ShapeTemplate[] { new Line(), new T(), new Sq(), new Z(), new L() };
            random = (int) (Math.random() * 5);
            k = m[random];
            boardFunctions();
            // boardStateRedo.push(new BoardState(b, stack, k, redoStack));
        }
    }

}

class BoardState {
    Board b;
    Stack<String> undo;
    ShapeTemplate k;
    Stack<String> redo;

    BoardState(Board b1, Stack<String> undo1, ShapeTemplate k1, Stack<String> redo1) {
        b = b1;
        undo = undo1;
        k = k1;
        redo = redo1;
    }

}

class Board {

    public char board[][] = new char[30][31];
    public char finalBoard[][] = new char[30][31];

    Board(Board b) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 31; j++) {
                board[i][j] = b.board[i][j];
                finalBoard[i][j] = b.finalBoard[i][j];
            }
        }
    }

    Board() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (i == 0 || i == 29 || j == 0 || j == 29) {
                    finalBoard[i][j] = '*';
                } else
                    finalBoard[i][j] = ' ';
            }
        }
        for (int i = 0; i < 30; i++) {
            finalBoard[i][30] = '0';
        }
    }

    public void makeBoard() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (i == 0 || i == 29 || j == 0 || j == 29) {
                    board[i][j] = '*';
                } else
                    board[i][j] = ' ';
            }
        }
    }

    void save(int[][] co) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] == '#') {
                    finalBoard[i][j] = '$';
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            finalBoard[co[i][0] + 4][30]++;
        }
        for (int i = 0; i < 30; i++) {
            if (finalBoard[i][30] - '0' == 28) {
                for (int m = i; finalBoard[m][30] > '0'; m--) {
                    for (int n = 1; n < 29; n++) {
                        finalBoard[m][n] = finalBoard[m - 1][n];
                    }
                }
            }
        }

    }

    public boolean insertShape(Shape s) {
        int[][] co = s.getCordinates();
        boolean flag = false;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] != '#')
                    board[i][j] = finalBoard[i][j];
            }
        }
        for (int i = 0; i < 4; i++) {
            if ((board[(co[i][0] + 5)][co[i][1] + 4] == '$' || board[(co[i][0] + 5)][co[i][1] + 4] == '*')) {
                flag = true;
            }
            board[(co[i][0] + 4)][co[i][1] + 4] = '#';
        }
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (board[i][j] == '$')
                    board[i][j] = '#';
            }
        }
        return flag;
    }

    public void printBoard() {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println("");
        }
    }
}

class Shape {
    int[][] shape = new int[4][2];

    Shape(int[][] ar) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                shape[i][j] = ar[i][j];
            }
        }

    }

    Shape(Shape s) {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                shape[i][j] = s.shape[i][j];
            }
        }

    }

    int[][] getCordinates() {
        return shape;
    }

    boolean moveRight(Board b) {

        for (int i = 0; i < 4; i++) {
            if (b.finalBoard[shape[i][0] + 4][shape[i][1] + 5] == '$'
                    || b.finalBoard[shape[i][0] + 4][shape[i][1] + 5] == '*') {
                return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 2; j++) {
                shape[i][j] += 1;
            }
        }

        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 4; i++) {
            if (b.finalBoard[shape[i][0] + 4][shape[i][1] + 3] == '$'
                    || b.finalBoard[shape[i][0] + 4][shape[i][1] + 3] == '*') {
                return false;
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 2; j++) {
                shape[i][j] -= 1;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 1; j++) {
                shape[i][j] += 1;
            }
        }

    }

    void moveUp() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 1; j++) {
                shape[i][j] -= 1;
            }
        }
    }
}

class ShapeTemplate {
    Shape main;
    Shape left;
    Shape right;
    Shape invert;
    int current = 0;

    Shape[] states;

    ShapeTemplate() {

    }

    ShapeTemplate(ShapeTemplate s) {
        main = new Shape(s.main);
        left = new Shape(s.left);
        right = new Shape(s.right);
        invert = new Shape(s.invert);
        states = new Shape[] { main, right, invert, left };
    }

    public Shape getCurrentShape() {
        return states[current];
    }

    public void rotateLeft() {
        if (current < 4 && current != 0) {
            current -= 1;
        } else if (current == 0) {
            current = 3;
        }
    }

    public void rotateRight() {
        if (current < 3) {
            current += 1;
        } else if (current == 3) {
            current = 0;
        }
    }

    boolean moveRight(Board b) {
        for (int i = 0; i < 4; i++) {
            if (!states[i].moveRight(b)) {
                return false;
            }
        }
        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 4; i++) {
            if (!states[i].moveLeft(b)) {
                return false;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 4; i++) {
            states[i].moveDown();
        }
    }

    void moveUp() {
        for (int i = 0; i < 4; i++) {
            states[i].moveUp();
        }
    }

}

class Line extends ShapeTemplate {

    Line(ShapeTemplate l) {
        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 2; j++) {
        // System.out.println(l.left.getCordinates()[i][j]);
        // }
        // System.out.println("");
        // }
        super(l);
    }

    Line() {
        current = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, -1 }, { 0, -2 }, { 0, -3 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -2, 0 }, { -3, 0 } });
        states = new Shape[] { main, right, invert, left };
    }

}

class T extends ShapeTemplate {

    T(ShapeTemplate t) {
        super(t);
    }

    T() {
        current = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { -1, 0 }, { 1, 0 } });
        right = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 1, 0 }, { 0, -1 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } });
        states = new Shape[] { main, right, invert, left };
    }

}

class L extends ShapeTemplate {

    L(ShapeTemplate l) {
        super(l);
    }

    L() {
        current = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { -1, 0 }, { -2, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { 0, -2 }, { 0, -1 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 1, 0 } });
        invert = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 0, -1 } });
        states = new Shape[] { main, right, invert, left };
    }
}

class Z extends ShapeTemplate {

    Z(ShapeTemplate z) {
        super(z);
    }

    Z() {
        current = 0;
        main = new Shape(new int[][] { { 0, 0 }, { -1, -1 }, { 0, -1 }, { 1, 0 } });
        right = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -1, 1 }, { 0, -1 } });
        states = new Shape[] { main, right };
    }

    public void rotateLeft() {
        if (current < 2 && current != 0) {
            current -= 1;
        } else if (current == 0) {
            current = 1;
        }
    }

    public void rotateRight() {
        if (current < 1) {
            current += 1;
        } else if (current == 1) {
            current = 0;
        }
    }

    boolean moveRight(Board b) {
        for (int i = 0; i < 2; i++) {
            if (!states[i].moveRight(b)) {
                return false;
            }
        }
        return true;
    }

    boolean moveLeft(Board b) {
        for (int i = 0; i < 2; i++) {
            if (!states[i].moveLeft(b)) {
                return false;
            }
        }
        return true;
    }

    void moveDown() {
        for (int i = 0; i < 2; i++) {
            states[i].moveDown();
        }
    }

    void moveUp() {
        for (int i = 0; i < 2; i++) {
            states[i].moveUp();
        }
    }
}

class Sq extends ShapeTemplate {

    Sq(ShapeTemplate s) {
        super(s);
    }

    Sq() {
        current = 0;
        main = new Shape(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 }, { 3, 0 } });
        left = new Shape(new int[][] { { 0, 0 }, { 0, -1 }, { 0, -2 }, { 0, -3 } });
        right = new Shape(new int[][] { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 3 } });
        invert = new Shape(new int[][] { { 0, 0 }, { -1, 0 }, { -2, 0 }, { -3, 0 } });
        states = new Shape[] { main, right, invert, left };
    }

}
