package client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class Login implements ActionListener,Runnable{
	JFrame jflogin = null;
	JPanel jp1 = null,jp2 = null;
	Label inputusrname = null;
	TextField usrname;
	Button login,cancel;
	Hashtable listTable; // ��������������ǳƵ�ɢ�б�
	String name = null;
	
	Socket socket;
	Thread thread = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	
	ChatArea ca = null;
	
	boolean namevalid = false;
	
	public Login(Hashtable listTable){
		jflogin = new JFrame("�û���¼");
		inputusrname = new Label("�������û���:",Label.LEFT);
		usrname = new TextField(30);
		login = new Button("��¼");
		cancel = new Button("ȡ��");
		login.addActionListener(this);
		cancel.addActionListener(this);
		thread = new Thread(this);
		this.listTable = listTable;
		
		jp1 = new JPanel();
		jp2 = new JPanel();
		jp1.add(inputusrname);
		jp1.add(usrname);
		jp2.add(login);
		jp2.add(cancel);
		
		jflogin.setBounds(300, 400, 300, 200);
		
		jflogin.setLayout(new GridLayout(2, 1));
		
		jflogin.add(jp1);
		jflogin.add(jp2);
		jflogin.setVisible(true);
		jflogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setSocketConnection(Socket socket, DataInputStream in, DataOutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		try {
			thread.start();
		} catch (Exception e) {
			usrname.setText(" " + e);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		int i;
		name = usrname.getText();
		if (e.getSource() == login) {
			i = JOptionPane.showConfirmDialog(null, "ȷ�ϵ�¼��?","��ʾ",JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION){
				if (name.length() == 0){
					JOptionPane.showMessageDialog(null, "�û�������Ϊ��","��ʾ", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					if (socket != null) {
						try {	// ���û���Ϣ���͸���������
							out.writeUTF("#�ǳ�:" + name);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
//					while(!namevalid){
//						//System.out.println("hhhhhhhhh");
//					}
//					System.out.println("ggggggg");
//					ca = new ChatArea(name,listTable,450,480);
//					
//					ca.setSocketConnection(socket, in, out);
				/*	
					if (listTable == null){
						namevalid = true;
						JOptionPane.showMessageDialog(null, "��¼�ɹ�!��ӭ��," + name, "��ʾ",JOptionPane.INFORMATION_MESSAGE);
						this.jflogin.setVisible(false);
						try {
							out.writeUTF("#�ǳ�:");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					} 
					else {
						if (listTable.containsKey(name)) {
							namevalid = false;
							JOptionPane.showMessageDialog(null,"�û����Ѵ���,����������!", "��ʾ",JOptionPane.INFORMATION_MESSAGE);
						} 
						else {
							namevalid = true;
							JOptionPane.showMessageDialog(null, "��¼�ɹ�!��ӭ��," + name, "��ʾ",JOptionPane.INFORMATION_MESSAGE);
							this.jflogin.setVisible(false);
							try {
								out.writeUTF("#�ǳ�:");
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					*/
				}
			}
			else if (i == JOptionPane.NO_OPTION){
				
			}
						
		}
		else if (e.getSource() == cancel) {
			try {
				out.writeUTF("�û��뿪:");
			} catch (IOException ee) {
			}
			//��δ�������ӣ���ֻ��رմ���
			this.jflogin.setVisible(false);
			thread.stop();
		}
		
	}

	public void windowClosing(WindowEvent e) 
	{ 
		try {
			out.writeUTF("�û��뿪:");
		} catch (IOException ee) {
		}
		//��δ�������ӣ���ֻ��رմ���
		this.jflogin.setVisible(false);
		thread.stop();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		String msg = null;
		while(true){
			if (in != null){
				try {
					msg = in.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					JOptionPane.showMessageDialog(null, "��������������ѶϿ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
			if (msg != null){
				if (msg.startsWith("���Կ�ʼ������:")){
					namevalid = true;
					JOptionPane.showMessageDialog(null, "��¼�ɹ�!��ӭ��," + name, "��ʾ", JOptionPane.INFORMATION_MESSAGE);
					//this.jflogin.setVisible(false);
					this.jflogin.dispose();
					
					ca = new ChatArea(name,listTable,450,480);
					ca.setSocketConnection(socket, in, out);
					break;
				}
				else if (msg.startsWith("������:")){
					namevalid = false;
					String people = msg.substring(msg.indexOf(":") + 1); // ��Ŀǰ���ߵ��������ǳ���ӵ�ɢ�б���
					listTable.put(people, people);
					
				}
				else if (msg.startsWith("�ǳ��Ѵ���:")){
					namevalid = false;
					JOptionPane.showMessageDialog(null, "�û����Ѵ���,����������!","��ʾ",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		
		
	}
	
	public String getName(){
		return this.name;
	}
	
	public Hashtable getPeoplelist(){
		return listTable;
	}
	
}


