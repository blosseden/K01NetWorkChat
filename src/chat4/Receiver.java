package chat4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

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
					InputStreamReader(this.socket.getInputStream()));
		}
		catch (Exception e) {
			System.out.println("예외 :"+  e);
		}
	}
	
	//Thread에서 main()메소드 역할을 하는 함수로
	//직접 호출하면 안되고 반드시 start()를 통해
	//간접 호출 해야 쓰레드가 생성된다.
	@Override
	public void run() {
		
		//스트림을 통해 서버가 보낸 내용을 라인단위로 읽어온다.
		while(in != null) {
			try {
				System.out.println("쓰레드 리시브 : "+ in.readLine());
				
			}
			catch (SocketException e) {
				System.out.println("뭐ㅇ무ㅁ아ㅣㅇㄴ");
				break;
			}
			catch (Exception e) {
				System.out.println("예외 : "+ e);
				/*
				 클라이언트가 접속을 종료 할 경우 SocketExcption이 발생되면서
				 무한루프에 빠지게 된다
				 */
			}
		}
		try {
			in.close();
		}
		catch (Exception e) {
			System.out.println("예외 : "+ e);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
