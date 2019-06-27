package gui;

import interfaces.PagePanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import tools.HannaTheme;
import tools.Tool;

public class ConsolePanel extends PagePanel{
	//Pane����
	private JTextPane showPane;
	private JTextField editField;
	private JPanel scrollPanel;
	private JScrollPane editscrollPane;
	//��ť����
	private JButton stopBtn;
	//�����¼�����
	private KeyAdapter enterListener;
	//��������
	private LinkedList<String> buff;
	private Scanner scan;
	
	private Process process;
	private boolean isrunning;
	
	//reader
	private BufferedReader reader;
	private boolean isreader;
	public PipedOutputStream pos;
	
	public Thread readingThread;
	
	//writer
	private BufferedWriter writer;
	private boolean iswriter;
	private PipedInputStream pis;
	
	
	/**
	 * Create the panel.
	 */
	public ConsolePanel() {
		super();
		//����EditPanel����
		
		this.setLayout(new BorderLayout());
		
		//��ʼ��scrollpanel
		scrollPanel=new JPanel();
		//���ò���Ϊ�ޱ߿�
		scrollPanel.setLayout(new BorderLayout(0,0));
		//��ʾPane
		showPane=new JTextPane();
		showPane.setEditable(false);
		showPane.addFocusListener(new FocusAdapter(){

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				//����ת��
				editField.requestFocus();
			}
			
		});
		//��������
		showPane.setFont(HannaTheme.CONSOLEFONT);
		showPane.setBackground(HannaTheme.CONSOLEBG);
		showPane.setForeground(HannaTheme.CONSOLEFG);
		
		//�༭�ı����ʼ��
		editField=new JTextField();
		editField.setFont(HannaTheme.CONSOLEFONT);
		editField.setBorder(null);
		editField.setLayout(new FlowLayout(FlowLayout.RIGHT));
		editField.setBorder(new EmptyBorder(10,10,10,10));
		editField.setBackground(HannaTheme.CONSOLEBG);
		editField.setForeground(HannaTheme.CONSOLEFG);
		
