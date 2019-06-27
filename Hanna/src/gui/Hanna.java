package gui;

import interfaces.FileEvents;
import interfaces.PagePanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import data.OriPage;
import tools.CopyCutPaste;
import tools.Global;
import tools.HannaTheme;
import tools.Tool;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class Hanna extends JFrame implements ActionListener{
//��Ա������������
	//Panel����
	/**
	 * 0-CENTER
	 * 1-NORTH
	 * 2-SOUTH
	 * 3-WEST
	 * 4-EAST
	 */
	private OrientationPanel[] OriPanels;
	private JPanel contentPane;
	private ArrayList<OriPage> Pages;
	
	//��������
	private int fileOrientation;
	private SimpleDateFormat timeformat;
	private CopyCutPaste CCP;
	private FileTree filetree[];
	final Icon iconMenu[];
	JButton buttonIcon[];
	final String strIconCommand[] = { "New File", "Save", "Save As","Find & Replace", "Compile","Run" };
	
	//�����¼�����
	private FileEvents fileevents;
	
	private final static int INDEX_CONSOLE=0;
	private final static int INDEX_FILEEXPLORER=1;
	

	/**
	 * Launch the application. ������
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Hanna frame = new Hanna();
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame.setVisible(true);
					//����ȫ�ֱ���
					Global.HANNA=frame;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame. ���캯��+�������
	 */
	public Hanna() {
		//����ͼ��
		setIconImage(getToolkit().getImage(getClass().getResource(HannaTheme.HANNALOGO)));
		Hanna.this.setTitle("Hanna IDE");
		//���ݳ�ʼ��
		timeformat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		fileOrientation=Global.ORIENTATIONCENTER;
		CCP=new CopyCutPaste();//����������
		
		
		//����JFrame�Ĵ��ڴ�С
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		
		//�˵���
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newFile();				
			}
		});
		mnFile.add(mntmNew);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//����ֻ��ѡ��Ŀ¼
				int returnVal = chooser.showOpenDialog(contentPane);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				  String selectPath =chooser.getSelectedFile().getPath() ;
				  filetree[0].setRoot(selectPath);
				  filetree[1].setRoot(selectPath);
				}
			}
		});
		mnFile.add(mntmOpen);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				//����
				saveFile(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()));
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmSaveAs = new JMenuItem("Save as");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				saveasFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
			}
		});
		mnFile.add(mntmSaveAs);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmShowLineNumber = new JMenuItem("Show Line Number");
		mntmShowLineNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				FilePanel f=((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
				f.showLineNumber(!f.isshowline());
			}
		});
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				CCP.setTextPane((JTextPane)(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getEditPane()));
				CCP.Copy();
			}
		});
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mntmCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				CCP.setTextPane((JTextPane)(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getEditPane()));
				CCP.Cut();;
			}
		});
		mnEdit.add(mntmCut);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				CCP.setTextPane((JTextPane)(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getEditPane()));
				CCP.Paste();;
			}
		});
		mnEdit.add(mntmPaste);
		mnEdit.add(mntmShowLineNumber);
		
		JMenu mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);
		
		JMenuItem mntmSearchReplace = new JMenuItem("Search Replace");
		mntmSearchReplace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//����Ƿ��п�ִ���ļ���ѡ��
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				new FindReplace((JTextPane)(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getEditPane()));
			}
		});
		mnSearch.add(mntmSearchReplace);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		JMenuItem mntmCompile = new JMenuItem("Compile");
		mntmCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					//����Ƿ��п�ִ���ļ���ѡ��
					if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||OriPanels[fileOrientation].getSelectedpage().getPagetype()!=Global.TYPECODE||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
					{
						//��ʾ��ѡ���ִ���ļ�
						JOptionPane.showMessageDialog(null, "Please select an excutable java source code!", "ERROR",JOptionPane.ERROR_MESSAGE);
						return;
					}
					compileFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
			}
		});
		
		JMenuItem mntmCmd = new JMenuItem("Cmd");
		mntmCmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//��ϵͳĬ��Ŀ¼��ִ��cmd
					final Process process=Runtime.getRuntime().exec("cmd.exe", null, FileSystemView.getFileSystemView().getDefaultDirectory());
					final BufferedWriter outputStream=new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
					final BufferedReader inputStream=new BufferedReader(new InputStreamReader(process.getInputStream()));
					
					//�����̴��ݸ�����̨
					final ConsolePanel console=((ConsolePanel)Pages.get(INDEX_CONSOLE).getComp());
					
					console.setProcess(process);
					
					//�����߳�
					new Thread(new Runnable(){
						String line;
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								while(console.isRunning())
								{
									if((line=inputStream.readLine())!=null)
									{
										cprintln(line);
									}
									//�ӳ�
									Thread.sleep(20);
								}
								//�ر�ͨ��
								inputStream.close();
								//System.out.println("�ر�input");
							} catch (IOException | InterruptedException e) {
								// TODO Auto-generated catch block
								//������ʾ
								JOptionPane.showMessageDialog(null, "CMD Get InputerStream ERROR!", "ERROR",JOptionPane.ERROR_MESSAGE);
							}
//cprintln("�ر�CMD���ͨ��");
						}
					}).start();
					
					//multiple Thread output
					new Thread(new Runnable(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							String temp;
							try {
								while(console.isRunning()&&console.getProcess().equals(process))
								{
									if((temp=console.getNextLine())!=null)
									{
										outputStream.write(temp+"\r\n");
										outputStream.flush();
									}
									//�ӳ�
									Thread.sleep(20);
								}
							} catch (IOException | InterruptedException e) {
								// TODO Auto-generated catch block
								JOptionPane.showMessageDialog(null, "CMD Get OutputerStream ERROR!", "ERROR",JOptionPane.ERROR_MESSAGE);
							}
//cprintln("ouputstreamͨ�����ڹر�");
							//�ر�ͨ��
							try {
								outputStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//cprintln("outputstream��ͨ���ر�");
						}
					}).start();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		mnRun.add(mntmCmd);
		mnRun.add(mntmCompile);
		
		JMenuItem mntmRun = new JMenuItem("Run");
		mntmRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//����Ƿ��п�ִ���ļ���ѡ��
				if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||OriPanels[fileOrientation].getSelectedpage().getPagetype()!=Global.TYPECODE||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
				{
					//��ʾ��ѡ���ִ���ļ�
					JOptionPane.showMessageDialog(null, "Please select an excutable java source code!", "ERROR",JOptionPane.ERROR_MESSAGE);
					return;
				}
				runFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
			}
		});
		mnRun.add(mntmRun);
		
		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);
		
		JMenuItem mntmFileTree = new JMenuItem("File Explorer");
		mntmFileTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPane(Hanna.INDEX_FILEEXPLORER);
			}
		});
		mnWindow.add(mntmFileTree);
		
		JMenuItem mntmDebug = new JMenuItem("Debug");
		mntmDebug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPane(Hanna.INDEX_CONSOLE);
			}
		});
		mnWindow.add(mntmDebug);
		
		JMenu mnHanna = new JMenu("Hanna");
		menuBar.add(mnHanna);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About about=new About();
			}
		});
		mnHanna.add(mntmAbout);
		
		//�����
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 5));
		setContentPane(contentPane);
		
		//��ʼ�����������JPanel
		OriPanels=new OrientationPanel[5];
		for(int i=0;i<5;i++)
		{
			OriPanels[i]=new OrientationPanel();
			OriPanels[i].setBackground(HannaTheme.ORIPANELBG);
			OriPanels[i].setPreferredSize(new Dimension(100,100));
		}
		
		getContentPane().add(OriPanels[2],BorderLayout.SOUTH);
		OriPanels[2].setPreferredSize(new Dimension(100,300));
			
		
		//Pages��ʼ��
		Pages=new ArrayList<OriPage>();
		
		//��ʼ��Consoleҳ��
		ConsolePanel consolePane=new ConsolePanel();
		final OriPage op=new OriPage("Concole", consolePane, Global.TYPECONSOLE, Global.ORIENTATIONSOUTH);
		//������ѡ����Ӧ�¼�
		op.getPageLabel().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				//ѡ���¼�
				OriPanels[op.getLocation()].selectEvent(op);
			}
		});
		
		//��ӹر��¼�
		//��ӹرհ�ť
		op.getPageLabel().setLayout(new FlowLayout(FlowLayout.RIGHT));
		Tool.addButton(op.getPageLabel(), HannaTheme.CLOSEBTNSIZE,(int)(op.getPageLabel().getPreferredSize().getWidth()-25),5,"Close", HannaTheme.CLOSEBTN_NORMAL_PATH, HannaTheme.CLOSEBTN_OVER_PATH, HannaTheme.CLOSEBTN_PRESSED_PATH, new MouseAdapter(){

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				OriPanels[op.getLocation()].deleteEvent(op);
			}
		});
		Pages.add(INDEX_CONSOLE, op);
		//��ʼ��TressFileҳ��
		final PagePanel explorerpanel=new PagePanel();
		explorerpanel.setLayout(new GridLayout(0,1,0,0));
		final JScrollPane filetreepanel1=new JScrollPane();
		final JScrollPane filetreepanel2=new JScrollPane();
		explorerpanel.add(filetreepanel1);
		explorerpanel.add(filetreepanel2);
	
		//��Ӵ��ļ������¼�
		fileevents=new FileEvents(){

			@Override
			public void FileOpen(File f) {
				// TODO Auto-generated method stub
				OpenFile(f);
				//ѡ����ҳ��
				OriPanels[fileOrientation].selectPage(f.getName());
			}

		};
		filetree=new FileTree[2];
		filetree[0]=new FileTree(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath(),filetreepanel1,true,fileevents);
		filetree[1]=new FileTree(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath(),filetreepanel2,false,fileevents);
		//��������
		filetree[0].setChild(filetree[1]);
		
		
		final OriPage optree=new OriPage("File Explorer",explorerpanel,Global.TYPEEXPLORER,Global.ORIENTATIONCENTER);
		//������ѡ����Ӧ�¼�
		optree.getPageLabel().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				//ѡ���¼�
				OriPanels[optree.getLocation()].selectEvent(optree);
			}
		});
		
		//��ӹر��¼�
		//��ӹرհ�ť
		optree.getPageLabel().setLayout(new FlowLayout(FlowLayout.RIGHT));
		Tool.addButton(optree.getPageLabel(), HannaTheme.CLOSEBTNSIZE,(int)(optree.getPageLabel().getPreferredSize().getWidth()-25),5,"Close", HannaTheme.CLOSEBTN_NORMAL_PATH, HannaTheme.CLOSEBTN_OVER_PATH, HannaTheme.CLOSEBTN_PRESSED_PATH, new MouseAdapter(){

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				OriPanels[optree.getLocation()].deleteEvent(optree);
			}
		});
				
		//��scrollpane��ӽ�ҳ��
		Pages.add(INDEX_FILEEXPLORER,optree);

		
		//ToolBar data
		iconMenu = (new Icon[] { new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/new.png"))),//�½���ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/save.png"))),//���水ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/saveas.png"))),//���Ϊ��ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/SearchReplace.png"))),//�����滻��ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/compile.png"))),//���밴ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/run.png"))) });//���а�ť
		
		ImageIcon[] overicon=new ImageIcon[]{
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/new_over.png"))),//�½���ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/save_over.png"))),//���水ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/saveas_over.png"))),//���Ϊ��ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/SearchReplace_over.png"))),//�����滻��ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/compile_over.png"))),//���밴ť
				new ImageIcon(getToolkit().getImage(getClass().getResource("/img/Toolbar/run_over.png")))//���а�ť
		};
		buttonIcon = new JButton[iconMenu.length];
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(HannaTheme.TOOLBARBG);
		contentPane.add(toolBar, BorderLayout.NORTH);
		for (int l = 0; l < iconMenu.length; l++) {
			buttonIcon[l] = new JButton(new ImageIcon(((ImageIcon)(iconMenu[l])).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			buttonIcon[l].setRolloverIcon(new ImageIcon((overicon[l]).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
			buttonIcon[l].setPressedIcon(new ImageIcon(((ImageIcon)(iconMenu[l])).getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH)));
			buttonIcon[l].setOpaque(false);
			buttonIcon[l].setContentAreaFilled(false);
			buttonIcon[l].setBorder(null);
			buttonIcon[l].setActionCommand(strIconCommand[l]);
			buttonIcon[l].setToolTipText(strIconCommand[l]);
			buttonIcon[l].addActionListener(this);
			buttonIcon[l].setFont(HannaTheme.TIPFONT);
			toolBar.add(buttonIcon[l]);
			toolBar.addSeparator();
		}
		//�������������
//		Hanna.this.addMouseMotionListener(new MouseMotionListener(){
//
//			@Override
//			public void mouseDragged(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void mouseMoved(MouseEvent e) {
//				// TODO Auto-generated method stub
//				
//				//���North
//				//���South
//				Point s=getOrientationPanelPosition(Global.ORIENTATIONSOUTH);
//				System.out.println(e.getPoint()+" "+s);
//				if(s!=null&&e.getPoint().getY()<=s.getY()&&e.getPoint().getY()>=s.getY()-5)
//				{
//					Hanna.this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
//				}
//				else
//				{
//					Hanna.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//				}
//				//���East
//				//���West
//			}
//			
//		});




		//����
		showPane(Hanna.INDEX_CONSOLE);
		showPane(Hanna.INDEX_FILEEXPLORER);
	}

	//��ť�ӿ�
	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		// "New File"
		if (s.equals(strIconCommand[0])) {
			newFile();
		}
		// "Save"
		if (s.equals(strIconCommand[1])) {
			if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
			{
				//��ʾ��ѡ���ִ���ļ�
				JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
			//����
			saveFile(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()));
		}
		//"Save As"
		if (s.equals(strIconCommand[2])) {
			if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
			{
				//��ʾ��ѡ���ִ���ļ�
				JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
			saveasFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
		}
		// "Find & Replace"
		if (s.equals(strIconCommand[3])) {
			//����Ƿ��п�ִ���ļ���ѡ��
			if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
			{
				//��ʾ��ѡ���ִ���ļ�
				JOptionPane.showMessageDialog(null, "Please select a file!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
			new FindReplace((JTextPane)(((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getEditPane()));
		}
		//"Compile"
		if (s.equals(strIconCommand[4])) {
			//����Ƿ��п�ִ���ļ���ѡ��
			if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||OriPanels[fileOrientation].getSelectedpage().getPagetype()!=Global.TYPECODE||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
			{
				//��ʾ��ѡ���ִ���ļ�
				JOptionPane.showMessageDialog(null, "Please select an excutable java source code!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
			compileFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
		}
		//"Run"
		if (s.equals(strIconCommand[5])) {
			//����Ƿ��п�ִ���ļ���ѡ��
			if(OriPanels[fileOrientation]==null||OriPanels[fileOrientation].getSelectedpage()==null||(FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()==null||OriPanels[fileOrientation].getSelectedpage().getPagetype()!=Global.TYPECODE||((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp()).getSourcefile()==null)
			{
				//��ʾ��ѡ���ִ���ļ�
				JOptionPane.showMessageDialog(null, "Please select an excutable java source code!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
			runFile((FilePanel)OriPanels[fileOrientation].getSelectedpage().getComp());
		}
		
	}
	
	public OrientationPanel showOrientation(int direction)
	{
		Component[] jcs=getContentPane().getComponents();
		for(int i=0;i<jcs.length;i++)
		{
			if(jcs[i].equals(OriPanels[direction]))
			{
				return OriPanels[direction];
			}
		}
		
		switch(direction)
		{
		case Global.ORIENTATIONCENTER:getContentPane().add(OriPanels[direction],BorderLayout.CENTER);break;
		case Global.ORIENTATIONNORTH:getContentPane().add(OriPanels[direction],BorderLayout.NORTH);break;
		case Global.ORIENTATIONSOUTH:getContentPane().add(OriPanels[direction],BorderLayout.SOUTH);break;
		case Global.ORIENTATIONWEST:getContentPane().add(OriPanels[direction],BorderLayout.WEST);break;
		case Global.ORIENTATIONEAST:getContentPane().add(OriPanels[direction],BorderLayout.EAST);break;
		}
		repaint();
		
		return OriPanels[direction];
	}
	
	//��ʾһ���Զ���Pane��orientation���
	//����ʾ��庯��
	public void showPane(String name,PagePanel panel,int orientation)
	{
		showOrientation(orientation).addPage(new OriPage(name, panel,Global.TYPECUSTOM,orientation),orientation);
		//���½���
		repaint();
	}
	
	public void showPane(int paneindex)
	{
		switch(paneindex)
		{
		case Hanna.INDEX_CONSOLE: showDebugPane();break;
		case Hanna.INDEX_FILEEXPLORER: showFileExplorer();break;
		}
		
		repaint();
	}
	
	//��ʾ�ļ�������
	private void showFileExplorer()
	{
		//�ж��Ƿ��Ѿ���
		if(Pages.get(INDEX_FILEEXPLORER)==null)
		{
			//��ʾ����
			JOptionPane.showMessageDialog(null, "File explorer is currently unavaliable, please relunach programe.", "ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
		//�ж��Ƿ��Ѿ���
		int loc=Pages.get(INDEX_FILEEXPLORER).getLocation();
		if(loc>=0&&OriPanels[loc].getPages().contains(Pages.get(INDEX_FILEEXPLORER)))
		{
			selectPage(Pages.get(INDEX_FILEEXPLORER),loc);
			return;
		}
		showOrientation(Global.ORIENTATIONWEST).showPage(Pages.get(INDEX_FILEEXPLORER),Global.ORIENTATIONWEST);
		OriPanels[Global.ORIENTATIONWEST].setPreferredSize(new Dimension(300,0));
	}

	//�򿪵��������orientation���
	private void showDebugPane()
	{
		if(Pages.get(INDEX_CONSOLE)==null)
		{
			//��ʾ����
			JOptionPane.showMessageDialog(null, "Console is currently unavaliable, please relunach programe.", "ERROR",JOptionPane.ERROR_MESSAGE);
			return;
		}
		//�ж��Ƿ��Ѿ���
		int loc=Pages.get(INDEX_CONSOLE).getLocation();
		if(loc>=0&&OriPanels[loc].getPages().contains(Pages.get(INDEX_CONSOLE)))
		{
			selectPage(Pages.get(INDEX_CONSOLE),loc);
			return;
		}
		//showOrientation(Global.ORIENTATIONSOUTH).addPage(Pages.get(INDEX_CONSOLE),Global.ORIENTATIONSOUTH);
		showOrientation(Global.ORIENTATIONSOUTH).showPage(Pages.get(INDEX_CONSOLE),Global.ORIENTATIONSOUTH);
	}
	
	//���ļ���orientation���
	private void OpenFile(File file)
	{
		//�ж��Ƿ��Ѿ���
		for(int i=0;i<OriPanels.length;i++)
		{
			LinkedList<OriPage> pages=OriPanels[i].getPages();
			Iterator itr=pages.iterator();
			OriPage page;
			while(itr.hasNext())
			{
				page=(OriPage)itr.next();
				if(page.getPagetype()==Global.TYPECODE&&((FilePanel)page.getComp()).getSourcefile().equals(file))
				{
					//ѡ�д�ѡ�
					selectPage(page,i);
					return;
				}
			}
		}
		
		//δ����
		FilePanel codePanel=new FilePanel(file);
		OriPage filepage=new OriPage(file.getName(), codePanel, Global.TYPECODE,fileOrientation);
		showOrientation(fileOrientation).addPage(filepage,fileOrientation);
		
		//���½���
		repaint();
	}
	
	//ͨ��page��orientation ѡ��ѡ�
	public void selectPage(OriPage page,int orientation)
	{
		OriPanels[orientation].selectEvent(page);
	}
	
	//ͨ��pageѡ��ѡ�
	public void selectPage(OriPage page)
	{
		//����page���ĸ�orientation��
		for(int i=0;i<OriPanels.length;i++)
		{
			LinkedList<OriPage> pages=OriPanels[i].getPages();
			Iterator itr=pages.iterator();
			OriPage p;
			while(itr.hasNext())
			{
				p=(OriPage)itr.next();
				if(p.equals(page))
				{
					//ѡ�д�ѡ�
					OriPanels[i].selectEvent(p);
					return;
				}
			}
		}
	}
	
	@Override
	public void repaint()
	{
		super.repaint();
		paintComponents(getGraphics());
	}
	
	/**
	 * Button Replacement Function
	 */
	
	//�½��ļ�
	public void newFile()
	{
		//�����ļ�ѡ���
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);//�����ļ�ģʽ
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//ֻ�ܲ鿴�ļ����ļ���
		//����
		chooser.setDialogTitle("New");
		//��ӿ�ѡ����ļ�����
		chooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if(f.isDirectory()||f.getName().contains(".java"))
				{
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.java";
			}
			
		});
		chooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if(f.isDirectory()||f.getName().contains(".txt"))
				{
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.txt";
			}
			
		});
		
		//��ʾ�½�
		int returnVal = chooser.showSaveDialog(contentPane);
		if(returnVal == JFileChooser.APPROVE_OPTION)//���ȷ��
		{
			if(!chooser.getSelectedFile().isDirectory())
			{
				if(chooser.getSelectedFile().exists())//�����Ի����Ƿ񸲸�
				{
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to rewrite file "+chooser.getSelectedFile().getName()+"?", "WARNING",JOptionPane.YES_NO_OPTION)!=0)//ѡ��
					{
						return;
					}
				}
				//�½��ļ�
				File a=new File(chooser.getSelectedFile().getAbsolutePath());
				try {
					a.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "Create New File Failed!", "ERROR",JOptionPane.ERROR_MESSAGE);
				}
				
				//���ļ�
				OpenFile(a);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Name cannot be empty!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	
	public boolean saveasFile(FilePanel filepanel)
	{
		//�����ļ�ѡ���
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);//�����ļ�ģʽ
		chooser.setCurrentDirectory(filepanel.getSourcefile());//���õ�ǰ�ļ���
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);//ֻ�ܲ鿴�ļ����ļ���
		//����
		chooser.setDialogTitle("Save As");
		//��ӿ�ѡ����ļ�����
		chooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if(f.isDirectory()||f.getName().contains(".java"))
				{
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.java";
			}
			
		});
		chooser.addChoosableFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				if(f.isDirectory()||f.getName().contains(".txt"))
				{
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "*.txt";
			}
			
		});
		
		//��ʾ���Ϊ
		int returnVal = chooser.showSaveDialog(contentPane);
		if(returnVal == JFileChooser.APPROVE_OPTION)//���ȷ��
		{
			if(!chooser.getSelectedFile().isDirectory())
			{
				if(chooser.getSelectedFile().exists())//�����Ի����Ƿ񸲸�
				{
					if(JOptionPane.showConfirmDialog(null, "Are you sure you want to rewrite file "+chooser.getSelectedFile().getName()+"?", "WARNING",JOptionPane.YES_NO_OPTION)!=0)//ѡ��
					{
						return false;
					}
				}
				//�����ļ�
				filepanel.saveas(chooser.getSelectedFile().getAbsolutePath());
				return true;
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Name cannot be empty!", "ERROR",JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return false;
	}
	
	//����
	public boolean saveFile(FilePanel filepanel)
	{
		return filepanel.save();
	}
	
	//Compile function
	public boolean compileFile(FilePanel filepanel)
	{
		//�õ���ѡ�еĿ�ִ���ļ�
		File sourcefile=filepanel.getSourcefile();
		
		//�鿴�Ƿ񱣴�
		if(!filepanel.issaved())
		{
			//�������ڱ���
			if(JOptionPane.showConfirmDialog(null, "File "+sourcefile.getName()+" hasn't saved yet. Do you want to save it now?", "WARNING",JOptionPane.YES_NO_OPTION)!=0)//�жϲ�����
			{
				return false;
			}
			//����
			if(!saveFile(filepanel))
			{
				//����ʧ��
				return false;
			}
		}
		
		//�����־
		cprintlog("Compiling "+sourcefile.getName());
		//�����ļ�
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int flag = compiler.run(null, null, null,sourcefile.getAbsolutePath());
        if(flag==0)
        {
        	cprintln("Compile Success!");
        	//���±���
        	filepanel.setcompiled(true);
        	return true;
        }
        else
        {
        	cprintln("Compile Failed!");
        	return false;
        }
	}
	
	//Run File
	public boolean runFile(FilePanel filepanel)
	{
		
		try {
			if(!filepanel.iscompiled())//û�б����
			{
				if(!compileFile(filepanel))//���벻�ɹ�
				{
					return false;
				}
			}
			
			//�õ���ѡ�еĿ�ִ���ļ�
			File sourcefile=filepanel.getSourcefile();
			
			//��ӡ��־
			cprintlog(" Running "+sourcefile.getName()+"\n\n");
			
	        //�л�����ѡ���ļ���Ŀ¼��ִ�и��ļ�
			final Process process;
			process = Runtime.getRuntime().exec("java "+sourcefile.getName().substring(0,sourcefile.getName().lastIndexOf('.')), null, sourcefile.getParentFile());
			
			final BufferedWriter outputStream=new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			final BufferedReader inputStream=new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			//�����̴��ݸ�����̨
			final ConsolePanel console=((ConsolePanel)Pages.get(INDEX_CONSOLE).getComp());
			
			console.setProcess(process);
			
			//�����߳�
			new Thread(new Runnable(){
				String line;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						//while(console.isRunning()&&console.getProcess().equals(process)&&(line=inputStream.readLine())!=null)
						while((line=inputStream.readLine())!=null)
						{
							cprintln(line);
							//�ӳ�
							//Thread.sleep(20);
						}
						//�ر�ͨ��
//cprintln(console.isRunning()?"true":"false");
//cprintln("�ر�input��");
						inputStream.close();
//cprintln("�ر�input���");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//��ʾ��ѡ���ִ���ļ�
						JOptionPane.showMessageDialog(null, "Running Get InputerStream ERROR!", "ERROR",JOptionPane.ERROR_MESSAGE);
					}
				}
			}).start();
			
			//multiple Thread output
			new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String temp;
					try {
						while(console.isRunning()&&console.getProcess().equals(process))
						{
							if((temp=console.getNextLine())!=null)
							{
								outputStream.write(temp+"\r\n");
								outputStream.flush();
							}
							//�ӳ�
							Thread.sleep(20);
						}
						
						
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Running Get OutputerStream ERROR!", "ERROR",JOptionPane.ERROR_MESSAGE);
					}
					//�ر�ͨ��
//cprintln("�ر�output��");
					try {
						outputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//cprintln("�ر�output���");
				}
				
			}).start();
			
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	
	//����̨���
	public void cprintlog(String message)
	{
		((ConsolePanel)Pages.get(INDEX_CONSOLE).getComp()).appendlnText("\n\n"+timeformat.format(new Date())+"\n"+message);// new Date()Ϊ��ȡ��ǰϵͳʱ��
	}
	
	public void cprint(String str)
	{
		((ConsolePanel)Pages.get(INDEX_CONSOLE).getComp()).appendText(str);
	}
	
	public void cprintln(String str)
	{
		((ConsolePanel)Pages.get(INDEX_CONSOLE).getComp()).appendlnText(str);
	}
	
	//setter and getter
	public Point getOrientationPanelPosition(int direction)
	{
		Component[] jcs=getContentPane().getComponents();
		for(int i=0;i<jcs.length;i++)
		{
			if(jcs[i].equals(OriPanels[direction]))//����
			{
				return OriPanels[direction].getLocation();
			}
		}
		//û����
		return null;
	}
	
	public int getCodeOrientation() {
		return fileOrientation;
	}

	public void setCodeOrientation(int fileOrientation) {
		this.fileOrientation = fileOrientation;
	}

	public OrientationPanel[] getOriPanels() {
		return OriPanels;
	}
	
	
}
