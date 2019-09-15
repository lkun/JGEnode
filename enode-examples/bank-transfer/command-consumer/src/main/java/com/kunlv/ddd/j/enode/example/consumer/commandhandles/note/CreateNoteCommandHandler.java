package com.kunlv.ddd.j.enode.example.consumer.commandhandles.note;

import com.kunlv.ddd.j.enode.core.annotation.Command;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.commanding.ICommandContext;
import com.kunlv.ddd.j.enode.example.commands.note.CreateNoteCommand;
import com.kunlv.ddd.j.enode.example.domain.note.Note;

@Command
public class CreateNoteCommandHandler {
    /**
     * Handle the given aggregate command.
     *
     * @param context
     * @param command
     * @return
     */
    @Subscribe
    public void handleAsync(ICommandContext context, CreateNoteCommand command) {
        Note note = new Note(command.getAggregateRootId(), command.getTitle());
        context.add(note);
    }
}
