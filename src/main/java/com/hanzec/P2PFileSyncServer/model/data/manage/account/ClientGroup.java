package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractGroup;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import lombok.Getter;
import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name ="CLIENT_GROUP",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "NAME")
        })
public class ClientGroup extends AbstractGroup implements Serializable {
    @Getter
    @OneToMany(mappedBy="group",cascade={CascadeType.ALL})
    private final Set<ClientAccount> clients = new HashSet<>();

    public ClientGroup(){
        this("DEFAULT_NAME");
    }

    public ClientGroup(String name){
        this(name,"DEFAULT_DESCRIPTION");
    }

    public ClientGroup(String name, String description){
        super(name,description);
    }
}
