package data;

import java.util.concurrent.ConcurrentHashMap;

import logic.Room;
import protocol.ProtocolCallback;


public class ServerCollectiveDatabase 
{
	public ConcurrentHashMap<String, String> IpToNickname;
	public ConcurrentHashMap<String, ProtocolCallback<String>> NicknameToCallback;
	public ConcurrentHashMap<String,String> NicknameToRoomName;
	public ConcurrentHashMap<String, Room> RoomNameToRoom;
	
	
	
	private static class DataHolder 
	{
		private static ServerCollectiveDatabase instance = new ServerCollectiveDatabase();

	}
	private ServerCollectiveDatabase() 
	{
		//initialize the data sets
		IpToNickname = new ConcurrentHashMap<String, String>();
		NicknameToCallback = new ConcurrentHashMap<String, ProtocolCallback<String>>();
		NicknameToRoomName = new ConcurrentHashMap<String, String>();
		RoomNameToRoom = new ConcurrentHashMap<String, Room>();
	}
	public static ServerCollectiveDatabase getInstance() 
	{
		return DataHolder.instance;
	}
}
