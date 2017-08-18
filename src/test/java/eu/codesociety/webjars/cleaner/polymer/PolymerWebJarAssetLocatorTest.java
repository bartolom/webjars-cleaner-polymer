package eu.codesociety.webjars.cleaner.polymer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;
import org.webjars.MultipleMatchesException;

/**
 * Tests for {@link PolymerWebJarAssetLocator}
 */
public class PolymerWebJarAssetLocatorTest {

	@Test
	public void testReversePath2() throws Exception {
		assertThat(PolymerWebJarAssetLocator.reversePath2(""), 
				equalTo("/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("aaa"), 
				equalTo("aaa/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("/aaa"), 
				equalTo("aaa/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("/aaa/"), 
				equalTo("aaa/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("/aaa/bbb"), 
				equalTo("bbb/aaa/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("/aaa/bbb/ccc"), 
				equalTo("ccc/bbb/aaa/"));
		assertThat(PolymerWebJarAssetLocator.reversePath2("/aaa//bbb//ccc"), 
				equalTo("ccc/bbb/aaa/"));
	}
	
	@Test(expected = NullPointerException.class)
	public void testReversePath2_null() throws Exception {
		PolymerWebJarAssetLocator.reversePath2(null);
	}
	
	@Test
	public void testGetFullPath2_happyPath() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageA", "one");
		pathIndex.put("zzz", "after");
		
		// ===== expect
		assertThat(PolymerWebJarAssetLocator.getFullPath2(pathIndex, "aaa/bbb/ccc"), 
				equalTo("one"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetFullPath2_none() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("zzz", "after");
		
		// ===== throws 
		PolymerWebJarAssetLocator.getFullPath2(pathIndex, "aaa/bbb/ccc");
	}
	
	@Test
	public void testGetFullPath2_multipleMatches() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageA", "one");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageB", "two");
		pathIndex.put("ccc/bbb/aaa/v1.1/packageA", "three");
		pathIndex.put("zzz", "after");
		
		// ===== expect throw
		try {
			PolymerWebJarAssetLocator.getFullPath2(pathIndex, "aaa/bbb/ccc");
			fail("We expect a MultipleMatchesException");
		} catch (MultipleMatchesException e) {
			assertThat(e.getMessage(), containsString("aaa/bbb/ccc"));
			assertThat(e.getMatches().size(), is(3));
			assertThat(e.getMatches(), hasItem("one"));
			assertThat(e.getMatches(), hasItem("two"));
			assertThat(e.getMatches(), hasItem("three"));
		}
	}
	
	
	@Test
	public void testCandidates_happyPath() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageA", "one");
		pathIndex.put("zzz", "after");
		
		// ===== when 
		List<String> candidates = PolymerWebJarAssetLocator.candidates(pathIndex, "ccc/bbb/aaa/");
		
		// ===== expect
		assertThat(candidates.size(), is(1));
		assertThat(candidates.get(0), equalTo("one"));
	}
	
	@Test
	public void testCandidates_none() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("zzz", "after");
		
		// ===== when 
		List<String> candidates = PolymerWebJarAssetLocator.candidates(pathIndex, "ccc/bbb/aaa/");
		
		// ===== expect
		assertThat(candidates.size(), is(0));
	}
	
	@Test
	public void testCandidates_multipleMatches() throws Exception {
		// ===== given
		SortedMap<String, String> pathIndex = new TreeMap<>();
		pathIndex.put("aaa", "before");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageA", "one");
		pathIndex.put("ccc/bbb/aaa/v1.0/packageB", "two");
		pathIndex.put("ccc/bbb/aaa/v1.1/packageA", "three");
		pathIndex.put("zzz", "after");
		
		// ===== when 
		List<String> candidates = PolymerWebJarAssetLocator.candidates(pathIndex, "ccc/bbb/aaa/");
		
		// ===== expect
		assertThat(candidates.size(), is(3));
		assertThat(candidates.get(0), equalTo("one"));
		assertThat(candidates.get(1), equalTo("two"));
		assertThat(candidates.get(2), equalTo("three"));
	}
}
