package gui;

import interfaces.PagePanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import tools.HannaTheme;

public class FilePanel extends PagePanel implements Runnable{
	//Pane����
	private JTextPane editPane;
//	private SelectTextPane editPane;
	private JTextPane lineNumber;
	private JPanel scrollPanel;
	private JScrollPane editscrollPane;
	//��������
	private LineNumberReader linereader;
	private File sourcefile;
	private boolean isdispose;
	private boolean issaved;
	private boolean iscompiled;
	
	/**
	 * Create the panel.
	 */
	public FilePanel(File file) {
		super();
		//����panel����
		scrollPanel=new JPanel();
		scrollPanel.setBorder(new EmptyBorder(0,0,0,0));
		scrollPanel.setLayout(new BorderLayout(0, 0));
		this.setLayout(new BorderLayout());
		editscrollPane=new JScrollPane(scrollPanel);
		editscrollPane.getVerticalScrollBar().setUnitIncrement(20);
		this.add(editscrollPane,BorderLayout.CENTER);
		
		//��ʾ�кű�ǩ
		lineNumber=new JTextPane();
		lineNumber.setOpaque(true);
		lineNumber.setFont(HannaTheme.FILEFONT);
		lineNumber.setBackground(HannaTheme.LINENUMBG);
		lineNumber.setForeground(HannaTheme.LINENUMCOLOR);
		lineNumber.setEditable(false);
		lineNumber.setLayout(new BorderLayout());
		scrollPanel.add(lineNumber,BorderLayout.WEST);
		lineNumber.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>=2)
				{
					lineNumber.setVisible(false);
				}
			}
		});
		//�༭Pane
		editPane=new JTextPane();
		editPane.setBackground(HannaTheme.TEXTBG);
		editPane.setFont(HannaTheme.FILEFONT);
		scrollPanel.add(editPane,BorderLayout.CENTER);
		
		//���ݳ�ʼ��
		sourcefile=file;
		isdispose=false;
		issaved=true;
		iscompiled=false;
		
		//�����߳���ʾ����
		new Thread(FilePanel.this).start();
	}
	
	public void appendText(String str)
	{
		editPane.setText(editPane.getText()+str);
	}
	
	public void appendlnText(String str)
	{
		editPane.setText(editPane.getText()+str+"\n");
	}
	
	public void loadFile()
	{
		if(sourcefile==null)
		{
			JOptionPane.showMessageDialog(null, "File is not readable!", "ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
		//get message
		try {
			BufferedReader buff=new BufferedReader(new FileReader(sourcefile));
			editPane.setText("");//������
			String line;
			while((line=buff.readLine())!=null)
			{
				appendlnText(line);
			}
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "File "+sourcefile.getName()+" is unavaliable", "ERROR",JOptionPane.ERROR_MESSAGE);
		}
		//���ù����ʼλ��
		editPane.setCaretPosition(0);
		
		//Ϊ�ı���Ӽ����¼�
		editPane.getDocument().addDocumentListener(new DocumentListener(){

			@Override
			public void changedUpdate(DocumentEvent e) {}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(issaved)
				{
					issaved=false;
					iscompiled=false;
					FilePanel.this.getTitle().setText("*"+FilePanel.this.getTitle().getText());
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				if(issaved)
				{
					issaved=false;
					iscompiled=false;
					FilePanel.this.getTitle().setText("*"+FilePanel.this.getTitle().getText());
				}
			}
			
		});
	}

	//��ʾ�����߳�
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//previousline Ϊ֮ǰ����ȡ�����������, currentlineΪ��ǰ�¶�ȡ�����������
		int previousline=1,currentline=1;
		//������������λ��
		int num=0;
		//ÿһ�е��к�
		String li="";
		//�����к�
		String lines="";
		//isdispose ���ļ��رձ�־�����ļ����ر�ʱ�߳�Ҳ�����
		while(!isdispose)
		{
			try {
				//����ˢ���ٶ�
				Thread.sleep(50);
				//�½��кŶ�ȡ��
				linereader=new LineNumberReader(new StringReader(editPane.getText()));
				//����������ĵ����
				linereader.skip(Long.MAX_VALUE);
				//��ȡ����кŲ�+1����Ϊ���෵��ֵ�±��Ǵ�0��ʼ��
				currentline=linereader.getLineNumber()+1;
				//�õ�����кŵ�λ��
				num=String.valueOf(currentline).length()-1;
				//ֻ�е��к���֮ǰ���ʱ���кŲ�һ��ʱ�ſ�ʼ���£��������Լ���ˢ��Ƶ��
				if(currentline!=previousline)
				{
					//��ʼ����
					lines="";
					//��ÿһ�и�ֵ�к�
					for(int i=1;i<=currentline;i++)
					{
						//�õ��к��ַ���
						li=String.valueOf(i);
						
						//����ո��кŶ���
						for(int j=String.valueOf(i).length();j<=num;j++)
						{
							li=" "+li;
						}
						lines=lines+li+"\n";
					}
					
					//����кŵ��к����
					lineNumber.setText(lines);
					//������������
					previousline=currentline;
				}
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//�򿪴����¼�
	@Override
	public void openEvent()
	{
		loadFile();
	}
	
	//�رմ����¼�
	@Override
	public void closeEvent()
	{
		//�Ƿ񱣴�
		if(!issaved)
		{
			//�������ڱ���
			if(JOptionPane.showConfirmDialog(null, "File "+sourcefile.getName()+" hasn't saved yet. Do you want to save it now?", "WARNING",JOptionPane.YES_NO_OPTION)==0)//�жϱ���
			{
				save();
			}
		}
		isdispose=true;
		
	}
	
	public boolean save()
	{
		try {
			FileOutputStream sa=new FileOutputStream(sourcefile);
			sa.write(editPane.getText().getBytes());
			sa.flush();
			sa.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//��ʾ�������
			JOptionPane.showMessageDialog(null, "File Save Failed!", "ERROR",JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(!issaved)
		{
			issaved=true;
			String t=FilePanel.this.getTitle().getText();
			if(t.charAt(0)=='*')
			{
				FilePanel.this.getTitle().setText(t.substring(1,t.length()));
			}
		}
		return true;
	}
	
	public void saveas(String path)
	{
		File f=new File(path);
		try {
			FileOutputStream sa=new FileOutputStream(f);
			sa.write(editPane.getText().getBytes());
			sa.flush();
			sa.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//��ʾ�������
			JOptionPane.showMessageDialog(null, "File Save Failed!", "ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
		sourcefile=f;
		issaved=true;
		FilePanel.this.getTitle().setText(sourcefile.getName());
	}
	
	public void showLineNumber(boolean b)
	{
		lineNumber.setVisible(b);
	}

	//getter and setter
	public JTextPane getEditPane() {
		return editPane;
	}
	
	public File getSourcefile() {
		return sourcefile;
	}

	public boolean isshowline()
	{
		return lineNumber.isVisible();
	}
	
	public boolean issaved()
	{
		return issaved;
	}
	
	public boolean iscompiled()
	{
		return iscompiled;
	}
	
	public void setcompiled(boolean b)
	{
		iscompiled=b;
	}
}
