package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.jms.JmsSender;
import no.rutebanken.nabu.repository.StatusRepository;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;


@Component
@Produces("application/json")
@Path("/opstatus")
public class FileUploadResource {

    @Value("${queue.gtfs.upload.destination.name}")
    private String destinationName;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    JmsSender jmsSender;

    private static final Logger logger = LoggerFactory.getLogger(FileUploadResource.class);

    @POST
    @Path("/{providerId}/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("providerId") Long providerId, @FormDataParam("file") File file, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String correlationId =  "" + System.currentTimeMillis();
        String fileName = fileDetail.getFileName();;
        try {
            logger.info("Placing file '" + fileDetail.getFileName() + "' from provider with id '" + providerId + "' on queue '" + destinationName + "'");
            jmsSender.sendBlobMessage(destinationName, file, fileName, providerId, correlationId);
            logger.info("Done sending.");
            statusRepository.update(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.STARTED, correlationId));
            return Response.ok().build();
        } catch (RuntimeException e){
            statusRepository.update(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.FAILED, correlationId));
            return Response.serverError().build();
        }
    }

}
