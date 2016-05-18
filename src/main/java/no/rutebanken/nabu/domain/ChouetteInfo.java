package no.rutebanken.nabu.domain;

import javax.persistence.*;

@Entity
public class ChouetteInfo {

    @Id
    public Long id;
    public String prefix;
    public String referential;
    public String organisation;
    @Column(name="cuser")
    public String user;
    public String regtoppVersion;
    public String regtoppCoordinateProjection;

    public ChouetteInfo(){}

    public ChouetteInfo(String prefix, String referential, String organisation, String user) {
        this.prefix = prefix;
        this.referential = referential;
        this.organisation = organisation;
        this.user = user;
    }

    public ChouetteInfo(Long id, String prefix, String referential, String organisation, String user) {
        this(prefix, referential, organisation, user);
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChouetteInfo{" +
                "id=" + id +
                ", prefix='" + prefix + '\'' +
                ", referential='" + referential + '\'' +
                ", organisation='" + organisation + '\'' +
                ", user='" + user + '\'' +
                ", regtoppVersion='" + regtoppVersion + '\'' +
                ", regtoppCoordinateProjection='" + regtoppCoordinateProjection + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChouetteInfo that = (ChouetteInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;
        if (referential != null ? !referential.equals(that.referential) : that.referential != null) return false;
        if (organisation != null ? !organisation.equals(that.organisation) : that.organisation != null) return false;
        return user != null ? user.equals(that.user) : that.user == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (referential != null ? referential.hashCode() : 0);
        result = 31 * result + (organisation != null ? organisation.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

}
