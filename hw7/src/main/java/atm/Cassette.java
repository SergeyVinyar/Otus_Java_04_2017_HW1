package atm;

import common.CurrencyType;

import java.util.List;

/* package */ interface Cassette extends Cloneable {

    /**
     * Возвращает номинал банкнот, на которые настроена кассета
     */
    int getNominal();

    /**
     * Возвращает максимальное количество банкнот, которые можно разместить в кассете
     */
    int getCapacity();

    /**
     * Возвращает тип валюты, на который настроена кассета
     */
    CurrencyType getCurrencyType();

    /**
     * Указание слудующей кассеты в chain of responsibility
     */
    void setNext(Cassette cassette);

    /**
     * Возвращает список поддерживаемых номиналов банкнот
     */
    int[] getSupportedNominals(CurrencyType currencyType);

    /**
     * Возвращает суммарное необходимое количество банкнот указанного номинала, чтобы заполнить кассету полностью
     */
    int getRequiredAmountOfBanknotes(CurrencyType currencyType, int nominal);

    /**
     * Добавить в кассеты заданное количество банкнот указанного номинала
     * @return Количество банкнот, которые не поместились в кассеты
     */
    int addBanknotes(CurrencyType currencyType, int nominal, int banknotesCount);

    /**
     * Возвращает оставшуюся сумму указанной валюты в кассетах
     */
    int getRestSum(CurrencyType currencyType);

    /**
     * Выдача средств
     * @param sum Запрашиваемая сумма
     * @return true - сумма выдана успешно
     */
    boolean withdraw(CurrencyType currencyType, int sum);

    /**
     * Возвращает наименьший список банкнот (номиналов), которыми можно выдать запрашиваемую сумму
     */
    List<Integer> getBanknoteNominals(CurrencyType currencyType, int sum);

    Cassette clone();
}
