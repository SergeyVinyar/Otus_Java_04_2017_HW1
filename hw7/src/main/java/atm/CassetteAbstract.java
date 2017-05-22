package atm;

import common.CurrencyType;

public abstract class CassetteAbstract implements Cassette {

    protected final int CAPACITY;
    protected final CurrencyType CURRENCY_TYPE;
    protected final int NOMINAL;

    private int amount = 0;

    private Cassette nextCassette;

    protected CassetteAbstract(CurrencyType currencyType, int nominal, int capacity) {
        this.CURRENCY_TYPE = currencyType;
        this.NOMINAL = nominal;
        this.CAPACITY = capacity;
    }

    @Override
    public void setNext(Cassette cassette) {
        this.nextCassette = cassette;
    }

    @Override
    public int getRequiredAmountOfBanknotes(CurrencyType currencyType, int nominal) {
        int result = 0;

        if (this.CURRENCY_TYPE.equals(currencyType) && this.NOMINAL == nominal)
            result += CAPACITY - amount;

        if (this.nextCassette != null)
            result += this.nextCassette.getRequiredAmountOfBanknotes(currencyType, nominal);

        return result;
    }

    @Override
    public int addBanknotes(CurrencyType currencyType, int nominal, int banknotesCount) {
        int result = banknotesCount;

        if (this.CURRENCY_TYPE.equals(currencyType) && this.NOMINAL == nominal) {
            int required = this.CAPACITY - this.amount;

            if (required >= banknotesCount) {
                this.amount += banknotesCount;
                result -= banknotesCount;
            }
            else {
                this.amount += required;
                result -= required;
            }
        }

        if (this.nextCassette != null && result > 0)
            result = this.nextCassette.addBanknotes(currencyType, nominal, result);

        return result;
    }

    @Override
    public int getRestSum(CurrencyType currencyType) {
        int result = 0;

        if (this.CURRENCY_TYPE.equals(currencyType))
            result += this.amount * this.NOMINAL;

        if (this.nextCassette != null)
            result += this.nextCassette.getRestSum(currencyType);

        return result;
    }

    @Override
    public Cassette clone() {
        try {
            return (Cassette) super.clone();
        } catch (CloneNotSupportedException e) {
            return null; // Мы сюда не должны попасть
        }
    }
}
