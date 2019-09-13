package com.kunlv.ddd.j.enode.common.utilities;

import com.kunlv.ddd.j.enode.common.function.Action;
import com.kunlv.ddd.j.enode.common.function.Func;

public class Helper {
    public static void eatException(Action action) {
        try {
            action.apply();
        } catch (Exception e) {
        }
    }

    public static <T> T eatException(Func<T> action, T defaultValue) {
        try {
            return action.apply();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
