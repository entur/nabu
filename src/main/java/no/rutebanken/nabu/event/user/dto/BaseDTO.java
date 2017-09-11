package no.rutebanken.nabu.event.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseDTO {

	public String id;

	public String codeSpace;

	public String privateCode;

	public String getPrivateCode() {
		return privateCode;
	}
}
