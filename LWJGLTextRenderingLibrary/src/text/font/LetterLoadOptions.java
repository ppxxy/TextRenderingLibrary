package text.font;

import java.util.Iterator;

public class LetterLoadOptions implements Iterable<Character>{
	
	private final Iterable<Character>[] contents;
	
	/**
	 * Letters 9 and 10
	 */
	public static final LetterGroup TAB_AND_LINE_CHANGE = new LetterGroup(9, 10);
	
	/**
	 * Contains letters 9, 10 and 32-255
	 */
	public static final LetterLoadOptions EXTENDED_LETTER_GROUP = new LetterLoadOptions(
			TAB_AND_LINE_CHANGE, new LetterGroup(32, 255));
	
	/**
	 * Contains letters 9, 10 and 32-127
	 */
	public static final LetterLoadOptions ASCII = new LetterLoadOptions(
			TAB_AND_LINE_CHANGE, new LetterGroup(32, 127));
	
	/**
	 * You can load letters that you need by specifying {@code Iterable<Character>} objects.<br>
	 * For this purpose there are {@link text.font.LetterLoadOptions.SingleLetter SingleLetter} and {@link text.font.LetterLoadOptions.LetterGroup LetterGroup} objects, which can be
	 * used to easily specify letters you need.
	 * @param characterContent
	 */
	@SafeVarargs
	public LetterLoadOptions(Iterable<Character>... characterContent) {
		this.contents = characterContent;
	}
	
	@Override
	public Iterator<Character> iterator() {
		return new Iterator<Character>() {

			int index = 0;
			Iterator<Character> currentIterator;
			
			@Override
			public boolean hasNext() {
				if(currentIterator != null && currentIterator.hasNext()) {
					return true;
				} else if(index < contents.length) {
					currentIterator = contents[index++].iterator();
					return true;
				}
				return false;
			}

			@Override
			public Character next() {
				return currentIterator.next();
			}
			
		};
	}
	
	/**
	 * An iterator that holds a single character. The only purpose of this is to easen the usage of {@link text.font.LetterLoadOptions LetterLoadOptions}.
	 * @author Kim Rautio
	 *
	 */
	public static class SingleLetter implements Iterable<Character>{

		private final Character c;
		
		public SingleLetter(Character c) {
			this.c = c;
		}
		
		public SingleLetter(int i) {
			this.c = (char) i;
		}
		
		@Override
		public Iterator<Character> iterator() {
			return new Iterator<Character>() {

				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Character next() {
					return c;
				}
				
			};
		}
		
	}
	
	/**
	 * An iterator that returns characters from {@code start} to {@code end}.
	 * @author Kim Rautio
	 *
	 */
	public static class LetterGroup implements Iterable<Character>{

		private final char start, end;
		
		public LetterGroup(char start, char end) {
			this.start = start;
			this.end = end;
		}
		
		public LetterGroup(int start, int end) {
			this.start = (char) start;
			this.end = (char) end;
		}
		
		@Override
		public Iterator<Character> iterator() {
			return new Iterator<Character>() {

				char currentCharacter = (char) (start-1);
				
				@Override
				public boolean hasNext() {
					return currentCharacter < end;
				}

				@Override
				public Character next() {
					return ++currentCharacter;
				}
				
			};
		}
		
	}
}
