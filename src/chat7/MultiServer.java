package chat7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;


public class MultiServer {


	static ServerSocket serverSocket = null;
	static Socket socket = null;
	//클라이언트 정보 저장을 위한 Map컬렉션 정의
	Map<String, PrintWriter> clientMap;

	//생성자
	public MultiServer() {
		//클라이언트의 이름과 출력스트림을 저장 할 HashMap생성
		clientMap = new HashMap<String, PrintWriter>();
		//HashMap동기화 설정. 쓰레드가 사용자정보에 동시에 접근 하는 것을 차단한다.
		Collections.synchronizedMap(clientMap);
	}

	//서버초기화
	public void init() {

		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버 시ㅡㅡㅡ작");

			while(true) {
				socket = serverSocket.accept();

				/*
				 클라이언트의 메세지를 모든 클라이언트에게 전달 하기 위한
				 쓰레드 생성 및 start()
				 */
				Thread mst = new MultiServerT(socket);
				mst.start();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}


	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();

	}

	//접속 된 모든 클라이언트에게 메세지를 전달 하는 역할의 메소드
	public void sendAllMsg(String name, String msg) {

		//Map에 저장된 객체의 키값을 먼저 얻어온다.
		Iterator<String> it = clientMap.keySet().iterator();
		//저장된 객체(클라이언트)의 갯수 만큼 반복한다.
		while(it.hasNext()) {
			try {
				//각 클라이언트의 PrintWriter객체를 얻어온다.
				PrintWriter it_out = (PrintWriter)
						clientMap.get(it.next());

				/*클라이언트에게 메세지를 전달한다.
					매개변수 name이 있는 경우에는 이름 + 메세지
					없는 경우에는 메세지만 클라이언트로 전달한다.
				 */
				if(name.equals("")) {
					it_out.println(URLEncoder.encode(msg, "UTF-8"));
				}
				else {
					it_out.println("["+ name +"]:"+ msg);
				}	
			}
			catch(Exception e) {
				System.out.println("예ㅡ외 :"+e);
			}
		}
	}


	//	public void ChatterAllData() {
	//
	//		Set<String> key = clientMap.keySet();
	//		Iterator<String> itr = key.iterator();
	//		while(itr.hasNext()) {
	//			String str = itr.next();
	//		}
	//	}

	//내부클래스
	class MultiServerT extends Thread {

		//멤버변수 
		String threadName;
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		//생성자 : Socket을 기반으로 입출력 스트림을 생성한다.
		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(),
						true);
				in = new BufferedReader(new
						InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			}
			catch (Exception e) {
				System.out.println("예외 : "+ e);
			}
		}

		public MultiServerT(String name) {
			threadName = name;
		}
		@Override
		public void run() {

			//클라이언트로부터 전송된 "대화명"을 저장 할 변수
			String name = "";
			String s = "";

			try {
				//클라이언트 이름을 읽어와서 저장
				name = in.readLine();
				name = URLDecoder.decode(name, "UTF-8");
				//접속한 클라이언트에게 새로운 사용자의 입장을 알림
				//접속자를 제외한 나머지 클라이언트만 입장메세지를 받는다
				sendAllMsg("", name + "님이 입장하셨습니다");

				clientMap.put(name, out);

				//HashMap에 저장된 객체의 수로 접속자수를 파악 할 수 있다.
				System.out.println(name+ "접속");
				System.out.println("현재 접속자 수는"+
						clientMap.size()+"명 입니다" );

				//입력한 메세지는 모든 클라이언트에게 Echo 된다.
				while (in!=null) {
					s = in.readLine();
					s = URLDecoder.decode(s, "UTF-8");
//
//					if(s.equalsIgnoreCase("/list")) {
//						System.out.println("현재 접속한 사람덜 ↓↓↓ "+clientMap.get(name));
//						Set<String> key = clientMap.keySet();
//						Iterator<String> it = key.iterator();
//						while(it.hasNext()) {
//							String keys = it.next();
//							PrintWriter value = clientMap.get(keys);
//							System.out.println(String.format("%s:%s",key, value));
//						}
//					}
					if ( s == null )
						break;

					System.out.println(name + ">>" + s);
					sendAllMsg(name, s);	
				}
			}
			catch (Exception e) {
				System.out.println("예ㅡ외 :"+ e);
			}
			finally {
				/*
				 클라이언트가 접속을 종료하면 예외가 발생하게 되어 finally로
				 넘어오게 된다. 이때 "대화명"을 통해 remove()시켜준다
				 */
				clientMap.remove(name);
				sendAllMsg("", name+ "님이 퇴장하셨습니다.");
				//퇴장하는 클라이언트의 쓰레드명을 보여준다.
				System.out.println(name + " ["+
						Thread.currentThread().getName() + "] 퇴ㅡ장");
				System.out.println("현재 접속자 수는"
						+clientMap.size()+"명 입니다");
				try {
					in.close();
					out.close();
					socket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
