package atm;

import common.CurrencyType;

import java.util.List;

/* package */ interface Cassette extends Cloneable {

    /**
     * Указание слудующей кассеты в chain of responsibility
     */
    void setNext(Cassette cassette);

    /**
     * Возвращает суммарное необходимое количество банкнот указанного номинала
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
