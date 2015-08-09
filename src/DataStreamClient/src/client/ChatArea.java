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
	String name = null; // 聊天者的昵称
	
	Thread threadMessage = null; // 读取服务器信息的线程
	TextArea MsgRecord = null;
	TextField EditMsg = null;
	Button send,exit;
	JPanel jp1=null,jp2=null,jp3=null;
	Label l1 = null,l2 = null;
	
	
	Hashtable listTable; // 存放在线聊天者昵称的散列表
	List listComponent = null; // 显示在线聊天者昵称的的List组件
	
	Socket socket = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	
	int width, height; // 聊天区的宽和高
	
	public ChatArea(String name, Hashtable listTable, int width, int height){
	
		this.name = name;
		this.listTable = listTable;
		this.width = width;
		this.height = height;
		this.setBounds(300, 400, width, height);
		
		threadMessage = new Thread(this);
		MsgRecord = new TextArea(10,10);
		EditMsg = new TextField(28);
		
		
		l1 = new Label("聊天记录",Label.LEFT);
		l2 = new Label("在线人员",Label.LEFT);
		
		send = new Button("发送消息");
		send.addActionListener(this);
		exit = new Button("退出聊天室");
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
		try // 启动线程，接受服务器信息
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
					out.writeUTF("公共聊天内容:" + name + "说:" + "\n" + "   " + msg);
					System.out.println("公共聊天内容:" + name + "说:" + "\n" + "   " + msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				EditMsg.setText("");
			}
		}
		else if (e.getSource() == exit){
			int i = JOptionPane.showConfirmDialog(null, "确认离开聊天室吗?", "提示", JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION){
				try {
					out.writeUTF("用户离线:");
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
				if (s.startsWith("公共聊天内容:")) // 读取服务器发来的信息
				{
					System.out.println(s);
					String content = s.substring(s.indexOf(":") + 1);
					MsgRecord.append("\n" + content);
				}
				else if (s.startsWith("聊天者:")) {// 显示新加入的聊天者的信息
					String newusr = s.substring(s.indexOf(":") + 1);
					if (newusr!=null)
						listTable.put(newusr, newusr);
					else
						listTable.put("aaa", "aaa");
					listComponent.add((String) listTable.get(newusr));
					listComponent.repaint(); // 刷新List组件，显示新用户昵称
				} else if (s.startsWith("用户离线:")) {// 删除已离线的聊天者信息
					String awayPeopleName = s.substring(s.indexOf(":") + 1);
					listComponent.remove((String) listTable.get(awayPeopleName));
					listComponent.repaint();
					MsgRecord.append("\n" + (String) listTable.get(awayPeopleName) + "离线");
					listTable.remove(awayPeopleName);
				}
				Thread.sleep(5);				
			} catch (IOException | InterruptedException e) { // 服务器关闭套接字连接时，导致IOException
				// TODO Auto-generated catch block
				listComponent.removeAll();
				listComponent.repaint();
				listTable.clear();
				MsgRecord.setText("和服务器的连接已断开\n必须刷新浏览器才能再次进入聊天室。");
				break;
			}			
		}
	}
	
	
	
	
}