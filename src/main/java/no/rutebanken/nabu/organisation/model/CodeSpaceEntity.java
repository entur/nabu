package no.rutebanken.nabu.organisation.model;

import com.google.common.base.Joiner;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@MappedSuperclass
public abstract class CodeSpaceEntity extends VersionedEntity {

	@NotNull
	@JoinColumn(name = "code_space_pk")
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
