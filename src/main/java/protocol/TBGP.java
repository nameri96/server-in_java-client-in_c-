package protocol;

import data.ServerCollectiveDatabase;
import logic.Room;
import logic.Room.Games;


/*
 * This class is our Text Based Game Protocol. It handles all the communication-to-backend interaction.
 * The protocol manages rooms, client names and socket to client mapping.
 */
public class TBGP implements AsyncServerProtocol<String>
{
	boolean ShouldClose = false;
	private ServerCollectiveDatabase _data;
	
	//Initialize the _data singleton that handles the information of rooms in this server.
	public TBGP()
	{
		_data = ServerCollectiveDatabase.getInstance();
	}
	
	//Process the incoming message.
	public void processMessage(String msg, ProtocolCallback<String> callback)
	{
		String[] splitForIP = msg.split("###");
		if(splitForIP.length<2)
			return;
		msg = splitForIP[1];
		String IP = splitForIP[0];
		String result = msg+" ";
		/*
		 * This part processes the message received and takes care of the game progress.
		 */
		String[] command = msg.split(" ");
		if(command.length<2 && !command[0].equals("QUIT") && !command[0].equals("LISTGAMES"))
		{
			command[0]="\""+command[0]+"\"";
		}
		String tmp;
		if(isEnd(command[0])){
			this.connectionTerminated();
			result+= " ACCEPTED";
		}
		else
		{
			try
			{
				switch (command[0])
				{
				case "NICK":
					if(_data.NicknameToCallback.containsKey(command[1]))
						result+="REJECTED nickname already taken ";
					else
					{
						tmp = _data.IpToNickname.putIfAbsent(IP, command[1]);
						if(tmp!=null)
							result+="REJECTED You already have a nickname ";
						else
						{
							_data.NicknameToCallback.put(command[1], callback);
							result+="ACCEPTED ";
						}
					}
					break;
				case "JOIN":
					String nickname = _data.IpToNickname.get(IP);
					if(nickname == null)
					{
						result+="REJECTED You don't have a nickname yet ";
						break;
					}
					if(_data.NicknameToRoomName.containsKey(nickname))
						if(_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(nickname)).isInProgress())
							result += "REJECTED can't switch rooms in the middle of a game ";
						else
						{
							_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(nickname)).removePlayer(nickname);
							_data.NicknameToRoomName.put(nickname, command[1]);
							_data.RoomNameToRoom.putIfAbsent(command[1], new Room(command[1]));
							_data.RoomNameToRoom.get(command[1]).addPlayer(nickname);
							result += "ACCEPTED moved to room "+command[1]+"";
						}
					else
					{
						_data.NicknameToRoomName.put(nickname, command[1]);
						_data.RoomNameToRoom.putIfAbsent(command[1], new Room(command[1]));
						_data.RoomNameToRoom.get(command[1]).addPlayer(nickname);
						result += "ACCEPTED joined room "+command[1]+"";
					}
					break;
				case "MSG":
					
					String MyName = _data.IpToNickname.get(IP);
					if(MyName == null)
					{
						result+="REJECTED You don't have a nickname yet ";
						break;
					}
					SendMessageToRoom(_data.NicknameToRoomName.get(_data.IpToNickname.get(IP)),"USRMSG from "+MyName+": "+putArrayTogether(command),MyName);
					result += "ACCEPTED ";
					break;
				case "LISTGAMES":
					Games[] games = Room.Games.values();
					result+= "ACCEPTED ";
					for(Games g : games)
					{
						result += g.name()+" ";
					}
					result+=" ";
					break;
				case "STARTGAME":
					if(_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(_data.IpToNickname.get(IP))).Start(command[1].toLowerCase()))
					{
						result += "ACCEPTED ";
						
					}else{
						result += "REJECTED ";
					}
					break;
				case "TXTRESP":
					try
					{
						_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(_data.IpToNickname.get(IP))).submitAnswer(putArrayTogether(command),_data.IpToNickname.get(IP));
						result+= "ACCEPTED ";
					} catch (Exception e1)
					{
						result += "REJECTED make sure you have entered a nickname, joined a room,started a game and sent a valid answer :) ";
					}
					break;
				case "SELECTRESP":
					try
					{
						result+="ACCEPTED "+_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(_data.IpToNickname.get(IP))).submitChoice(command[1], _data.IpToNickname.get(IP))+"";
					} catch (Exception e1)
					{
						result += "REJECTED make sure you have entered a nickname, joined a room,started a game and sent a valid answer :) ";
					}
					break;
				case "QUIT":
					this.connectionTerminated();
					//handle player leaving!!!
					String nick = _data.IpToNickname.get(IP);
					(_data.RoomNameToRoom.get(_data.NicknameToRoomName.get(nick))).removePlayer(nick);;
					break;
				default:
					result += "UNIDENTIFIED Syntax error in command "+command[0];
					break;
				}
			} catch (Exception e1)
			{
				result += "REJECTED make sure you have entered a nickname, joined a room,started a game and sent a valid answer :) ";
			}
		}
		result = "SYSMSG "+result;
		/*
		 * End of Processing
		 */
		
		try
		{
			callback.sendMessage(result);
		} catch (Exception e){
			System.err.println("Failed to send message to client");
		}
		try{
			String broadcast;
			Room room = _data.RoomNameToRoom.get(_data.NicknameToRoomName.get(_data.IpToNickname.get(IP)));
			if((broadcast = room.hasBroadcast()) != null)
			{
				SendMessageToRoom(room.getName(), broadcast, "");
			}
		} catch (Exception e){
		}
	}
	
	private String putArrayTogether(String[] array)
	{
		String ans="";
		for(int i = 1; i<array.length ; i++)
		{
			if(array[i]!=null)
				ans+=array[i]+" ";
		}
		return ans;
	}
	
	private void SendMessageToRoom(String roomName, String msg, String Exclude)
	{
		String[] nicknames = _data.RoomNameToRoom.get(roomName).getPlayers();
		for(String s : nicknames)
		{
			if(!(s.equals(Exclude)))
			{
				try
				{
					_data.NicknameToCallback.get(s).sendMessage(msg);
				} catch (Exception e)
				{
					System.err.println("Client "+s+" has left and will not get the message...");
					_data.RoomNameToRoom.get(roomName).removePlayer(s);
				}
			}
		}
	}
	
	public boolean isEnd(String msg)
	{
		return msg.equals("QUIT");
	}
	
	public boolean shouldClose()
	{
		return ShouldClose;
	}
	
	public void connectionTerminated()
	{
		ShouldClose = true;
	}
	
	
}
