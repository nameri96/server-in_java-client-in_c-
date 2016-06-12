package logic;

import java.util.Vector;

public class Room {

	private IGameLogic roomCurrentGame;
	private Vector<String> players;
	private String roomName;
	public enum Games { bluffer };
	
	public Room(String name) {
		this.roomName = name;
		players = new Vector<String>();
		
	}
	public String getName()
	{
		return this.roomName;
	}
	public void addPlayer(String player){
		players.addElement(player);
	}

	public synchronized boolean Start(String gameToPlay){
		if(!isInProgress() &&  Games.valueOf(gameToPlay)!=null){
			Games game = Games.valueOf(gameToPlay);
			switch (game)
			{
			case bluffer:
				if(roomCurrentGame == null)
					roomCurrentGame = new BlufferGameLogic(players, roomName);
				else
					roomCurrentGame.reset(players);
				return true;
			default:
				break;
			}
		}else
			return false;
		return false;
	}

	public boolean isInProgress()
	{
		return roomCurrentGame != null && roomCurrentGame.isInProgress();

	}

	public void removePlayer(String nickname)
	{
		players.remove(nickname);
		if(roomCurrentGame != null)
			roomCurrentGame.removePlayer(nickname);
	}

	public String[] getPlayers()
	{
		String[] ret = new String[players.size()];
		players.toArray(ret);
		return ret;
	}

	public String hasBroadcast()
	{
		String answer = roomCurrentGame.hasBroadcast();
		if(answer.equals("EXIT"))
		{
			answer = roomCurrentGame.hasBroadcast();
			roomCurrentGame = null;
		}
		return answer;
	}
	public void submitAnswer(String ans,String playerName)
	{
		roomCurrentGame.submitClientAnswer(ans,playerName);
	}
	public String submitChoice(String ans, String nickname) throws Exception
	{
		return roomCurrentGame.submitChoice(ans, nickname);
	}

}
