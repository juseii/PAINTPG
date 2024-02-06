package paint;
// Importar las clases necesarias de Swing y AWT
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Clase principal que extiende JFrame
public class PaintApp extends JFrame {

    // Tamaño predeterminado de la ventana
    private static final int ANCHO = 800;
    private static final int ALTO = 600;

    // Variables para el seguimiento de posiciones anteriores, color y grosor actual del lápiz
    private int prevX, prevY;
    private Color colorActual = Color.BLACK;
    private int grosorActual = 10;

    // Lista para almacenar los puntos del dibujo
    private List<Point> puntosDibujo = new ArrayList<>();
    
    // Componentes de la interfaz gráfica
    private Lienzo lienzo;
    private JFrame frameProyector;

    // Constructor de la aplicación
    public PaintApp() {
        configurarVentana();
        configurarLienzo();
        configurarPanelBotones();
        configurarBotonProyector();
    }

    // Configuración de la ventana principal
    private void configurarVentana() {
        setTitle("PAINT");
        setSize(ANCHO, ALTO);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    // Configuración del lienzo de dibujo
    private void configurarLienzo() {
        lienzo = new Lienzo();
        lienzo.setBackground(Color.WHITE);
        lienzo.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
                puntosDibujo.clear();  
                puntosDibujo.add(new Point(prevX, prevY));
            }
        });

        lienzo.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                puntosDibujo.add(new Point(e.getX(), e.getY()));
                dibujarEnLienzo(lienzo.getGraphics(), puntosDibujo);
                actualizarLienzoProyector(puntosDibujo);
            }
        });

        add(lienzo, BorderLayout.CENTER);
    }

    // Configuración del panel de botones de la interfaz
    private void configurarPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(173, 216, 230));

        // Crear y configurar botones para seleccionar color, grosor y borrar
        JButton btnColor = crearBotonEstilizado("Seleccionar Color");
        btnColor.addActionListener(e -> elegirColor());

        JButton btnGrosor = crearBotonEstilizado("Grosor");
        btnGrosor.addActionListener(e -> elegirGrosor());

        JButton btnBorrar = crearBotonEstilizado("Borrar");
        btnBorrar.addActionListener(e -> borrarLienzo());

        // Agregar botones al panel
        panelBotones.add(btnColor);
        panelBotones.add(btnGrosor);
        panelBotones.add(btnBorrar);

        // Agregar panel de botones al sur de la ventana
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Configuración del botón para abrir el proyector
    private void configurarBotonProyector() {
        JButton btnProyector = crearBotonEstilizado("Proyector");
        btnProyector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirProyector();
            }
        });

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(173, 216, 230));
        panelBotones.add(btnProyector);

        add(panelBotones, BorderLayout.NORTH);
    }

    // Método para crear botones con un estilo personalizado
    private JButton crearBotonEstilizado(String texto) {
        JButton boton = new JButton(texto);
        estilizarBoton(boton);
        return boton;
    }

    // Método para aplicar estilos a los botones
    private void estilizarBoton(JButton boton) {
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.PLAIN, 14));
        boton.setBackground(new Color(221, 160, 221));
        boton.setForeground(Color.BLACK);
        boton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        ButtonModel modelo = boton.getModel();
        modelo.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (modelo.isPressed() || modelo.isSelected()) {
                    boton.setBackground(new Color(173, 216, 230));
                } else {
                    boton.setBackground(new Color(221, 160, 221));
                }
            }
        });
    }

    // Método para dibujar en el lienzo
    private void dibujarEnLienzo(Graphics g, List<Point> puntos) {
        g.setColor(colorActual);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(grosorActual));

        if (puntos.size() > 1) {
            for (int i = 1; i < puntos.size(); i++) {
                Point p1 = puntos.get(i - 1);
                Point p2 = puntos.get(i);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // Método para actualizar el lienzo del proyector
    private void actualizarLienzoProyector(List<Point> puntos) {
        if (frameProyector != null) {
            Lienzo lienzoProyector = (Lienzo) frameProyector.getContentPane().getComponent(0);
            lienzoProyector.dibujar(puntos);
        }
    }

    // Método para elegir un nuevo color
    private void elegirColor() {
        Color nuevoColor = JColorChooser.showDialog(this, "Seleccionar Color", colorActual);
        if (nuevoColor != null) {
            colorActual = nuevoColor;
        }
    }

    // Método para elegir un nuevo grosor de línea
    private void elegirGrosor() {
        try {
            String entrada = JOptionPane.showInputDialog(this, "Ingrese el grosor del lápiz (1-20):", grosorActual);
            int grosor = Integer.parseInt(entrada);
            if (grosor >= 1 && grosor <= 20) {
                grosorActual = grosor;
            } else {
                mostrarMensajeError("El grosor debe estar en el rango de 1 a 20.");
            }
        } catch (NumberFormatException e) {
            mostrarMensajeError("Ingrese un número válido para el grosor.");
        }
    }

    // Método para borrar el lienzo
    private void borrarLienzo() {
        puntosDibujo.clear();
        lienzo.repaint();
        actualizarLienzoProyector(puntosDibujo);
    }

    // Método para mostrar mensajes de error
    private void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Método para abrir el proyector
    private void abrirProyector() {
        if (frameProyector == null) {
            frameProyector = new JFrame("Proyector");
            frameProyector.setSize(ANCHO, ALTO);
            frameProyector.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameProyector.setLayout(new BorderLayout());

            Lienzo lienzoProyector = new Lienzo();
            lienzoProyector.setBackground(Color.WHITE);
            frameProyector.add(lienzoProyector, BorderLayout.CENTER);

            lienzo.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    lienzoProyector.dibujar(prevX, prevY, e.getX(), e.getY());
                    prevX = e.getX();
                    prevY = e.getY();
                }
            });
        }

        frameProyector.setVisible(true);
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            PaintApp paintApp = new PaintApp();
            paintApp.setVisible(true);
        });
    }

    // Clase interna que representa el lienzo de dibujo
    private class Lienzo extends JPanel {
        private List<Point> puntosDibujo = new ArrayList<>();

        // Método para dibujar en el lienzo
        public void dibujar(List<Point> puntos) {
            puntosDibujo = puntos;
            repaint();
        }

        // Método para dibujar una línea en el lienzo
        public void dibujar(int x1, int y1, int x2, int y2) {
            puntosDibujo.add(new Point(x1, y1));
            puntosDibujo.add(new Point(x2, y2));
            repaint();
        }

        // Método para pintar el componente
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarEnLienzo(g, puntosDibujo);
        }
    }
}
