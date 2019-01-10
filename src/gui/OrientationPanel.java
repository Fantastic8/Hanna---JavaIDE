package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.OriPage;
import tools.Global;
import tools.HannaTheme;
import tools.Tool;

public class OrientationPanel extends JPanel {
//��Ա������������
	//Panel����
	private JPanel switchPanel;
	private JPanel pagePanel;
	
	//Icon ����
	private ImageIcon switcherselectedicon;
	private ImageIcon switcherunselectedicon;
	
	//layout����
	private CardLayout cards;
	
	//��������
	private LinkedList<OriPage> pages;
	private OriPage selectedpage;

	/**
	 * Create the panel.
	 */
	public OrientationPanel() {
		super();
		//���ҳ�ʼ��
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout(5, 0));
		
		//���ݳ�ʼ��
		pages=new LinkedList<OriPage>();
		
		//Panel��ʼ��
		switchPanel=new JPanel();
		switchPanel.setLayout(new FlowLayout(FlowLayout.LEADING,5,0));
		this.setBorder(new EmptyBorder(0, 0, 0, 0));
		switchPanel.setBackground(HannaTheme.SWITCHERPANELBG);
		this.add(switchPanel,BorderLayout.NORTH);
		
		//icon ��ʼ��
		switcherselectedicon=new ImageIcon(OrientationPanel.class.getResource(HannaTheme.SWITCHERSELECTED_PATH));
		switcherunselectedicon=new ImageIcon(OrientationPanel.class.getResource(HannaTheme.SWITCHERUNSELECTED_PATH));
		
		cards=new CardLayout();
		pagePanel=new JPanel();
		pagePanel.setLayout(cards);
		this.add(pagePanel,BorderLayout.CENTER);
	}
	
	//���ҳ��
	public void addPage(final OriPage op,int location)
	{
		//����δ��ѡ��Ĭ����ɫ
		if(selectedpage==null)
		{
			selectedpage=op;
			op.getPageLabel().setIcon(switcherselectedicon);
			op.getPageLabel().setForeground(HannaTheme.SWITCHERSELECTED_FONTC);
			selectEvent(op);
		}
		else
		{
			op.getPageLabel().setIcon(switcherunselectedicon);
			op.getPageLabel().setForeground(HannaTheme.SWITCHERUNSELECTED_FONTC);
		}
		
		//������ѡ����Ӧ�¼�
		op.getPageLabel().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				//ѡ���¼�
				selectEvent(op);
			}
		});
		
		//��ӹر��¼�
		//��ӹرհ�ť
		op.getPageLabel().setLayout(new FlowLayout(FlowLayout.RIGHT));
		Tool.addButton(op.getPageLabel(), HannaTheme.CLOSEBTNSIZE,(int)(op.getPageLabel().getPreferredSize().getWidth()-25),5,"Close", HannaTheme.CLOSEBTN_NORMAL_PATH, HannaTheme.CLOSEBTN_OVER_PATH, HannaTheme.CLOSEBTN_PRESSED_PATH, new MouseAdapter(){

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				deleteEvent(op);
			}
		});
		//����opλ��
		op.setLocation(location);
		//���л���ť�����pages��
		pages.add(op);
		//���л������ӵ��л�panel��
		switchPanel.add(op.getPageLabel());
		//��ҳ�������ӵ���Ƭ������
		pagePanel.add(op.getPageLabel().getText(),op.getComp());
		
		//�������¼�
		op.getComp().openEvent();
		
		//�������
		this.paintComponents(getGraphics());
	}
	
	public void showPage(final OriPage op,int location)
	{
		//����δ��ѡ��Ĭ����ɫ
		if(selectedpage==null)
		{
			selectedpage=op;
			op.getPageLabel().setIcon(switcherselectedicon);
			op.getPageLabel().setForeground(HannaTheme.SWITCHERSELECTED_FONTC);
			selectEvent(op);
		}
		else
		{
			op.getPageLabel().setIcon(switcherunselectedicon);
			op.getPageLabel().setForeground(HannaTheme.SWITCHERUNSELECTED_FONTC);
		}
		
		op.setLocation(location);
		//���л���ť�����pages��
		pages.add(op);
		//���л������ӵ��л�panel��
		switchPanel.add(op.getPageLabel());
		//��ҳ�������ӵ���Ƭ������
		pagePanel.add(op.getPageLabel().getText(),op.getComp());
		
		//�������¼�
		op.getComp().openEvent();
		
		//�������
		this.paintComponents(getGraphics());
	}
	
	//ɾ��ҳ���¼�
	public void deleteEvent(OriPage page)
	{		
		//�ж��Ƿ�Ϊ��ѡ���
		if(selectedpage==page)
		{
			//Ĭ��ѡ�е�һ��
			if(pages.size()-1>0)
			{
				selectEvent(pages.get(0));
			}
			else
			{
				selectedpage=null;
			}
		}
		
		//��������
		page.setLocation(-1);

		//ɾ�����
		pages.remove(page);
		switchPanel.remove(page.getPageLabel());
		pagePanel.remove(page.getComp());
		
		//û��ҳ����Զ�ɾ��
		if(pages.size()<=0)
		{
			Global.HANNA.getContentPane().remove(OrientationPanel.this);
			Global.HANNA.repaint();
		}
		
		//�����ر��¼�
		page.getComp().closeEvent();
		
		//�������
		this.paintComponents(getGraphics());
	}
	
	//��ʾҳ��
	public void showPage(String title)
	{
		cards.show(pagePanel, title);
		repaint();
	}
	
	//ҳ��ѡ���¼�
	public void selectEvent(OriPage page)
	{		
		//����δ��ѡ��
		if(selectedpage!=null)
		{
			selectedpage.getPageLabel().setIcon(switcherunselectedicon);
			selectedpage.getPageLabel().setForeground(HannaTheme.SWITCHERUNSELECTED_FONTC);
		}
		//���ñ�ѡ��
		selectedpage=page;
		page.getPageLabel().setIcon(switcherselectedicon);
		page.getPageLabel().setForeground(HannaTheme.SWITCHERSELECTED_FONTC);
		if(page.getPagetype()==Global.TYPECODE&&!((FilePanel)page.getComp()).issaved()&&page.getPageLabel().getText().indexOf('*')==0)
		{
			//ȥ��*
			showPage(page.getPageLabel().getText().substring(1,page.getPageLabel().getText().length()));
		}
		showPage(page.getPageLabel().getText());
	}
	
	//ͨ��titleѡ��ҳ��
	public void selectPage(String title)
	{
		Iterator itr=pages.iterator();
		OriPage page;
		while(itr.hasNext())
		{
			page=(OriPage)itr.next();
			if(page.getPageLabel().getText().equals(title))
			{
				selectEvent(page);
				return;
			}
		}
	}
	
	//��д�����������
	@Override
	public void paintComponents(Graphics g)
	{
		super.paintComponents(g);
		
		Iterator itr=pages.iterator();
		OriPage page;
		switchPanel.removeAll();
		while(itr.hasNext())
		{
			page=(OriPage)itr.next();
			switchPanel.add(page.getPageLabel());
		}
	}

	//getter and setter
	public LinkedList<OriPage> getPages() {
		return pages;
	}

	public OriPage getSelectedpage() {
		return selectedpage;
	}
}
