package backprop;

public class ScoreRange {

	int[] scores;
	int partitons;
	
	public ScoreRange(int[] scores) {
		this.scores = scores.clone();
		this.partitons = scores.length + 1;
	}
	
	public int getPlace(int score) throws Exception {
		if(scores.length == 0) {
			return 0;
		}
		else if(score < scores[0]) {
			return 0;
		}
		else if(score >= scores[scores.length - 1]) {
			return scores.length;
		}
		else  {
			for(int i = 1; i < scores.length; i++) {
				if(score >= scores[i - 1] && score < scores[i]) {
					return i;
				}
			}
		}
		throw new Exception("Should have gotten a place in the range for the score!");
	}
	
}
