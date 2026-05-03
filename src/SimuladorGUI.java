import algoritmos.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class SimuladorGUI extends JFrame {

    // ── Paleta de cores ──────────────────────────────────────────────────────
    private static final Color BG_DARK     = new Color(0x0D, 0x11, 0x17);
    private static final Color BG_PANEL    = new Color(0x13, 0x1A, 0x24);
    private static final Color BG_INPUT    = new Color(0x1A, 0x23, 0x30);
    private static final Color BORDER_CLR  = new Color(0x26, 0x35, 0x47);
    private static final Color ACCENT      = new Color(0x00, 0xD4, 0xFF);
    private static final Color ACCENT2     = new Color(0xFF, 0x6B, 0x35);
    private static final Color TEXT_MAIN   = new Color(0xE8, 0xF4, 0xFF);
    private static final Color TEXT_SUB    = new Color(0x7A, 0x9B, 0xBC);
    private static final Color TEXT_DIM    = new Color(0x3D, 0x58, 0x73);

    private static final Color[] ALG_COLORS = {
            new Color(0x00, 0xD4, 0xFF),   // FIFO  – ciano
            new Color(0xFF, 0x6B, 0x35),   // NFU   – laranja
            new Color(0x39, 0xD3, 0x53),   // Aging – verde
            new Color(0xBD, 0x54, 0xFF),   // LRU   – roxo
    };

    // ── Componentes de entrada ───────────────────────────────────────────────
    private JTextField  cadeiaField;
    private JSpinner    framesSpinner;
    private JButton     simularBtn;

    // ── Painel do gráfico ────────────────────────────────────────────────────
    private GraficoPanel graficoPanel;

    // ── Dados ────────────────────────────────────────────────────────────────
    private List<ResultadoSimulacao> resultados = new ArrayList<>();
    private String[]                 nomes      = {};

    // ════════════════════════════════════════════════════════════════════════
    public SimuladorGUI() {
        super("Simulador de Substituição de Páginas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 680);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        setBackground(BG_DARK);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);

        root.add(buildHeader(),     BorderLayout.NORTH);
        root.add(buildCenter(),     BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0x0A,0x14,0x1F),
                        getWidth(), 0, new Color(0x10,0x1E,0x2D));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // linha decorativa inferior com gradiente
                g2.setPaint(new GradientPaint(0, getHeight()-1, ACCENT,
                        getWidth(), getHeight()-1, new Color(0x00,0x44,0x66)));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        // Título
        JLabel title = new JLabel("PAGE REPLACEMENT SIMULATOR");
        title.setFont(loadFont(22f, true));
        title.setForeground(TEXT_MAIN);

        // Sub-título com letra colorida
        JLabel sub = new JLabel("comparação de algoritmos de substituição de páginas");
        sub.setFont(loadFont(11f, false));
        sub.setForeground(TEXT_SUB);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.add(title);
        texts.add(Box.createVerticalStrut(3));
        texts.add(sub);

        // Badge de versão
        JLabel badge = new JLabel("v1.0");
        badge.setFont(loadFont(10f, false));
        badge.setForeground(ACCENT);
        badge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT, 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        header.add(texts, BorderLayout.WEST);
        header.add(badge,  BorderLayout.EAST);
        return header;
    }

    // ── Área central ─────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBackground(BG_DARK);
        center.setBorder(BorderFactory.createEmptyBorder(20, 28, 24, 28));

        center.add(buildInputCard(), BorderLayout.NORTH);

        graficoPanel = new GraficoPanel();
        center.add(graficoPanel, BorderLayout.CENTER);

        return center;
    }

    // ── Card de inputs ───────────────────────────────────────────────────────
    private JPanel buildInputCard() {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Campo: Cadeia de páginas
        card.add(buildLabel("Cadeia de páginas"));
        cadeiaField = new JTextField("1 2 1 3 2 1 4 1 1 2 5 1 2 3 4 5", 22);
        styleTextField(cadeiaField);
        card.add(cadeiaField);

        card.add(Box.createHorizontalStrut(8));

        // Campo: Nº de frames
        card.add(buildLabel("Frames"));
        framesSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        styleSpinner(framesSpinner);
        card.add(framesSpinner);

        card.add(Box.createHorizontalStrut(8));

        // Botão simular
        simularBtn = buildSimularButton();
        card.add(simularBtn);

        return card;
    }

    private JLabel buildLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(loadFont(11f, false));
        lbl.setForeground(TEXT_SUB);
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(loadFont(12.5f, false));
        tf.setForeground(TEXT_MAIN);
        tf.setBackground(BG_INPUT);
        tf.setCaretColor(ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleSpinner(JSpinner sp) {
        sp.setFont(loadFont(12.5f, false));
        sp.setPreferredSize(new Dimension(72, 34));
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) sp.getEditor()).getTextField();
        ftf.setForeground(TEXT_MAIN);
        ftf.setBackground(BG_INPUT);
        ftf.setFont(loadFont(12.5f, false));
        ftf.setCaretColor(ACCENT);
        sp.setBorder(new LineBorder(BORDER_CLR, 1, true));
    }

    private JButton buildSimularButton() {
        JButton btn = new JButton("▶  SIMULAR") {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hover = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base  = hover ? ACCENT : new Color(0x00,0x8C,0xAA);
                Color light = hover ? new Color(0x33,0xDE,0xFF) : ACCENT;
                GradientPaint gp = new GradientPaint(0, 0, light, 0, getHeight(), base);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(BG_DARK);
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(loadFont(12f, true));
        btn.setPreferredSize(new Dimension(130, 34));
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
        private int[]                    animValues;   // para animação de crescimento
        private javax.swing.Timer        animTimer;
        private float                    animProgress  = 1f; // 0→1

        GraficoPanel() {
            setOpaque(false);
            setMinimumSize(new Dimension(400, 300));
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

            // fundo do card
            g2.setColor(BG_PANEL);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            g2.setColor(BORDER_CLR);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);

            if (dados == null || dados.isEmpty()) {
                drawPlaceholder(g2);
            } else {
                drawGrafico(g2);
            }
            g2.dispose();
        }

        // ── placeholder ──────────────────────────────────────────────────────
        private void drawPlaceholder(Graphics2D g2) {
            String msg1 = "Configure os parâmetros e clique em SIMULAR";
            String msg2 = "para visualizar a comparação entre os algoritmos";
            g2.setFont(loadFont(13f, false));
            g2.setColor(TEXT_DIM);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg1, (getWidth() - fm.stringWidth(msg1))/2, getHeight()/2 - 10);
            g2.drawString(msg2, (getWidth() - fm.stringWidth(msg2))/2, getHeight()/2 + 14);

            // linhas de grade decorativas
            g2.setColor(new Color(BORDER_CLR.getRed(), BORDER_CLR.getGreen(), BORDER_CLR.getBlue(), 80));
            g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            int pad = 48;
            for (int i = 1; i <= 4; i++) {
                int y = pad + (getHeight() - 2*pad) * i / 5;
                g2.drawLine(pad, y, getWidth()-pad, y);
            }
        }

        // ── gráfico principal ────────────────────────────────────────────────
        private void drawGrafico(Graphics2D g2) {
            int n       = dados.size();
            int padL    = 70, padR = 28, padT = 50, padB = 100;
            int chartW  = getWidth()  - padL - padR;
            int chartH  = getHeight() - padT - padB;

            int maxFaults = dados.stream().mapToInt(ResultadoSimulacao::getPageFaults).max().orElse(1);
            // arredondar pra cima em múltiplo de 2
            maxFaults = ((maxFaults / 2) + 1) * 2;

            // ── grade e eixos ─────────────────────────────────────────────
            int gridLines = 5;
            g2.setStroke(new BasicStroke(0.6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
            g2.setFont(loadFont(10f, false));
            FontMetrics fmSmall = g2.getFontMetrics();

            for (int i = 0; i <= gridLines; i++) {
                int y    = padT + chartH - (chartH * i / gridLines);
                int val  = maxFaults * i / gridLines;
                g2.setColor(new Color(BORDER_CLR.getRed(), BORDER_CLR.getGreen(), BORDER_CLR.getBlue(), 90));
                g2.drawLine(padL, y, padL + chartW, y);

                g2.setColor(TEXT_DIM);
                String lbl = String.valueOf(val);
                g2.drawString(lbl, padL - fmSmall.stringWidth(lbl) - 8, y + 4);
            }

            // eixo Y label
            g2.setFont(loadFont(10f, false));
            g2.setColor(TEXT_SUB);
            AffineTransform orig = g2.getTransform();
            g2.rotate(-Math.PI/2, 14, padT + chartH/2);
            String yLabel = "Page Faults";
            g2.drawString(yLabel, 14 - g2.getFontMetrics().stringWidth(yLabel)/2, padT + chartH/2 + 4);
            g2.setTransform(orig);

            // ── barras ────────────────────────────────────────────────────
            int barGroupW = chartW / n;
            int barW      = (int)(barGroupW * 0.52);
            int barX0     = padL + (barGroupW - barW) / 2;

            float ease = easeOut(animProgress);

            for (int i = 0; i < n; i++) {
                int faults   = dados.get(i).getPageFaults();
                int fullH    = (int)((double) faults / maxFaults * chartH);
                int animH    = (int)(fullH * ease);
                int x        = padL + i * barGroupW + (barGroupW - barW) / 2;
                int y        = padT + chartH - animH;

                Color algColor = ALG_COLORS[i % ALG_COLORS.length];

                // sombra/brilho lateral
                g2.setPaint(new GradientPaint(
                        x, y, algColor,
                        x + barW, y, algColor.darker().darker()
                ));
                g2.fillRoundRect(x, y, barW, animH, 6, 6);

                // borda sutil
                g2.setColor(new Color(255,255,255, 30));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, barW, animH, 6, 6);

                // reflexo no topo da barra
                if (animH > 8) {
                    GradientPaint ref = new GradientPaint(
                            x, y, new Color(255,255,255,50),
                            x, y+8, new Color(255,255,255,0)
                    );
                    g2.setPaint(ref);
                    g2.fillRoundRect(x+1, y+1, barW-2, 8, 5, 5);
                }

                // valor no topo da barra
                if (animProgress > 0.85f) {
                    float alpha = Math.min(1f, (animProgress - 0.85f) / 0.15f);
                    g2.setFont(loadFont(13f, true));
                    FontMetrics fm2 = g2.getFontMetrics();
                    String val = String.valueOf(faults);
                    int tx = x + (barW - fm2.stringWidth(val)) / 2;
                    g2.setColor(new Color(algColor.getRed(), algColor.getGreen(), algColor.getBlue(), (int)(255*alpha)));
                    g2.drawString(val, tx, y - 6);
                }

                // rótulo X (nome curto)
                String shortName = shortName(labels[i]);
                g2.setFont(loadFont(11f, true));
                FontMetrics fmN = g2.getFontMetrics();
                int lx = x + (barW - fmN.stringWidth(shortName)) / 2;
                g2.setColor(algColor);
                g2.drawString(shortName, lx, padT + chartH + 22);

                // page faults count
                g2.setFont(loadFont(10f, false));
                fmN = g2.getFontMetrics();
                String sub = faults + " faults";
                int slx = x + (barW - fmN.stringWidth(sub)) / 2;
                g2.setColor(TEXT_SUB);
                g2.drawString(sub, slx, padT + chartH + 38);

                // porcentagem de faults
                if (totalAcessos > 0) {
                    String pct = String.format("%.0f%%", (double) faults / totalAcessos * 100);
                    int plx = x + (barW - fmN.stringWidth(pct)) / 2;
                    g2.setColor(TEXT_DIM);
                    g2.drawString(pct, plx, padT + chartH + 52);
                }
            }

            // ── título do gráfico ─────────────────────────────────────────
            g2.setFont(loadFont(12.5f, true));
            g2.setColor(TEXT_MAIN);
            String titulo = "Page Faults por Algoritmo";
            g2.drawString(titulo, padL, padT - 14);

            // ── info pequena à direita ────────────────────────────────────
            g2.setFont(loadFont(10f, false));
            g2.setColor(TEXT_DIM);
            String info = "acessos: " + totalAcessos + "  |  frames: " + (int) framesSpinner.getValue();
            g2.drawString(info, getWidth() - padR - g2.getFontMetrics().stringWidth(info), padT - 14);

            // ── linha de base ──────────────────────────────────────────────
            g2.setColor(BORDER_CLR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
        }

        private float easeOut(float t) {
            return 1f - (1f-t)*(1f-t)*(1f-t);
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
    // Utilitário de fonte
    // ════════════════════════════════════════════════════════════════════════
    private static Font loadFont(float size, boolean bold) {
        int style = bold ? Font.BOLD : Font.PLAIN;
        // Tenta usar JetBrains Mono ou Monospaced como fallback
        Font f = new Font("JetBrains Mono", style, (int) size);
        if (f.getFamily().equals("JetBrains Mono")) return f.deriveFont(size);
        f = new Font("Consolas", style, (int) size);
        if (f.getFamily().equals("Consolas")) return f.deriveFont(size);
        return new Font(Font.MONOSPACED, style, (int) size).deriveFont(size);
    }

    // ════════════════════════════════════════════════════════════════════════
    // main
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Activar rendering de hardware se disponível
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}

            new SimuladorGUI().setVisible(true);
        });
    }
}