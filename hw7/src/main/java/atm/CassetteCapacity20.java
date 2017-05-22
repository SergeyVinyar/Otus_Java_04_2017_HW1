package atm;

import common.CurrencyType;

/* package */ class CassetteCapacity20 extends CassetteAbstract {

    CassetteCapacity20(CurrencyType currencyType, int nominal) {
        super(currencyType, nominal, 20);
    }
}
