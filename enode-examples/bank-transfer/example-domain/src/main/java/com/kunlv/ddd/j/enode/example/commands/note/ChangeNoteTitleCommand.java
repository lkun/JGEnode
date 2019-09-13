package com.kunlv.ddd.j.enode.example.commands.note;

import com.kunlv.ddd.j.enode.core.commanding.Command;

public class ChangeNoteTitleCommand extends Command<String> {
    private String title;

    public ChangeNoteTitleCommand(String aggregateRootId, String title) {
        super(aggregateRootId);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
