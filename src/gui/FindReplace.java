package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FindReplace extends JFrame {

	private JPanel contentPane;
	private JTextField Replace;
	private JTextField Find;
	private JButton btnReplace;
	
	private static JTextPane textPane;
	
	//Direction ����
	private static int direction;	//direction Ϊ1��ʾ���ϣ�backward�����ң�Ϊ2��ʾ���£�forward������

	/**
	 * Create the frame.
	 */
	public FindReplace(JTextPane textPane1) {
		
		this.textPane=textPane1;
		
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 384);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblFind = new JLabel("Find:");
		lblFind.setBounds(70, 37, 72, 18);
		contentPane.add(lblFind);
		
		JLabel lblReplaceWith = new JLabel("Replace with:");
		lblReplaceWith.setBounds(70, 68, 104, 18);
		contentPane.add(lblReplaceWith);
		
		Replace = new JTextField();
		Replace.setBounds(188, 65, 152, 24);
		contentPane.add(Replace);
		Replace.setColumns(10);
		
		Find = new JTextField();
		Find.setBounds(188, 34, 152, 24);
		contentPane.add(Find);
		Find.setColumns(10);
		
		
		
		
		
		
		//Replace
		btnReplace = new JButton("Replace");
		btnReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Replace
				btnReplace.setEnabled(false);			//����Replace��ť���ɵ��
				
				String find_text=Find.getText();		//��ȡ��������
				String replace_text=Replace.getText();	//��ȡ�滻����
				
				textPane.replaceSelection(replace_text);//�滻
			}
		});
		btnReplace.setBounds(219, 193, 121, 27);
		contentPane.add(btnReplace);
		
		//Replace All
		final JButton btnReplaceAll = new JButton("Replace All");
		btnReplaceAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Replace All
				btnReplaceAll.setEnabled(false);			//����ReplaceAll��ť���ɵ��
				btnReplace.setEnabled(false);				//����Replace��ť���ɵ��
				
				String find_text=Find.getText();			//��ȡ��������
				int find_text_length=find_text.length();	//��ȡ�������ݵĳ���
				String replace_text=Replace.getText();		//��ȡ�滻����
				
				String text=new String();				
				text=textPane.getText();					//��ȡ�ı�����
				
				int next=0;									//��ʲôλ����,Ϊ0��ʾ���ı���ͷ��ʼ��
				int start=0;								//���ҵ��ĵ�һ���ַ���λ�ã�Ϊ0��ʾ���ı���ͷ��ʼ��
				
				
				while(text.indexOf(find_text,next)!=-1){
					start=text.indexOf(find_text, next);			//���Ҵӹ��λ�ÿ�ʼ ���²�ѯ��һ�����ϵ�����
					textPane.setSelectionStart(start);				//ѡ�п�ʼλ�õ�����λ���м������
					textPane.setSelectionEnd(start+find_text_length);
					textPane.replaceSelection(replace_text);		//�滻
					text=textPane.getText();						//���»�ȡ�ı�����
					next=start+find_text_length;;					//�޸�λ������
				}
				JOptionPane.showMessageDialog
                (null, "Replace all successfully", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnReplaceAll.setBounds(70, 243, 121, 27);
		contentPane.add(btnReplaceAll);
		
		btnReplace.setEnabled(false);			//����Replace��ť���ɵ��
		btnReplaceAll.setEnabled(false);		//����ReplaceAll��ť���ɵ��
		
		//Find
		JButton btnFind = new JButton("Find");
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Find
				String find_text=Find.getText();			//��ȡ��������
				int find_text_length=find_text.length();	//��ȡ�������ݵĳ���
				if(find_text.equals("")){					//�жϲ��ҿ��Ƿ�Ϊ��
					JOptionPane.showMessageDialog
                    (null, "Please enter the find content��", "Sorry", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				String text=new String();				
				text=textPane.getText();								//��ȡ�ı�����
				
				int next=textPane.getSelectionEnd();					//��ʲôλ����
				int start=textPane.getSelectionStart();					//���ҵ��ĵ�һ���ַ���λ��
				
				if(direction==2){										//���²���
					if(text.indexOf(find_text, next)!=-1){				//����ָ���ַ��ڴ��ַ����е�һ�γ��ִ�������,û����᷵��-1
						start=text.indexOf(find_text, next);			//���Ҵӹ��λ�ÿ�ʼ ���²�ѯ��һ�����ϵ�����
						textPane.setSelectionStart(start);				//ѡ�п�ʼλ�õ�����λ���м������
						textPane.setSelectionEnd(start+find_text_length);
						next=start+find_text_length;					//�޸�λ������
						btnReplace.setEnabled(true);					//����Replace��ť�ɵ��
						btnReplaceAll.setEnabled(true);					//����ReplaceAll��ť�ɵ��
					}else{ 												//û�ҵ��������ݣ���ʾ����Ի���
	                     JOptionPane.showMessageDialog
	                      (null, "Very sorry! we cann't find it!", "Sorry", JOptionPane.ERROR_MESSAGE);     
	                     }
				}else{													//���ϲ���
					if(text.lastIndexOf(find_text,start-1)!=-1){	
		                int n = text.lastIndexOf(find_text,start-1);	//���Ҵӹ��λ�ÿ�ʼ ���ϲ�ѯ��һ�����ϵ�����
		                textPane.setSelectionStart(n);					//ѡ�п�ʼλ�õ�����λ���м������
		                textPane.setSelectionEnd(n+find_text_length);
		                start = n;										//�޸�λ������
		                next = n + find_text_length;					//�޸�λ������
		                btnReplace.setEnabled(true);					//����Replace��ť�ɵ��
		                btnReplaceAll.setEnabled(true);					//����ReplaceAll��ť�ɵ��
		             }else{ 
		            	 JOptionPane.showMessageDialog
		                      (null, "Very sorry! we cann't find it!", "Sorry", JOptionPane.ERROR_MESSAGE);     
		            	 } 
				}
			}
		});
		
		btnFind.setBounds(70, 193, 121, 27);
		contentPane.add(btnFind);
		
		JLabel lblDirection = new JLabel("Direction");
		lblDirection.setBounds(140, 99, 72, 18);
		contentPane.add(lblDirection);
		
		
		
		
		
		//direction forward
		JRadioButton rdbtnForward = new JRadioButton("Backward");
		rdbtnForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//direction forward
				direction=1;								//����directionΪ1��������(backward)����
			}
		});
		rdbtnForward.setBounds(126, 126, 157, 27);
		contentPane.add(rdbtnForward);
		
		//direction backward
		JRadioButton rdbtnBackward = new JRadioButton("Forward");
		rdbtnBackward.setSelected(true);					//Ĭ��ѡ��rdbtnBackward
		direction=2;										//����directionΪ2�������£�forward������
		rdbtnBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//direction forward
				direction=2;								//����directionΪ2�������£�forward������
			}
		});
											
		rdbtnBackward.setBounds(126, 158, 157, 27);
		contentPane.add(rdbtnBackward);
		
		//����Direction��ButtonGroup
		ButtonGroup direc_bg=new ButtonGroup();
		direc_bg.add(rdbtnForward);			//���Backward����ѡ��ť
		direc_bg.add(rdbtnBackward);		//���Forward����ѡ��ť
		
		
		//Close
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Close
				FindReplace.this.dispose();
			}
		});
		btnClose.setBounds(219, 243, 121, 27);
		contentPane.add(btnClose);
		
		
		FindReplace.this.setVisible(true);
		FindReplace.this.setResizable(false);
		
		
	}
}
