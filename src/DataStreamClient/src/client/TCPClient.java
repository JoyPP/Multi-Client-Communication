package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

public class TCPClient implements Runnable {

	private String host;// = "192.168.164.12";
	private InetAddress ip = InetAddress.getLocalHost();
	//指定服务器提供服务的端口号
	private int port = 8080;
	Socket socket;
	Thread thread;
	
	Login lg = null;
	ChatArea ca = null;
	Hashtable listTable = null;
	
	DataInputStream in = null;
	DataOutputStream out = null;
	
	public static void main(String[] args) throws IOException,UnknownHostException{
		
		TCPClient tcpclient = new TCPClient();
		//tcpclient.talk();
		
	}
	
	//初始化客户端套接字，请求与服务器的连接
	public TCPClient() throws IOException,UnknownHostException{
		System.out.println("准备客户来了。。。");
		System.out.println(ip);
		host = ip.toString().substring(ip.toString().indexOf("/")+1);
		System.out.println(host);
		socket = new Socket(host,port);	
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
		listTable = new Hashtable();
		if (socket != null){
			JOptionPane.showMessageDialog(null, "客户与服务器连接建立成功！","提示",JOptionPane.INFORMATION_MESSAGE);
			
			thread = new Thread(this);
			
			lg = new Login(listTable);
			
			lg.setSocketConnection(socket, in, out);
			
			//lg.login.validate();
			//ca = new ChatArea("", listTable, 500, 800);
			
	
		}
	}
	
	public void talk() throws IOException{
		try{
			BufferedReader br = getReader(socket);
			PrintWriter pw = getWriter(socket);
			BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
			
			String msg = null;
			while((msg=localReader.readLine())!=null){
				pw.println(msg);
				if(msg.equals("bye"))
					break;
			}			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				socket.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
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
	
	public void stop() {
		try {
			socket.close();
			thread = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (thread != null) {
			if (lg.namevalid) {
				System.out.println("fiajefia");
				ca = new ChatArea(lg.getName(), listTable, 500, 800);
				ca.setVisible(true);
				ca.setSocketConnection(socket, in, out);
				System.out.println("lllllll");
				ca.validate();
				break;
			}
			try {
				Thread.sleep(100);
			} catch (Exception e) {
			}
		}
	}
}



