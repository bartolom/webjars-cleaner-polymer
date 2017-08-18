package eu.codesociety.webjars.cleaner.polymer;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.webjars.MultipleMatchesException;
import org.webjars.WebJarAssetLocator;

public class PolymerWebJarAssetLocator extends WebJarAssetLocator {

	private final Function<String, String> tweak;

	public PolymerWebJarAssetLocator(SortedMap<String, String> fullPathIndex, Function<String, String> tweak) {
		super(fullPathIndex);
		this.tweak = (tweak != null) ? tweak : (noop) -> noop;
	}

	@Override
	public String getFullPath(final String partialPath) {
		return getFullPath2(getFullPathIndex(), partialPath);
	}

	@Override
	public String getFullPath(final String webjar, final String partialPath) {
		return getFullPath2(filterPathIndexByPrefixWithStream(getFullPathIndex(),
				WEBJARS_PATH_PREFIX + "/" + tweak.apply(webjar) + "/"), partialPath);
	}

	private SortedMap<String, String> filterPathIndexByPrefixWithStream(SortedMap<String, String> pathIndex,
			String prefix) {
		return pathIndex.entrySet().stream().filter(e -> e.getValue().startsWith(prefix))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), throwingMerger(), TreeMap::new));
	}

	/**
	 * This is a rewrite of
	 * <code>org.webjars.WebJarAssetLocator.getFullPath(SortedMap<String, String>, String)</code>
	 * the original implementation is private.
	 * 
	 * @param pathIndex
	 *            the full index managed by WebJars
	 * @param partialPath
	 *            "aaa/bbb/ccc/"
	 * @return "aaa/bbb/ccc/1.0/someJavaScriptProject"
	 */
	// exposed at package scope for unit testing
	static String getFullPath2(SortedMap<String, String> pathIndex, String partialPath) {
		String reversed = reversePath2(partialPath);
		List<String> candidates = candidates(pathIndex, reversed);
		if (candidates.isEmpty()) {
			throw new IllegalArgumentException(
					format("'%s' could not be found. Pleaase make sure the WebJar is really added", partialPath));
		}
		return (candidates.size() == 1)
				? candidates.get(0)
				: throwAmbigious(candidates, partialPath);

	}

	// exposed at package scope for unit testing
	static List<String> candidates(SortedMap<String, String> pathIndex, String reversed) {
		return pathIndex.tailMap(reversed).entrySet().stream()
				.filter(e -> e.getKey().startsWith(reversed))
				.map(e -> e.getValue())
				.collect(toList());
	}

	private static String throwAmbigious(List<String> candidates, String partialPath) {
		throw new MultipleMatchesException(format(
				"Multiple (actually %s) matches found for partialPath '%s'. Please request a more specific path. For instance one containing a version number.",
				candidates.size(), partialPath), candidates);
	}

	/**
	 * This is a reimplementation of
	 * <code>org.webjars.WebJarAssetLocator.reversePath(String)</code> because the
	 * original implementation is private.
	 * <p>
	 * Extended it to be more of a general purpose method with test coverage.
	 * 
	 * @param path
	 *            something like "/aaa/bbb/ccc"
	 * @return "ccc/bbb/aaa/" with always a slash at the end. It will also remove
	 *         unnecessary double slashes.
	 */
	// exposed at package scope for unit testing
	static String reversePath2(String path) {
		Objects.requireNonNull(path, "path can not be null");
		if (path.isEmpty()) {
			return "/";
		}
		String[] fragments = path.startsWith("/")
				? path.substring(1).split("/")
				: path.split("/");
		StringBuilder sb = new StringBuilder();
		for (int i = fragments.length - 1; i >= 0; i--) {
			if (fragments[i].length() > 0) {
				sb.append(fragments[i]);
				sb.append("/");
			}
		}
		return sb.toString();
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
