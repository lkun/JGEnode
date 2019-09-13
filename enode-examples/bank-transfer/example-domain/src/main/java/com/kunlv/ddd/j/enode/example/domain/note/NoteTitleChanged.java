package com.kunlv.ddd.j.enode.example.domain.note;

import com.kunlv.ddd.j.enode.core.eventing.DomainEvent;

public class NoteTitleChanged extends DomainEvent<String> {
    private String title;

    public NoteTitleChanged(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
