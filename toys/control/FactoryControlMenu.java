package toys.control;

import toys.interfaces.*;

public class FactoryControlMenu implements IFactoryControl {
    @Override
    public IControl create(IView view, IToys toys, ILottery lottery, IFactoryToy fToy, ICSV csv
            , String dbFileToys, String dbFilePrizes, String fileGiveOutPrizes, String charset) {
        return new ControlMenu(view, toys, lottery, fToy, csv, dbFileToys, dbFilePrizes, fileGiveOutPrizes, charset);
    }
}
