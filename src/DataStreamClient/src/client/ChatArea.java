package client;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ChatArea extends JFrame implements ActionListener,Runnable{
	String name = null; // �����ߵ��ǳ�
	
	Thread threadMessage = null; // ��ȡ��������Ϣ���߳�
	TextArea MsgRecord = null;
	TextField EditMsg = null;
	Button send,exit;
	JPanel jp1=null,jp2=null,jp3=null;
	Label l1 = null,l2 = null;
	
	
	Hashtable listTable; // ��������������ǳƵ�ɢ�б�
	List listComponent = null; // ��ʾ�����������ǳƵĵ�List���
	
	Socket socket = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	
	int width, height; // �������Ŀ�͸�
	
	public ChatArea(String name, Hashtable listTable, int width, int height){
	
		this.name = name;
		this.listTable = listTable;
		this.width = width;
		this.height = height;
		this.setBounds(300, 400, width, height);
		
		threadMessage = new Thread(this);
		MsgRecord = new TextArea(10,10);
		EditMsg = new TextField(28);
		
		
		l1 = new Label("�����¼",Label.LEFT);
		l2 = new Label("������Ա",Label.LEFT);
		
		send = new Button("������Ϣ");
		send.addActionListener(this);
		exit = new Button("�˳�������");
		exit.addActionListener(this);
		
		listComponent = new List();
		this.setTitle(name+",you are chatting!");
		this.setBackground(Color.pink);
		
		Container cont = this.getContentPane();
		cont.setBackground(Color.pink);
		
		jp1 = new JPanel();
		jp1.setBackground(Color.pink);
		
		jp1.setLocation(0, 0);
		jp1.setSize(width, height*3/4 + 50);
		
		jp1.add(l1);
		l1.setBounds(10, 10, width*2/3, 20);
		
		jp1.add(MsgRecord);
		MsgRecord.setBounds(10, 30, width*2/3, height/2);
		
		jp1.add(EditMsg);
		EditMsg.setBounds(10, 50 + height/2, width*2/3, height/4-20);
		
		jp1.add(l2);
		l2.setBounds(30+width*2/3, 10, width/4 - 10, 20);
		
		jp1.add(listComponent);
		listComponent.setBounds(30 + width*2/3, 30, width/4 - 10, height*3/4);
		
		jp1.setLayout(null);
		
		jp3 = new JPanel();
		jp3.setBackground(Color.pink);
		jp3.setLocation(10, 60+height*3/4);
		jp3.setSize(width, height/4 - 50);
		
		jp3.add(send);
		//send.setBounds(10, 60, width/5, 20);
		send.setBounds(width/5, 50 + height*3/4, width/5, 20);
		
		jp3.add(exit);
		//exit.setBounds(10, 60, width/5, 20);
		exit.setBounds(width*3/5, 50 + height*3/4, width/5, 20);
		
		jp3.setLayout(null);
		
		cont.add(jp1);
		cont.add(jp3);
		
		
		
		this.setVisible(true);
		
	}
	
	public void setSocketConnection(Socket socket, DataInputStream in,
			DataOutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		try // �����̣߳����ܷ�������Ϣ
		{
			threadMessage.start();
		} catch (Exception e) {
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == send){
			String msg = EditMsg.getText();
			if (msg.length() > 0){
				try {
					out.writeUTF("������������:" + name + "˵:" + "\n" + "   " + msg);
					System.out.println("������������:" + name + "˵:" + "\n" + "   " + msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				EditMsg.setText("");
			}
		}
		else if (e.getSource() == exit){
			int i = JOptionPane.showConfirmDialog(null, "ȷ���뿪��������?", "��ʾ", JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION){
				try {
					out.writeUTF("�û�����:");
					threadMessage.stop();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				this.dispose();
			}
		}
		
	
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			String s = null;
			try {
				s = in.readUTF();
				if (s.startsWith("������������:")) // ��ȡ��������������Ϣ
				{
					System.out.println(s);
					String content = s.substring(s.indexOf(":") + 1);
					MsgRecord.append("\n" + content);
				}
				else if (s.startsWith("������:")) {// ��ʾ�¼���������ߵ���Ϣ
					String newusr = s.substring(s.indexOf(":") + 1);
					if (newusr!=null)
						listTable.put(newusr, newusr);
					else
						listTable.put("aaa", "aaa");
					listComponent.add((String) listTable.get(newusr));
					listComponent.repaint(); // ˢ��List�������ʾ���û��ǳ�
				} else if (s.startsWith("�û�����:")) {// ɾ�������ߵ���������Ϣ
					String awayPeopleName = s.substring(s.indexOf(":") + 1);
					listComponent.remove((String) listTable.get(awayPeopleName));
					listComponent.repaint();
					MsgRecord.append("\n" + (String) listTable.get(awayPeopleName) + "����");
					listTable.remove(awayPeopleName);
				}
				Thread.sleep(5);				
			} catch (IOException | InterruptedException e) { // �������ر��׽�������ʱ������IOException
				// TODO Auto-generated catch block
				listComponent.removeAll();
				listComponent.repaint();
				listTable.clear();
				MsgRecord.setText("�ͷ������������ѶϿ�\n����ˢ������������ٴν��������ҡ�");
				break;
			}			
		}
	}
	
	
	
	
}