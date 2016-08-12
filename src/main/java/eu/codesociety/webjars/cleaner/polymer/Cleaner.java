package eu.codesociety.webjars.cleaner.polymer;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Cleaner {

	private static final Logger logger = LoggerFactory.getLogger(Cleaner.class);

	public static SortedMap<String, String> doBlacklist(
			SortedMap<String, String> map, 
			Predicate<Entry<String, String>> identifyPack,
			Predicate<Entry<String, String>> identifyViolation,
			String info) {
		
		logger.info("D Filtered out due to '{}'", info);
		List<String> blacklist = map.entrySet().stream()
				.filter(identifyPack)
				.filter(identifyViolation)
				.peek(e -> logger.info("  - MATCH  {}", e.getKey()))
				.map(Entry::getKey)
				.collect(toList());
				
		return map.entrySet().stream()
				.filter(e -> blacklist.contains(e.getKey()) == false)
				.collect(toMap(
						e -> e.getKey(), 
						e -> e.getValue(), 
						throwingMerger(), 
						TreeMap::new));
	}
	
	/**
	 * <code>http://stackoverflow.com/questions/31004899/java-8-collectors-tomap-sortedmap</code>
	 */
	private static <T> BinaryOperator<T> throwingMerger() {
		return (u, v) -> {
			throw new IllegalStateException(format("Duplicate key %s", u));
		};
	}
}
