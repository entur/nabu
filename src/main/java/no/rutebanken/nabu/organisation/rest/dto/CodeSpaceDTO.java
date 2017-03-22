package no.rutebanken.nabu.organisation.rest.dto;

public class CodeSpaceDTO extends BaseDTO {

	public String xmlns;
	public String xmlnsUrl;

	public CodeSpaceDTO(String id, String xmlns, String xmlnsUrl) {
		this.id = id;
		this.xmlns = xmlns;
		this.xmlnsUrl = xmlnsUrl;
	}

	public CodeSpaceDTO() {
	}
}
