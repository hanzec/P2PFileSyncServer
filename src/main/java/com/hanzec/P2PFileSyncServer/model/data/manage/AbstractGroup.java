package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.google.gson.annotations.Expose;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@MappedSuperclass
public abstract class AbstractGroup implements Serializable{
    @Getter
    @Expose
    @Id @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Expose
    @Setter
    @NotNull
    @Column(name = "NAME")
    private String name;

    @Expose
    @Getter
    @Setter
    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    public AbstractGroup(){
        this("DEFAULT_NAME");
    }

    public AbstractGroup(String name){
        this(name,"DEFAULT_DESCRIPTION");
    }

    public AbstractGroup(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Override
    public int hashCode(){ return id; }

    @Override
    public String toString(){ return "[" + name + ":" + id + "]:" + description; }
}
