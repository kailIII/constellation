/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.constellation.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a summary of dataset information necessary for dashboard.
 *
 * @author Mehdi Sidhoum (Geomatys).
 * @version 0.9
 * @since 0.9
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSetBrief implements Serializable {
    private Integer id;
    @XmlElement(name="Name")
    private String name;
    @XmlElement(name="Type")
    private String type;
    @XmlElement(name="Owner")
    private String owner;
    @XmlElement(name="Children")
    private List<DataBrief> children = new ArrayList<>();
    @XmlElement(name="Keywords")
    private List<String> keywords = new ArrayList<>();
    @XmlElement(name="Resume")
    private String resume;
    @XmlElement(name="Date")
    private Long date;
    @XmlElement(name="MdCompletion")
    private Integer mdCompletion;

    public DataSetBrief() {}

    public DataSetBrief(final Integer id, final String name, final String type, final String owner,
                        final List<DataBrief> children, final Long date, final Integer mdCompletion) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.children = children;
        this.date = date;
        this.mdCompletion = mdCompletion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<DataBrief> getChildren() {
        return children;
    }

    public void setChildren(List<DataBrief> children) {
        this.children = children;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMdCompletion() {
        return mdCompletion;
    }

    public void setMdCompletion(Integer mdCompletion) {
        this.mdCompletion = mdCompletion;
    }

    @Override
    public String toString() {
        return "DatasetBrief{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", owner='" + owner + '\'' +
                ", children=" + children +
                ", keywords=" + keywords +
                ", resume=" + resume +
                ", date=" + date +
                '}';
    }
}
