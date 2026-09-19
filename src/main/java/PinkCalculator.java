import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Random;

public class PinkCalculator extends JFrame {

    private JTextField display;
    private StringBuilder currentInput = new StringBuilder();
    private double result = 0;
    private String pendingOp = "";
    private boolean startNewNumber = true;

    private final Color BG_PINK = new Color(255, 228, 240);
    private final Color CARD_PINK = new Color(255, 245, 250);
    private final Color HOT_PINK = new Color(255, 105, 180);
    private final Color SOFT_PINK = new Color(255, 182, 213);
    private final Color DARK_PINK = new Color(199, 21, 133);
    private final Color TEXT_PINK = new Color(140, 40, 90);

    private final int BORDER_THICKNESS = 12;
    private final int CORNER_RADIUS = 40;

    private PetPanel petPanel;
    private JPanel rootPanel;

    public PinkCalculator() {
        setTitle("Pink Calculator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(340, 560);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                int w = getWidth();
                int h = getHeight();
                int t = BORDER_THICKNESS;
                int r = CORNER_RADIUS;

                g2.setColor(BG_PINK);
                g2.fillRoundRect(t / 2, t / 2, w - t, h - t, r, r);

                g2.setStroke(new BasicStroke(t, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(HOT_PINK);
                g2.drawRoundRect(t / 2, t / 2, w - t, h - t, r, r);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        rootPanel.setOpaque(false);
        rootPanel.setBorder(new EmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS));

        petPanel = new PetPanel();
        petPanel.setPreferredSize(new Dimension(340, 130));
        petPanel.setOpaque(false);
        rootPanel.add(petPanel, BorderLayout.NORTH);

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setOpaque(false);
        displayPanel.setBorder(new EmptyBorder(4, 12, 8, 12));

        display = new JTextField("0") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(SOFT_PINK);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        display.setOpaque(false);
        display.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        display.setForeground(TEXT_PINK);
        display.setBackground(CARD_PINK);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBorder(new EmptyBorder(12, 18, 12, 18));
        displayPanel.add(display, BorderLayout.CENTER);
        rootPanel.add(displayPanel, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new GridLayout(5, 4, 8, 8));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(6, 12, 16, 12));

        String[] buttons = {
                "C", "±", "%", "DEL",
                "7", "8", "9", "÷",
                "4", "5", "6", "×",
                "1", "2", "3", "−",
                "0", ".", "=", "+"
        };

        for (String text : buttons) {
            buttonsPanel.add(createButton(text));
        }

        rootPanel.add(buttonsPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));

        addDragSupport();
        addCloseButton();
        addKeyboardSupport();

        SwingUtilities.invokeLater(() -> rootPanel.requestFocusInWindow());

        setVisible(true);
    }

