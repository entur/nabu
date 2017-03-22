package no.rutebanken.nabu.organisation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = {
		                           @UniqueConstraint(columnNames = {"privateCode", "entityVersion"})
})
public class CodeSpace extends VersionedEntity {

	@NotNull
	@Column(unique = true)
	private String xmlns;
	@NotNull
	@Column(unique = true)
	private String xmlnsUrl;

	public CodeSpace(String typeId, String xmlns, String xmlnsUrl) {
		this.setPrivateCode(typeId);
		this.xmlns = xmlns;
		this.xmlnsUrl = xmlnsUrl;
	}

	public CodeSpace() {
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getXmlnsUrl() {
		return xmlnsUrl;
	}

	public void setXmlnsUrl(String xmlnsUrl) {
		this.xmlnsUrl = xmlnsUrl;
	}

}
