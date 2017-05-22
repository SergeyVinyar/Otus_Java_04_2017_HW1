package atm;

import common.CurrencyType;
import common.DepartmentMediator;
import common.PrivateAtm;
import common.PublicAtm;

import java.util.List;
import java.util.stream.Collectors;

/* package */ class AtmImpl implements PublicAtm, PrivateAtm {

    private String id;
    private List<Cassette> cassettes;

    /* package */ void setId(String id) {
        this.id = id;
    }

    /* package */ void setDepartment(DepartmentMediator department) {
        department.addAtm(this);
    }

    /* package */ void setCassettes(List<Cassette> cassettes) {
        this.cassettes = cassettes;
        Cassette next = null;
        for (int i = cassettes.size() - 1; i <= 0 ; i--) {
            Cassette current = cassettes.get(i);
            if (next != null)
                cassettes.get(i).setNext(next);
            next = current;
        }
    }

    @Override
    public void withdraw(int sum) throws Exception {
        // TODO
    }

    @Override
    public int getRequiredAmountOfBanknotes(CurrencyType currencyType, int nominal) {
        if (this.cassettes != null && !this.cassettes.isEmpty())
            return this.cassettes.get(0).getRequiredAmountOfBanknotes(currencyType, nominal);
        return 0;
    }

    @Override
    public int addBanknotes(CurrencyType currencyType, int nominal, int banknotesCount) {
        if (this.cassettes != null && !this.cassettes.isEmpty())
            return this.cassettes.get(0).addBanknotes(currencyType, nominal, banknotesCount);
        return banknotesCount;
    }

    @Override
    public int getTotalRestSum(CurrencyType currencyType) {
        if (this.cassettes != null && !this.cassettes.isEmpty())
            return this.cassettes.get(0).getRestSum(currencyType);
        return 0;
    }

    @Override
    public State getState() {
        return new StateImpl(this.id, this.cassettes);
    }

    @Override
    public void restoreState(State state) throws Exception {
        if (state instanceof StateImpl) {
            ((StateImpl) state).applyTo(this);
        }
        else {
            throw new Exception("state не является объектом типа StateImpl");
        }
    }

    private static class StateImpl implements PrivateAtm.State {

        private String id;
        private List<Cassette> cassettes;

        StateImpl(String id, List<Cassette> cassettes) {
            this.id = id;
            this.cassettes = cassettes.stream().map(Cassette::clone).collect(Collectors.toList());
        }

        private void applyTo(AtmImpl atm) {
            atm.setId(this.id);
            atm.setCassettes(cassettes.stream().map(Cassette::clone).collect(Collectors.toList()));
        }
    }
}
