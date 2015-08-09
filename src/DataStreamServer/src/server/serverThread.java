package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;






/**
*serverThread被每个客户线程调用，专门处理服务器与每个客户之间的通信数据。服务器循环接收客户端输入的文本信息，
*并显示在命令行，直到客户端输入文本"bye"。
*/
public class serverThread extends Thread{

	Socket socket;
	Hashtable peopleList;
	
	DataInputStream in = null;
	DataOutputStream out = null;
	
	String name = null;
	
	public serverThread(Socket socket,Hashtable peopleList){
		this.socket = socket;
		this.peopleList = peopleList;
		
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	// 服务器与客户端通信线程
	public void run(){
		while (true) {
			String s = null;
			try {
				s = in.readUTF();
				if (s.startsWith("#昵称:")){
					name = s.substring(s.indexOf(":")+1);
					if (peopleList.containsKey(name)){
						out.writeUTF("昵称已存在:");
					}
					else {
						out.writeUTF("可以开始聊天啦:");
						// 显示新的socket连接及其IP地址和端口号
						System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
						
						peopleList.put(name, this); // 将当前线程添加到散列表，昵称作为关键字
						
						Enumeration enum1 = peopleList.elements();

						while (enum1.hasMoreElements()) // 获取所有的与客户端通信的服务器线程
						{
							serverThread th = (serverThread) enum1.nextElement();// 将当前聊天者的昵称和性别通知所有的用户
							th.out.writeUTF("聊天者:" + name); // 也将其他聊天者的姓名通知本线程（当前用户）
							if (th != this) {
								out.writeUTF("聊天者:" + th.name);
							}
						}
						
					}
				}
				else if (s.startsWith("公共聊天内容:")){
					String message = s.substring(s.indexOf(":") + 1);
					Enumeration enum1 = peopleList.elements(); // 获取所有的与客户端通信的服务器线程
					while (enum1.hasMoreElements()) {
						((serverThread) enum1.nextElement()).out.writeUTF("公共聊天内容:" + message);
					}
				}
				else if (s.startsWith("用户离开:")){
					socket.close();
					System.out.println("未登录客户离开了");
					break;
				}
				else if (s.startsWith("用户离线:")){
					Enumeration enum1 = peopleList.elements(); // 获取所有的与客户端通信的服务器线程
					while (enum1.hasMoreElements()) // 通知其他聊天者，该用户已离线
					{
						try {
							serverThread th = (serverThread) enum1.nextElement();
							if (th != this && th.isAlive()) {
								th.out.writeUTF("用户离线:" + name);
							}
						} catch (IOException eee) {
							eee.printStackTrace();
						}
					}
					if (name != null)
						peopleList.remove(name);
					socket.close(); // 关闭当前连接
					System.out.println(name + "客户离开了");
					break; // 结束本线程的工作，线程死亡
				}
				
				/*
				BufferedReader br = getReader(socket);
				PrintWriter pw = getWriter(socket);

				String msg = null;
				// 循环接收客户输入信息，并显示在服务器命令行，直到客户输入文本bye
				// msg就是服务器读取的客户端输入的信息
				while ((msg = br.readLine()) != null) {
					msg = socket.getInetAddress() + ":" + socket.getPort()
							+ " say : " + msg;
					System.out.println(msg);
					pw.println(echo(msg)); // 服务器内部写echo
					if (msg.equals("bye"))
						break;
				}
				
				
				*/
			} catch (IOException e) {
				Enumeration enum1 = peopleList.elements(); // 获取所有的与客户端通信的服务器线程

				while (enum1.hasMoreElements()) // 通知其他聊天者，该用户离线
				{
					try {
						serverThread th = (serverThread) enum1.nextElement();
						if (th != this && th.isAlive()) {
							th.out.writeUTF("用户离线:" + name);
						}
					} catch (IOException eee) {
					}
				}
				if (name != null)
					peopleList.remove(name);
				try // 关闭当前连接
				{
					socket.close();
				} catch (IOException eee) {
				}
				System.out.println(name + "用户离开了");
				break; // 结束本线程的工作，线程死亡
			}/* finally {
				try {
					if (socket != null)
						socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
		}
	}
		
	private PrintWriter getWriter(Socket socket2) throws IOException {
		// TODO Auto-generated method stub
		OutputStream socketOut = socket2.getOutputStream();
		return new PrintWriter(socketOut,true);

	}

	private BufferedReader getReader(Socket socket2) throws IOException {
		// TODO Auto-generated method stub
		InputStream socketIn = socket2.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
	}

	public String echo(String msg){
		return "echo : " + msg ;
	}
}
