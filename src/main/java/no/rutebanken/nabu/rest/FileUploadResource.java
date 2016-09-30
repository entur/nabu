package no.rutebanken.nabu.rest;

import com.google.cloud.storage.Storage;
import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.jms.JmsSender;
import no.rutebanken.nabu.repository.ProviderRepository;
import no.rutebanken.nabu.repository.StatusRepository;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.rutebanken.helper.gcp.BlobStoreHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
@Produces("application/json")
@Path("/files")
public class FileUploadResource {

    @Value("${queue.upload.destination.name}")
    private String destinationName;

    @Value("${blobstore.gcs.credential.path}")
    private String credentialPath;

    @Value("${blobstore.gcs.project.id}")
    private String projectId;

    @Value("${blobstore.gcs.container.name}")
    private String containerName;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    JmsSender jmsSender;

    private static final Logger logger = LoggerFactory.getLogger(FileUploadResource.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @POST
    @Path("/{providerId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@PathParam("providerId") Long providerId, @FormDataParam("file") File file, @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String correlationId =  "" + System.currentTimeMillis();
        if (fileDetail == null || fileDetail.getFileName() == null){
            logger.debug("Missing fileName info");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String fileName = fileDetail.getFileName();
        try {
            logger.info("Placing file '" + fileDetail.getFileName() + "' from provider with id '" + providerId + "' in blob store.");
            Provider provider = providerRepository.getProvider(providerId);
            Storage storage = BlobStoreHelper.getStorage(credentialPath, projectId);
            String referential = provider.chouetteInfo.referential;
            LocalDateTime dateTime = LocalDateTime.now();
            String timeStamp = dateTime.format(formatter);
            String blobName = "inbound/" + referential + "/" + referential + "-" + timeStamp + "-" + fileName;
            logger.info("Blob created is: " + blobName);
            BlobStoreHelper.uploadBlob(storage, containerName, blobName, new FileInputStream(file), false);
            logger.info("Notifying queue '" + destinationName + "' about the uploaded file.");
            jmsSender.sendBlobNotificationMessage(destinationName, blobName, fileName, providerId, correlationId);
            logger.info("Done sending.");
            statusRepository.add(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.STARTED, correlationId,Date.from(Instant.now(Clock.systemDefaultZone()))));
            return Response.ok().build();
        } catch (FileNotFoundException | RuntimeException e) {
            logger.warn("Failed to put file in blobstore or notification on queue.", e);
            statusRepository.add(new Status(fileName, providerId, Status.Action.FILE_TRANSFER, Status.State.FAILED, correlationId, Date.from(Instant.now(Clock.systemDefaultZone()))));
            return Response.serverError().build();
        }
    }

}
