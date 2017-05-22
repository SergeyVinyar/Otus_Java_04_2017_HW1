package atm;

import common.CurrencyType;

/* package */ final class CassetteFactory {

    private CassetteFactory() {
    }

    static Cassette getNew(CurrencyType currencyType, int nominal, int capacity) throws Exception {
        switch (capacity) {
            case 10: return new CassetteCapacity10(currencyType, nominal);
            case 20: return new CassetteCapacity20(currencyType, nominal);
            case 30: return new CassetteCapacity30(currencyType, nominal);
        }
        throw new Exception(String.format("Нет кассеты с вместимостью %s банкнот", capacity));
    }
}
