package chat7;

import java.net.Socket;
import java.util.Scanner;

public class MultiClient {

	public static void main(String[] args) {

		System.out.println("존함을 어디 한번 써보십쇼:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		
		try {

			String ServerIP = "localhost";
			if(args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP,9999);
			System.out.println("연결댐");

			//서버에서 보내는 Echo메세지를 클라이언트에 출력하기 위한 쓰레드 생성
			Thread receiver = new Receiver(socket);
			receiver.start();

			//클라이언트의 메세지를 서버로 전송해주는 쓰레드 생성
			Thread sender = new Sender(socket,s_name);
			sender.start();
		}
		catch (Exception e) {
			System.out.println(" 예외 발생[MultiClient]" + e);
		}
//		catch (UnsupportedEncodingException e) {}
	}
}
