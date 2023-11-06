import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileFrame extends JFrame {

    final int WIDTH, HEIGHT;
    static Font SMALL_BOLD, MED_PLAIN, MED_BOLD, MED_BOLD_ITAL, BIG_BOLD;
    static BufferedImage FRAME_IMAGE;
    JPanel cardPnl, mainMenu, fileList;
    final String MAIN = "MAIN MENU",
            FILE_VIEW = "DIRECTORY";
    static final String DEFAULT_DIR = System.getProperty("user.dir");
    JTextArea textArea, directoryName;
    JScrollPane scrollPane;

    public FileFrame() {
        super("Recursive Lister");
        getFonts();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // attempts to read BufferedImage from file;
        // if it fails, getImage() will return null and the program won't set the frame icon to anything.
        getImage();
        if (FRAME_IMAGE != null) setIconImage(FRAME_IMAGE);

        // sets frame size and location based on local screen dimensions
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenw = screenSize.width,
                screenh = screenSize.height;
        WIDTH = screenw * 3 / 4;
        HEIGHT = screenh * 3 / 4;
        setSize(WIDTH, HEIGHT);
        setLocation((screenw - WIDTH) / 2, (screenh - HEIGHT) / 3);

        // attempts to set the look and feel to local system look and feel;
        // if it fails, leaves it as the default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    UnsupportedLookAndFeelException
                ignored) {
        }

        // adds border with title
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 5, true),
                " Recursive Lister ", TitledBorder.CENTER, TitledBorder.TOP);
        border.setTitleFont(BIG_BOLD);
        getRootPane().setBorder(BorderFactory.createCompoundBorder
                (BorderFactory.createEmptyBorder(30, 30, 30, 30), border));

        // creates quit button that shows JOptionPane
        JButton quitBtn = createButton("Quit", e -> {
                    if (JOptionPane.showConfirmDialog(null, "Quit?", "Quit",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null) == JOptionPane.OK_OPTION)
                            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        quitBtn.setFont(SMALL_BOLD);

        // lays out the menu
        setLayout(new GridBagLayout());

        createMainMenu();
        createFileListPanel();

        cardPnl = new JPanel(new CardLayout());
        cardPnl.add(mainMenu, MAIN);
        cardPnl.add(fileList, FILE_VIEW);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(50, 50, 0, 50);
        add(cardPnl, gbc);

        gbc.weighty = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(new JPanel(), gbc);

        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(quitBtn, gbc);

        ((CardLayout) cardPnl.getLayout()).show(cardPnl, MAIN);
        revalidate();
    }

    public void updateFileList(String file) {
        textArea.append(file.concat("\n"));
    }

    private void createMainMenu() {
        JButton startBtn = createButton("Choose directory", e -> openJFileChooser());
        mainMenu = new JPanel(new GridBagLayout());
        mainMenu.setOpaque(false);
        GridBagConstraints mainMenuCons = new GridBagConstraints();
        mainMenu.add(startBtn, mainMenuCons);
    }

    private void openJFileChooser() {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Directory");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setCurrentDirectory(new File(DEFAULT_DIR));

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            directoryName.append(jfc.getSelectedFile().getAbsolutePath().concat("\""));
            RecursiveLister.listFiles(jfc.getSelectedFile(), 0);
            if (textArea.getText() != null && !textArea.getText().isEmpty())
                textArea.setText(textArea.getText().substring(0, textArea.getText().length() - 1));
            textArea.select(0,0);

            ((CardLayout) cardPnl.getLayout()).show(cardPnl, FILE_VIEW);
            revalidate();
        }
    }

    private void createFileListPanel() {
        fileList = new JPanel(new GridBagLayout());
        fileList.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        textArea = new JTextArea();
        textArea.setOpaque(false);
        textArea.setFont(MED_PLAIN);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        directoryName = new JTextArea("Files in directory \"");
        directoryName.setOpaque(false);
        directoryName.setFont(MED_BOLD_ITAL);
        directoryName.setLineWrap(true);
        directoryName.setWrapStyleWord(true);
        directoryName.setEditable(false);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        fileList.add(directoryName, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        fileList.add(scrollPane, gbc);
    }

    private static JButton createButton(String text, ActionListener actionListener) {
        JButton btn = new JButton(text);
        btn.addActionListener(actionListener);
        btn.setFocusPainted(false);
        btn.setFont(MED_BOLD);
        return btn;
    }

    private static void getImage() {
        try {
            FRAME_IMAGE = ImageIO.read(new File(System.getProperty("user.dir") +
                    "//images//left-arrow-of-triangular-shape-of-dots-pattern.png"));
        } catch (IOException e) {
            FRAME_IMAGE = null;
        }
    }

    private static void getFonts() {
        File dir = new File(System.getProperty("user.dir") + "//fonts//");
        try {
            Font reg = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-Regular.ttf"));
            MED_PLAIN = reg.deriveFont(Font.PLAIN, 20f);
            Font bold = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-Bold.ttf"));
            SMALL_BOLD = bold.deriveFont(Font.PLAIN, 20f);
            MED_BOLD = bold.deriveFont(Font.PLAIN, 24f);
            BIG_BOLD = bold.deriveFont(Font.PLAIN, 30f);
            Font bi = Font.createFont(Font.TRUETYPE_FONT, new File(dir +
                    "//Hack-BoldItalic.ttf"));
            MED_BOLD_ITAL = bi.deriveFont(Font.PLAIN, 20f);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
