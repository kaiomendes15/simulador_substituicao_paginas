import algoritmos.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class SimuladorGUI extends JFrame {

    // ── Paleta de cores ──────────────────────────────────────────────────────
    private static final Color BG_MAIN    = new Color(0xF4, 0xF6, 0xF8);
    private static final Color BG_PANEL   = Color.WHITE;
    private static final Color BG_INPUT   = Color.WHITE;
    private static final Color BORDER_CLR = new Color(0xCC, 0xD1, 0xD9);
    private static final Color ACCENT     = new Color(0x1A, 0x73, 0xC8);
    private static final Color TEXT_MAIN  = new Color(0x1F, 0x2D, 0x3D);
    private static final Color TEXT_SUB   = new Color(0x55, 0x6A, 0x7D);
    private static final Color TEXT_DIM   = new Color(0xA0, 0xAD, 0xBB);
    private static final Color HEADER_BG  = new Color(0x1A, 0x73, 0xC8);

    private static final Color[] ALG_COLORS = {
            new Color(0x21, 0x96, 0xF3),   // FIFO  – azul
            new Color(0xFF, 0x98, 0x00),   // NFU   – laranja
            new Color(0x43, 0xA0, 0x47),   // Aging – verde
            new Color(0x9C, 0x27, 0xB0),   // LRU   – roxo
    };

    // ── Componentes de entrada ───────────────────────────────────────────────
    private JTextField cadeiaField;
    private JSpinner   framesSpinner;
    private JButton    simularBtn;

    // ── Painel do gráfico ────────────────────────────────────────────────────
    private GraficoPanel graficoPanel;

    // ── Dados ────────────────────────────────────────────────────────────────
    private List<ResultadoSimulacao> resultados = new ArrayList<>();
    private String[]                 nomes      = {};

    // ════════════════════════════════════════════════════════════════════════
    public SimuladorGUI() {
        super("Simulador de Substituição de Páginas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(860, 620);
        setMinimumSize(new Dimension(680, 480));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_MAIN);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel title = new JLabel("Simulador de Substituição de Páginas");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Comparação entre algoritmos: FIFO, NFU, Aging e LRU");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(new Color(0xC5, 0xDE, 0xF7));

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(title);
        texts.add(Box.createVerticalStrut(3));
        texts.add(sub);

        header.add(texts, BorderLayout.WEST);
        return header;
    }

    // ── Área central ─────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setBackground(BG_MAIN);
        center.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

        center.add(buildInputCard(), BorderLayout.NORTH);

        graficoPanel = new GraficoPanel();
        center.add(graficoPanel, BorderLayout.CENTER);

        return center;
    }

    // ── Card de inputs ───────────────────────────────────────────────────────
    private JPanel buildInputCard() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_CLR, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        card.add(buildLabel("Cadeia de páginas:"));
        cadeiaField = new JTextField("1 2 1 3 2 1 4 1 1 2 5 1 2 3 4 5", 24);
        styleTextField(cadeiaField);
        card.add(cadeiaField);

        card.add(Box.createHorizontalStrut(6));

        card.add(buildLabel("Frames:"));
        framesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        styleSpinner(framesSpinner);
        card.add(framesSpinner);

        card.add(Box.createHorizontalStrut(10));

        simularBtn = buildSimularButton();
        card.add(simularBtn);

        return card;
    }

    private JLabel buildLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SUB);
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_CLR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sp.setPreferredSize(new Dimension(68, 30));
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        ftf.setForeground(TEXT_MAIN);
        ftf.setBackground(BG_INPUT);
        ftf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sp.setBorder(new LineBorder(BORDER_CLR, 1));
    }

    private JButton buildSimularButton() {
        JButton btn = new JButton("Simular") {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hover ? new Color(0x15, 0x60, 0xAA) : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(Color.WHITE);
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(100, 30));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> executarSimulacao());
        return btn;
    }

    // ── Simulação ─────────────────────────────────────────────────────────────
    private void executarSimulacao() {
        String cadeiaStr = cadeiaField.getText().trim();
        if (cadeiaStr.isEmpty()) {
            showError("Informe a cadeia de páginas.");
            return;
        }
        int frames;
        try {
            frames = (int) framesSpinner.getValue();
        } catch (Exception ex) {
            showError("Número de frames inválido.");
            return;
        }

        int[] cadeia;
        try {
            EntradaSimulacao entrada = new EntradaSimulacao(cadeiaStr);
            cadeia = entrada.getCadeia();
        } catch (NumberFormatException ex) {
            showError("Cadeia inválida – use apenas números separados por espaço.");
            return;
        }

        List<AlgoritmoSubstituicao> algoritmos = List.of(
                new Fifo(), new Nfu(), new Aging(), new Lru()
        );

        resultados.clear();
        List<String> nomesList = new ArrayList<>();
        for (AlgoritmoSubstituicao alg : algoritmos) {
            ResultadoSimulacao r = alg.executar(cadeia, frames);
            resultados.add(r);
            nomesList.add(alg.getNome());
        }
        nomes = nomesList.toArray(new String[0]);

        graficoPanel.setDados(resultados, nomes, cadeia.length);
        graficoPanel.repaint();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Painel de gráficos
    // ════════════════════════════════════════════════════════════════════════
    private class GraficoPanel extends JPanel {

        private List<ResultadoSimulacao> dados;
        private String[]                 labels;
        private int                      totalAcessos;
        private float                    animProgress = 1f;
        private javax.swing.Timer        animTimer;

        GraficoPanel() {
            setBackground(BG_PANEL);
            setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
            setMinimumSize(new Dimension(400, 280));
        }

        void setDados(List<ResultadoSimulacao> d, String[] l, int total) {
            this.dados        = d;
            this.labels       = l;
            this.totalAcessos = total;
            this.animProgress = 0f;

            if (animTimer != null && animTimer.isRunning()) animTimer.stop();
            animTimer = new javax.swing.Timer(16, e -> {
                animProgress = Math.min(1f, animProgress + 0.04f);
                repaint();
                if (animProgress >= 1f) animTimer.stop();
            });
            animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (dados == null || dados.isEmpty()) {
                drawPlaceholder(g2);
            } else {
                drawGrafico(g2);
            }
            g2.dispose();
        }

        // ── placeholder ──────────────────────────────────────────────────────
        private void drawPlaceholder(Graphics2D g2) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
            g2.setColor(TEXT_DIM);
            String msg1 = "Configure os parâmetros e clique em Simular";
            String msg2 = "para visualizar a comparação entre os algoritmos";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg1, (getWidth() - fm.stringWidth(msg1)) / 2, getHeight() / 2 - 10);
            g2.drawString(msg2, (getWidth() - fm.stringWidth(msg2)) / 2, getHeight() / 2 + 14);

            g2.setColor(new Color(BORDER_CLR.getRed(), BORDER_CLR.getGreen(), BORDER_CLR.getBlue(), 120));
            g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            int pad = 48;
            for (int i = 1; i <= 4; i++) {
                int y = pad + (getHeight() - 2 * pad) * i / 5;
                g2.drawLine(pad, y, getWidth() - pad, y);
            }
        }

        // ── gráfico principal ────────────────────────────────────────────────
        private void drawGrafico(Graphics2D g2) {
            int n      = dados.size();
            int padL   = 64, padR = 24, padT = 44, padB = 90;
            int chartW = getWidth()  - padL - padR;
            int chartH = getHeight() - padT - padB;

            int maxFaults = dados.stream().mapToInt(ResultadoSimulacao::getPageFaults).max().orElse(1);
            maxFaults = ((maxFaults / 2) + 1) * 2;

            // ── grade e eixos ─────────────────────────────────────────────
            int gridLines = 5;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            FontMetrics fmSmall = g2.getFontMetrics();

            for (int i = 0; i <= gridLines; i++) {
                int y   = padT + chartH - (chartH * i / gridLines);
                int val = maxFaults * i / gridLines;

                g2.setColor(new Color(0xE0, 0xE5, 0xEB));
                g2.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
                g2.drawLine(padL, y, padL + chartW, y);

                g2.setColor(TEXT_SUB);
                g2.setStroke(new BasicStroke(1f));
                String lbl = String.valueOf(val);
                g2.drawString(lbl, padL - fmSmall.stringWidth(lbl) - 6, y + 4);
            }

            // eixo Y label
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(TEXT_SUB);
            AffineTransform orig = g2.getTransform();
            g2.rotate(-Math.PI / 2, 12, padT + chartH / 2);
            String yLabel = "Page Faults";
            g2.drawString(yLabel, 12 - g2.getFontMetrics().stringWidth(yLabel) / 2, padT + chartH / 2 + 4);
            g2.setTransform(orig);

            // ── barras ────────────────────────────────────────────────────
            int barGroupW = chartW / n;
            int barW      = (int)(barGroupW * 0.50);
            float ease    = easeOut(animProgress);

            for (int i = 0; i < n; i++) {
                int faults = dados.get(i).getPageFaults();
                int fullH  = (int)((double) faults / maxFaults * chartH);
                int animH  = (int)(fullH * ease);
                int x      = padL + i * barGroupW + (barGroupW - barW) / 2;
                int y      = padT + chartH - animH;

                Color algColor = ALG_COLORS[i % ALG_COLORS.length];

                // barra
                g2.setColor(algColor);
                g2.fillRect(x, y, barW, animH);

                // borda superior da barra
                g2.setColor(algColor.darker());
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(x, y, barW, animH);

                // valor no topo da barra
                if (animProgress > 0.85f) {
                    float alpha = Math.min(1f, (animProgress - 0.85f) / 0.15f);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                    FontMetrics fm2 = g2.getFontMetrics();
                    String val = String.valueOf(faults);
                    int tx = x + (barW - fm2.stringWidth(val)) / 2;
                    g2.setColor(new Color(TEXT_MAIN.getRed(), TEXT_MAIN.getGreen(), TEXT_MAIN.getBlue(), (int)(255 * alpha)));
                    g2.drawString(val, tx, y - 5);
                }

                // rótulo X (nome do algoritmo)
                String shortName = shortName(labels[i]);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fmN = g2.getFontMetrics();
                int lx = x + (barW - fmN.stringWidth(shortName)) / 2;
                g2.setColor(algColor.darker());
                g2.drawString(shortName, lx, padT + chartH + 20);

                // page faults count
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                fmN = g2.getFontMetrics();
                String sub = faults + " faltas";
                int slx = x + (barW - fmN.stringWidth(sub)) / 2;
                g2.setColor(TEXT_SUB);
                g2.drawString(sub, slx, padT + chartH + 35);

                // porcentagem
                if (totalAcessos > 0) {
                    String pct = String.format("%.0f%%", (double) faults / totalAcessos * 100);
                    int plx = x + (barW - fmN.stringWidth(pct)) / 2;
                    g2.setColor(TEXT_DIM);
                    g2.drawString(pct, plx, padT + chartH + 50);
                }
            }

            // ── título do gráfico ─────────────────────────────────────────
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            g2.setColor(TEXT_MAIN);
            g2.drawString("Faltas de página por algoritmo", padL, padT - 14);

            // ── info à direita ────────────────────────────────────────────
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(TEXT_DIM);
            String info = "Acessos: " + totalAcessos + "  |  Frames: " + (int) framesSpinner.getValue();
            g2.drawString(info, getWidth() - padR - g2.getFontMetrics().stringWidth(info), padT - 14);

            // ── linha de base ─────────────────────────────────────────────
            g2.setColor(new Color(0x99, 0xA8, 0xB8));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);

            // ── eixo vertical ─────────────────────────────────────────────
            g2.drawLine(padL, padT, padL, padT + chartH);
        }

        private float easeOut(float t) {
            return 1f - (1f - t) * (1f - t) * (1f - t);
        }

        private String shortName(String nome) {
            if (nome.contains("FIFO"))  return "FIFO";
            if (nome.contains("NFU"))   return "NFU";
            if (nome.contains("AGING") || nome.contains("ENVELHECIMENTO")) return "Aging";
            if (nome.contains("LRU"))   return "LRU";
            return nome.length() > 8 ? nome.substring(0, 8) : nome;
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // main
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            new SimuladorGUI().setVisible(true);
        });
    }
}
