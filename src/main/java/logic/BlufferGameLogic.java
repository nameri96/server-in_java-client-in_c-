package logic;

import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class BlufferGameLogic implements IGameLogic{
	
	private HashMap<String, AtomicInteger> scores;
	private HashMap<String, String> answerToPlayer;
	private AtomicInteger questionAsked;
	private Vector<String> answers;
	private Questions currentQuestion;
	private boolean isGameEnd;
	private String roomName;
	private int Stage,selectAns;
	
	
	
	public BlufferGameLogic(Vector<String> players,String RoomName) {
		this.roomName = RoomName;
		this.reset(players);
		
	}
	
	public void reset(Vector<String> players){
		this.questionAsked = new AtomicInteger(0);
		this.answers = new Vector<String>();
		this.answerToPlayer = new HashMap<String, String>();
		this.scores = new HashMap<String, AtomicInteger>();
		for (String player : players) {
			this.scores.put(player, new AtomicInteger(0));
		}
		this.Stage = 0;
		this.selectAns = 0;
		this.isGameEnd = false;
	}
	
	public String getQuestion(){
		this.answers.clear();
		this.answerToPlayer.clear();
		this.selectAns = 0;
		Questions[] questions = JsonParser.parseQuestions(System.getProperty("user.dir")+"/questions.json");
		if(this.questionAsked.incrementAndGet() <= 3){
			int questionNumber = (int)(Math.random()*questions.length);
			this.currentQuestion = questions[questionNumber];
			this.answers.add(questions[questionNumber].getRealAnswer().toLowerCase());
			return "ASKTXT "+questions[questionNumber].getQuestionText();
		}
		else
		{
			Stage = 4;
			this.isGameEnd = true;
			return "EXIT";
		}
	}
	
	public synchronized void submitClientAnswer(String oneAnswer,String playerName){
		this.answers.add((int)Math.random()*answers.size(),oneAnswer.toLowerCase());
		this.answerToPlayer.put(oneAnswer, playerName);
	}
	
	public String getAnswers(){
		return "ASKCHOICES "+answers();
	}
	
	private String answers() {
		String result ="";
		int i = 0;
		for (String answer : answers) {
			result += i+". "+answer+" ";
			i++;
		}
		return result;
	}
	
	public boolean changePlayerScore(int Answer,String Player) throws Exception{
		if(Answer<0 || Answer>answers.size()-1)
			throw new Exception();
		this.selectAns++;
		if(answers.get(Answer).equals(this.currentQuestion.getRealAnswer().toLowerCase()))
		{
			this.scores.get(Player).addAndGet(10);//10 pts for choose the corect answer
			return true;
		}else{
			if(Player != answerToPlayer.get(answers.get(Answer)))
				this.scores.get(answerToPlayer.get(answers.get(Answer))).addAndGet(5);//5 pts for other player for choose is answer
			return false;
		}
	}
	
	public boolean isInProgress(){
		return !this.isGameEnd;
	}
	
	public String getRoomName() {
		return roomName;
	}
	
	@Override
	public synchronized String hasBroadcast()
	{
		switch (Stage)
		{
		case 0:
			Stage=1;
			return getQuestion();
		case 1:
			if(answers.size() == scores.size()+1)
				Stage = 2;
			else
				return null;
		case 2:
			Stage = 3;
			return getAnswers();
		case 3:
			if(scores.size() == selectAns)
			{
				Stage = 0;
				return hasBroadcast();
			}
			return null;
		case 4:
			return gameSummary();
		default:
			return null;
		}
	}
	
	private String gameSummary()
	{
		String answer = "GAMEMSG Summary: ";
		for(String Nickname : scores.keySet())
		{
			answer+=Nickname+": "+scores.get(Nickname)+"pts ";
		}
		
		return answer;
	}
	
	@Override
	public String submitChoice(String ans, String nickname) throws Exception
	{
		
		if(changePlayerScore(Integer.parseInt(ans), nickname))
		{
			return "GAMEMSG correct! +10pts";
		}
		
		return "GAMEMSG incorrect... The correct answer was: "+ this.currentQuestion.getRealAnswer();
	}
	
	@Override
	public void removePlayer(String nickname) 
	{
		scores.remove(nickname);
		
	}
	
	
}
