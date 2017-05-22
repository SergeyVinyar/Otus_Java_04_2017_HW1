package atm;

import common.PrivateAtm;
import common.CurrencyType;
import common.DepartmentMediator;

import static common.Utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AtmBuilder {

    private String id;
    private DepartmentMediator department;
    private List<CassetteMetaInfo> cassettes = new ArrayList<>();

    /**
     * Созданный АТМ будет иметь указанный идентификатор
     */
    public AtmBuilder withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Созданный АТМ будет подключен к указанному департаменту
     */
    public AtmBuilder connectTo(DepartmentMediator departmentMediator) {
        this.department = departmentMediator;
        return this;
    }

    /**
     * Созданный АТМ будет оборудован указанным типом кассеты
     * @param currencyType Тип валюты
     * @param nominal Номинал банкнот
     * @param capacity Емкость кассеты
     */
    public AtmBuilder addCassette(CurrencyType currencyType, int nominal, int capacity) {
        this.cassettes.add(new CassetteMetaInfo(currencyType, nominal, capacity));
        return this;
    }

    public PrivateAtm build() throws Exception {
        if (this.id == null)
            throw new Exception("Не указан идентификатор ATM");

        try {
            if (this.department == null)
                throw new Exception("Не указан департамент ATM");

            if (this.cassettes.isEmpty())
                throw new Exception("Не добавлена ни одна кассета");

            AtmImpl atmImpl = new AtmImpl();
            atmImpl.setId(this.id);
            atmImpl.setDepartment(this.department);

            atmImpl.setCassettes(this.cassettes.stream()
                    .map(metainfo -> toRunTimeException(() -> CassetteFactory.getNew(metainfo.currencyType, metainfo.nominal, metainfo.capacity)))
                    .collect(Collectors.toList()));

            return atmImpl;
        } catch (Exception e) {
            throw new Exception(String.format("Ошибка создания ATM с кодом '%s'", this.id), e);
        }
    }

    /**
     * Параметры типа кассеты
     */
    private static class CassetteMetaInfo {

        public CassetteMetaInfo(CurrencyType currencyType, int nominal, int capacity) {
            this.currencyType = currencyType;
            this.nominal = nominal;
            this.capacity = capacity;
        }

        private CurrencyType currencyType;
        private int nominal;
        private int capacity;

        public CurrencyType getCurrencyType() {
            return currencyType;
        }

        public int getNominal() {
            return nominal;
        }

        public int getCapacity() {
            return capacity;
        }
    }
}
