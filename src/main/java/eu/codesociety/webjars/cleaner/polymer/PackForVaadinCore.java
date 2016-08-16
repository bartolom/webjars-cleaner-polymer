package eu.codesociety.webjars.cleaner.polymer;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class PackForVaadinCore implements Function<SortedMap<String, String>, SortedMap<String, String>> {

	private static Predicate<Entry<String, String>> demoDir =
			e -> e.getKey().contains("/demo/");

	private static Predicate<Entry<String, String>> angularDir =
			e -> e.getKey().contains("/directives/");

	private static Predicate<Entry<String, String>> docsDir =
			e -> e.getKey().contains("/docs/");

	private static Predicate<Entry<String, String>> javaDir =
			e -> e.getKey().contains("/java/");

	private static Predicate<Entry<String, String>> testDir =
			e -> e.getKey().contains("/test/");

	/**
	 * Vaadin is based on Java code and the Vaadin projects adds a lot of demo data
	 * as well-
	 */
	private static Predicate<Entry<String, String>> vaadinPackage =
			javaDir.or(demoDir).or(testDir).or(docsDir).or(angularDir);

	private static Predicate<Entry<String, String>> dotGemini =
			e -> e.getKey().startsWith(".gemini.yml/");

	private static Predicate<Entry<String, String>> dotJscsrc =
			e -> e.getKey().startsWith(".jscsrc/");

	private static Predicate<Entry<String, String>> dotJshintrc =
				e -> e.getKey().startsWith(".jshintrc/");

	private static Predicate<Entry<String, String>> dotNpmignore =
					e -> e.getKey().startsWith(".npmignore/");

	/**
	 * Vaadin contains some hidden dot-something file
	 * as well-
	 */
	private static Predicate<Entry<String, String>> vaadinHiddenFiles =
			dotGemini.or(dotJscsrc).or(dotJshintrc).or(dotNpmignore);


	private static Predicate<Entry<String, String>> screenshot =
			e -> e.getKey().startsWith("screenshot.png/");


	@Override
	public SortedMap<String, String> apply(SortedMap<String, String> map) {
		return Cleaner.doBlacklist(
				map,
				entry -> entry.getKey().contains("/github-com-vaadin"),
				vaadinPackage
						.or(vaadinHiddenFiles)
						.or(screenshot)
						.or(PackForSaulis.wctConfJs)
						.or(CommonViolations.githubDocumentation)
						.or(CommonViolations.metaData),
				this.getClass().getSimpleName());
	}

}
