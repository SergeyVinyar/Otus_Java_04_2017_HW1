package bank;

import common.Atm;
import common.CurrencyType;
import common.DepartmentMediator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DepartmentMediatorImplTest {

    private DepartmentMediatorImpl department;
    private Atm atm1;
    private Atm atm2;
    private Atm atm3;

    @BeforeEach
    void beforeEach() {
        department = new DepartmentMediatorImpl();

        atm1 = mock(Atm.class);
        atm2 = mock(Atm.class);
        atm3 = mock(Atm.class);

        department.addAtm(atm1);
        department.addAtm(atm2);
        department.addAtm(atm3);
    }

    @Test
    void getTotalRestSum() {
        when(atm1.getTotalRestSum(CurrencyType.ROUBLE)).thenReturn(8);
        when(atm2.getTotalRestSum(CurrencyType.ROUBLE)).thenReturn(10);
        when(atm3.getTotalRestSum(CurrencyType.ROUBLE)).thenReturn(4);

        assertEquals(8 + 10 + 4, department.getTotalRestSum(CurrencyType.ROUBLE));
        assertEquals(0, department.getTotalRestSum(CurrencyType.DOLLAR));
    }

    @Test
    void getTotalRequiredAmountOfBanknotes() {
        when(atm1.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 10)).thenReturn(8);
        when(atm2.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 10)).thenReturn(10);
        when(atm3.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 10)).thenReturn(4);

        assertEquals(8 + 10 + 4, department.getTotalRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 10));
        assertEquals(0, department.getTotalRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 50));
        assertEquals(0, department.getTotalRequiredAmountOfBanknotes(CurrencyType.DOLLAR, 10));
    }

    @Test
    void refill() {
        when(atm1.getSupportedNominals(CurrencyType.ROUBLE)).thenReturn(new int[] { 10, 20 });
        when(atm2.getSupportedNominals(CurrencyType.ROUBLE)).thenReturn(new int[] { 20, 30 });
        when(atm3.getSupportedNominals(CurrencyType.ROUBLE)).thenReturn(new int[] { 30 });

        when(atm1.getSupportedNominals(CurrencyType.DOLLAR)).thenReturn(new int[0]);
        when(atm2.getSupportedNominals(CurrencyType.DOLLAR)).thenReturn(new int[0]);
        when(atm3.getSupportedNominals(CurrencyType.DOLLAR)).thenReturn(new int[0]);

        when(atm1.getSupportedNominals(CurrencyType.EURO)).thenReturn(new int[0]);
        when(atm2.getSupportedNominals(CurrencyType.EURO)).thenReturn(new int[0]);
        when(atm3.getSupportedNominals(CurrencyType.EURO)).thenReturn(new int[0]);

        when(atm1.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 10)).thenReturn(12);
        when(atm1.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 20)).thenReturn(23);

        when(atm2.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 20)).thenReturn(34);
        when(atm2.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 30)).thenReturn(45);

        when(atm3.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 30)).thenReturn(65);

        department.refill();

        verify(atm1).addBanknotes(CurrencyType.ROUBLE, 10, 12);
        verify(atm1).addBanknotes(CurrencyType.ROUBLE, 20, 23);

        verify(atm2).addBanknotes(CurrencyType.ROUBLE, 20, 34);
        verify(atm2).addBanknotes(CurrencyType.ROUBLE, 30, 45);

        verify(atm3).addBanknotes(CurrencyType.ROUBLE, 30, 65);
    }

    @Test
    void states() throws Exception {
        Atm.State state1 = mock(Atm.State.class);
        Atm.State state2 = mock(Atm.State.class);
        Atm.State state3 = mock(Atm.State.class);

        when(atm1.getState()).thenReturn(state1);
        when(atm2.getState()).thenReturn(state2);
        when(atm3.getState()).thenReturn(state3);

        department.saveStates();

        department.restoreStates();

        verify(atm1).restoreState(state1);
        verify(atm2).restoreState(state2);
        verify(atm3).restoreState(state3);
    }
}