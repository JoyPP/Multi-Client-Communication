package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class TCPServer {
	//����������ṩ����Ķ˿�
	private int port = 8080;
	private ServerSocket serverSocket;
	static Hashtable peopleList;
	
	public static void main(String[] args) throws IOException{
		peopleList = new Hashtable<String,InetAddress>(); // �����������߿ͻ���ͨ�ŵķ������̵߳�ɢ�б�
		TCPServer tcpserver = new TCPServer();
		tcpserver.sevice();
	}
	
	
	public TCPServer() throws IOException{
		serverSocket = new ServerSocket(port);
		System.out.println("�������ɹ�����....");
	}
	
	//����ͻ����ӣ����ö��̵߳ķ�ʽ��ÿ���ͻ���������һ�������߳�
	public void sevice(){
		while(true){
			Socket socket = null;
			try{
				//���տͻ�����
				socket = serverSocket.accept();
				//Ϊÿ���ͻ����Ӵ���һ�������߳�
				Thread workThread = new serverThread(socket,peopleList);
				//���������߳�
				workThread.start();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
}
























