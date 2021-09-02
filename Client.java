import java.io.*; 
import java.net.*;  
import java.util.Scanner; 

//class Client to send/receive messages to-from the Server
public class Client 
{
	public static Scanner sc = new Scanner(System.in);
	//Method read_message to read the messages received from Server	
	public static Thread read_message(DataInputStream dis)
	{
		Thread readFromServer = new Thread(new Runnable() 
		{ 
			@Override
			public void run()
			{ 
				String msg = "";
				while (!msg.equals("Exit")) 
				{ 
					try 
					{ 
						// Read Message from Server
						msg = dis.readUTF(); 
						System.out.println("\nServer : "+msg + "\n");						
						// To check the disconnection request
						if(msg.equals("Exit"))
						{
							//to stop the thread execution on receiving Exit from Server						
							Thread.currentThread().interrupt();
							//System.exit(0);
							break;
						}
					} 
					catch (IOException e) 
					{ 
						//e.printStackTrace(); 
					} 
				} 
			} 
		}); 			
		return readFromServer;
	}

	//Method send_message to send messages to Server
	public static Thread send_message(DataOutputStream dos,Scanner scn)
	{
		Thread sendToServer = new Thread(new Runnable() 
		{ 
			@Override
			public void run() 
			{ 
				String msg = "";
				while (!msg.equals("Exit")) 
				{ 
					msg = scn.nextLine(); 										
					try 
					{ 
						// Write Message to Server
						while(msg.length()==0)
						{
							System.out.println("Please enter something to send.");
							msg = scn.nextLine();
						}
						if(msg.equals("Exit"))
						{
							dos.writeUTF(msg);
							//to stop the thread execution on entering Exit
							Thread.currentThread().interrupt();
							break;
						}
						//System.out.println("\nClient : ");
						dos.writeUTF(msg);
					} 
					catch (IOException e) 
					{ 
						e.printStackTrace(); 
					} 
				} 
			} 
		}); 
		return sendToServer;
	}

	public static void main(String args[]) throws UnknownHostException, IOException
	{ 
		Scanner scn = new Scanner(System.in); 
		//Reading Input regarding IP & Port of server to connect with it
		System.out.println("Enter Server IP : ");
		String ip = sc.nextLine();
		System.out.println("Enter Server Port : ");
		int port = sc.nextInt();	
		
		// establish the connection 
		Socket s = new Socket(ip, port); 
		if(s.isConnected())
			System.out.println("Connection Established !! ");
		else
			System.out.println("Connection not Established !! ");
		System.out.println("Enter Message to send: ");

		// To Read and Write data to/from other client 
		DataInputStream dis = new DataInputStream(s.getInputStream()); 
		DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
 		
		//Multithreading to continuously send and receive messages to/from Server
		Thread readFromServer = read_message(dis);
		Thread sendToServer = send_message(dos,scn);
				
		sendToServer.start(); 
		readFromServer.start();
		
		while(true)
		{
			if(!readFromServer.isAlive() || !sendToServer.isAlive())
			{
				//to close the connection and exit from the program 
				s.close();
				System.exit(0);
			}
		}
	} 
} 
