package atm;

import common.CurrencyType;
import common.DepartmentMediator;
import common.Atm;
import common.PublicAtmAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AtmImplTest {

    private DepartmentMediator department;
    private Atm atm;

    @BeforeEach
    void beforeEach() throws Exception {
        department = mock(DepartmentMediator.class);

        atm = new AtmBuilder()
                .withId("ATM")
                .connectTo(department)
                .addCassette(CurrencyType.ROUBLE,   50, 30)
                .addCassette(CurrencyType.ROUBLE,  100, 30)
                .addCassette(CurrencyType.ROUBLE,  100, 20)
                .addCassette(CurrencyType.ROUBLE,  500, 20)
                .addCassette(CurrencyType.ROUBLE, 1000, 10)
                .addCassette(CurrencyType.ROUBLE, 5000, 10)
                .build();
    }

    @Test
    void connectionToDepartment() {
        verify(department).addAtm(atm);
    }

    private int initCassettes() {
        assertEquals(5, atm.addBanknotes(CurrencyType.ROUBLE, 50, 35));
        assertEquals(8, atm.addBanknotes(CurrencyType.ROUBLE, 100, 58));
        assertEquals(6, atm.addBanknotes(CurrencyType.ROUBLE, 100, 6));
        assertEquals(0, atm.addBanknotes(CurrencyType.ROUBLE, 500, 12));
        assertEquals(0, atm.addBanknotes(CurrencyType.ROUBLE, 1000, 2));
        assertEquals(0, atm.addBanknotes(CurrencyType.ROUBLE, 5000, 3));

        return  50 * 30 +
                100 * 50 +
                500 * 12 +
                1000 * 2 +
                5000 * 3;
    }

    @Test
    void addBanknotes() {
        int expectedSum = initCassettes();
        assertEquals(expectedSum, atm.getTotalRestSum(CurrencyType.ROUBLE));
    }

    @Test
    void withdraw() {
        atm.addBanknotes(CurrencyType.ROUBLE, 50, 5);
        atm.addBanknotes(CurrencyType.ROUBLE, 100, 5);

        int sumBefore = atm.getTotalRestSum(CurrencyType.ROUBLE);

        PublicAtmAPI publicAtmAPI = atm.getPublicAtmAPI();

        assertTrue(publicAtmAPI.withdraw(CurrencyType.ROUBLE, 250));
        assertEquals(sumBefore - 250, atm.getTotalRestSum(CurrencyType.ROUBLE));

        assertTrue(publicAtmAPI.withdraw(CurrencyType.ROUBLE, 100));
        assertEquals(sumBefore - 250 - 100, atm.getTotalRestSum(CurrencyType.ROUBLE));

        // Недостаточная сумма
        assertFalse(publicAtmAPI.withdraw(CurrencyType.ROUBLE, atm.getTotalRestSum(CurrencyType.ROUBLE) + 100));

        // Невозможно выдать банкнотами имеющихся номиналов
        assertFalse(publicAtmAPI.withdraw(CurrencyType.ROUBLE, 25));
    }

    @Test
    void getRequiredAmountOfBanknotes() {
        initCassettes();

        assertEquals(0, atm.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 50));
        assertEquals(0, atm.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 100));
        assertEquals(8, atm.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 500));
        assertEquals(8, atm.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 1000));
        assertEquals(7, atm.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 5000));
    }


    @Test
    void state() throws Exception {
        int initialSum = initCassettes();

        Atm.State state = atm.getState();

        atm.addBanknotes(CurrencyType.ROUBLE, 100, 1);

        assertEquals(initialSum + 100, atm.getTotalRestSum(CurrencyType.ROUBLE));

        atm.restoreState(state);

        assertEquals(initialSum, atm.getTotalRestSum(CurrencyType.ROUBLE));
    }
}