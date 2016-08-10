package eu.codesociety.webjars.cleaner.polymer;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class PomValueExtractor {

	/**
	 * Considered thread safe after initialization.
	 * <code>http://www.cowtowncoder.com/blog/archives/2006/06/entry_2.html</code>
	 */
	private static final XMLInputFactory factory = XMLInputFactory.newInstance();

	/**
	 * @see #parseAllWebjarPomXmlFiles(SortedMap, ClassLoader)
	 */
	static SortedMap<String, PomDto> parseAllWebjarPomXmlFiles(SortedMap<String, String> fullPathIndex) {
		return parseAllWebjarPomXmlFiles(fullPathIndex, PomValueExtractor.class.getClassLoader());
	}

	/**
	 * We try to extract entries like:
	 *
	 * <pre>
	 * pom.xml/github-com-PolymerElements-paper-spinner/org.webjars.bower/maven/META-INF/
	 * pom.xml/github-com-PolymerElements-platinum-bluetooth/org.webjars.bower/maven/META-INF/
	 * </pre>
	 *
	 * @param fullPathIndex
	 *            typically from
	 *            {@link WebJarAssetLocator#getFullPathIndex(java.util.regex.Pattern, ClassLoader...)}
	 * @param because
	 *            {@link WebJarAssetLocator#getFullPathIndex(java.util.regex.Pattern, ClassLoader...)}
	 *            can be configured with a specific {@link ClassLoader} it
	 *            should also be used here. We currently only cover the case
	 *            with a single one.
	 */
	static SortedMap<String, PomDto> parseAllWebjarPomXmlFiles(SortedMap<String, String> fullPathIndex,
			ClassLoader classLoader) {
		return fullPathIndex.entrySet().stream()
				.filter(e -> e.getKey().endsWith("org.webjars.bower/maven/META-INF/"))
				.filter(e -> e.getKey().startsWith("pom.xml"))
				.map(e -> extractStrings(e.getValue(), classLoader))
				.collect(toMap(dto -> dto.getArtifactId(), dto -> dto, throwingMerger(), TreeMap::new));
	}

	/**
	 * @param location
	 *            absolute path within the {@link ClassLoader}
	 * @param classLoader
	 *            will be used to
	 *            {@link ClassLoader#getResourceAsStream(String)}
	 * @return
	 */
	static PomDto extractStrings(String location, ClassLoader classLoader) {
		try (InputStream inputStream = classLoader.getResourceAsStream(location)) {
			return extractStrings(inputStream, location);
		} catch (IOException e) {
			throw new IllegalStateException(format("The pom.xml parsing failed %s", location), e);
		}
	}

	/**
	 * Extract information from a pom.xml into the {@link PomDto}.
	 * <p>
	 * Try-with-resource so its automatically closed once we return in the
	 * middle of the file.
	 * 
	 * @param it
	 *            the responsibility of the caller to close the input stream
	 * @param location
	 *            just used for information in exceptions messages
	 */
	static PomDto extractStrings(InputStream inputStream, String location) {
		XMLStreamReader reader = null;
		try {
			reader = factory.createXMLStreamReader(inputStream, "UTF-8");

			String url = null;
			String artifactId = null;
			Boolean isArtifactId = false;
			Boolean isScm = false;
			Boolean isScmUrl = false;

			while (reader.hasNext()) {
				int event = reader.next();

				switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					if ("artifactId".equals(reader.getLocalName())) {
						isArtifactId = true;
					} else if ("scm".equals(reader.getLocalName())) {
						isScm = true;
					} else if ("url".equals(reader.getLocalName()) && isScm) {
						isScmUrl = true;
					}
					break;

				case XMLStreamConstants.CHARACTERS:
					if (isArtifactId) {
						artifactId = reader.getText().trim();
					} else if (isScmUrl) {
						url = reader.getText().trim();
					}
					break;

				case XMLStreamConstants.END_ELEMENT:
					if ("artifactId".equals(reader.getLocalName())) {
						isArtifactId = false;
					} else if ("scm".equals(reader.getLocalName())) {
						isScm = false;
					} else if ("url".equals(reader.getLocalName()) && isScmUrl) {
						requireNonNull(artifactId,
								format("Failed to extract the 'artifactId' from the pom.xml file %s", location));
						requireNonNull(url,
								format("Failed to extract the 'scm.url' from the pom.xml file %s", location));
						return new PomDto(null, artifactId, url);
					}
					break;
				}
			}
		} catch (XMLStreamException e1) {
			throw new IllegalStateException(format("The pom.xml parsing failed %s", location), e1);
		} finally {
			if (reader != null) {
				try {
					// does not close the inputStream, but helps the parser to
					// be efficient
					reader.close();
				} catch (XMLStreamException e) {
					// do nothing
				}
			}
		}
		throw new IllegalStateException(
				format("The pom.xml parsing failed, we must have found the desired tags earlier. %s", location));
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
