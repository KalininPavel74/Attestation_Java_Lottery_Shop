package toys.view;

import toys.interfaces.IFactoryView;
import toys.interfaces.IView;

public class FactoryView implements IFactoryView {
    public IView create(String title, String charset, char[] exitLetters) {
        return new ViewConsole(title, charset, exitLetters);
    }

}
