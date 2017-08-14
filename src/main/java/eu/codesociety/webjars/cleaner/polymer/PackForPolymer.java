package eu.codesociety.webjars.cleaner.polymer;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Covers:
 * <ul>
 *   <li>https://github.com/webcomponents/webcomponentsjs</li>
 *   <li>https://github.com/Polymer/polymer</li>
 *   <li>https://github.com/PolymerElements/*</li>
 * </ul>
 */
public class PackForPolymer implements 
		Function<SortedMap<String, String>, SortedMap<String, String>>
		// WebjarFunction<SortedMap<String, String>, SortedMap<String, String>>
{

	private static Predicate<Entry<String, String>> testDir = 
			e -> e.getKey().contains("/test/");
			
	private static Predicate<Entry<String, String>> testsDir = 
			e -> e.getKey().contains("/tests/");
	
	private static Predicate<Entry<String, String>> classesDir = 
			e -> e.getKey().contains("/classes/");
			
	private static Predicate<Entry<String, String>> demoDir = 
			e -> e.getKey().contains("/demo/");
			
	private static Predicate<Entry<String, String>> examplesDir = 
			e -> e.getKey().contains("/examples/");
		
			
	/**
	 * The PolymerElements have often test, demos and some odd classes directories
	 * Which contain files which might be the same like the real HTML custom element
	 * we are interested in.
	 */
	private static Predicate<Entry<String, String>> polymerDirs = 
			testDir.or(testsDir).or(classesDir).or(demoDir).or(examplesDir);
	
	private static Predicate<Entry<String, String>> buildLog = 
			e -> e.getKey().contains("build.log/");
	
	private static Predicate<Entry<String, String>> jsMap = 
			e -> e.getKey().contains(".js.map/");
	
	private static Predicate<Entry<String, String>> esLint = 
			e -> e.getKey().contains(".eslintrc.json/");
	
			
	/**
	 * Many third party projects follow the same conventions that the Polymer team
	 * is using therefore we can reuse this.
	 */
	public static Predicate<Entry<String, String>> polymerConventions = 
			polymerDirs
					.or(buildLog)
					.or(jsMap)
					.or(esLint)
					.or(CommonViolations.githubDocumentation)
					.or(CommonViolations.metaData);
	
	// TODO by convention there is only one custom element per html file in the
	// Polymer project. The web component standard requires that there must be a 
	// dash/minus "-" in a custom element. Therefore we could filter out all
	// html files that do not contain a "-". 
	// But this relies on the discipline of the contributors. Nothings stops you
	// create allmycomponents.html and then add multiple correct custom elements
	// in there.
	
	/**
	 * Because Polymer is the main use case for these packs and cleaners. It will
	 * also cover the webcomponentsjs polyfill
	 * 
	 * @param entry
	 * @return
	 */
	private static boolean identify(Entry<String, String> entry) {
		return entry.getKey().toLowerCase().contains("/github-com-polymer")
				|| entry.getKey().toLowerCase().contains("/polymer/webjars")
				|| entry.getKey().toLowerCase().contains("/github-com-webcomponents")
				|| entry.getKey().toLowerCase().contains("/webcomponentsjs");
	}
	
	@Override
	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map, 
				PackForPolymer::identify,
				polymerConventions,
				this.getClass().getSimpleName());
	}
	
}
