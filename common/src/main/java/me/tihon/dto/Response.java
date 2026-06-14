package me.tihon.dto;

import me.tihon.model.HumanBeing;
import java.io.Serializable;
import java.util.TreeSet;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private TreeSet<HumanBeing> collection = new TreeSet<>();

    public Response (String message) {
        this.message = message;
    }

    public Response (String message, TreeSet<HumanBeing> collection) {
        this.message = message;
        this.collection = collection;
    }

    public String getMessage() {
        return message;
    }

    public TreeSet<HumanBeing> getCollection() {
        return collection;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCollection(TreeSet<HumanBeing> collection) {
        this.collection = collection;
    }
}