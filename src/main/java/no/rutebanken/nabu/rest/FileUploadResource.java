package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.jms.JmsSender;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    JmsSender jmsSender;

    private static final Logger logger = LoggerFactory.getLogger(FileUploadResource.class);

    @POST
    @Path("/{providerId}/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("providerId") Long providerId, @FormDataParam("file") File file, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        logger.info("Placing file '" + fileDetail.getFileName() + "' from provider with id '" + providerId + "' on queue '" + destinationName + "'");
        jmsSender.sendBlobMessage(destinationName, file, fileDetail.getFileName(), providerId);
        logger.info("Done sending.");
        return Response.ok().build();
    }

}
