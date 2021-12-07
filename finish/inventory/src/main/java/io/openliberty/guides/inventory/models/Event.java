// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory.models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Event")
@NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e")
public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "hostname")
    private String hostname;

    @Column(name = "property")
    private String property;

    @Column(name = "value")
    private String value;
    @Column(name = "sended")
    private boolean sended;
    @Column(name = "readed")
    private boolean readed;

    public Event() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Event(String property) {
        this.property = property;
    }

    public Event(String property, boolean sended) {
        this.property = property;
        this.sended = sended;
    }

    public Event(int id, String hostname, String value, boolean readed) {
        this.id = id;
        this.hostname = hostname;
        this.value = value;
        this.readed = readed;
    }

    public Event(int id, String hostname, String property, String value, boolean sended, boolean readed) {
        this.id = id;
        this.hostname = hostname;
        this.property = property;
        this.value = value;
        this.sended = sended;
        this.readed = readed;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", hostname='" + hostname + '\'' +
                ", property='" + property + '\'' +
                ", value='" + value + '\'' +
                ", sended=" + sended +
                ", readed=" + readed +
                '}';
    }
}
// end::Event[]

