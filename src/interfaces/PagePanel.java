package interfaces;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PagePanel extends JPanel {
	
	private JLabel Title;
	
	public PagePanel()
	{
		super();
	}
	
	/**
	 * ���¼�
	 */
	public void openEvent(){}
	
	/**
	 * �ر��¼�
	 */
	public void closeEvent(){}

	/**
	 * Getter and Setter
	 * @return
	 */
	public JLabel getTitle() {
		return Title;
	}

	public void setTitle(JLabel title) {
		Title = title;
	}
}
