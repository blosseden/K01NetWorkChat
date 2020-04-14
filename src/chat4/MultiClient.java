package chat4;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiClient {

	public static void main(String[] args) {
		
		System.out.println("존함을 어디 한번 써보십쇼:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		
		PrintWriter out = null;
		//서버의 메세지를 읽 어오는 기능이 Receiver로 옮겨짐
		//BufferedReader in = null;
		
		try {
			/*
			 c:\> java 패키지명.MultiClient 접속 할 IP주소
			 => 위와 같이 하면 해당 IP주소로 접속 할 수 있다
			 만약 IP주소가없다면 localhost(127.0.0.1)로 접속된다
			 */
			String ServerIP = "localhost";
			if(args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP,9999);
			System.out.println("연결댐");
			
			//서버에서 보내는 메세지를 읽어 올 Receiver 쓰레드 시작
			Thread receiver = new Receiver(socket);
			receiver.start();
			
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(s_name);
			
			while(out!=null) {
				try {
					String s2 = scanner.nextLine();
					if(s2.equals("q") || s2.equals("Q")) {
						break;
					}
					else {
						//클라이언트의 메세지를 서버로 전송한다
						out.println(s2);
					}
				}
				catch (Exception e) {
					System.out.println("예-외 : "+ e);
				}
			}
			
			//스트림과 소켓을 종료한다
			out.close();
			socket.close();
			
		}
		catch (Exception e) {
			System.out.println("예외발생"+e);
		}

	}

}
