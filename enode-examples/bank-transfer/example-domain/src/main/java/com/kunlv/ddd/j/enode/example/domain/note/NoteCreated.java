package com.kunlv.ddd.j.enode.example.domain.note;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

public class NoteCreated extends DomainEvent<String> {
    private String title;

    public NoteCreated(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
