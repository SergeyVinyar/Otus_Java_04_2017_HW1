package common;

/**
 * Приватный интерфес АТМ для департамента
 */
public interface Atm {

    /**
     * Возвращает публичный API
     */
    PublicAtmAPI getPublicAtmAPI();

    /**
     * Возвращает список поддерживаемых номиналов банкнот
     */
    int[] getSupportedNominals(CurrencyType currencyType);

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
     * Возвращает общую сумму заданной валюты в кассетах
     */
    int getTotalRestSum(CurrencyType currencyType);

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
