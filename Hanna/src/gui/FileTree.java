package gui;

import interfaces.FileEvents;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.*;

import data.FileNode;
import gui.TreeRenderer;
/**
 * �Զ����JTree�ؼ�����Ҫ�����������ļ���
 */
public class FileTree extends JTree {
	JTree jtree;
	DefaultMutableTreeNode root;
	private DefaultTreeModel dt;
	
	private FileEvents FE;
	/**
	 * FileTree������
	 */
	public JScrollPane spanel;
	/**
	 * FileTree�Ĺ��캯��
	 * @param dir String����Ŀ¼��·��
	 * @param panel FileTree������
	 * @param isRootShow �Ƿ���Ҫ��ʾ���ڵ�
	 */
	public FileTree(String dir,final JScrollPane panel,final boolean isRootShow,FileEvents fe){
		FE=fe;
		//������·����File����
		File file = new File(dir);
		root=new DefaultMutableTreeNode(new FileNode(file));
		//����������Ϣ�������Ŀؼ�������Ӧ����
		spanel = panel;
		//�����������ļ���Ϊ���ڵ���ļ���
		createTree(root,file);
		//����JTree
		dt=new DefaultTreeModel(root);
		jtree=new JTree(dt);
		//Sets the TreeCellRenderer that will be used to draw each cell. 
		jtree.setCellRenderer(new TreeRenderer());
		//����ǲ���Ҫ��ʾ���ڵ�ľ������ļ����ĸ��ڵ�
		if(!isRootShow){
			jtree.setRootVisible(false);
		}
		//��ӵ�������
		panel.setViewportView(jtree);
		
		//����ļ������������¼�
		jtree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//�����˫����Ӷ���
				if(e.getClickCount()==2){
					//��ȡ�������λ�õ�TreePath
					TreePath path = jtree.getPathForLocation(e.getX(), e.getY()); // �ؼ������������ʹ��
				 	//path��null˵�������λ�ò������ļ�����
					if (path == null) {
				 		return;
				 	}
					//������ѡ�񣨵�����Ľڵ�DefaultMutableTreeNode
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
					//��øýڵ���ļ�File
					FileNode f = (FileNode)node.getUserObject();
					//�����һ���ļ����Ͱ�����ʾ���ı���ʾ����û�н��о����ļ����͵��жϣ�
					if(f.file.isFile()){
						//���ļ��¼�
						FE.FileOpen(f.file);
					}else{
						//������ļ��У������ǲ���Ҫ��ʾ���ڵ���ļ���
						if(!isRootShow){
							//�͸ı���ļ����ĸ��ڵ㣬���ֻ��ʾ��Ŀ¼�µ��ļ�����ʽ
							dt.setRoot(node);
						}
					}
				}
				super.mouseClicked(e);
			}
			//������갴���¼�
			@Override
			public void mousePressed(MouseEvent e) {
				//��ȡ�������λ�õ�TreePath
				final TreePath path = jtree.getPathForLocation(e.getX(), e.getY()); // �ؼ������������ʹ��
				if(path==null){
					if (e.getButton() == 3) {
				 		//�½��Ҽ������Ĳ˵�����
				 		JPopupMenu popupMenu = new JPopupMenu();
				 		//�½�ˢ�²˵���
						JMenuItem menuItem1 = new JMenuItem("Refresh");
						//����ˢ�²˵���Ĵ�������
						menuItem1.addActionListener(new ActionListener() {
							//ˢ�²�����ˢ��������ļ���������ļ���Ϣ
							@Override
							public void actionPerformed(ActionEvent e) {
						 		//�ؽ��ýڵ��µ��ļ���
								root.removeAllChildren();
								createTree(root, ((FileNode)root.getUserObject()).file);
								((FileNode)root.getUserObject()).isInit = false;
								// ֪ͨģ�ͽڵ㷢���仯
								DefaultTreeModel treeModel1 = (DefaultTreeModel) getModel();
								treeModel1.nodeStructureChanged(root);
								//ʹ��DefaultTreeModel��reload��������ˢ��
								dt.reload();
							}
						});
						//�ѽ��õĲ˵�����ӵ������˵���
						popupMenu.add(menuItem1);
						//���һ���λ����ʾ�õ����˵�
						popupMenu.show(jtree, e.getX(), e.getY());
					}
				}else{
					//���øýڵ��·��Ϊѡ��״̬�������Ͽ���ȥ����δ���Թ���
				 	jtree.setSelectionPath(path);
				 	//������ѡ�񣨵�����Ľڵ�DefaultMutableTreeNode
				 	final DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
				 	//��øýڵ���ļ�File
				 	final FileNode f = (FileNode)node.getUserObject();
					//��������һ��Ķ���
				 	if (e.getButton() == 3) {
				 		//�½��Ҽ������Ĳ˵�����
				 		JPopupMenu popupMenu = new JPopupMenu();
				 		//�½�ˢ�²˵���
						JMenuItem menuItem1 = new JMenuItem("Refresh");
						//����ˢ�²˵���Ĵ�������
						menuItem1.addActionListener(new ActionListener() {
							//ˢ�²�����ˢ��������ļ���������ļ���Ϣ
							@Override
							public void actionPerformed(ActionEvent e) {
						 		//�ؽ��ýڵ��µ��ļ���
								node.removeAllChildren();
								createTree(node, f.file);
								f.isInit = false;
								//ʹ��DefaultTreeModel��reload��������ˢ��
								dt.reload(node);
							}
						});
						//�½�ɾ���˵����û������ɾ��ǰ�����Ѳ�����
						JMenuItem menuItem2 = new JMenuItem("Delete");
						//ɾ���˵������¼�
						menuItem2.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int res = JOptionPane.showConfirmDialog(null,
										"Are you sure you want to delete this file?", "Warning",
										JOptionPane.YES_NO_OPTION);
								if (res == JOptionPane.YES_OPTION) {
									//����ýڵ����ļ��ͽ���ɾ������
									if(f.file.isFile()){
										f.file.delete();
										node.removeFromParent();
										// ֪ͨģ�ͽڵ㷢���仯
										DefaultTreeModel treeModel1 = (DefaultTreeModel) getModel();
										treeModel1.nodeStructureChanged(node);
										dt.reload(node);
									}else{
										deleteDir(f.file);
									}
								} else {
									return;
								}
								//TODO �����ļ��е�ɾ������
							}
						});
						JMenuItem menuItem3 = new JMenuItem("New Package");
						menuItem3.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if(f.file.isDirectory()){
									String inputValue = JOptionPane.showInputDialog("Package Name:");
									if(inputValue!=null){
										inputValue.trim();
										if(!inputValue.isEmpty()){
										String dir = f.file.getAbsolutePath();
										inputValue += '.';
											for(String str:inputValue.split("\\.")){
												if(!dir.endsWith(File.separator)){
													dir += File.separator;
												}
												dir += str;
												File newFile = new File(dir);
												if(!newFile.exists()){
													if(!newFile.mkdirs()){
														JOptionPane.showMessageDialog(panel, "Fail,please check the package name.");;
													}
												}
											}
										}
									}
								}
							}
						});
						//�ѽ��õĲ˵�����ӵ������˵���
						popupMenu.add(menuItem1);
						popupMenu.add(menuItem2);
						if(f.file.isDirectory()){
							popupMenu.add(menuItem3);
						}
						//���һ���λ����ʾ�õ����˵�
						popupMenu.show(jtree, e.getX(), e.getY());
				 	}
				}
				super.mousePressed(e);
			}
		});
		jtree.addTreeWillExpandListener(new TreeWillExpandListener() {
			
			@Override
			public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) event
						.getPath().getLastPathComponent();
				FileNode fileNode = (FileNode) node.getUserObject();
				if(!fileNode.isInit){
					node.removeAllChildren();
					createTree(node,fileNode.file);
					// ֪ͨģ�ͽڵ㷢���仯
					DefaultTreeModel treeModel1 = (DefaultTreeModel) getModel();
					treeModel1.nodeStructureChanged(node);
				}
				// ���ı�ʶ�������ظ�����
				fileNode.isInit = true;
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event)
					throws ExpandVetoException {
				
			}
		});
		
	}
	
	/*
     * �ݹ�ɾ��Ŀ¼�µ������ļ�����Ŀ¼�������ļ�
     * @param dir ��Ҫɾ�����ļ�Ŀ¼
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //�ݹ�ɾ��Ŀ¼�е���Ŀ¼��
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // Ŀ¼��ʱΪ�գ�����ɾ��
        return dir.delete();
    }
	
	/*
	 * �����ļ�����������ڵ�͸��ڵ��File�������Ϳ��Դ����Ըýڵ�Ϊ���ڵ���ļ���
	 */
	private void createTree(DefaultMutableTreeNode root,File file){
		File[] ff=file.listFiles();
		//�ж��Ƿ�Ϊ��
		if(ff==null)
		{
			return;
		}
		for(File f:ff){
			DefaultMutableTreeNode newNode = createNode(root, f);
			if(f.isDirectory()){
				//���˵���'.'��ͷ���ļ���
				if(!f.getName().startsWith(".")){
					//�ݹ�����ļ����µ������ļ��к��ļ���ͬʱ�����ļ���
					File[] fl = f.listFiles();
					if(fl!=null)
					for(File fle:fl){
						createNode(newNode, fle);
					}
				}
			}
		}
	}
	
	/*
	 * �����ڵ����һ���µĽڵ�
	 * file �¸��ڵ���ļ�
	 */
	private DefaultMutableTreeNode createNode(DefaultMutableTreeNode root,File file){
		//�������ļ����µ������ļ�
		if(file==null)
		{
			return null;
		}
		DefaultMutableTreeNode newNode=null;
		//���˵���'.'��ͷ���ļ��к��ļ�
//					if(!f.getName().startsWith(".")){
//						//�ļ�������ӵ�Ҫ��ʹ���һ���µĽڵ�
//						newNode = new DefaultMutableTreeNode(f);
//					}
		if(file.isDirectory()){
			//���˵���'.'��ͷ���ļ���
			if(!file.getName().startsWith(".")){
				//�ļ�������ӵ�Ҫ��ʹ���һ���µĽڵ�
				newNode = new DefaultMutableTreeNode(new FileNode(file));
			}
		} else if (file.isFile() && file.getName().endsWith(".java")) {// �����ã������̨��ӡ���е��ļ�·��
			if (!file.getName().startsWith(".")) {
				// �ļ�������ӵ�Ҫ��ʹ���һ���µĽڵ�
				newNode = new DefaultMutableTreeNode(new FileNode(file));
			}
		}
		// ���newNode��Ϊnull���ͰѸýڵ���ӵ����ĸ��ڵ���ȥ
		if (newNode != null) {
			root.add(newNode);
		}
		return newNode;
	}
		  
	/**
	 * ����ļ�����DefaultTreeModel
	 * @return ���ļ�����DefaultTreeModel
	 */
	public DefaultTreeModel getDefTreeModel(){
		return dt;
	}
	
	/**
	 * ���ô�����ϵ�����������ļ���֮����л�������������ļ������ĳ���ļ��У����ļ�����ʾ���ļ����µ������ļ�
	 * @param tree ��Ҫ���������ļ���
	 */
	public void setChild(final FileTree tree){
		//���JTree ����굥���¼���ÿ�ε��������ļ�����ʾ��������ļ����µ������ļ�
		jtree.addMouseListener(new MouseAdapter() { 
			//����������¼�����
			@Override
			public void mouseClicked(MouseEvent e) {
				//�ж��Ƿ��ǵ���
				if(e.getClickCount()==1){
					//��ȡ�������λ�õ�TreePath
					TreePath path = jtree.getPathForLocation(e.getX(), e.getY()); // �ؼ������������ʹ��
				 	//path��null˵�������λ�ò������ļ�����
					if (path == null) {
				 		return;
				 	}
					//������ѡ�񣨵�����Ľڵ�DefaultMutableTreeNode
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree.getLastSelectedPathComponent();
					//��øýڵ���ļ�File
					FileNode f = (FileNode)node.getUserObject();
					//������ļ���һ��Ŀ¼
					if(f.file.isDirectory()){
						//�͸ı����ļ����ĸ��ڵ㣬�����ļ���ֻ��ʾ���ļ����µ��ļ�
						tree.getDefTreeModel().setRoot(node);
					}
				}
				super.mouseClicked(e);
			}
		});
	}
	
	/**
	 * �ı��ļ����ĸ��ڵ�
	 * @param dir �µ��ļ�·��
	 */
	public void setRoot(String dir){
		//������·����File����
		File file = new File(dir);
		DefaultMutableTreeNode newroot=new DefaultMutableTreeNode(new FileNode(file));
		//�����������ļ���Ϊ���ڵ���ļ���
		createTree(newroot,file);
		//�ı��ļ����ĸ��ڵ�
		dt.setRoot(newroot);
		dt.reload();
	}
	
}
