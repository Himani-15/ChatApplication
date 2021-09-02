import java.net.*;
import java.io.*;
import java.util.Scanner;

//class Server which will act as Server and accepts connection from client
public class Server
{
	//Method read_message reads and prints the message received from client
	public static Thread read_message(DataInputStream in)
	{
		Thread readFromClient = null;
		readFromClient = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String line="";
				//to read message from client
				while(!line.equals("Exit"))
				{
					try
					{
						line = in.readUTF();
						System.out.println("\nClient : "+line+"\n");
						if(line.equals("Exit"))
						{
							//to stop the thread
							Thread.currentThread().interrupt();
							//System.exit(0);
							break;
						}
					}
					catch(IOException i)
					{
						System.out.println("Exception : " +i);
					}
				}
			}
		});
		return readFromClient;
	}
	
	//Method send_message sends message to the connected client
	public static Thread send_message(DataOutputStream out, Scanner input)
	{
		Thread sendToClient = null;
		sendToClient = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				String message="";
				//send message to client
				while(!message.equals("Exit"))
				{
					try
					{
						message = input.nextLine();
						//if message is empty
						while(message.length()==0)
						{
							System.out.println("Please enter something to send.");
							message = input.nextLine();
						}
						if(message.equals("Exit"))
						{
							out.writeUTF(message);
							//to stop the thread
							Thread.currentThread().interrupt();
							//System.exit(0);
							break;
						}
						out.writeUTF(message);
					}
					catch(IOException i)
					{
						System.out.println("Exception : " +i);
					}
				}
			}
		});
		return sendToClient;
	}

	public static void main(String args[])
	{
		Socket socket = null;
		ServerSocket serverS = null;
		DataInputStream din = null;
		DataOutputStream dos = null;
		Scanner input = null;

		//to run server on specified port
		int port;
		input = new Scanner(System.in);
		System.out.println("Enter Port Number on which Server will be running: ");
		port = input.nextInt();
		
		try
		{
			//starting server to wait and accept the connection
			serverS = new ServerSocket(port);
			System.out.println("Server started");
			System.out.println("Waiting for a client...");
			socket = serverS.accept();
			System.out.println("Client accepted");
			System.out.println("Enter message to send: ");
		
			din = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			input = new Scanner(System.in);
		
			//Multithreading to continuously send and receive message to/from client
			Thread readFromClient = read_message(din);
			Thread sendToClient = send_message(dos, input);

			readFromClient.start();
			sendToClient.start();

			while(true)
			{
				if(!readFromClient.isAlive() || !sendToClient.isAlive())
				{
					//to close the connection and stop the program
					socket.close();
					System.exit(0);
				}
			}
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}
}