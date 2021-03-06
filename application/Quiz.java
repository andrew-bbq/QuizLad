package application;

import java.util.ArrayList;

public class Quiz {
  int numQuestions;
  int numCorrect;
  int numAnswered;
  int index;
  ArrayList<Question> questions;

  public Quiz() {
    numQuestions = 0;
    numCorrect = 0;
    index = 0;
    questions = null;
  }

  public Quiz(int numQuestions, ArrayList<Question> questions) {
    this.numQuestions = numQuestions;
    numCorrect = 0;
    index = 0;
    this.questions = questions;
  }

  public int getCorrect() {
    return numCorrect;
  }
  
  public void incCorrect() {
	  numCorrect++;
  }

  public Question getQuestion() {
    Question toReturn = questions.get(index);
    index++;
    return toReturn;
  }

  public int getNumQuestions() {
    return this.numQuestions;
  }

  public int getIndex() {
    return index;
  }

  // TEMP METHOD
  public void setNumQuestions(int i) {
    numQuestions = i;
  }
}
