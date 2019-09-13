package com.kunlv.ddd.j.enode.core.commanding;

import com.kunlv.ddd.j.enode.common.extensions.IEnum;

public enum CommandAddResult implements IEnum {
    @Deprecated
    Other(0), //为兼容C#版本而增加，不会用到

    Success(1),
    DuplicateCommand(2);

    CommandAddResult(int status) {
        _status = status;
    }

    private int _status;

    @Override
    public int status() {
        return _status;
    }
}
