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

	private static Predicate<Entry<String, String>> testDirHtml = 
			e -> e.getKey().contains(".html/test/");
	
	private static Predicate<Entry<String, String>> classesDirHtml = 
			e -> e.getKey().contains(".html/classes/");
			
	private static Predicate<Entry<String, String>> demoDirHtml = 
			e -> e.getKey().contains(".html/demo/");
			
	private static Predicate<Entry<String, String>> demoSrcDir = 
			e -> e.getKey().contains("/src/demo/");
	
	/**
	 * The PolymerElements have often test, demos and some odd classes directories
	 * Which contain files which might be the same like the real HTML custom element
	 * we are interested in.
	 */
	private static Predicate<Entry<String, String>> polymerDirs = 
			testDirHtml.or(classesDirHtml).or(demoDirHtml).or(demoSrcDir);
	
	private static Predicate<Entry<String, String>> buildLog = 
			e -> e.getKey().contains("build.log/");
			
	/**
	 * Many third party projects follow the same conventions that the Polymer team
	 * is using therefore we can reuse this.
	 */
	public static Predicate<Entry<String, String>> polymerConvetions = 
			polymerDirs
					.or(buildLog)
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
				|| entry.getKey().toLowerCase().contains("/github-com-webcomponents-webcomponentsjs")
				|| entry.getKey().toLowerCase().contains("/webcomponentsjs");
	}
	
	@Override
	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map, 
				PackForPolymer::identify,
				polymerConvetions,
				this.getClass().getSimpleName());
	}
	
}
