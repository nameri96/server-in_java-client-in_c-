package logic;

public class Questions {

	private String questionText,realAnswer;
	
	public Questions(String questionText,String realAnswer) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
	}

	public String getQuestionText() {
		return questionText;
	}

	public String getRealAnswer() {
		return realAnswer;
	}

}
