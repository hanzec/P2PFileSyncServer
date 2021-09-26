package com.hanzec.P2PFileSyncServer.model.data.manage.authenticate;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
        name ="PERMISSION",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "name")
        })
public class Permission implements GrantedAuthority {
    @Id @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Getter
    @Setter
    @NotNull
    @Column(name = "NAME")
    private String name;

    @Getter
    @Setter
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    public Permission(){
        this("DEFAULT NAME");
    }

    public Permission(String name){
        this(name, "DEFAULT DESCRIPTION");
    }

    public Permission(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    @Override
    public String toString(){ return "[" + name + "：" + id + "]:" + description;}
}
