package com.alimama.alimamaspringboot.items;

import org.bson.Document;

public class ModifyRequest {

    private Document filter;
    private Document updatedFields;

    public Document getFilter() {return filter;}
    public void setFilter(Document filter) {this.filter = filter;}

    public Document getUpdatedFields() {return updatedFields;}
    public void setUpdatedFields(Document updatedFields) {this.updatedFields = updatedFields;}
}
