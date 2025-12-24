import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Visualization and Comparison of Sorting Algorithms
public class Main extends JPanel {

    private static final long serialVersionUID = 1L;

    private SortPanel[] sortPanels = new SortPanel[2];
    private static int size = 100;
    private int sleepTime = 2;
    private String animationName = "";

    private JComboBox<String> algo1Box;
    private JComboBox<String> algo2Box;
    private JComboBox<String> dataBox;
    private JButton startButton;
    private SortPanelsHolder sortPanelHolder;

    public Main() {
        setLayout(new BorderLayout());

        // ---------- CONTROL PANEL ----------
        JPanel controlPanel = new JPanel();

        algo1Box = new JComboBox<>(new String[]{
                "Heap Sort",
                "Shell Sort"
        });

        algo2Box = new JComboBox<>(new String[]{
                "Heap Sort",
                "Shell Sort"
        });
		        dataBox = new JComboBox<>(new String[]{
		        "Random",
		        "Reversed",
		        "Almost Sorted",
		        "Few Unique"
		});

		
        startButton = new JButton("Start");

        controlPanel.add(new JLabel("Algorithm 1:"));
        controlPanel.add(algo1Box);
        controlPanel.add(new JLabel("Algorithm 2:"));
        controlPanel.add(algo2Box);
        controlPanel.add(new JLabel("Data:"));
		controlPanel.add(dataBox);
		controlPanel.add(startButton);

        add(controlPanel, BorderLayout.NORTH);

        // ---------- SORT PANELS HOLDER ----------
        sortPanelHolder = new SortPanelsHolder();
        sortPanelHolder.setLayout(new GridLayout(0, 2, 0, 0));
        sortPanelHolder.setBackground(Color.decode("#3B9797"));

        add(sortPanelHolder, BorderLayout.CENTER);

        // ---------- BUTTON ACTION ----------
        startButton.addActionListener(e -> startSelectedAlgorithms());

        setPreferredSize(new Dimension(1000, 600));
    }

    // ---------- CREATE SORT PANEL ----------
    private SortPanel createSortPanel(String name, int width, int height) {
        switch (name) {
            case "Heap Sort":
                return new HeapSortPanel(" Heap Sort ", sleepTime, width, height);
            case "Shell Sort":
                return new ShellSortPanel(" Shell Sort ", sleepTime, width, height);
            default:
                throw new IllegalArgumentException("Unknown algorithm");
        }
    }

    // ---------- START ANIMATION ----------
    private void startSelectedAlgorithms() {
        sortPanelHolder.removeAll();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width / 2;
        int height = screenSize.height / 2;

        sortPanels[0] = createSortPanel(
                (String) algo1Box.getSelectedItem(), width, height);
        sortPanels[1] = createSortPanel(
                (String) algo2Box.getSelectedItem(), width, height);

        for (SortPanel panel : sortPanels) {
            panel.setVisible(false);
            sortPanelHolder.add(panel);
        }

        sortPanelHolder.revalidate();
        sortPanelHolder.repaint();

		String dataType = (String) dataBox.getSelectedItem();
		int[] list = generateData(dataType);

		new Thread(() -> beginAnimation(dataType, list)).start();

    }

    private int[] generateData(String type) {
	    int[] list = new int[size];

	    switch (type) {
	        case "Random":
	            for (int i = 0; i < size; i++) list[i] = i + 1;
	            shuffle(list);
	            break;

	        case "Reversed":
	            for (int i = 0; i < size; i++) list[i] = size - i;
	            break;

	        case "Almost Sorted":
	            for (int i = 0; i < size / 2; i++) list[i] = i + 1;
	            for (int i = size / 2; i < size; i++) list[i] = i + 2;
	            list[size - 1] = size / 2 + 1;
	            break;

	        case "Few Unique":
	            for (int i = 0; i < size; i++) {
	                list[i] = (1 + i / (size / 4)) * (size / 4);
	            }
	            shuffle(list);
	            break;
	    }
	    return list;
	}

	private void shuffle(int[] list) {
	    for (int i = 0; i < list.length; i++) {
	        int j = (int) (Math.random() * list.length);
	        int tmp = list[i];
	        list[i] = list[j];
	        list[j] = tmp;
	    }
	}


    // ---------- HOLDER ----------
    class SortPanelsHolder extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            Font font = new Font(Font.MONOSPACED, Font.BOLD, 150);
            FontMetrics fm = getFontMetrics(font);
            g.setFont(font);
            int x = (getWidth() - fm.stringWidth(animationName)) / 2;
            int y = getHeight() / 2;
            g.drawString(animationName, x, y);
        }
    }

    // ---------- ANIMATION ----------
    public void beginAnimation(String animationName, int[] list) {
        try {
            this.animationName = animationName;
            repaint();
            Thread.sleep(2000);
            this.animationName = "";
            repaint();

            for (SortPanel panel : sortPanels) {
                panel.setList(list.clone());
                panel.setVisible(true);
            }

            ExecutorService executor = Executors.newFixedThreadPool(sortPanels.length);
            for (SortPanel panel : sortPanels) {
                executor.execute(panel);
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }

            for (SortPanel panel : sortPanels) {
                panel.setVisible(false);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sorting Algorithm Animations");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Main());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
