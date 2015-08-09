package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class TCPServer {
	//定义服务器提供服务的端口
	private int port = 8080;
	private ServerSocket serverSocket;
	static Hashtable peopleList;
	
	public static void main(String[] args) throws IOException{
		peopleList = new Hashtable<String,InetAddress>(); // 存放与各聊天者客户端通信的服务器线程的散列表
		TCPServer tcpserver = new TCPServer();
		tcpserver.sevice();
	}
	
	
	public TCPServer() throws IOException{
		serverSocket = new ServerSocket(port);
		System.out.println("服务器成功启动....");
	}
	
	//处理客户链接，采用多线程的方式，每个客户链接启动一个工作线程
	public void sevice(){
		while(true){
			Socket socket = null;
			try{
				//接收客户连接
				socket = serverSocket.accept();
				//为每个客户连接创建一个工作线程
				Thread workThread = new serverThread(socket,peopleList);
				//启动工作线程
				workThread.start();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
}
























