package no.rutebanken.nabu.organization.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO {

	public String id;

	public long version;

	public String codeSpace;

	public String privateCode;
}
