package common;

/**
 * Публичный интерфейс АТМ доступный пользователю перед экраном АТМ
 */
public interface PublicAtm {

    /**
     * Выдача указанной суммы
     */
    void withdraw(int sum) throws Exception;
}
