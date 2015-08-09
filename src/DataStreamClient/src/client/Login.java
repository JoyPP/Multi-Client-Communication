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
	Hashtable listTable; // 存放在线聊天者昵称的散列表
	String name = null;
	
	Socket socket;
	Thread thread = null;
	DataInputStream in = null;
	DataOutputStream out = null;
	
	ChatArea ca = null;
	
	boolean namevalid = false;
	
	public Login(Hashtable listTable){
		jflogin = new JFrame("用户登录");
		inputusrname = new Label("请输入用户名:",Label.LEFT);
		usrname = new TextField(30);
		login = new Button("登录");
		cancel = new Button("取消");
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
			i = JOptionPane.showConfirmDialog(null, "确认登录吗?","提示",JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION){
				if (name.length() == 0){
					JOptionPane.showMessageDialog(null, "用户名不能为空","提示", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					if (socket != null) {
						try {	// 将用户信息发送给服务器端
							out.writeUTF("#昵称:" + name);
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
						JOptionPane.showMessageDialog(null, "登录成功!欢迎您," + name, "提示",JOptionPane.INFORMATION_MESSAGE);
						this.jflogin.setVisible(false);
						try {
							out.writeUTF("#昵称:");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					} 
					else {
						if (listTable.containsKey(name)) {
							namevalid = false;
							JOptionPane.showMessageDialog(null,"用户名已存在,请重新输入!", "提示",JOptionPane.INFORMATION_MESSAGE);
						} 
						else {
							namevalid = true;
							JOptionPane.showMessageDialog(null, "登录成功!欢迎您," + name, "提示",JOptionPane.INFORMATION_MESSAGE);
							this.jflogin.setVisible(false);
							try {
								out.writeUTF("#昵称:");
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
				out.writeUTF("用户离开:");
			} catch (IOException ee) {
			}
			//尚未建立连接，故只需关闭窗口
			this.jflogin.setVisible(false);
			thread.stop();
		}
		
	}

	public void windowClosing(WindowEvent e) 
	{ 
		try {
			out.writeUTF("用户离开:");
		} catch (IOException ee) {
		}
		//尚未建立连接，故只需关闭窗口
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
					JOptionPane.showMessageDialog(null, "与服务器的连接已断开", "提示", JOptionPane.INFORMATION_MESSAGE);
					break;
				}
			}
			if (msg != null){
				if (msg.startsWith("可以开始聊天啦:")){
					namevalid = true;
					JOptionPane.showMessageDialog(null, "登录成功!欢迎您," + name, "提示", JOptionPane.INFORMATION_MESSAGE);
					//this.jflogin.setVisible(false);
					this.jflogin.dispose();
					
					ca = new ChatArea(name,listTable,450,480);
					ca.setSocketConnection(socket, in, out);
					break;
				}
				else if (msg.startsWith("聊天者:")){
					namevalid = false;
					String people = msg.substring(msg.indexOf(":") + 1); // 将目前在线的聊天者昵称添加到散列表中
					listTable.put(people, people);
					
				}
				else if (msg.startsWith("昵称已存在:")){
					namevalid = false;
					JOptionPane.showMessageDialog(null, "用户名已存在,请重新输入!","提示",JOptionPane.INFORMATION_MESSAGE);
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


