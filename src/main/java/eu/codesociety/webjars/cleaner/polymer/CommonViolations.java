package eu.codesociety.webjars.cleaner.polymer;

import java.util.Map.Entry;
import java.util.function.Predicate;

public class CommonViolations {
			
	private static Predicate<Entry<String, String>> markdown = 
			e -> e.getKey().contains(".md/");
					
	private static Predicate<Entry<String, String>> indexHtml = 
			e -> e.getKey().contains("index.html/");
	
	/**
	 * Github pages usually have markdown files. Also a index.html 
	 * seems never be a web component
	 * 
	 */
	public static Predicate<Entry<String, String>> githubDocumentation = 
			indexHtml.or(markdown);
			
			
	private static Predicate<Entry<String, String>> bowerJson = 
			e -> e.getKey().contains("bower.json/");
			
	private static Predicate<Entry<String, String>> packageJson = 
			e -> e.getKey().contains("package.json/");
			
	private static Predicate<Entry<String, String>> dotTravis = 
			e -> e.getKey().contains(".travis.yml/");
			
	private static Predicate<Entry<String, String>> pomProperties = 
			e -> e.getKey().contains("pom.properties/");
	
	private static Predicate<Entry<String, String>> pomXml = 
			e -> e.getKey().contains("pom.xml/");
			
	/**
	 * There is typical JavaScript and WebJars related meta data (pom)
	 * stored in various files. Which seems common to many web components.
	 */
	public static Predicate<Entry<String, String>> metaData = 
			bowerJson.or(packageJson).or(dotTravis).or(pomProperties).or(pomXml);
	
	private static Predicate<Entry<String, String>> cssScss = 
			e -> e.getKey().contains(".scss/");
	
	private static Predicate<Entry<String, String>> cssLess = 
			e -> e.getKey().contains(".less/");
			
			
	/**
	 * CSS preprocessing files are only useful on the server side, so no need
	 * to serve them
	 */
	public static Predicate<Entry<String, String>> cssPreprocessing = 
			cssScss.or(cssLess);
			
}
