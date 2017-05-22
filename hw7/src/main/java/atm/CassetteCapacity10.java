package atm;

import common.CurrencyType;

/* package */ class CassetteCapacity10 extends CassetteAbstract {

    CassetteCapacity10(CurrencyType currencyType, int nominal) {
        super(currencyType, nominal, 10);
    }
}
