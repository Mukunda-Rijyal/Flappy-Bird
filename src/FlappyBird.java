import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    private int birdY = 300, velocity = 0, gravity = 2;
    private final int birdX = 100, birdWidth = 40, birdHeight = 30;
    private ArrayList<Rectangle> pipes;
    private int score = 0;
    private boolean gameOver = false;
    private Timer timer;
    private JButton retryButton;
    private JFrame frame;

    public FlappyBird() {
        frame = new JFrame("Flappy Bird");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Important for absolute positioning

        this.setBounds(0, 0, 800, 600);
        frame.add(this);

        retryButton = new JButton("Retry");
        retryButton.setBounds(330, 350, 120, 50);
        retryButton.setVisible(false);
        retryButton.setFont(new Font("Arial", Font.BOLD, 20));
        retryButton.addActionListener(e -> resetGame());
        frame.add(retryButton);

        frame.addKeyListener(this);
        frame.setResizable(false);
        frame.setVisible(true);

        pipes = new ArrayList<>();
        addPipe(true);
        addPipe(true);

        timer = new Timer(20, this);
        timer.start();
    }

    public void addPipe(boolean start) {
        int space = 200;
        int width = 80;
        int height = 50 + new Random().nextInt(300);
        int x = 800; // Default starting X

        if (!start && pipes.size() > 0) {
            Rectangle last = pipes.get(pipes.size() - 1);
            x = last.x + 400;
        }

        pipes.add(new Rectangle(x, 0, width, height));
        pipes.add(new Rectangle(x, height + space, width, 600 - height - space));
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            velocity += gravity;
            birdY += velocity;

            for (int i = 0; i < pipes.size(); i++) {
                Rectangle pipe = pipes.get(i);
                pipe.x -= 5;
            }

            pipes.removeIf(pipe -> pipe.x + pipe.width < 0);

            while (pipes.size() < 4) {
                addPipe(false);
            }

            for (Rectangle pipe : pipes) {
                if (pipe.intersects(new Rectangle(birdX, birdY, birdWidth, birdHeight))) {
                    gameOver = true;
                    retryButton.setVisible(true);
                }
            }

            if (birdY > 600 || birdY < 0) {
                gameOver = true;
                retryButton.setVisible(true);
            }

            for (Rectangle pipe : pipes) {
                if (pipe.y == 0 && pipe.x + pipe.width == birdX) {
                    score++;
                }
            }
        }

        repaint();
    }

    public void resetGame() {
        birdY = 300;
        velocity = 0;
        pipes.clear();
        addPipe(true);
        addPipe(true); // this is fine since it's using `start = true`
        score = 0;
        gameOver = false;
        retryButton.setVisible(false);
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, 800, 600);

        // Bird
        g.setColor(Color.red);
        g.fillRect(birdX, birdY, birdWidth, birdHeight);

        // Pipes
        g.setColor(Color.green);
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // Ground
        g.setColor(Color.orange);
        g.fillRect(0, 580, 800, 20);

        // Score
        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 40);

        if (gameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game Over", 280, 300);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            if (velocity > 0) velocity = 0;
            velocity -= 10;
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            resetGame();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new FlappyBird();
    }
}
