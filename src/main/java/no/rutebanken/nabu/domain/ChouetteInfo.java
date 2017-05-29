package no.rutebanken.nabu.domain;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class ChouetteInfo {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;
    public String xmlns;
    public String xmlnsurl;
    public String referential;
    public String organisation;
    @Column(name="cuser")
    public String user;
    public String regtoppVersion;
    public String regtoppCoordinateProjection;
    public String regtoppCalendarStrategy;
    public String dataFormat;
    public boolean enableValidation;
    public boolean enableStopPlaceUpdate;
    public Long migrateDataToProvider = null;

    public ChouetteInfo(){}

    public ChouetteInfo(String xmlns,String xmlnsurl, String referential, String organisation, String user) {
        this.xmlns = xmlns;
        this.referential = referential;
        this.organisation = organisation;
        this.user = user;
    }

    public ChouetteInfo(Long id, String prefix, String xmlnsurl, String referential, String organisation, String user) {
        this(prefix, xmlnsurl,referential, organisation, user);
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChouetteInfo{" +
                "id=" + id +
                ", xmlns='" + xmlns + '\'' +
                ", xmlnsurl='" + xmlnsurl + '\'' +
                ", referential='" + referential + '\'' +
                ", organisation='" + organisation + '\'' +
                ", user='" + user + '\'' +
                (regtoppVersion != null ? ", regtoppVersion='" + regtoppVersion + '\'' : "") +
                (regtoppCoordinateProjection != null? ", regtoppCoordinateProjection='" + regtoppCoordinateProjection + '\'' : "")+
                (regtoppCalendarStrategy != null? ", regtoppCalendarStrategy='" + regtoppCalendarStrategy + '\'' : "")+
                ", enableValidation='" + enableValidation + '\'' +
                ", enableStopPlaceUpdate='" + enableStopPlaceUpdate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChouetteInfo that = (ChouetteInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (xmlns != null ? !xmlns.equals(that.xmlns) : that.xmlns != null) return false;
        if (referential != null ? !referential.equals(that.referential) : that.referential != null) return false;
        if (organisation != null ? !organisation.equals(that.organisation) : that.organisation != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (xmlns != null ? xmlns.hashCode() : 0);
        result = 31 * result + (referential != null ? referential.hashCode() : 0);
        result = 31 * result + (organisation != null ? organisation.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

}
