package btaw.shared.model;

import java.io.Serializable;

public class SynonymData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3642843536661380980L;
	private String word;
	private String synonym;

	public SynonymData() {
		
	}

	public SynonymData(String word, String synonym) {
		this.setWord(word);
		this.setSynonym(synonym);
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}
}
