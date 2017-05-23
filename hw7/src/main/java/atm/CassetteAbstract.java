package atm;

import common.CurrencyType;

import java.util.Collections;
import java.util.List;

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
    public boolean withdraw(CurrencyType currencyType, int sum) {
        List<Integer> nominals = getBanknoteNominals(currencyType, sum);

        if (nominals.isEmpty())
            return false; // Выдать не можем

        // Нам важно равномерно расходовать номиналы, а не кассеты, поэтому возвращаем банкноты соответствующих номиналов
        // из первых попавшихся подходящих кассет
        doWithdraw(currencyType, sum);

        return true;
    }

    @Override
    public List<Integer> getBanknoteNominals(CurrencyType currencyType, int sum) {
        if (!this.CURRENCY_TYPE.equals(currencyType) || sum < this.NOMINAL) {
            if (this.nextCassette != null) {
                return this.nextCassette.getBanknoteNominals(currencyType, sum);
            }
            else {
                return Collections.emptyList(); // Не можем выдать запрошенную сумму
            }
        }

        if (sum == this.NOMINAL)
            return Collections.singletonList(this.NOMINAL); // Одной банкнотой выдадим, меньшим количеством уже никак не получится

        // Варианты, среди которых мы ищем оптимальный по количеству выдаваемых банкнот:
        List<Integer> nominals1 = Collections.emptyList();
        List<Integer> nominals2 = Collections.emptyList();
        if (this.nextCassette != null) {

            // 1) Мы можем выдать банкноту из данной кассеты и какое-то количество банкнот из других кассет
            nominals1 = this.nextCassette.getBanknoteNominals(currencyType, sum - NOMINAL);
            nominals1.add(this.NOMINAL);

            // 2) Мы можем не выдать ничего из данной кассеты и какое-то количество банкнот из других кассет
            nominals2 = this.nextCassette.getBanknoteNominals(currencyType, sum);
        }

        // 3) Мы можем выдать несколько банкнот из данной кассеты и какое-то количество банкнот из других кассет
        List<Integer> nominals3 = getBanknoteNominals(currencyType, sum - NOMINAL);
        nominals3.add(this.NOMINAL);

        // Выбираем тот вариант, количество банкнот в котором минимально и при этот составляет запрашиваемую сумму

        int nominals1Size = Integer.MAX_VALUE;
        if (nominals1.stream().reduce(0, (accumulator, nominal) -> accumulator + nominal).equals(sum))
            nominals1Size = nominals1.size();

        int nominals2Size = Integer.MAX_VALUE;
        if (nominals2.stream().reduce(0, (accumulator, nominal) -> accumulator + nominal).equals(sum))
            nominals2Size = nominals2.size();

        int nominals3Size = Integer.MAX_VALUE;
        if (nominals3.stream().reduce(0, (accumulator, nominal) -> accumulator + nominal).equals(sum))
            nominals3Size = nominals3.size();

        int minSize = Math.min(nominals1Size, Math.min(nominals2Size, nominals3Size));

        if (minSize == Integer.MAX_VALUE)
            return Collections.emptyList(); // Ни одна комбинация не позволит выдать запрашиваемую сумму

        if (minSize == nominals1Size)
            return nominals1;
        else if (minSize == nominals2Size)
            return nominals2;
        else if (minSize == nominals3Size)
            return nominals3;

        return Collections.emptyList(); // Сюда мы не должны попасть
    }

    private void doWithdraw(CurrencyType currencyType, int sum) {

        if (this.CURRENCY_TYPE.equals(currencyType)) {
            int amount = sum / this.NOMINAL;

            this.amount -= amount;
            sum -= amount * this.NOMINAL;
        }

        if (this.nextCassette != null) {
            ((CassetteAbstract) this.nextCassette).doWithdraw(currencyType, sum);
        }
        else {
            assert sum == 0;
        }
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
