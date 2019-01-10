package gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import tools.HannaTheme;

public class About extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public About() {
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setFont(HannaTheme.FILEFONT);
		JScrollPane scrollPane = new JScrollPane(textPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textPane.setText("Hanna JavaIDE 1.0�ǻ���MyEclipse2014����ƿ����ġ�Hanna�Ķ�λ����������IDE����һ���������������ѻ�ʹ�ã���Ҫ��������ɳ���ı�д�����ԡ����뼰���У�����֮�⣬����һЩ�������ĵ��༭���ܡ�Hanna�Ľ����࣬�����򵥣�û�и��ӵĲ��裬�������֡�������Ϊһ�Դ���Զ���IDE�����ʹ���˿��Ը����Լ���������������Ͻ����޸ģ������ݡ�\r\nCopyright  2017, HANNA, All Rights Reserve");
	}

}
