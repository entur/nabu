package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.jms.JmsSender;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.List;


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
    @Path("/uploadFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response testPost1(FormDataMultiPart multiPart) {
        logger.info("Fields " + multiPart.getFields().keySet());

        List<FormDataBodyPart> fileFields = multiPart.getFields("file");
        if (fileFields.size() < 1 ){
            throw new IllegalArgumentException("Missing file field.");
        }
        File file = fileFields.get(0).getValueAs(File.class);

        String fileName = fileFields.get(0).getFormDataContentDisposition().getFileName();
        //TODO validate fileName?

        List<FormDataBodyPart> providerIdFields = multiPart.getFields("providerId");
        if (providerIdFields.size() < 1 ){
            throw new IllegalArgumentException("Missing providerId field.");
        }
        String providerId = providerIdFields.get(0).getValue();
        logger.debug("providerId: " + providerId);

        logger.info("Placing on queue '" + destinationName + "'");
        jmsSender.sendBlobMessage(destinationName, file, fileName, providerId);
        logger.info("Done sending.");
        return Response.ok().build();
    }

}
