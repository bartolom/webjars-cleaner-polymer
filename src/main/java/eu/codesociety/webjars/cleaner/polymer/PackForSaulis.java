package eu.codesociety.webjars.cleaner.polymer;

import java.util.SortedMap;
import java.util.function.Function;

/**
 * https://github.com/Saulis/iron-data-table/
 */
public class PackForSaulis implements Function<SortedMap<String, String>, SortedMap<String, String>> {
	
	/**
	 * github-com-Saulis follows the conventions from the Polymer team
	 */
	@Override
	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map, 
				entry -> entry.getKey().contains("/github-com-Saulis"),
				PackForPolymer.polymerConvetions,
				this.getClass().getSimpleName());
	}
	
}
