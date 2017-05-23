package common;

/**
 * Публичный интерфейс АТМ доступный пользователю перед экраном АТМ
 */
public interface PublicAtmAPI {

    /**
     * Выдача указанной суммы
     * @return true - выдано успешно
     */
    boolean withdraw(CurrencyType currencyType, int sum);
}
