package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name ="USER_ROLE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "NAME")
        })
public class Group{
    @Getter
    @Id @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Setter
    @NotNull
    @Column(name = "NAME")
    private String name;

    @Getter
    @Setter
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    @Getter
    @OneToMany(
            mappedBy = "role",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private final Set<UserAccount> roleAccount = new HashSet<>();

    @Getter
    @JoinTable()
    @ManyToMany(targetEntity = Permission.class)
    private final Set<GrantedAuthority> permissions = new HashSet<>();

    public Group(){
        this("DEFAULT_NAME");
    }

    public Group(String name){
        this(name,"DEFAULT_DESCRIPTION");
    }

    public Group(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Override
    public int hashCode(){ return id; }

    @Override
    public String toString(){ return "[" + name + ":" + id + "]:" + description; }
}
