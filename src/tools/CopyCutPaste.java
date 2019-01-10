package tools;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JTextPane;

public class CopyCutPaste {
	
	private static JTextPane textPane;
	private Clipboard clipboard;//��ȡϵͳ������
	
	public CopyCutPaste(JTextPane textPane1) {
		this.textPane=textPane1;
		clipboard = textPane.getToolkit().getSystemClipboard();	//��ȡϵͳ������
	}
	
	public CopyCutPaste() {
		this.textPane=null;
		clipboard = null;
	}
	
	public void setTextPane(JTextPane textPane1)
	{
		this.textPane=textPane1;
		clipboard = textPane.getToolkit().getSystemClipboard();	//��ȡϵͳ������
	}
	
	public void Copy(){
		String tempText = textPane.getSelectedText();  //�϶����ѡȡ�ı�
		   
		StringSelection editText =new StringSelection(tempText);	//�����ѡȡ���ı����ݣ���ֵ��editText
		   
		clipboard.setContents(editText,null);			//��editText ��ֵ��ϵͳ������
	}
	
	public void Cut(){
		String tempText = textPane.getSelectedText();  //�϶����ѡȡ�ı�
		   
		StringSelection editText =new StringSelection(tempText);	//�����ѡȡ���ı����ݣ���ֵ��editText
		   
		clipboard.setContents(editText,null);			//��editText ��ֵ��ϵͳ������
		 
		textPane.replaceSelection("");					//�����ѡȡ���ı��������
	}
	
	public void Paste(){
		Transferable contents = clipboard.getContents(this);	//��ȡϵͳ�����������
	    DataFlavor  flavor= DataFlavor.stringFlavor;			//������������� flavor
	    if( contents.isDataFlavorSupported(flavor))				//�ж��Ƿ�Ϊflavor ����
	    {
		    try{
		    	
			    String str;
			    str = (String)contents.getTransferData(flavor);	//ת��ϵͳ����������� ΪString
			    textPane.replaceSelection(str);					//�޸��ı����ݣ�ճ��
		     }catch(Exception ex){
		    	 ex.printStackTrace();
		     }
	     }
	}

}
