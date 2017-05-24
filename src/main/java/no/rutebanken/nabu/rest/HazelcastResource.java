package no.rutebanken.nabu.rest;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/hazelcast")
public class HazelcastResource {

    @Autowired
    private HazelcastInstance hazelcastInstance;


    @GET
    public List<String> getMembers() {
        return hazelcastInstance.getCluster().getMembers().stream().map(member -> member.toString()).collect(Collectors.toList());
    }
}
