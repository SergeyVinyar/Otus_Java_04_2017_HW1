package common;

/**
 * Приватный интерфес АТМ для департамента
 */
public interface PrivateAtm {

    /**
     * Возвращает потребное количество банкнот соответствующего номинала, чтобы заполнить соответствующие кассеты полностью
     */
    int getRequiredAmountOfBanknotes(CurrencyType currencyType, int nominal);

    /**
     * Добавить в ATM заданное количество банкнот соответствующего номинала
     * @return Количество банкнот, которые не поместилось в кассеты
     */
    int addBanknotes(CurrencyType currencyType, int nominal, int banknotesCount);

    /**
     * Возращает объект, хранящий слепок текущего состояния АТМ
     */
    State getState();

    /**
     * Восстанавливает состояние АТМ на момент создания слепка
     */
    void restoreState(State state) throws Exception;

    /**
     * Интерфейс слепка
     * Здесь действительно ничего нет :)
     */
    interface State {
    }
}
