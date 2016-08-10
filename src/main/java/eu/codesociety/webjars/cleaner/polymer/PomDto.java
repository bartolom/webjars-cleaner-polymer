package eu.codesociety.webjars.cleaner.polymer;

import java.util.Objects;

public class PomDto {

	private final String location;
	private final String artifactId;
	private final String url;
	private final String simpleBowerId;

	public PomDto(String location, String artifactId, String url) {
		this.location = location;
		this.artifactId = artifactId;
		this.url = Objects.requireNonNull(url, "url can not be null");
		this.simpleBowerId = url.substring(url.lastIndexOf("/") + 1);
	}

	public String getLocation() {
		return location;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getUrl() {
		return url;
	}

	public String getSimpleBowerId() {
		return simpleBowerId;
	}
	
}
