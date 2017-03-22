package no.rutebanken.nabu.organisation.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO {

	public String id;

	public String codeSpace;

	public String privateCode;
}
