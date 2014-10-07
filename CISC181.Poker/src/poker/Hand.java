package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Hand {
	private ArrayList<Card> CardsInHand;

	private int HandStrength;
	private int HiHand;
	private int LoHand;
	private int Kicker;
	private boolean bScored = false;

	private boolean Flush;
	private boolean Straight;
	private boolean Ace;
	
	
	private boolean notWild = true;
	private int numberOfJokers;
	//Outside of loop so values are preserved throughout recursion
	private ArrayList<Card> copiedHand;
	private ArrayList<Hand> jokerHands;

	public Hand(Deck d) {
		ArrayList<Card> Import = new ArrayList<Card>();
		for (int x = 0; x < 5; x++) {
			Import.add(d.drawFromDeck());
		}
		CardsInHand = Import;
	}
	
	public int getHiHand() {
		return HiHand;
	}

	public int getLoHand() {
		return LoHand;
	}

	public ArrayList<Card> getCards() {
		return CardsInHand;
	}

	public int getHandStrength() {
		return HandStrength;
	}

	public int getKicker() {
		return Kicker;
	}

	public int getHighPairStrength() {
		return HiHand;
	}

	public int getLowPairStrength() {
		return LoHand;
	}

	public boolean getAce() {
		return Ace;
	}

	public static Hand EvalHand(ArrayList<Card> SeededHand) {		
		Deck d = new Deck();
		Hand h = new Hand(d);
		h.CardsInHand = SeededHand;
		h.EvalHand();
		
		return h;
	}	
	
	private int numberOfJokers(ArrayList<Card> cards){
		int numberOfJokers = 0;
		for (Card card:cards){
			if (card.getSuit().getSuit() == eSuit.JOKERS.getSuit()){
				numberOfJokers++;
			}
		}
		return numberOfJokers;
	}
	
	private void noJokerCopy(){
		for (int j = 0; j< (CardsInHand.size() - 1 );j++){
			Card copy = new Card(CardsInHand.get(j).getSuit(), CardsInHand.get(j).getRank());
			copiedHand.add(copy);
		}
		//remove the jokers
		for (int j = 0; j<this.numberOfJokers;j++){
		copiedHand.remove(j);
		}
		
	}
	
	public void jokerRecursion(ArrayList<Card> seededCard){
		//this doesn't work properly. hard to get a base case.
		//numberOfJokers corresponds to jokercardvalues because
		//of the loop logic in the else statement (more than one joker)
		if (this.numberOfJokers <= 52){
			for (short i = 0; i <= 3; i++) {
				eSuit SuitValue = eSuit.values()[i];			
				for (short j = 0; j <= 12; j++) {
					//reset the hand, removing the last card we added and starting fresh
					ArrayList<Card> jokerHand = new ArrayList<Card>();
					jokerHand.addAll(seededCard);
					eRank RankValue = eRank.values()[j];				
					Card NewCard = new Card(SuitValue,RankValue);
					//add new card to evaluate
					jokerHand.add(NewCard);
					//evaluate the hand and add to joker hands
					jokerHands.add(EvalHand(jokerHand));
					this.numberOfJokers--;
					System.out.println(NewCard.getSuit());
				}
			}
		}
		else{
			for (short i = 0; i <= 3; i++) {
				eSuit SuitValue = eSuit.values()[i];			
				for (short j = 0; j <= 12; j++) {
					ArrayList<Card> jokerHand = new ArrayList<Card>();
					jokerHand.addAll(seededCard);
					eRank RankValue = eRank.values()[j];				
					Card NewCard = new Card(SuitValue,RankValue);
					jokerHand.add(NewCard);
					this.numberOfJokers--;
					jokerRecursion(jokerHand);
				}
			}
		}
	}
	
	public void EvalHand() {
		// Evaluates if the hand is a flush and/or straight then figures out
		// the hand's strength attributes


		// Sort the cards!
		Collections.sort(CardsInHand, Card.CardRank);
		
		//Account for Jokers
		
		//Account for NaturalRoyalFlush by changing a boolean value
		//Create a copy of the hand without jokers
		this.numberOfJokers = numberOfJokers(CardsInHand)*52;
		if (this.numberOfJokers > 0){
			this.notWild = false;
			noJokerCopy();
			jokerRecursion(copiedHand);
			Collections.sort(jokerHands, Hand.HandRank);
			//set to hand to best joker hand after all evaluations next because
			//I didn't want to make everything an else if because i'm lazy
			//and don't want to screw up any scoring logic
			
	
		}
		
		// Ace Evaluation
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == eRank.ACE) {
			Ace = true;
		}
		
		//Five of a Kind Evaluation
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()){
			ScoreHand(eHandStrength.FiveOfAKind, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, 0);
		}

		// Flush Evaluation
		if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getSuit()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getSuit() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getSuit()) {
			Flush = true;
		} else {
			Flush = false;
		}

		// Straight Evaluation
		if (Ace) {
			// Looks for Ace, King, Queen, Jack, 10
			if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.KING
					&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.QUEEN
					&& CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == eRank.JACK
					&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN) {
				Straight = true;
				// Looks for Ace, 2, 3, 4, 5
			} else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TWO
					&& CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == eRank.THREE
					&& CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == eRank.FOUR
					&& CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == eRank.FIVE) {
				Straight = true;
			} else {
				Straight = false;
			}
			// Looks for straight without Ace
		} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
				.getRank() + 1
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
						.getRank() + 2
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()
						.getRank() + 3
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()
						.getRank() + 4) {
			Straight = true;
		} else {
			Straight = false;
		}

		// Evaluates the hand type
		if (Straight == true && Flush == true && notWild == true
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN && Ace) {
			ScoreHand(eHandStrength.NaturalRoyalFlush, 0, 0, 0);
		}
		//Royal flush with wilds
		else if (Straight == true && Flush == true && notWild == false
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == eRank.TEN && Ace) {
			ScoreHand(eHandStrength.RoyalFlush, 0, 0, 0);
		}

		// Straight Flush
		else if (Straight == true && Flush == true) {
			ScoreHand(eHandStrength.StraightFlush, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, 0);
		}
		// Four of a Kind

		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.FourOfAKind, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FifthCard.getCardNo())
					.getRank().getRank());
		}

		else if (CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.FourOfAKind, CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank());
		}

		// Full House
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.FullHouse, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), CardsInHand.get(eCardNo.FourthCard.getCardNo())
					.getRank().getRank(), 0);
		}

		else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()
				&& CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.FullHouse, CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank(), 0);
		}

		// Flush
		else if (Flush) {
			ScoreHand(eHandStrength.Flush, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, 0);
		}

		// Straight
		else if (Straight) {
			ScoreHand(eHandStrength.Straight, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, 0);
		}

		// Three of a Kind
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.ThreeOfAKind, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FourthCard.getCardNo())
					.getRank().getRank());
		}

		else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.ThreeOfAKind, CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FifthCard.getCardNo())
					.getRank().getRank());
		} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.ThreeOfAKind, CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank());
		}

		// Two Pair
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank())) {
			ScoreHand(eHandStrength.TwoPair, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), CardsInHand.get(eCardNo.ThirdCard.getCardNo())
					.getRank().getRank(), CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank().getRank());
		} else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank())) {
			ScoreHand(eHandStrength.TwoPair, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), CardsInHand.get(eCardNo.FourthCard.getCardNo())
					.getRank().getRank(), CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank());
		} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()
				&& (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank())) {
			ScoreHand(eHandStrength.TwoPair, CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank().getRank(), CardsInHand.get(eCardNo.FourthCard.getCardNo())
					.getRank().getRank(), CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank());
		}

		// Pair
		else if (CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.Pair, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.ThirdCard.getCardNo())
					.getRank().getRank());
		} else if (CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.Pair, CardsInHand.get(eCardNo.SecondCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank());
		} else if (CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.Pair, CardsInHand.get(eCardNo.ThirdCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank());
		} else if (CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank() == CardsInHand.get(eCardNo.FifthCard.getCardNo()).getRank()) {
			ScoreHand(eHandStrength.Pair, CardsInHand.get(eCardNo.FourthCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.FirstCard.getCardNo())
					.getRank().getRank());
		}

		else {
			ScoreHand(eHandStrength.HighCard, CardsInHand.get(eCardNo.FirstCard.getCardNo()).getRank().getRank(), 0, CardsInHand.get(eCardNo.SecondCard.getCardNo())
					.getRank().getRank());
		}
		if(notWild == false){
			//Set hand with wilds to strongest hand
			Hand h = jokerHands.get(0);
			this.HandStrength = h.getHandStrength();
			this.HiHand = h.getHiHand();
			this.LoHand = h.getLoHand();
			this.Kicker = h.getKicker();
			
		}
	}

	private void ScoreHand(eHandStrength hST, int HiHand, int LoHand, int Kicker) {
		this.HandStrength = hST.getHandStrength();
		this.HiHand = HiHand;
		this.LoHand = LoHand;
		this.Kicker = Kicker;
		this.bScored = true;

	}

	/**
	 * Custom sort to figure the best hand in an array of hands
	 */
	public static Comparator<Hand> HandRank = new Comparator<Hand>() {

		public int compare(Hand h1, Hand h2) {

			int result = 0;

			result = h2.HandStrength - h1.HandStrength;

			if (result != 0) {
				return result;
			}
			
			result = h2.HiHand - h1.HiHand;
			if (result != 0) {
				return result;
			}
			
			result = h2.LoHand = h1.LoHand;
			if (result != 0) {
				return result;
			}

			result = h2.Kicker = h1.Kicker;
			if (result != 0) {
				return result;
			}

			return 0;
		}
	};
}