    private void addDragSupport() {
        final Point[] dragOffset = {null};
        rootPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragOffset[0] = e.getPoint();
            }
        });
        rootPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragOffset[0] != null) {
                    Point p = e.getLocationOnScreen();
                    setLocation(p.x - dragOffset[0].x, p.y - dragOffset[0].y);
                }
            }
        });
    }

    private void addCloseButton() {
        JButton close = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? DARK_PINK : HOT_PINK);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        close.setFont(new Font("Dialog", Font.BOLD, 12));
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.setOpaque(false);
        close.setMargin(new Insets(0, 0, 0, 0));
        close.setBorder(new EmptyBorder(0, 0, 0, 0));
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addActionListener(e -> System.exit(0));

        getLayeredPane().add(close, JLayeredPane.PALETTE_LAYER);
        close.setBounds(296, 10, 28, 28);
    }

    private void addKeyboardSupport() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED) return false;

            int code = e.getKeyCode();
            String key = e.getKeyText(code);

            if (code >= KeyEvent.VK_0 && code <= KeyEvent.VK_9) {
                handleButton(String.valueOf((char) code));
                return true;
            }
            if (code >= KeyEvent.VK_NUMPAD0 && code <= KeyEvent.VK_NUMPAD9) {
                handleButton(String.valueOf((char) (code - KeyEvent.VK_NUMPAD0 + '0')));
                return true;
            }

            switch (code) {
                case KeyEvent.VK_ADD:
                case KeyEvent.VK_PLUS:
                    handleButton("+"); return true;
                case KeyEvent.VK_SUBTRACT:
                case KeyEvent.VK_MINUS:
                    handleButton("−"); return true;
                case KeyEvent.VK_MULTIPLY:
                    handleButton("×"); return true;
                case KeyEvent.VK_DIVIDE:
                case KeyEvent.VK_SLASH:
                    handleButton("÷"); return true;
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_EQUALS:
                    handleButton("="); return true;
                case KeyEvent.VK_BACK_SPACE:
                    handleButton("DEL"); return true;
                case KeyEvent.VK_ESCAPE:
                    handleButton("C"); return true;
                case KeyEvent.VK_DELETE:
                    handleButton("C"); return true;
                case KeyEvent.VK_PERIOD:
                case KeyEvent.VK_DECIMAL:
                case KeyEvent.VK_COMMA:
                    handleButton("."); return true;
            }
            return false;
        });
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text) {
            private float pressScale = 1f;
            private Timer animTimer;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        animateTo(0.85f);
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        animateTo(1f);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (!getModel().isPressed()) animateTo(1f);
                    }
                });
            }

            private void animateTo(float target) {
                if (animTimer != null && animTimer.isRunning()) animTimer.stop();
                animTimer = new Timer(15, null);
                animTimer.addActionListener(e -> {
                    float diff = target - pressScale;
                    if (Math.abs(diff) < 0.01f) {
                        pressScale = target;
                        animTimer.stop();
                    } else {
                        pressScale += diff * 0.35f;
                    }
                    repaint();
                });
                animTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                AffineTransform old = g2.getTransform();
                g2.translate(cx, cy);
                g2.scale(pressScale, pressScale);
                g2.translate(-cx, -cy);

                GradientPaint gp;
                if (getModel().isPressed()) {
                    gp = new GradientPaint(0, 0, DARK_PINK, 0, getHeight(), HOT_PINK);
                } else if (getModel().isRollover()) {
                    gp = new GradientPaint(0, 0, HOT_PINK, 0, getHeight(), SOFT_PINK);
                } else {
                    gp = new GradientPaint(0, 0, SOFT_PINK, 0, getHeight(), HOT_PINK);
                }

                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                g2.setColor(new Color(255, 255, 255, 80));
                g2.fillRoundRect(4, 3, getWidth() - 8, getHeight() / 3, 18, 18);

                g2.setTransform(old);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        int fontSize;
        if (text.length() > 1) {
            fontSize = 14;
        } else {
            fontSize = 22;
        }
        btn.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(new EmptyBorder(0, 0, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);

        if (text.equals("=")) {
            btn.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
        }

        btn.addActionListener(e -> handleButton(text));
        return btn;
    }

    private void handleButton(String text) {
        switch (text) {
            case "C":
                currentInput.setLength(0);
                result = 0;
                pendingOp = "";
                startNewNumber = true;
                display.setText("0");
                break;

            case "DEL":
                if (currentInput.length() > 0) {
                    currentInput.deleteCharAt(currentInput.length() - 1);
                    display.setText(currentInput.length() == 0 ? "0" : currentInput.toString());
                }
                break;

            case "±":
                if (currentInput.length() > 0 && !currentInput.toString().equals("0")) {
                    if (currentInput.charAt(0) == '-') {
                        currentInput.deleteCharAt(0);
                    } else {
                        currentInput.insert(0, '-');
                    }
                    display.setText(currentInput.toString());
                }
                break;

            case ".":
                if (!currentInput.toString().contains(".")) {
                    if (currentInput.length() == 0) currentInput.append("0");
                    currentInput.append(".");
                    display.setText(currentInput.toString());
                }
                break;

            case "=":
                compute();
                break;

            case "+": case "−": case "×": case "÷": case "%":
                if (currentInput.length() > 0) {
                    compute();
                }
                pendingOp = text;
                startNewNumber = true;
                break;

            default:
                if (startNewNumber) {
                    currentInput.setLength(0);
                    startNewNumber = false;
                }
                currentInput.append(text);
                display.setText(currentInput.toString());
        }
    }

    private void compute() {
        if (currentInput.length() == 0 && pendingOp.isEmpty()) return;

        double current = currentInput.length() == 0 ? result : Double.parseDouble(currentInput.toString());

        if (pendingOp.isEmpty()) {
            result = current;
        } else {
            switch (pendingOp) {
                case "+": result += current; break;
                case "−": result -= current; break;
                case "×": result *= current; break;
                case "÷":
                    if (current == 0) {
                        display.setText("Ошибка 💔");
                        currentInput.setLength(0);
                        pendingOp = "";
                        startNewNumber = true;
                        return;
                    }
                    result /= current;
                    break;
                case "%": result = result * current / 100; break;
            }
        }

        String out;
        if (result == (long) result) {
            out = String.valueOf((long) result);
        } else {
            out = String.format("%.6f", result).replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        display.setText(out);
        currentInput.setLength(0);
        currentInput.append(out);
        pendingOp = "";
        startNewNumber = true;
    }

    class PetPanel extends JPanel {
        private double petX = 120;
        private double petY = 70;
        private double targetX = 170;
        private double targetY = 70;
        private final Random rand = new Random();
        private int blinkCounter = 0;
        private int frame = 0;
        private Timer timer;
        private float scale = 1f;

        public PetPanel() {
            setOpaque(false);

            timer = new Timer(30, e -> {
                frame++;

                double dx = targetX - petX;
                double dy = targetY - petY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 3) {
                    targetX = 50 + rand.nextInt(Math.max(1, getWidth() - 100));
                    targetY = 30 + rand.nextInt(60);
                } else {
                    petX += dx * 0.03;
                    petY += dy * 0.03;
                }

                scale = 1f + (float) Math.sin(frame * 0.15) * 0.03f;

                if (rand.nextInt(80) == 0) blinkCounter = 8;
                if (blinkCounter > 0) blinkCounter--;

                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            drawCloud(g2, 30, 20, 0.8f);
            drawCloud(g2, 240, 35, 1f);
            drawCloud(g2, 140, 90, 0.6f);

            drawPet(g2, (int) petX, (int) petY, scale * 0.75f);

            g2.dispose();
        }

        private void drawCloud(Graphics2D g2, int x, int y, float alpha) {
            g2.setColor(new Color(255, 255, 255, (int)(100 * alpha)));
            g2.fillOval(x, y, 30, 20);
            g2.fillOval(x + 15, y - 6, 34, 22);
            g2.fillOval(x + 34, y, 26, 17);
        }

        private void drawPet(Graphics2D g2, int x, int y, float scale) {
            AffineTransform old = g2.getTransform();
            g2.translate(x, y);
            g2.scale(scale, scale);

            g2.setColor(new Color(199, 21, 133, 40));
            g2.fillOval(-30, 25, 60, 15);

            g2.setColor(new Color(255, 240, 248));
            g2.fillOval(-35, -30, 70, 60);

            g2.setColor(new Color(255, 240, 248));
            int[] xL = {-30, -18, -10};
            int[] yL = {-25, -45, -20};
            g2.fillPolygon(xL, yL, 3);
            int[] xR = {30, 18, 10};
            int[] yR = {-25, -45, -20};
            g2.fillPolygon(xR, yR, 3);

            g2.setColor(new Color(255, 182, 213));
            int[] xL2 = {-26, -18, -13};
            int[] yL2 = {-27, -40, -23};
            g2.fillPolygon(xL2, yL2, 3);
            int[] xR2 = {26, 18, 13};
            int[] yR2 = {-27, -40, -23};
            g2.fillPolygon(xR2, yR2, 3);

            g2.setColor(new Color(80, 30, 60));
            if (blinkCounter > 0) {
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(-18, -5, -8, -5);
                g2.drawLine(8, -5, 18, -5);
            } else {
                g2.fillOval(-20, -10, 14, 16);
                g2.fillOval(6, -10, 14, 16);
                g2.setColor(Color.WHITE);
                g2.fillOval(-17, -8, 5, 5);
                g2.fillOval(9, -8, 5, 5);
            }

            g2.setColor(new Color(255, 150, 190, 180));
            g2.fillOval(-30, 0, 12, 8);
            g2.fillOval(18, 0, 12, 8);

            g2.setColor(new Color(255, 105, 180));
            int mx = 0, my = 5;
            Path2D heart = new Path2D.Double();
            heart.moveTo(mx, my + 4);
            heart.curveTo(mx - 6, my - 2, mx - 4, my - 6, mx, my - 2);
            heart.curveTo(mx + 4, my - 6, mx + 6, my - 2, mx, my + 4);
            g2.fill(heart);

            g2.setColor(HOT_PINK);
            int bx = 25, by = -40;
            g2.fillOval(bx - 8, by - 4, 10, 10);
            g2.fillOval(bx + 2, by - 4, 10, 10);
            g2.setColor(DARK_PINK);
            g2.fillOval(bx, by, 4, 4);

            g2.setColor(new Color(255, 220, 235));
            g2.fillOval(-22, 22, 14, 10);
            g2.fillOval(8, 22, 14, 10);

            g2.setTransform(old);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(PinkCalculator::new);
    }
}