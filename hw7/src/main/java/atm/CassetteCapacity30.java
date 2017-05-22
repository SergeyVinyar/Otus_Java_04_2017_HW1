package atm;

import common.CurrencyType;

/* package */ class CassetteCapacity30 extends CassetteAbstract {

    CassetteCapacity30(CurrencyType currencyType, int nominal) {
        super(currencyType, nominal, 30);
    }
}
