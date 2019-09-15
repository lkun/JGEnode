package com.kunlv.ddd.j.enode.example.consumer.eventhandles.note;

import com.kunlv.ddd.j.enode.common.io.AsyncTaskResult;
import com.kunlv.ddd.j.enode.core.annotation.Event;
import com.kunlv.ddd.j.enode.core.annotation.Subscribe;
import com.kunlv.ddd.j.enode.example.domain.note.NoteCreated;
import com.kunlv.ddd.j.enode.example.domain.note.NoteTitleChanged;
import com.kunlv.ddd.j.enode.example.domain.note.NoteTitleChanged2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Event
public class NoteEventHandler {
    public static Logger logger = LoggerFactory.getLogger(NoteEventHandler.class);

    @Subscribe
    public AsyncTaskResult handleAsync(NoteTitleChanged evnt) {
        logger.info("NoteTitleChanged Note denormalizered, title：{}, Version: {},endTime:{}", evnt.getTitle(), evnt.getVersion(), System.currentTimeMillis());
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(NoteCreated evnt) {
        logger.info("NoteCreated title：{}, Version: {},endTime:{}", evnt.getTitle(), evnt.getVersion(), System.currentTimeMillis());
        return AsyncTaskResult.Success;
    }

    @Subscribe
    public AsyncTaskResult handleAsync(NoteTitleChanged2 evnt) {
        logger.info("NoteTitleChanged2 Note denormalizered, title：{}, Version: {}", evnt.getTitle(), evnt.getVersion());
        return AsyncTaskResult.Success;
    }
}
