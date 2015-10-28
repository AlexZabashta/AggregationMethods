package misc;

import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageViewer extends JFrame {
	private static final long serialVersionUID = 1L;

	public ImageViewer(final BufferedImage bufferedImage) {
		setLayout(null);
		final int cw = bufferedImage.getWidth(), ch = bufferedImage.getHeight();

		final JLabel label = new JLabel();
		add(label);

		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int tw = Math.max(32, getWidth() - 16);
				int th = Math.max(24, getHeight() - 38);

				BufferedImage temp = new BufferedImage(tw, th, BufferedImage.TYPE_INT_ARGB);

				Graphics g = temp.getGraphics();
				g.drawImage(bufferedImage, 0, 0, tw, th, null);

				label.setIcon(new ImageIcon(temp));
				label.setBounds(0, 0, tw, th);
				setTitle("ImageViewer " + tw + "x" + th);
			}
		});

		setSize(cw, ch);

	}

}
