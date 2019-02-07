import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class ParticleDisplay {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParticleDisplay window = new ParticleDisplay();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ParticleDisplay() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String ParticleNumInput = "";
		int ParticleNum = 0;
		
		ParticleNumInput = JOptionPane.showInputDialog("Enter number of particles.");
		ParticleNum = Integer.parseInt(ParticleNumInput);
		
		frame = new JFrame();
		//frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setSize(1280, 720);
		frame.setUndecorated(false);
		frame.setTitle("Gravity Particle Simulation, Colin Johnson 2015");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(new particleDisplayPanel(ParticleNum));
	}

}
