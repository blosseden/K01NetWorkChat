package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

public class Receiver extends Thread {
	
	Socket socket;
	BufferedReader in = null;
	
	//Socket객체를 매개변수로 받는 생성자
	public Receiver(Socket socket) {
		this.socket = socket;
		
		//Socket객체를 기반으로 input스트림을 생성한다
		//서버가 보내는 메세지를 읽어오는 역할을 한다.
		try {
			in = new BufferedReader(new
				InputStreamReader(this.socket.getInputStream(), "UTF-8"));
		}
		catch (Exception e) {
			System.out.println("예외 :"+  e);
		}
	}

	@Override
	public void run() {
		
		//소켓이 종료되면 while()문을 벗어나서 input스트림을 종료한다
		while(in != null) {
			try {
				System.out.println("쓰레드 리시브 : "
			+ URLDecoder.decode(in.readLine(), "UTF-8"));
				
			}
			catch (SocketException e) {
				System.out.println("SocketException");
				break;
			}
			catch (Exception e) {
				System.out.println("예외>Receiver>run1:"+ e);
			}
		}
		try {
			in.close();
		}
		catch (Exception e) {
			System.out.println("예외>Receiver>run2: "+ e);
		}
	}
}
