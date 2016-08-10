package eu.codesociety.webjars.cleaner.polymer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.SortedMap;
import java.util.regex.Pattern;

import org.junit.Test;
import org.webjars.WebJarAssetLocator;

/**
 * Tests for {@link PomValueExtractor}
 */
public class PomValueExtractorTest {

	private static final String SIMPLIFIED_PLATINUM_BLUETOOTH = 
			"platinum-bluetooth";

	private static final String QUALIFIED_PLATINUM_BLUETOOTH = 
			"github-com-PolymerElements-platinum-bluetooth";

	private static final String PLATINUN_BLUETOOTH_POM_XML = 
			"META-INF/maven/org.webjars.bower/github-com-PolymerElements-platinum-bluetooth/pom.xml";

	/**
	 * We know that platinum-bluetooth is on the class path because we have
	 * explicitly added it as a testCompile dependency in Gradle.
	 */
	@Test
	public void testParseAllWebjarPomXmlFiles() throws Exception {

		SortedMap<String, String> fullPathIndex = WebJarAssetLocator.getFullPathIndex(
				Pattern.compile(".*"),
				this.getClass().getClassLoader());
		
		SortedMap<String, PomDto> pomMap =
				PomValueExtractor.parseAllWebjarPomXmlFiles(fullPathIndex);

		assertNotNull(pomMap);
		assertThat(pomMap.keySet().isEmpty(), is(false));
		assertThat(pomMap.containsKey(QUALIFIED_PLATINUM_BLUETOOTH), is(true));

		PomDto dto = pomMap.get(QUALIFIED_PLATINUM_BLUETOOTH);
		assertThat(dto.getArtifactId(), equalTo(QUALIFIED_PLATINUM_BLUETOOTH));
		assertThat(dto.getUrl(), equalTo("https://github.com/PolymerElements/platinum-bluetooth"));
		assertThat(dto.getSimpleBowerId(), equalTo(SIMPLIFIED_PLATINUM_BLUETOOTH));

	}
	
	@Test
	public void testExtractStringsFromClassPath() throws Exception {
		PomDto dto = PomValueExtractor.extractStrings(
				this.getClass().getResourceAsStream("platinum-bluetooth-2.1.1-pom.xml"),
				"platinum-bluetooth-2.1.1-pom.xml");

		assertThat(dto.getArtifactId(), equalTo("github-com-PolymerElements-platinum-bluetooth"));
		assertThat(dto.getUrl(), equalTo("https://github.com/PolymerElements/platinum-bluetooth"));
		assertThat(dto.getSimpleBowerId(), equalTo("platinum-bluetooth"));
	}
	
	@Test
	public void testExtractStringsFromWebjar() throws Exception {
		PomDto dto = PomValueExtractor.extractStrings(
				PLATINUN_BLUETOOTH_POM_XML, 
				this.getClass().getClassLoader());

		assertThat(dto.getArtifactId(), equalTo("github-com-PolymerElements-platinum-bluetooth"));
		assertThat(dto.getUrl(), equalTo("https://github.com/PolymerElements/platinum-bluetooth"));
		assertThat(dto.getSimpleBowerId(), equalTo("platinum-bluetooth"));
	}

}
