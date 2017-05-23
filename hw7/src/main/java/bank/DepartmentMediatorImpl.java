package bank;

import common.CurrencyType;
import common.DepartmentMediator;
import common.Atm;

import static common.Utils.*;

import java.util.*;

/**
 * Имплементация департамента
 */
public class DepartmentMediatorImpl implements DepartmentMediator {

    private Set<Atm> atmSet = new HashSet<>();
    private Map<Atm, Atm.State> states = new HashMap<>();

    @Override
    public void addAtm(Atm atm) {
        this.atmSet.add(atm);
    }

    /**
     * Возвращает общий остаток соответствующей валюты во всех АТМ
     */
    public int getTotalRestSum(CurrencyType currencyType) {
        return atmSet.stream()
                .map(atm -> atm.getTotalRestSum(currencyType))
                .reduce(0, (total, current) -> total + current);
    }

    /**
     * Возвращает общее потребное количество соответствующих банкнот во всех АТМ
     */
    public int getTotalRequiredAmountOfBanknotes(CurrencyType currencyType, int nominal) {
        return atmSet.stream()
                .map(atm -> atm.getRequiredAmountOfBanknotes(currencyType, nominal))
                .reduce(0, (total, current) -> total + current);
    }

    /**
     * Дозаполняет все АТМ до полного заполнения кассет
     */
    public void refill() {
        refillOneCurrency(CurrencyType.ROUBLE);
        refillOneCurrency(CurrencyType.DOLLAR);
        refillOneCurrency(CurrencyType.EURO);
    }

    private void refillOneCurrency(CurrencyType currencyType) {
        for (Atm atm: atmSet) {
            for (int nominal : atm.getSupportedNominals(currencyType)) {
                int required = atm.getRequiredAmountOfBanknotes(currencyType, nominal);
                atm.addBanknotes(currencyType, nominal, required);
            }
        }
    }

    /**
     * Сохраняет состояния всех АТМ
     */
    public void saveStates() {
        this.states.clear();
        atmSet.forEach(atm -> this.states.put(atm, atm.getState()));
    }

    /**
     * Восстанавливает состояния всех АТМ
     */
    public void restoreStates() {
        this.states.forEach((atm, state) -> toRunTimeException(() -> atm.restoreState(state)));
    }
}
