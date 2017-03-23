package no.rutebanken.nabu.organisation.model;

import com.google.common.base.Joiner;
import org.springframework.util.StringUtils;

public class Id {

	private String codeSpace;

	private String type;

	private String privateCode;

	public static final String SEPARATOR_CHAR = ":";

	public Id(String codeSpace, String type, String privateCode) {
		this.codeSpace = codeSpace;
		this.type = type;
		this.privateCode = privateCode;
	}

	public static Id fromString(String publicId) {
		if (!isValid(publicId)) {
			throw new IllegalArgumentException("Malformed ID: " + publicId);
		}
		String[] parts = publicId.split(":");

		if (parts.length > 2) {
			return new Id(parts[0], parts[1], parts[2]);
		} else if (parts.length == 2) {
			return new Id(null, parts[0], parts[1]);
		}

		return new Id(null, null, publicId);
	}

	private static boolean isValid(String publicId) {
		if (StringUtils.isEmpty(publicId)) {
			return false;
		}
		return true;
	}

	public String getCodeSpace() {
		return codeSpace;
	}

	public String getPrivateCode() {
		return privateCode;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return Joiner.on(SEPARATOR_CHAR).join(codeSpace, privateCode);
	}

}