		//��ӹرս��̰�ť
		stopBtn=Tool.addButton(editField, HannaTheme.STOPBTNSIZE, "STOP Running", HannaTheme.STOPBTN_NORMAL_PATH,HannaTheme.STOPBTN_OVER_PATH,HannaTheme.STOPBTN_PRESSED_PATH, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//�رս���
				destroy();
			}
			
		});
		stopBtn.setVisible(false);
		
		//�����հ�ť
		Tool.addButton(editField, HannaTheme.CLEARBTNSIZE, "Clear Console", HannaTheme.CLEARBTN_NORMAL_PATH,HannaTheme.CLEARBTN_OVER_PATH,HannaTheme.CLEARBTN_PRESSED_PATH, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//��տ���̨
				clear();
			}
			
		});
		
		//��ӽ�scrollpanel
		scrollPanel.add(showPane,BorderLayout.CENTER);
		scrollPanel.add(editField,BorderLayout.SOUTH);
		
		
		//��ӽ�scrollpane
		editscrollPane=new JScrollPane(scrollPanel);
		editscrollPane.getVerticalScrollBar().setUnitIncrement(20);
		this.add(editscrollPane,BorderLayout.CENTER);
		
		//���ݳ�ʼ��
		buff=new LinkedList<String>();
		isreader=false;
		iswriter=false;
		isrunning=false;
		
		//�ӹܿ���̨���
		takeoverReaderControl();
		
		//�����¼���ʼ��
		enterListener=new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if(e.getKeyChar()==KeyEvent.VK_ENTER)
				{
					//��ӻ���
					buff.addLast(editField.getText());
					//���jtextfield
					editField.setText("");
				}
			}				
		};
		
		//�رտ���̨����
		setEditable(false);
		
		//��ջ���
		buff.clear();
	}
	
	public void appendText(String str)
	{
		showPane.setText(showPane.getText()+str);
//**������������һ��
		editscrollPane.getVerticalScrollBar().setValue(editscrollPane.getVerticalScrollBar().getMaximum());
	}
	
	public void appendlnText(String str)
	{
		showPane.setText(showPane.getText()+str+"\n");
//**������������һ��
		editscrollPane.getVerticalScrollBar().setValue(editscrollPane.getVerticalScrollBar().getMaximum());
	}
	
	//��ȡ����̨Ȩ��
	public void takeoverControl()
	{
		takeoverWriterControl();
		takeoverReaderControl();
	}
	
	//�ӹܿ���̨����
	public void takeoverWriterControl()
	{
		if(iswriter)
		{
			retakeWriterControl();
			return;
		}
		//���±���
		iswriter=true;
		
		//���ùܵ�����
		pis=new PipedInputStream();

		//�ӹ�ϵͳ����
		System.setIn(pis);
		
		try {
			writer=new BufferedWriter(new OutputStreamWriter(new PipedOutputStream(pis)));			
		} catch (IOException e) {
			// TODO Auto-generated catch block
//appendlnText("Take Over System Input Failed.");
		}
	}
	
	//��������̨����Ȩ��
	public void giveupWriterControl()
	{
		iswriter=false;
		try {
			if(pis!=null)
			{
				pis.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.setIn(null);
	}
	
	//���»�ȡ����̨����Ȩ��
	public void retakeWriterControl()
	{
		giveupWriterControl();
		takeoverWriterControl();
	}
	
	//�ӹܿ���̨���
	public void takeoverReaderControl() {
//appendlnText("TakeOverReaderControl isreader="+isreader);
		//�Ѿ��ӹ�
		if(isreader)
		{
			retakeReaderControl();
			return;
		}
		isreader=true;
		
		pos=new PipedOutputStream();
		
		//�ӹ�ϵͳ���
		System.setOut(new PrintStream(pos,true));

		//�ӹܴ�����Ϣ
		System.setErr(new PrintStream(pos,true));
		
		try {
			reader=new BufferedReader(new InputStreamReader(new PipedInputStream(pos)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
//appendlnText("Take Over System Output Failed.");
		}
		
		
		if(readingThread==null||!readingThread.isAlive())
		{
			readingThread=new Thread(new Runnable(){
				//�������
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String line="";
					try {
//appendlnText("Start Running Thread isreader="+isreader);
						while(isreader&&(line=reader.readLine())!=null)
						{
							appendlnText(line);
							Thread.sleep(20);
						}
//appendlnText("Reader Close");
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//appendlnText(e.getMessage()+"-! isreader="+isreader+" line="+line+" reader="+reader);
						retakeReaderControl();
//appendlnText("Reader Failed");
					}
				}});
			readingThread.start();
		}
	}
	
	//��������̨���Ȩ��
	public void giveupReaderControl()
	{
//appendlnText("GiveUpReaderControl isreader="+isreader);
		isreader=false;
		try {
			if(pos!=null)
			{
				pos.flush();
				pos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.setOut(null);
		System.setErr(null);
	}
	
	//���»�ȡ����̨���Ȩ��
	public void retakeReaderControl()
	{
//appendlnText("ReTakeOverReaderControl isreader="+isreader);
		giveupReaderControl();
		takeoverReaderControl();
	}
	
	//ǿ�ƹرս���
	public void destroy()
	{
		if(process==null)
		{
			isrunning=false;
			//�رտ���̨����
			setEditable(false);
			
			//���ذ�ť
			stopBtn.setVisible(false);
			return;
		}
		process.destroy();
	}
	
	//��տ���̨
	public void clear()
	{
		showPane.setText("");
		editField.setText("");
		buff.clear();
	}
	
	//getter and setter
	public String getNextLine()
	{
		return buff.pollFirst();
	}
	
	public void setEditable(boolean b)
	{
		editField.setEnabled(b);
		buff.clear();
		if(b)
		{
			//���ظ���Ӽ����¼�
			KeyListener[] listeners=editField.getKeyListeners();
			for(int i=0;i<listeners.length;i++)
			{
				if(listeners[i].equals(enterListener))
				{
					return;
				}
			}
			editField.addKeyListener(enterListener);
		}
		else
		{
			editField.removeKeyListener(enterListener);
		}
		
	}
	
	public String getText()
	{
		return showPane.getText();
	}
	
	public String getInputText()
	{
		return editField.getText();
	}
	
	public boolean isReading()
	{
		return isreader;
	}
	
	public boolean isRunning()
	{
		return isrunning;
	}
	
	public Process getProcess()
	{
		return process;
	}
	
	public void setProcess(Process process)
	{
		this.process=process;
		isrunning=true;
		
		//����̨���
		setEditable(true);
		
		//��ʾ��ť
		stopBtn.setVisible(true);
//appendlnText("Process Recieved");
		//�ȴ��߳̽��������ñ�־
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					ConsolePanel.this.process.waitFor();
//appendlnText("Process D");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Programm Running Failed", "ERROR",JOptionPane.ERROR_MESSAGE);
				}
				isrunning=false;
				ConsolePanel.this.process=null;
				
				buff.addLast("\r\n");//��ӽ�����
				
				//�رտ���̨���벢���
				editField.setText("");
				setEditable(false);
				
				//���ذ�ť
				stopBtn.setVisible(false);
			}
			
		}).start();
	}
	
}
