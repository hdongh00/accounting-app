import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel{
	private Image img;
	public ImagePanel(Image img){
		this.img = img;
		setSize(new Dimension(img.getWidth(null),img.getHeight(null)));
		setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
		setLayout(null);
	}
	
	public void paintComponent(Graphics g){
		g.drawImage(img, 0, 0, 1450, 700, this);
	}
	
	public Dimension getDim(){
		return new Dimension(img.getWidth(null),img.getHeight(null)+47);
	}
}
