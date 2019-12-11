package io.nybbles.progcalc.shell.contracts;

import com.diogonunes.jcdp.color.api.Ansi;
import io.nybbles.progcalc.common.Result;

public interface Shell {
    Result run();

    void format(
            Ansi.Attribute attribute,
            Ansi.FColor foregroundColor,
            Ansi.BColor backgroundColor,
            String fmt,
            Object... args);

    boolean initialize(Result r);

    void format(String fmt, Object... args);
}
