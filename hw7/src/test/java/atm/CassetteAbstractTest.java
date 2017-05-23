package atm;

import common.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CassetteAbstractTest {

    Cassette cassette;

    @BeforeEach
    void beforeEach() throws Exception {
        cassette = CassetteFactory.getNew(CurrencyType.ROUBLE, 100, 20);
    }

    @Test
    void getRequiredAmountOfBanknotes() {
        cassette.addBanknotes(CurrencyType.ROUBLE, 100, 16);

        assertEquals(4, cassette.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 100));

        assertEquals(0, cassette.getRequiredAmountOfBanknotes(CurrencyType.DOLLAR, 100));
        assertEquals(0, cassette.getRequiredAmountOfBanknotes(CurrencyType.ROUBLE, 50));
    }

    @Test
    void addBanknotes() {
        int coulntPutInside = cassette.addBanknotes(CurrencyType.ROUBLE, 100, 10);
        assertEquals(0, coulntPutInside);

        assertEquals(100 * 10, cassette.getRestSum(CurrencyType.ROUBLE));

        coulntPutInside = cassette.addBanknotes(CurrencyType.ROUBLE, 100, 13);
        assertEquals(3, coulntPutInside);

        coulntPutInside = cassette.addBanknotes(CurrencyType.DOLLAR, 100, 2);
        assertEquals(2, coulntPutInside);
    }

    @Test
    void getRestSum() {
        cassette.addBanknotes(CurrencyType.ROUBLE, 100, 10);
        assertEquals(0, cassette.getRestSum(CurrencyType.DOLLAR));
    }

    @Test
    void withdraw() {
        cassette.addBanknotes(CurrencyType.ROUBLE, 100, 10);
        assertEquals(100 * 10, cassette.getRestSum(CurrencyType.ROUBLE));

        cassette.withdraw(CurrencyType.ROUBLE, 100 * 5);
        assertEquals(100 * 5, cassette.getRestSum(CurrencyType.ROUBLE));
    }

    @Test
    void getSupportedNominals() throws Exception {
        Cassette cassette30 = CassetteFactory.getNew(CurrencyType.ROUBLE, 30, 10);
        Cassette cassette50 = CassetteFactory.getNew(CurrencyType.ROUBLE, 50, 10);
        Cassette cassette100 = CassetteFactory.getNew(CurrencyType.ROUBLE, 100, 10);

        cassette30.setNext(cassette50);
        cassette50.setNext(cassette100);

        Cassette root = cassette30;

        assertTrue(Arrays.equals(new int[] { 100, 50, 30 }, root.getSupportedNominals(CurrencyType.ROUBLE)));
    }

    @Test
    void getBanknoteNominals() {
        cassette.addBanknotes(CurrencyType.ROUBLE, 100, 10);

        // Возьмем три банкноты
        List<Integer> nominals = cassette.getBanknoteNominals(CurrencyType.ROUBLE, 300);
        nominals.sort(Integer::compareTo);

        List<Integer> expectedNominals = new ArrayList<>(3);
        expectedNominals.add(100);
        expectedNominals.add(100);
        expectedNominals.add(100);

        assertEquals(expectedNominals, nominals);

        // Попытаемся взять больше, чем есть
        nominals = cassette.getBanknoteNominals(CurrencyType.ROUBLE, 100 * 11);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>(0);

        assertEquals(expectedNominals, nominals);

        // Попытаемся взять сумму, которую нельзя выдать банкнотами номинала 100
        nominals = cassette.getBanknoteNominals(CurrencyType.ROUBLE, 100 * 5 + 50);
        nominals.sort(Integer::compareTo);

        assertEquals(expectedNominals, nominals);
    }

    @Test
    void getBanknoteNominalsWithCassetteChain() throws Exception {
        Cassette cassette30 = CassetteFactory.getNew(CurrencyType.ROUBLE, 30, 10);
        Cassette cassette50 = CassetteFactory.getNew(CurrencyType.ROUBLE, 50, 10);
        Cassette cassette100 = CassetteFactory.getNew(CurrencyType.ROUBLE, 100, 10);

        cassette30.setNext(cassette50);
        cassette50.setNext(cassette100);

        Cassette root = cassette30;

        root.addBanknotes(CurrencyType.ROUBLE, 30, 4);
        root.addBanknotes(CurrencyType.ROUBLE, 50, 4);
        root.addBanknotes(CurrencyType.ROUBLE, 100, 1);

        // -------------------

        List<Integer> nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 90);
        nominals.sort(Integer::compareTo);

        List<Integer> expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(30);

        assertEquals(expectedNominals, nominals);

        // -------------------

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 80);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(50);

        assertEquals(expectedNominals, nominals);

        // -------------------

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 110);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(50);

        assertEquals(expectedNominals, nominals);

        // -------------------

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 120);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(30);

        assertEquals(expectedNominals, nominals);

        // -------------------

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 170);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(30);
        expectedNominals.add(50);

        assertEquals(expectedNominals, nominals);

        // -------------------

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 180);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(30);
        expectedNominals.add(50);
        expectedNominals.add(100);

        assertEquals(expectedNominals, nominals);

        // -------------------
        // Здесь оптимальным решением было бы выдать 100 + 100,
        // но у нас только одна банкнота 100 рублей,
        // комбайн пытается найти оптимальный вариант с учетом фактического наличия.
        // В результате получается 50 + 50 + 100. Bingo!

        nominals = root.getBanknoteNominals(CurrencyType.ROUBLE, 200);
        nominals.sort(Integer::compareTo);

        expectedNominals = new ArrayList<>();
        expectedNominals.add(50);
        expectedNominals.add(50);
        expectedNominals.add(100);

        assertEquals(expectedNominals, nominals);
    }
}