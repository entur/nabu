package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organization.rest.dto.CodeSpaceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/code_spaces")
@Transactional
public class CodeSpaceResource {


	@Autowired
	private CodeSpaceRepository repository;

	@POST
	public void createCodeSpace(CodeSpaceDTO dto) {
		repository.save(fromDTO(dto, new CodeSpace()));
	}

	@PUT
	@Path("{id}")
	public void updateCodeSpace(@PathParam("id") String id, CodeSpaceDTO dto) {
		CodeSpace codeSpace = repository.getOneByPublicId(id);
		repository.save(fromDTO(dto, codeSpace));
	}

	@GET
	@Path("{id}")
	public CodeSpaceDTO getCodeSpace(@PathParam("id") String id) {
		CodeSpace codeSpace = repository.getOneByPublicId(id);
		return toDTO(codeSpace);
	}

	@GET
	public List<CodeSpaceDTO> listAllCodeSpaces() {
		return repository.findAll().stream().map(r -> toDTO(r)).collect(Collectors.toList());
	}

	private CodeSpaceDTO toDTO(CodeSpace codeSpace) {
		return new CodeSpaceDTO(codeSpace.getId(), codeSpace.getXmlns(), codeSpace.getXmlnsUrl());
	}

	private CodeSpace fromDTO(CodeSpaceDTO dto, CodeSpace codeSpace) {
		codeSpace.setPrivateCode(dto.id);
		codeSpace.setXmlns(dto.xmlns);
		codeSpace.setXmlnsUrl(dto.xmlnsUrl);
		return codeSpace;
	}
}
