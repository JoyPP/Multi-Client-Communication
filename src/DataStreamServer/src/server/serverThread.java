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
*serverThread��ÿ���ͻ��̵߳��ã�ר�Ŵ����������ÿ���ͻ�֮���ͨ�����ݡ�������ѭ�����տͻ���������ı���Ϣ��
*����ʾ�������У�ֱ���ͻ��������ı�"bye"��
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
	
	// ��������ͻ���ͨ���߳�
	public void run(){
		while (true) {
			String s = null;
			try {
				s = in.readUTF();
				if (s.startsWith("#�ǳ�:")){
					name = s.substring(s.indexOf(":")+1);
					if (peopleList.containsKey(name)){
						out.writeUTF("�ǳ��Ѵ���:");
					}
					else {
						out.writeUTF("���Կ�ʼ������:");
						// ��ʾ�µ�socket���Ӽ���IP��ַ�Ͷ˿ں�
						System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
						
						peopleList.put(name, this); // ����ǰ�߳���ӵ�ɢ�б��ǳ���Ϊ�ؼ���
						
						Enumeration enum1 = peopleList.elements();

						while (enum1.hasMoreElements()) // ��ȡ���е���ͻ���ͨ�ŵķ������߳�
						{
							serverThread th = (serverThread) enum1.nextElement();// ����ǰ�����ߵ��ǳƺ��Ա�֪ͨ���е��û�
							th.out.writeUTF("������:" + name); // Ҳ�����������ߵ�����֪ͨ���̣߳���ǰ�û���
							if (th != this) {
								out.writeUTF("������:" + th.name);
							}
						}
						
					}
				}
				else if (s.startsWith("������������:")){
					String message = s.substring(s.indexOf(":") + 1);
					Enumeration enum1 = peopleList.elements(); // ��ȡ���е���ͻ���ͨ�ŵķ������߳�
					while (enum1.hasMoreElements()) {
						((serverThread) enum1.nextElement()).out.writeUTF("������������:" + message);
					}
				}
				else if (s.startsWith("�û��뿪:")){
					socket.close();
					System.out.println("δ��¼�ͻ��뿪��");
					break;
				}
				else if (s.startsWith("�û�����:")){
					Enumeration enum1 = peopleList.elements(); // ��ȡ���е���ͻ���ͨ�ŵķ������߳�
					while (enum1.hasMoreElements()) // ֪ͨ���������ߣ����û�������
					{
						try {
							serverThread th = (serverThread) enum1.nextElement();
							if (th != this && th.isAlive()) {
								th.out.writeUTF("�û�����:" + name);
							}
						} catch (IOException eee) {
							eee.printStackTrace();
						}
					}
					if (name != null)
						peopleList.remove(name);
					socket.close(); // �رյ�ǰ����
					System.out.println(name + "�ͻ��뿪��");
					break; // �������̵߳Ĺ������߳�����
				}
				
				/*
				BufferedReader br = getReader(socket);
				PrintWriter pw = getWriter(socket);

				String msg = null;
				// ѭ�����տͻ�������Ϣ������ʾ�ڷ����������У�ֱ���ͻ������ı�bye
				// msg���Ƿ�������ȡ�Ŀͻ����������Ϣ
				while ((msg = br.readLine()) != null) {
					msg = socket.getInetAddress() + ":" + socket.getPort()
							+ " say : " + msg;
					System.out.println(msg);
					pw.println(echo(msg)); // �������ڲ�дecho
					if (msg.equals("bye"))
						break;
				}
				
				
				*/
			} catch (IOException e) {
				Enumeration enum1 = peopleList.elements(); // ��ȡ���е���ͻ���ͨ�ŵķ������߳�

				while (enum1.hasMoreElements()) // ֪ͨ���������ߣ����û�����
				{
					try {
						serverThread th = (serverThread) enum1.nextElement();
						if (th != this && th.isAlive()) {
							th.out.writeUTF("�û�����:" + name);
						}
					} catch (IOException eee) {
					}
				}
				if (name != null)
					peopleList.remove(name);
				try // �رյ�ǰ����
				{
					socket.close();
				} catch (IOException eee) {
				}
				System.out.println(name + "�û��뿪��");
				break; // �������̵߳Ĺ������߳�����
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
