package toys.util;

import toys.interfaces.ICSV;
import toys.interfaces.IFactoryUtil;

public class FactoryUtil implements IFactoryUtil {
    @Override
    public ICSV create() { return new CSV(); };
}
