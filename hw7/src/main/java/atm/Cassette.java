package atm;

import common.CurrencyType;

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

    Cassette clone();
}
