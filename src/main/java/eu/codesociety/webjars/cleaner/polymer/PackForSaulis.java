package eu.codesociety.webjars.cleaner.polymer;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * https://github.com/Saulis/iron-data-table/
 * <p>
 * As of 1.0.1 I need to exclude the following from Gradle dependencies:
 * <ul>
 * 	<li>github-com-PolymerElements-iron-meta</li>
 * </ul>
 */
public class PackForSaulis implements Function<SortedMap<String, String>, SortedMap<String, String>> {

	/**
	 * TODO switch to regexp so I can remove the directory in one got
	 */
	private static Predicate<Entry<String, String>> demoDirJson =
			e -> e.getKey().contains(".json/demo/");

	/**
	 * TODO switch to regexp so I can remove the directory in one got
	 */
	private static Predicate<Entry<String, String>> testDirJson =
			e -> e.getKey().contains(".json/test/");

	// TODO iron-data-table.png

	/**
	 * Saulis has some more demo data as part of the distribution.
	 */
	private static Predicate<Entry<String, String>> demoData =
			demoDirJson.or(testDirJson);
	/**
	 * Web Component Tester (wct) setup
	 */
	// reused by vaadin
	static Predicate<Entry<String, String>> wctConfJs =
			e -> e.getKey().contains("wct.conf.js/");

	/**
	 * TypeScript configuration?
	 */
	private static Predicate<Entry<String, String>> tsconfig =
			e -> e.getKey().contains("tsconfig.json/");

	/**
	 * Saulis has so metaData
	 */
	private static Predicate<Entry<String, String>> metaData =
			wctConfJs.or(tsconfig);
	/**
	 * github-com-Saulis follows the conventions from the Polymer team
	 */
	@Override
	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map,
				entry -> entry.getKey().contains("/github-com-Saulis"),
				PackForPolymer.polymerConvetions
						.or(demoData)
						.or(metaData),
				this.getClass().getSimpleName());
	}

}
