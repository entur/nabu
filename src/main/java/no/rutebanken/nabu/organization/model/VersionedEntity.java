package no.rutebanken.nabu.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.api.client.repackaged.com.google.common.base.Joiner;

import javax.persistence.*;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@MappedSuperclass
public abstract class VersionedEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long pk;

	@Version
	@JsonIgnore
	// Version for optimistic locking
	private Long lockVersion = Long.valueOf(1);

	@NotNull
	// Publicly exposed version of entity
	private Long entityVersion = Long.valueOf(1);

	@JsonIgnore
	protected String getType() {
		return getClass().getSimpleName();
	}

	@NotNull
	private String privateCode;

	public String getPrivateCode() {
		return privateCode;
	}

	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}

	public String getId() {
		return getPrivateCode();
	}

	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
	}

	public Long getLockVersion() {
		return lockVersion;
	}

	public void setLockVersion(Long lockVersion) {
		this.lockVersion = lockVersion;
	}

	public Long getEntityVersion() {
		return entityVersion;
	}

	public void setEntityVersion(Long entityVersion) {
		this.entityVersion = entityVersion;
	}
}
