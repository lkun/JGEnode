package com.kunlv.ddd.j.enode.example.consumer.commandhandles.note;
import com.kunlv.ddd.j.enode.common.io.Task;
import com.kunlv.ddd.j.enode.core.annotation.Command;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.core.commanding.ICommandContext;
import com.kunlv.ddd.j.enode.example.commands.note.ChangeNoteTitleCommand;
import com.kunlv.ddd.j.enode.example.domain.note.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Command
public class ChangeNoteTitleCommandHandler {
    private Logger logger = LoggerFactory.getLogger(ChangeNoteTitleCommandHandler.class);

    @Subscribe
    public void handleAsync(ICommandContext context, ChangeNoteTitleCommand command) {
        logger.info(command.getTitle());
        CompletableFuture<Note> future = context.getAsync(command.getAggregateRootId(), false, Note.class);
        Note note = Task.await(future);
        if (note == null) {
            return;
        }
        logger.info("note:{}", note.getId());
        note.changeTitle(command.getTitle());
    }
}
