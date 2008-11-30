package backprop;

import junit.framework.TestCase;

public class TestRanges extends TestCase {

	int[] vals = { -5, 0, 1, 6 };
	ScoreRange scoreRange = new ScoreRange(vals);

	public void testVals() {
		try {
			assertEquals(0, scoreRange.getPlace(-6));
			assertEquals(1, scoreRange.getPlace(-5));
			assertEquals(1, scoreRange.getPlace(-1));
			assertEquals(2, scoreRange.getPlace(0));
			assertEquals(3, scoreRange.getPlace(1));
			assertEquals(3, scoreRange.getPlace(5));
			assertEquals(4, scoreRange.getPlace(6));
			

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
