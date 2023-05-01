package toys.interfaces;

public interface IFactoryControl {
    IControl create(IView view, IToys toys, ILottery lottery, IFactoryToy fToy, ICSV csv
            , String dbFileToys, String dbFilePrizes, String fileGiveOutPrizes, String charset);
}
