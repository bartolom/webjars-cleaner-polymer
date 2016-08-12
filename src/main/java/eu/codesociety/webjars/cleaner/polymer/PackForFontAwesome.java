package eu.codesociety.webjars.cleaner.polymer;

import java.util.SortedMap;
import java.util.function.Function;

public class PackForFontAwesome implements Function<SortedMap<String, String>, SortedMap<String, String>>{

	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map, 
				entry -> entry.getKey().contains("/github-com-Saulis"),
				CommonViolations.cssPreprocessing
						.and(CommonViolations.githubDocumentation)
						.and(CommonViolations.metaData),
				this.getClass().getSimpleName());
	}
	
}
