package gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import data.FileNode;

/**
 * ��дDefaultTreeCellRenderer
 * �ػ�ÿ���ڵ�Ԫ�ص���ʾ��ʽ������ʾ��ͼ����ϵͳ��ʾ��ͼ��
 * 
 */
public class TreeRenderer extends DefaultTreeCellRenderer {
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		FileNode filenode = (FileNode) node.getUserObject();
		if(filenode.file.isDirectory()){
			leaf = false;
		}
		//ȥ���ڵ�����
		hasFocus = false;
//		setForeground(Color.RED);
//	    setTextSelectionColor(Color.black);
		//����ѡ��ʱ�ı�����ɫ
	    setBackgroundSelectionColor(new Color(205, 232, 255));
	    setBackgroundNonSelectionColor(Color.white);
        // ִ�и���ԭ�Ͳ���
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        //������ʾ��ͼ��Ϊϵͳ��ʾ��ͼ��
		setIcon(fileSystemView.getSystemIcon(filenode.file));
		//������ʾ������
		setText(filenode.file.getName());
		
		return this;
	}
	
}
