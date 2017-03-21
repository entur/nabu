package no.rutebanken.nabu.organization.model;

import com.google.common.base.Joiner;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class CodeSpaceEntity extends VersionedEntity {

	@NotNull
	@ManyToOne
	private CodeSpace codeSpace;


	public CodeSpace getCodeSpace() {
		return codeSpace;
	}

	public void setCodeSpace(CodeSpace codeSpace) {
		this.codeSpace = codeSpace;
	}


	@Override
	public String getId() {
		return Joiner.on(":").join(codeSpace.getXmlns(), getType(), getPrivateCode());
	}


}
