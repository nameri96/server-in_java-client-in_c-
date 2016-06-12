package logic;

import java.util.Vector;

public interface IGameLogic {
	
	void reset(Vector<String> players);
	public boolean isInProgress();
	String hasBroadcast();
	public void submitClientAnswer(String oneAnswer, String nickname);
	String submitChoice(String ans, String nickname) throws Exception;
	void removePlayer(String nickname);
}
